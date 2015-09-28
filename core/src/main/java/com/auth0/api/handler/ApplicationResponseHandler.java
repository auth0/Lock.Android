package com.auth0.api.handler;

import android.util.Log;

import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * Callback and response handler used when requesting Auth0's app info.
 */
public abstract class ApplicationResponseHandler extends AsyncHttpResponseHandler implements BaseCallback<Application> {

    /**
     * JSON-POJO mapper (Jackson)
     */
    private ObjectMapper mapper;

    protected ApplicationResponseHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * @see com.loopj.android.http.AsyncHttpResponseHandler#onSuccess(int, cz.msebera.android.httpclient.Header[], byte[])
     */
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        try {
            String jsonp = new String(responseBody);
            JSONTokener tokener = new JSONTokener(jsonp);
            tokener.nextValue();
            JSONObject jsonObject = (JSONObject) tokener.nextValue();
            Log.d(ApplicationResponseHandler.class.getName(), "Obtained JSON object from JSONP: " + jsonObject);
            Application app = this.mapper.readValue(jsonObject.toString(), Application.class);
            this.onSuccess(app);
        } catch (JSONException | IOException e) {
            this.onFailure(statusCode, headers, responseBody, e);
        }
    }

    /**
     * @see com.loopj.android.http.AsyncHttpResponseHandler#onFailure(int, cz.msebera.android.httpclient.Header[], byte[], Throwable)
     */
    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        this.onFailure(error);
    }
}
