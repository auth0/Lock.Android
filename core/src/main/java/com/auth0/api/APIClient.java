package com.auth0.api;

import android.util.Log;

import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.api.callback.RefreshIdTokenCallback;
import com.auth0.api.handler.APIResponseHandler;
import com.auth0.core.Application;
import com.auth0.core.Auth0;
import com.auth0.core.Connection;
import com.auth0.core.Strategy;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

import static com.auth0.api.ParameterBuilder.GRANT_TYPE_PASSWORD;

/**
 * API client for Auth0 Authentication API.
 * @see <a href="https://auth0.com/docs/auth-api">Auth API docs</a>
 */
public class APIClient extends BaseAPIClient {

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String DEFAULT_DB_CONNECTION = "Username-Password-Authentication";
    private static final String ID_TOKEN_KEY = "id_token";
    private static final String EMAIL_KEY = "email";
    private static final String TOKEN_TYPE_KEY = "token_type";
    private static final String EXPIRES_IN_KEY = "expires_in";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    private Application application;
    private AuthenticationAPIClient newClient;

    /**
     * Creates a new API client instance providing Auth API and Configuration Urls different than the default. (Useful for on premise deploys).
     * @param clientID Your application clientID.
     * @param baseURL Auth0's auth API endpoint
     * @param configurationURL Auth0's enpoint where App info can be retrieved.
     * @param tenantName Name of the tenant. Can be null
     */
    public APIClient(String clientID, String baseURL, String configurationURL, String tenantName) {
        super(clientID, baseURL, configurationURL, tenantName);
        this.newClient = new AuthenticationAPIClient(new Auth0(clientID, baseURL, configurationURL));
    }

    /**
     * Creates a new API client instance providing Auth API and Configuration Urls different than the default. (Useful for on premise deploys).
     * @param clientID Your application clientID.
     * @param baseURL Auth0's auth API endpoint
     * @param configurationURL Auth0's enpoint where App info can be retrieved.
     */
    public APIClient(String clientID, String baseURL, String configurationURL) {
        this(clientID, baseURL, configurationURL, null);
    }

    /**
     * Creates a new API client using clientID and tenant name.
     * @param clientID Your application clientID.
     * @param tenantName Name of the tenant.
     * @deprecated since 1.7.0, instead build using clientID and baseURL
     */
    public APIClient(String clientID, String tenantName) {
        super(clientID, tenantName);
        this.newClient = new AuthenticationAPIClient(new Auth0(clientID, getBaseURL()));
    }

    /**
     * Fetch application information from {@link #getConfigurationURL()}
     * @param callback called with the application info on success or with the failure reason.
     */
    public void fetchApplicationInfo(final BaseCallback<Application> callback) {
        newClient.fetchApplicationInfo(callback);
    }

    /**
     * Performs a Database connection login with username/email and password.
     * @param username email or username required to login. By default it should be an email
     * @param password user's password
     * @param params additional parameters sent to the API like 'scope'
     * @param callback called with User's profile and tokens or failure reason
     */
    public void login(final String username, String password, Map<String, Object> params, final AuthenticationCallback callback) {
        final String loginURL = getBaseURL() + "/oauth/ro";

        Map<String, Object> request = ParameterBuilder.newBuilder()
                .set(USERNAME_KEY, username)
                .set(PASSWORD_KEY, password)
                .setGrantType(GRANT_TYPE_PASSWORD)
                .setClientId(getClientID())
                .setConnection(getDBConnectionName())
                .addAll(params)
                .asDictionary();

        Log.v(APIClient.class.getName(), "Performing login with parameters " + request);
        login(loginURL, request, callback);
    }

    /**
     * Performs a Social connection login using Identity Provider (IdP) credentials.
     * @param connectionName name of the social connection
     * @param accessToken accessToken from the IdP.
     * @param parameters additional parameters sent to the API like 'scope'
     * @param callback called with User's profile and tokens or failure reason
     */
    public void socialLogin(final String connectionName, String accessToken, Map<String, Object> parameters, final AuthenticationCallback callback) {
        final String loginURL = getBaseURL() + "/oauth/access_token";

        Map<String, Object> params = parameters != null ? new HashMap<>(parameters) : new HashMap<String, Object>();
        if (params.containsKey("access_token")) {
            params.put("main_access_token", params.remove("access_token"));
        }
        Map<String, Object> request = ParameterBuilder.newBuilder()
                .setClientId(getClientID())
                .setConnection(connectionName)
                .setAccessToken(accessToken)
                .addAll(params)
                .asDictionary();

        Log.v(APIClient.class.getName(), "Performing social login with parameters " + request);
        login(loginURL, request, callback);
    }

