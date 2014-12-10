package com.auth0.api;

/**
 * Created by hernan on 11/28/14.
 */
public interface BaseCallback<T> extends Callback {

    void onSuccess(T payload);

}
