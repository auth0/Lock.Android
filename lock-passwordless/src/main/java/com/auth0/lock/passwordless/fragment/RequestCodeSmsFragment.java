/*
 * RequestCodeFragment.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.lock.passwordless.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.fragment.BaseTitledFragment;
import com.auth0.lock.passwordless.R;
import com.auth0.lock.passwordless.event.CountryCodeSelectedEvent;
import com.auth0.lock.passwordless.event.PasscodeRequestedEvent;
import com.auth0.lock.passwordless.event.SelectCountryCodeEvent;
import com.auth0.lock.passwordless.event.PasscodeSentEvent;
import com.auth0.lock.passwordless.task.LoadCountriesTask;
import com.auth0.lock.passwordless.validation.PhoneNumberValidator;
import com.auth0.lock.passwordless.widget.PhoneField;
import com.auth0.lock.validation.Validator;
import com.squareup.otto.Subscribe;

import java.util.Locale;
import java.util.Map;

public class RequestCodeSmsFragment extends BaseTitledFragment {

    private static final String TAG = RequestCodeSmsFragment.class.getName();
    private static final String LAST_PHONE_NUMBER_KEY = "LAST_PHONE_NUMBER";
    private static final String LAST_PHONE_DIAL_CODE_KEY = "LAST_PHONE_DIAL_CODE_KEY";

    private static final String USE_MAGIC_LINK_ARGUMENT = "USE_MAGIC_LINK_ARGUMENT";

    private boolean useMagicLink;

    AsyncTask<String, Void, Map<String, String>> task;
    Validator validator;
    PhoneField phoneField;
    Button sendButton;
    ProgressBar progressBar;

    public static RequestCodeSmsFragment newInstance(boolean useMagicLink) {
        RequestCodeSmsFragment fragment = new RequestCodeSmsFragment();
        Bundle args = new Bundle();
        args.putBoolean(USE_MAGIC_LINK_ARGUMENT, useMagicLink);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Loading countries...");

        if (getArguments() != null) {
            useMagicLink = getArguments().getBoolean(USE_MAGIC_LINK_ARGUMENT);
        }

        bus.register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (task != null) {
            task.cancel(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    protected int getTitleResource() {
        return R.string.com_auth0_passwordless_title_send_passcode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_auth0_passwordless_fragment_request_code_sms, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validator = new PhoneNumberValidator(R.id.com_auth0_passwordless_phone_field_sms, R.string.com_auth0_passwordless_send_code_error_tile_sms, R.string.com_auth0_passwordless_send_code_no_phone_message_sms);
        phoneField = (PhoneField) view.findViewById(R.id.com_auth0_passwordless_phone_field_sms);
        phoneField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new SelectCountryCodeEvent());
            }
        });
        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String phoneNumber = preferences.getString(LAST_PHONE_NUMBER_KEY, null);
        final String dialCode = preferences.getString(LAST_PHONE_DIAL_CODE_KEY, null);
        phoneField.setPhoneNumber(phoneNumber);
        task = new LoadCountriesTask(getActivity()) {
            @Override
            protected void onPostExecute(Map<String, String> codes) {
                task = null;
                if (codes != null) {
                    Locale locale = Locale.getDefault();
                    String code = dialCode != null ? dialCode : codes.get(locale.getCountry());
                    if (code != null) {
                        phoneField.setDialCode(code);
                    }
                }
            }
        };
        task.execute(LoadCountriesTask.COUNTRIES_JSON_FILE);
        sendButton = (Button) view.findViewById(R.id.com_auth0_passwordless_access_button_sms);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSmsCode();
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.com_auth0_passwordless_send_code_progress_indicator_sms);
        final Button hasCodeButton = (Button) view.findViewById(R.id.com_auth0_passwordless_already_has_code_button_sms);
        hasCodeButton.setVisibility((!useMagicLink && phoneNumber != null && dialCode != null) ? View.VISIBLE : View.GONE);
        hasCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                final String phoneNumber = preferences.getString(LAST_PHONE_NUMBER_KEY, null);
                final String dialCode = preferences.getString(LAST_PHONE_DIAL_CODE_KEY, null);
                if (phoneNumber != null && dialCode != null) {
                    bus.post(new PasscodeSentEvent(dialCode + phoneNumber));
                }
            }
        });
    }

    private void requestSmsCode() {
        AuthenticationError error = validator.validateFrom(this);
        if (error == null) {
            sendRequestCode();
        } else {
            bus.post(error);
        }
    }

    private void sendRequestCode() {
        sendButton.setEnabled(false);
        sendButton.setText("");
        progressBar.setVisibility(View.VISIBLE);

        String phoneNumber = phoneField.getCompletePhoneNumber();
        bus.post(new PasscodeRequestedEvent(phoneNumber));
    }

    @SuppressWarnings("unused")
    @Subscribe public void onPasscodeSentEvent(PasscodeSentEvent event) {
        final SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(LAST_PHONE_NUMBER_KEY, phoneField.getPhoneNumber());
        editor.putString(LAST_PHONE_DIAL_CODE_KEY, phoneField.getDialCode());
        editor.apply();
        sendButton.setEnabled(true);
        sendButton.setText(R.string.com_auth0_passwordless_send_passcode_btn_text);
        progressBar.setVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    @Subscribe public void onAuthenticationError(AuthenticationError error) {
        sendButton.setEnabled(true);
        sendButton.setText(R.string.com_auth0_passwordless_send_passcode_btn_text);
        progressBar.setVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    @Subscribe public void onCountrySelected(CountryCodeSelectedEvent event) {
        Log.d(TAG, "Received selected country " + event.getIsoCode() + " dial code " + event.getDialCode());
        phoneField.setDialCode(event.getDialCode());
    }
}