    /**
     * Performs a SMS connection login with a phone number and verification code.
     * @param phoneNumber number where the verificationCode was received.
     * @param verificationCode received by SMS.
     * @param parameters additional parameters sent to the API like 'scope'
     * @param callback called with User's profile and tokens or failure reason
     */
    public void smsLogin(String phoneNumber, String verificationCode, Map<String, Object> parameters, final AuthenticationCallback callback) {
        final String loginURL = getBaseURL() + "/oauth/ro";

        Map<String, Object> request = ParameterBuilder.newBuilder()
                .set(USERNAME_KEY, phoneNumber)
                .set(PASSWORD_KEY, verificationCode)
                .setGrantType(GRANT_TYPE_PASSWORD)
                .setClientId(getClientID())
                .setConnection("sms")
                .addAll(parameters)
                .asDictionary();

        Log.v(APIClient.class.getName(), "Performing sms code login with parameters " + request);
        login(loginURL, request, callback);
    }

    /**
     * Performs an Email connection login with an email and verification code.
     * @param email where the user received the verificationCode
     * @param verificationCode sent by email
     * @param parameters to be sent for authentication in the request, useful to add extra values to Auth0 or override defaults
     * @param callback called with user's profile and tokens, or failure reason
     */
    public void emailLogin(String email, String verificationCode, Map<String, Object> parameters, final AuthenticationCallback callback) {
        final String loginURL = getBaseURL() + "/oauth/ro";

        final Map<String, Object> request = ParameterBuilder.newBuilder()
                .set(USERNAME_KEY, email)
                .set(PASSWORD_KEY, verificationCode)
                .setGrantType(GRANT_TYPE_PASSWORD)
                .setClientId(getClientID())
                .setConnection("email")
                .addAll(parameters)
                .asDictionary();

        Log.v(APIClient.class.getName(), "Performing email code login with parameters " + request);
        login(loginURL, request, callback);
    }

