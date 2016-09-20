package com.auth0.android.lock.internal.configuration;

interface BaseConnection {

    /**
     * Getter for the connection name.
     *
     * @return the connection name
     */
    String getName();

    /**
     * Getter for the strategy name.
     *
     * @return the strategy name
     */
    String getStrategy();

    /**
     * Returns a value using its key
     *
     * @param key    a key
     * @param tClazz type of value to retrieve from the map
     * @param <T>    type of value to return
     * @return a value
     */
    <T> T valueForKey(String key, Class<T> tClazz);

    /**
     * Returns a boolean value for the given key
     *
     * @param key a key
     * @return a boolean value
     */
    boolean booleanForKey(String key);
}
