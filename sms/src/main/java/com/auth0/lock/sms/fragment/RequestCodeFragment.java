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

package com.auth0.lock.sms.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.auth0.lock.fragment.BaseTitledFragment;
import com.auth0.lock.sms.R;
import com.auth0.lock.sms.task.LoadCountriesTask;

import java.util.Locale;
import java.util.Map;

public class RequestCodeFragment extends BaseTitledFragment {

    public static final String TAG = RequestCodeFragment.class.getName();

    Map<String, String> codes;
    AsyncTask<String, Void, Map<String, String>> task;

    Button countryButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Loading countries...");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (task != null) {
            task.cancel(true);
        }
    }

    @Override
    protected int getTitleResource() {
        return R.string.sms_title_send_passcode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_code, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        countryButton = (Button) view.findViewById(R.id.sms_country_code_button);
        task = new LoadCountriesTask(getActivity()) {
            @Override
            protected void onPostExecute(Map<String, String> result) {
                codes = result;
                task = null;
                if (codes != null) {
                    Locale locale = Locale.getDefault();
                    String code = codes.get(locale.getCountry());
                    if (code != null) {
                        countryButton.setText(code);
                    }
                }
            }
        };
        task.execute(LoadCountriesTask.COUNTRIES_JSON_FILE);
    }

}
