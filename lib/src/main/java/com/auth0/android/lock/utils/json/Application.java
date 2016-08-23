/*
 * Application.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
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

package com.auth0.android.lock.utils.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Class with your Auth0's application information and the list of enabled connections (DB, Social, Enterprise, Passwordless).
 * Disclaimer: This class may change in future releases. Don't use it directly.
 */
public class Application {

    private String id;
    private String tenant;
    private String authorizeURL;
    private String callbackURL;
    private String subscription;
    private boolean hasAllowedOrigins;
    private List<AuthData> connections;

    public Application() {
        connections = new ArrayList<>();
    }

    public Application(Application application) {
        id = application.id;
        tenant = application.tenant;
        authorizeURL = application.authorizeURL;
        callbackURL = application.callbackURL;
        subscription = application.subscription;
        hasAllowedOrigins = application.hasAllowedOrigins;
        connections = application.connections;
    }

    /**
     * Creates a new application instance
     *
     * @param id                app id.
     * @param tenant            name of the tenant who owns the app.
     * @param authorizeURL      url used to authorize during oauth flow.
     * @param callbackURL       url used after a oauth flow.
     * @param subscription      type of subscription.
     * @param hasAllowedOrigins if the app allows other origins
     * @param connections       list of the connections enabled for the app (Social, DB, etc).
     */
    public Application(String id, String tenant, String authorizeURL, String callbackURL, String subscription, boolean hasAllowedOrigins, List<AuthData> connections) {
        this.id = id;
        this.tenant = tenant;
        this.authorizeURL = authorizeURL;
        this.callbackURL = callbackURL;
        this.subscription = subscription;
        this.hasAllowedOrigins = hasAllowedOrigins;
        this.connections = connections;
    }

    /**
     * Returns the id of the application.
     *
     * @return an ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the tenant who owns the app.
     *
     * @return name of the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * Returns url used to authorize during oauth flow.
     *
     * @return a url string
     */
    public String getAuthorizeURL() {
        return authorizeURL;
    }

    /**
     * Returns url used after a oauth flow.
     *
     * @return a url string
     */
    public String getCallbackURL() {
        return callbackURL;
    }

    /**
     * Returns the type of subscription
     *
     * @return type of subscription
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * Returns if the app allows other origins.
     *
     * @return hasAllowedOrigins flag
     */
    public boolean hasAllowedOrigins() {
        return hasAllowedOrigins;
    }

    /**
     * Returns all available auth connections for the app.
     *
     * @return the list of available connections
     */
    public List<AuthData> getConnections() {
        return new ArrayList<>(connections);
    }

}