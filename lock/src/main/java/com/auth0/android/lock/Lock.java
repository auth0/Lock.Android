package com.auth0.android.lock;

/**
 * Created by nikolaseu on 1/21/16.
 */
public class Lock {
    private final LocalResultReceiver receiver;
    private AuthenticationCallback callback;
    private final LockOptions options;

    public static final String AUTHENTICATION_ACTION = "Lock.Authentication";
    public static final String CANCELED_ACTION = "Lock.Canceled";
    public static final String SIGNUP_ACTION = "Lock.Signup";


    private static class LocalResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent data) {
            // Get extra data included in the Intent
            String action = data.getAction();
            if (action.equals(Lock.AUTHENTICATION_ACTION)){
                processEvent(data);
            } else if (action.equals(Lock.CANCELED_ACTION)){
              callback.onCanceled();
            } else if (action.equals(Lock.SIGNUP_ACTION)){
              //processSignup()?
            }
        }
    };

    protected Lock() {
        // no public constructor √
        throw Exception();
    }

    protected Lock(LockOptions options) {
        this.options = options;
        this.receiver = new LocalResultReceiver();
    }

    public void onCreate(Activity activity) {
        // nikolaseu: register broadcast listener only when callback is set?
        //            i.e. when NOT using startForResult
        //  L= lets leave this as an improvement

        //if (callback != null) //can the callback be optional?
            LocalBroadcastManager.getInstance(activity).register(this.receiver);
    }

    public void onDestroy(Activity activity) {
        // unregister listener (if something was registered)
        if (options.receiver != null) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(this.receiver);
            options.receiver = null;
        }
    }

    /*
    Evaluate changing the name of this method: parseActivityResult? processResult?
    */
    public void onActivityResult(Activity activity, int resultCode, Intent data) {
      if (resultCode == Result.RESULT_OK){
        processEvent(data);
        return;
      }

      //user pressed back.
      callback.onCanceled();
    }

    private void processEvent(Intent eventData) {
      //black magic. (?) parse eventData
      Authentication authentication = (Authentication) eventData.getExtra("authentication");

      if (authentication != null) {
          callback.onAuthentication(authentication);
      } else {
        Auth0Exception up = new Auth0Exception("wops!");
        callback.onError(up);
        //throw up. haha
      }
      return null;
    }

    public static class Auth0Exception extends Exception {
      public Auth0Exception(String message){
        super(message);
      }
    }

    private static class LockOptions {  //lets imlement Parcelable
        public Auth0 account;
        public AuthenticationCallback callback;
        public boolean useBrowser;
        public boolean closable;
        public boolean fullscreen;
        public Map<String, Object> authenticationParameters;
        public boolean sendSDKInfo = true;
        public boolean useEmail = false;
        public boolean signUpEnabled = true;
        public boolean changePasswordEnabled = true;
        public List<String> connections;
        public List<String>enterpriseConnectionsUsingWebForm;
        public String defaultDatabaseConnection;
    }

    public static class Builder {
        private LockOptions options;

        public Builder(){
            options = new LockOptions();
        }

        public Lock build(){
            return new Lock(options);
        }

        public Builder newBuilder(){
            return new Lock.Builder();
        }

        public Builder withAccount(Auth0 account){
            options.account = account;
            return this;
        }

        public Builder withCallback(AuthenticationCallback callback){
            options.callback = callback;
            return this;
        }

        public Builder useBrowser(boolean useBrowser){
            options.useBrowser = useBrowser;
            return this;
        }

        public Builder closable(boolean closable) {
            options.closable = closable;
            return this;
        }

        public Builder fullscreen(boolean fullscreen) {
            options.fullscreen = fullscreen;
            return this;
        }

        public Builder withAuthenticationParameters(Map<String, Object> authenticationParameters) {
            options.authenticationParameters = authenticationParameters;
            return this;
        }

        public Builder onlyUseConnections(Map<String> connections) {
            options.connections = connections;
            return this;
        }

        public Builder doNotSendSDKInfo() {
            options.sendSDKInfo = false;
            return this;
        }

        public Builder useEmail() {
            options.useEmail = true;
            return this;
        }

        public Buidler disableSignUp() {
            options.signUpEnabled = false;
            return this;
        }

        public Buidler disableChangePassword() {
            options.changePasswordEnabled = false;
            return this;
        }
    }
}