    private void login(final String url, final Map<String, Object> request, final AuthenticationCallback callback) {
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

    /**
     * Creates and logs in a new User using a Database connection.
     * @param email new user email
     * @param username new user username
     * @param password new user password
     * @param parameters additional parameters additional parameters sent to the API like 'scope'
     * @param callback called with User's profile and tokens or failure reason
     */
    public void signUp(final String email, final String username, final String password, final Map<String, Object> parameters, final AuthenticationCallback callback) {
        AsyncHttpResponseHandler handler = new APIResponseHandler<AuthenticationCallback>(callback) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(APIClient.class.getName(), "Signed up username " + email);
                APIClient.this.login(email, password, parameters, this.callback);
            }
        };
        signUp(email, username, password, parameters, handler);
    }

    /**
     * Creates and logs in a new User using a Database connection.
     * @param email new user email
     * @param password new user password
     * @param parameters additional parameters additional parameters sent to the API like 'scope'
     * @param callback called with User's profile and tokens or failure reason
     */
    public void signUp(final String email, final String password, final Map<String, Object> parameters, final AuthenticationCallback callback) {
        signUp(email, null, password, parameters, callback);
    }

    /**
     * Creates a new user using a Database connection
     * @param email new user email
     * @param username new user username
     * @param password new user password
     * @param parameters additional parameters additional parameters sent to the API like 'scope'
     * @param callback callback that will notify if the user was successfully created or not.
     */
    public void createUser(final String email, final String username, final String password, final Map<String, Object> parameters, final BaseCallback<Void> callback) {
        AsyncHttpResponseHandler handler = new APIResponseHandler<BaseCallback<Void>>(callback) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(APIClient.class.getName(), "Signed up username " + email);
                this.callback.onSuccess(null);
            }
        };
        signUp(email, username, password, parameters, handler);
    }

    /**
     * Creates a new user using a Database connection
     * @param email new user email
     * @param password new user password
     * @param parameters additional parameters additional parameters sent to the API like 'scope'
     * @param callback callback that will notify if the user was successfully created or not.
     */
    public void createUser(final String email, final String password, final Map<String, Object> parameters, final BaseCallback<Void> callback) {
        createUser(email, null, password, parameters, callback);
    }

    private void signUp(final String email, final String username, final String password, final Map<String, Object> parameters, final AsyncHttpResponseHandler callback) {
        String signUpUrl = getBaseURL() + "/dbconnections/signup";

        Map<String, Object> request = ParameterBuilder.newBuilder()
                .set(EMAIL_KEY, email != null ? email : username)
                .set(PASSWORD_KEY, password)
                .set(USERNAME_KEY, username != null ? username : email)
                .setClientId(getClientID())
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

    /**
     * Request a change for the user's password using a Database connection.
     * @param email user's email
     * @param newPassword new password for the user
     * @param parameters additional parameters additional parameters sent to the API like 'scope'
     * @param callback callback that will notify if the user password request was sent or not.
     */
    public void changePassword(final String email, String newPassword, Map<String, Object> parameters, BaseCallback<Void> callback) {
        String changePasswordUrl = getBaseURL() + "/dbconnections/change_password";

        Map<String, Object> request = ParameterBuilder.newBuilder()
                .set(EMAIL_KEY, email)
                .set(PASSWORD_KEY, newPassword)
                .setClientId(getClientID())
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

    /**
     * Fetches the user's profile with a valid 'id_token'
     * @param idToken a JWT token associated to the user.
     * @param callback called with the user's profile on success or with the failure reason
     */
    public void fetchUserProfile(String idToken, final BaseCallback<UserProfile> callback) {
        Log.v(APIClient.class.getName(), "Fetching user profile with token " + idToken);
        final String profileURL = getBaseURL() + "/tokeninfo";
        Map<String, Object> request = ParameterBuilder.newBuilder()
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

    /**
     * Obtains a new id_token from Auth0 using another valid id_token
     * @param idToken user's id_token
     * @param parameters delegation api parameters
     * @param callback called with new token in success or with the failure reason on error
     */
    public void fetchIdTokenWithIdToken(String idToken, Map<String, Object> parameters, final RefreshIdTokenCallback callback) {
        Map<String, Object> request = ParameterBuilder.newEmptyBuilder()
                .set(ID_TOKEN_KEY, idToken)
                .addAll(parameters)
                .asDictionary();
        fetchDelegationToken(request, new BaseCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> payload) {
                final String id_token = (String) payload.get(ID_TOKEN_KEY);
                final String token_type = (String) payload.get(TOKEN_TYPE_KEY);
                final Integer expires_in = (Integer) payload.get(EXPIRES_IN_KEY);
                callback.onSuccess(id_token, token_type, expires_in);
            }

            @Override
            public void onFailure(Throwable error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Obtains a new id_token from Auth0 using a refresh_token obtained on login when the scope has 'offline_access'
     * @param refreshToken user's refresh token
     * @param parameters delegation api parameters
     * @param callback called with new token in success or with the failure reason on error
     */
    public void fetchIdTokenWithRefreshToken(String refreshToken, Map<String, Object> parameters, final RefreshIdTokenCallback callback) {
        Map<String, Object> request = ParameterBuilder.newEmptyBuilder()
                .set(REFRESH_TOKEN_KEY, refreshToken)
                .addAll(parameters)
                .asDictionary();
        fetchDelegationToken(request, new BaseCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> payload) {
                final String id_token = (String) payload.get(ID_TOKEN_KEY);
                final String token_type = (String) payload.get(TOKEN_TYPE_KEY);
                final Integer expires_in = (Integer) payload.get(EXPIRES_IN_KEY);
                callback.onSuccess(id_token, token_type, expires_in);
            }

            @Override
            public void onFailure(Throwable error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Calls Auth0 delegation API to perform a delegated authentication, e.g. fetch Firebase or AWS tokens to call their API.
     * The response of this API call will return a different response based on 'api_type' parameters, that's why the callback only returns a {@link java.util.Map}.
     * @param parameters delegation api parameters
     * @param callback called with delegation api response in success or with the failure reason on error.
     */
    public void fetchDelegationToken(Map<String, Object> parameters, final BaseCallback<Map<String, Object>> callback) {
        Log.v(APIClient.class.getName(), "Fetching delegation token");
        final String delegationURL = getBaseURL() + "/delegation";
        Map<String, Object> request = ParameterBuilder.newEmptyBuilder()
                .setClientId(getClientID())
                .setGrantType("urn:ietf:params:oauth:grant-type:jwt-bearer")
                .addAll(parameters)
                .asDictionary();
        try {
            HttpEntity entity = this.entityBuilder.newEntityFrom(request);
            this.client.post(null, delegationURL, entity, APPLICATION_JSON, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
                        final Map<String, Object> delegation = new ObjectMapper().readValue(responseBody, typeRef);
                        Log.d(APIClient.class.getName(), "Obtained delegation token info: " + delegation);
                        callback.onSuccess(delegation);
                    } catch (IOException e) {
                        Log.e(APIClient.class.getName(), "Failed to parse JSON of delegation token info", e);
                        this.onFailure(statusCode, headers, responseBody, e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(APIClient.class.getName(), "Failed obtain delegation token info", error);
                    callback.onFailure(error);
                }
            });
        } catch (JsonEntityBuildException e) {
            Log.e(APIClient.class.getName(), "Failed to build request parameters " + request, e);
            callback.onFailure(e);
        }
    }

    /**
     * Remove an account from another accounts identities.
     * @param userId Id of the user account to remove, e.g.: if its a facebook account it will be 'facebook|fb_user_id'.
     * @param accessToken Access token of the account that owns the account to unlink
     * @param callback to call on either success or failure.
     */
    public void unlinkAccount(String userId, String accessToken, final BaseCallback<Void> callback) {
        String signUpUrl = getBaseURL() + "/unlink";

        Map<String, Object> request = ParameterBuilder.newBuilder()
                .set("clientID", this.getClientID())
                .set("user_id", userId)
                .set("access_token", accessToken)
                .asDictionary();

        Log.v(APIClient.class.getName(), "Performing unlink with parameters " + request);
        try {
            HttpEntity entity = entityBuilder.newEntityFrom(request);
            this.client.post(null, signUpUrl, entity, APPLICATION_JSON, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    callback.onSuccess(null);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(APIClient.class.getName(), "Failed to unlink user", error);
                    callback.onFailure(error);
                }
            });
        } catch (JsonEntityBuildException e) {
            Log.e(APIClient.class.getName(), "Failed to build request parameters " + request, e);
            callback.onFailure(e);
        }
    }

    /**
     * Start passwordless authentication flow calling "/passwordless/start" API.
     * @param parameters sent to API to start the flow
     * @param callback to call on either success or failure
     */
    public void startPasswordless(Map<String, Object> parameters, final BaseCallback<Void> callback) {
        String startUrl = getBaseURL() + "/passwordless/start";

        Map<String, Object> request = ParameterBuilder.newBuilder()
                .clearAll()
                .setClientId(this.getClientID())
                .addAll(parameters)
                .asDictionary();

        Log.v(APIClient.class.getName(), "Starting passwordless authentication with parameters " + request);
        try {
            HttpEntity entity = entityBuilder.newEntityFrom(request);
            this.client.post(null, startUrl, entity, APPLICATION_JSON, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    callback.onSuccess(null);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(APIClient.class.getName(), "Failed to start passwordless authentication", error);
                    callback.onFailure(error);
                }
            });
        } catch (JsonEntityBuildException e) {
            Log.e(APIClient.class.getName(), "Failed to build request parameters " + request, e);
            callback.onFailure(e);
        }
    }

    /**
     * Request a verification code to login to be sent via SMS
     * @param phoneNumber where the code is sent
     * @param callback to call on either success or failure
     */
    public void requestSMSVerificationCode(String phoneNumber, final BaseCallback<Void> callback) {
        Map<String, Object> request = ParameterBuilder.newBuilder()
                .clearAll()
                .setClientId(this.getClientID())
                .setConnection("sms")
                .set("phone_number", phoneNumber)
                .asDictionary();
        startPasswordless(request, callback);
    }

    /**
     * Request a verification code to login to be sent via email
     * @param email where the code is sent
     * @param callback to call on either success or failure
     */
    public void requestEmailVerificationCode(String email, final BaseCallback<Void> callback) {
        Map<String, Object> request = ParameterBuilder.newBuilder()
                .clearAll()
                .setClientId(this.getClientID())
                .setConnection("email")
                .set("email", email)
                .set("send", "code")
                .asDictionary();
        startPasswordless(request, callback);
    }

    /**
     * Starts passwordless authentication with SMS, this will send a One Time Password to the user's phone via SMS
     * @param phoneNumber to where the SMS one time password will be sent
     * @param callback to call on either success or failure
     * @deprecated in favor of generic {@link #startPasswordless(Map, BaseCallback)} or more specific ones {@link #requestSMSVerificationCode(String, BaseCallback)} and {@link #requestEmailVerificationCode(String, BaseCallback)}
     */
    public void startPasswordless(String phoneNumber, final BaseCallback<Void> callback) {
        requestSMSVerificationCode(phoneNumber, callback);
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

}
