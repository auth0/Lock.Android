package com.auth0.api;

import android.os.Build;
import android.util.Log;

import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.api.handler.APIResponseHandler;
import com.auth0.api.handler.ApplicationResponseHandler;
import com.auth0.core.Application;
import com.auth0.core.Connection;
import com.auth0.core.Strategy;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.auth0.api.ParameterBuilder.GRANT_TYPE_PASSWORD;

/**
 * Created by hernan on 11/27/14.
 */
public class APIClient {

    private static final String APPLICATION_JSON = "application/json";

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String DEFAULT_DB_CONNECTION = "Username-Password-Authentication";
    private static final String ID_TOKEN_KEY = "id_token";

    private static final String BASE_URL_FORMAT = "https://%s.auth0.com";
    private static final String APP_INFO_CDN_URL_FORMAT = "https://cdn.auth0.com/client/%s.js";
    private static final String EMAIL_KEY = "email";
    private static final String TENANT_KEY = "tenant";

    private final String clientID;
    private final String tenantName;
    private final AsyncHttpClient client;
    private final String baseURL;
    private final JsonEntityBuilder entityBuilder;
    private Application application;

    public APIClient(String clientID, String tenantName) {
        this.clientID = clientID;
        this.tenantName = tenantName;
        this.client = new AsyncHttpClient();
        this.client.setUserAgent(String.format("%s (%s Android %s)", tenantName, Build.MODEL, Build.VERSION.RELEASE));
        this.baseURL = String.format(BASE_URL_FORMAT, tenantName);
        this.entityBuilder = new JsonEntityBuilder(new ObjectMapper());
    }

    public Application getApplication() {
        return application;
    }

    public String getClientID() {
        return clientID;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void fetchApplicationInfo(final BaseCallback<Application> callback) {
        final String appInfoURL = String.format(APP_INFO_CDN_URL_FORMAT, this.clientID);
        Log.v(APIClient.class.getName(), "Fetching application info from " + appInfoURL);
        this.client.get(appInfoURL, null, new ApplicationResponseHandler(new ObjectMapper()) {
            @Override
            public void onSuccess(Application app) {
                Log.d(APIClient.class.getName(), "Obtained application with id " + app.getId() + " tenant " + app.getTenant());
                callback.onSuccess(app);
                APIClient.this.application = app;
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(APIClient.class.getName(), "Failed to fetch application from Auth0 CDN", error);
                callback.onFailure(error);
            }
        });
    }

    public void login(final String username, String password, Map<String, String> params, final AuthenticationCallback callback) {
        final String loginURL = this.baseURL + "/oauth/ro";

        Map<String, String> request = ParameterBuilder.newBuilder()
                .set(USERNAME_KEY, username)
                .set(PASSWORD_KEY, password)
                .setGrantType(GRANT_TYPE_PASSWORD)
                .setClientId(this.clientID)
                .setConnection(getDBConnectionName())
                .addAll(params)
                .asDictionary();

        Log.v(APIClient.class.getName(), "Performing login with parameters " + request);
        login(loginURL, request, callback);
    }

    public void socialLogin(final String connectionName, String accessToken, Map<String, String> parameters, final AuthenticationCallback callback) {
        final String loginURL = this.baseURL + "/oauth/access_token";

        Map<String, String> params = parameters != null ? new HashMap<>(parameters) : new HashMap<String, String>();
        if (params.containsKey("access_token")) {
            params.put("main_access_token", params.remove("access_token"));
        }
        Map<String, String> request = ParameterBuilder.newBuilder()
                .setClientId(this.clientID)
                .setConnection(connectionName)
                .setAccessToken(accessToken)
                .addAll(params)
                .asDictionary();

        Log.v(APIClient.class.getName(), "Performing social login with parameters " + request);
        login(loginURL, request, callback);
    }

    private void login(final String url, final Map<String, String> request, final AuthenticationCallback callback) {
        try {
            HttpEntity entity = this.entityBuilder.newEntityFrom(request);
            this.client.post(null, url, entity, APPLICATION_JSON, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        final Token token = new ObjectMapper().readValue(responseBody, Token.class);
                        Log.d(APIClient.class.getName(), "Logged in with " + url + " jwt " + token.getIdToken());
                        APIClient.this.fetchProfile(token, callback);
                    } catch (IOException e) {
                        Log.e(APIClient.class.getName(), "Failed to parse JSON of token info", e);
                        this.onFailure(statusCode, headers, responseBody, e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(APIClient.class.getName(), "Failed login user with " + url, error);
                    Map errorResponse = null;
                    if (statusCode == 400 || statusCode == 401) {
                        try {
                            errorResponse = new ObjectMapper().readValue(responseBody, Map.class);
                            Log.e(APIClient.class.getName(), "Login error " + errorResponse);
                        } catch (IOException e) {
                            Log.w(APIClient.class.getName(), "Failed to parse json error response", error);
                        }
                    }
                    callback.onFailure(new APIClientException("Failed to perform login", error, statusCode, errorResponse));
                }
            });
        } catch (JsonEntityBuildException e) {
            Log.e(APIClient.class.getName(), "Failed to build request parameters " + request, e);
            callback.onFailure(e);
        }
    }

