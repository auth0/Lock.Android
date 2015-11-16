/*
 * InProgressFragment.java
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

package com.auth0.lock.email.fragment;


import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.auth0.lock.email.R;
import com.auth0.lock.fragment.BaseTitledFragment;

public class InProgressFragment extends BaseTitledFragment {

    private static final String TITLE_ARGUMENT = "TITLE_ARGUMENT";
    private static final String MESSAGE_ARGUMENT = "MESSAGE_ARGUMENT";

    private int titleResourceId;
    private String email;


    public static InProgressFragment newInstance(int titleResourceId, String email) {
        InProgressFragment fragment = new InProgressFragment();
        Bundle args = new Bundle();
        args.putInt(TITLE_ARGUMENT, titleResourceId);
        args.putString(MESSAGE_ARGUMENT, email);
        fragment.setArguments(args);
        return fragment;
    }

    public InProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            titleResourceId = getArguments().getInt(TITLE_ARGUMENT);
            email = getArguments().getString(MESSAGE_ARGUMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_auth0_fragment_in_progress, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String messageFormat = getString(R.string.com_auth0_email_login_message_in_progress);
        TextView progressTextView = (TextView) view.findViewById(R.id.com_auth0_email_progress_message);
        progressTextView.setText(Html.fromHtml(String.format(messageFormat, email)));
    }

    @Override
    protected int getTitleResource() {
        return titleResourceId;
    }
}
