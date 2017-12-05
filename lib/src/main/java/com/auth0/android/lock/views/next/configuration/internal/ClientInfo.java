package com.auth0.android.lock.views.next.configuration.internal;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.Auth0Exception;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by lbalmaceda on 30/11/2017.
 */

public class ClientInfo {
    private static final String TAG = ClientInfo.class.getSimpleName();
    private static final String JSONP_PREFIX = "Auth0.setClient(";

    public ClientInfo(@NonNull Auth0 account, ClientInfoCallback callback) {
        loadData(account.getConfigurationUrl(), account.getClientId(), callback);
    }

    private void loadData(String configurationUrl, String clientId, final ClientInfoCallback callback) {
        Uri uri = Uri.parse(configurationUrl).buildUpon().appendPath("client")
                .appendPath(clientId + ".js").build();

        Request req = new Request.Builder()
                .url(uri.toString())
                .build();

        new OkHttpClient().newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                Log.e(TAG, "Failed to fetch the Client Info: " + e.getMessage(), e);
                callback.onClientInfoError(new Auth0Exception("Failed to fetch the Client Info.", e));
            }

            @Override
            public void onResponse(Response response) {
                List<Connection> connections;
                try {
                    connections = parseJSONP(response);
                } catch (Auth0Exception e) {
                    Log.e(TAG, "Could not parse Client Info JSONP: " + e.getMessage());
                    callback.onClientInfoError(new Auth0Exception("Failed to parse the Client Info.", e));
                    return;
                }

                Log.i(TAG, "Client Info received!");
                callback.onClientInfoReceived(connections);
            }
        });
    }

    private List<Connection> parseJSONP(Response response) throws Auth0Exception {
        try {
            String json = response.body().string();
            final int length = JSONP_PREFIX.length();
            if (json.length() < length) {
                throw new JSONException("Invalid Client Info JSONP");
            }
            json = json.substring(length);
            JSONTokener tokenizer = new JSONTokener(json);
            if (!tokenizer.more()) {
                throw tokenizer.syntaxError("Invalid Client Info JSONP");
            }
            Object nextValue = tokenizer.nextValue();
            if (!(nextValue instanceof JSONObject)) {
                tokenizer.back();
                throw tokenizer.syntaxError("Invalid JSON value of Client Info");
            }
            JSONObject jsonObject = (JSONObject) nextValue;
            Type applicationType = new TypeToken<List<Connection>>() {
            }.getType();
            return createGson().fromJson(jsonObject.toString(), applicationType);
        } catch (IOException | JSONException e) {
            throw new Auth0Exception("Failed to parse response to request", e);
        }
    }

    private Gson createGson() {
        Type type = new TypeToken<List<Connection>>() {
        }.getType();
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(type, new ClientInfoDeserializer())
                .create();
    }
}
