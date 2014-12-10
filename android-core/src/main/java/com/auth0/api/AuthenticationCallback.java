package com.auth0.api;

import com.auth0.core.Token;
import com.auth0.core.UserProfile;

/**
 * Created by hernan on 12/1/14.
 */
public interface AuthenticationCallback extends Callback {

    void onSuccess(UserProfile profile, Token token);

}