    public void signUp(final String email, final String password, final Map<String, String> parameters, final AuthenticationCallback callback) {
        AsyncHttpResponseHandler handler = new APIResponseHandler<AuthenticationCallback>(callback) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(APIClient.class.getName(), "Signed up username " + email);
                APIClient.this.login(email, password, parameters, this.callback);
            }
        };
        signUp(email, password, parameters, handler);
    }

    public void createUser(final String email, final String password, final Map<String, String> parameters, final BaseCallback<Void> callback) {
        AsyncHttpResponseHandler handler = new APIResponseHandler<BaseCallback>(callback) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(APIClient.class.getName(), "Signed up username " + email);
                this.callback.onSuccess(null);
            }
        };
        signUp(email, password, parameters, handler);
    }

    private void signUp(String email, String password, Map<String, String> parameters, AsyncHttpResponseHandler callback) {
        String signUpUrl = this.baseURL + "/dbconnections/signup";

        Map<String, String> request = ParameterBuilder.newBuilder()
                .set(EMAIL_KEY, email)
                .set(PASSWORD_KEY, password)
                .setClientId(this.clientID)
                .set(TENANT_KEY, getTenant())
                .setConnection(getDBConnectionName())
                .addAll(parameters)
                .asDictionary();

        Log.v(APIClient.class.getName(), "Performing signup with parameters " + request);
        try {
            HttpEntity entity = entityBuilder.newEntityFrom(request);
            this.client.post(null, signUpUrl, entity, APPLICATION_JSON, callback);
        } catch (JsonEntityBuildException e) {
            Log.e(APIClient.class.getName(), "Failed to build request parameters " + request, e);
            callback.onFailure(0, null, null, e);
        }
    }

    public void changePassword(final String email, String newPassword, Map<String, String> parameters, BaseCallback<Void> callback) {
        String changePasswordUrl = this.baseURL + "/dbconnections/change_password";

        Map<String, String> request = ParameterBuilder.newBuilder()
                .set(EMAIL_KEY, email)
                .set(PASSWORD_KEY, newPassword)
                .setClientId(this.clientID)
                .set(TENANT_KEY, getTenant())
                .setConnection(getDBConnectionName())
                .addAll(parameters)
                .asDictionary();

        Log.v(APIClient.class.getName(), "Performing change password with parameters " + request);
        try {
            HttpEntity entity = this.entityBuilder.newEntityFrom(request);
            this.client.post(null, changePasswordUrl, entity, APPLICATION_JSON, new APIResponseHandler<BaseCallback<Void>>(callback) {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    callback.onSuccess(null);
                }
            });
        } catch (JsonEntityBuildException e) {
            Log.e(APIClient.class.getName(), "Failed to build request parameters " + request, e);
            callback.onFailure(e);
        }
    }

    public void fetchUserProfile(String idToken, final BaseCallback<UserProfile> callback) {
        Log.v(APIClient.class.getName(), "Fetching user profile with token " + idToken);
        final String profileURL = this.baseURL + "/tokeninfo";
        Map<String, String> request = ParameterBuilder.newBuilder()
                .set(ID_TOKEN_KEY, idToken)
                .asDictionary();
        try {
            HttpEntity entity = this.entityBuilder.newEntityFrom(request);
            this.client.post(null, profileURL, entity, APPLICATION_JSON, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        UserProfile profile = new ObjectMapper().readValue(responseBody, UserProfile.class);
                        Log.d(APIClient.class.getName(), "Obtained user profile");
                        callback.onSuccess(profile);
                    } catch (IOException e) {
                        Log.e(APIClient.class.getName(), "Failed to parse JSON of profile", e);
                        this.onFailure(statusCode, headers, responseBody, e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(APIClient.class.getName(), "Failed obtain user profile", error);
                    callback.onFailure(error);
                }
            });
        } catch (JsonEntityBuildException e) {
            Log.e(APIClient.class.getName(), "Failed to build request parameters " + request, e);
            callback.onFailure(e);
        }
    }

    private void fetchProfile(final Token token, final AuthenticationCallback callback) {
        this.fetchUserProfile(token.getIdToken(), new BaseCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                callback.onSuccess(profile, token);
            }

            @Override
            public void onFailure(Throwable error) {
                callback.onFailure(error);
            }
        });
    }

    private String getDBConnectionName() {
        if (this.application == null || this.application.getDatabaseStrategy() == null) {
            return DEFAULT_DB_CONNECTION;
        }
        Strategy strategy = this.application.getDatabaseStrategy();
        Connection db = strategy.getConnections().get(0);
        return db.getName();
    }

    private String getTenant() {
        return application.getTenant() != null ? application.getTenant(): tenantName;
    }
}
