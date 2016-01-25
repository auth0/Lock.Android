package com.auth0.android.lock;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.auth0.Auth0Exception;
import com.auth0.android.lock.utils.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

/**
 * Created by lbalmaceda on 1/21/16.
 */
public class LockActivity extends Activity {

    private static final String TAG = LockActivity.class.getSimpleName();
    private LockOptions options;
    private Application application;
    private static final String JSONP_PREFIX = "Auth0.setClient(";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = getIntent().getParcelableExtra(Lock.OPTIONS_EXTRA);
        if (options == null) {
            throw new IllegalArgumentException("Missing LockOptions");
        }

        fetchApplicationInfo();
    }

    /**
     * Fetch application information from Auth0
     *
     * @return a Auth0 request to start
     */
    public void fetchApplicationInfo() {
        OkHttpClient client = new OkHttpClient();
        Uri uri = Uri.parse(options.account.getConfigurationUrl()).buildUpon().appendPath("client")
                .appendPath(options.account.getClientId() + ".js").build();

        com.squareup.okhttp.Request req = new com.squareup.okhttp.Request.Builder()
                .url(uri.toString())
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                Log.e(TAG, "Failed to fetchApplication: " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                application = parseJSONP(response);
                Log.i(TAG, "Application received!: " + application.getId());
            }
        });

    }


    private Application parseJSONP(Response response) {
        try {
            String json = response.body().string();
            final int length = JSONP_PREFIX.length();
            if (json.length() < length) {
                throw new JSONException("Invalid App Info JSONP");
            }
            json = json.substring(length);
            JSONTokener tokenizer = new JSONTokener(json);
            if (!tokenizer.more()) {
                throw tokenizer.syntaxError("Invalid App Info JSONP");
            }
            Object nextValue = tokenizer.nextValue();
            if (!(nextValue instanceof JSONObject)) {
                tokenizer.back();
                throw tokenizer.syntaxError("Invalid JSON value of App Info");
            }
            JSONObject jsonObject = (JSONObject) nextValue;
            return new ObjectMapper().reader(Application.class).readValue(jsonObject.toString());
        } catch (IOException | JSONException e) {
            throw new Auth0Exception("Failed to parse response to request", e);
        }
    }


}
