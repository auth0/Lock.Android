/*
 * Strategies.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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

package com.auth0.android.lock.utils;

/**
 * An enum with all strategies available in Auth0
 */
public enum Strategies {
    Auth0("auth0", Type.DATABASE),

    Email("email", Type.PASSWORDLESS),
    SMS("sms", Type.PASSWORDLESS),

    Amazon("amazon", Type.SOCIAL),
    AOL("aol", Type.SOCIAL),
    Baidu("baidu", Type.SOCIAL),
    Box("box", Type.SOCIAL),
    Dwolla("dwolla", Type.SOCIAL),
    EBay("ebay", Type.SOCIAL),
    Evernote("evernote", Type.SOCIAL),
    EvernoteSandbox("evernote-sandbox", Type.SOCIAL),
    Exact("exact", Type.SOCIAL),
    Facebook("facebook", Type.SOCIAL),
    Fitbit("fitbit", Type.SOCIAL),
    Github("github", Type.SOCIAL),
    GooglePlus("google-oauth2", Type.SOCIAL),
    Instagram("instagram", Type.SOCIAL),
    Linkedin("linkedin", Type.SOCIAL),
    Miicard("miicard", Type.SOCIAL),
    Paypal("paypal", Type.SOCIAL),
    PlanningCenter("planningcenter", Type.SOCIAL),
    RenRen("renren", Type.SOCIAL),
    Salesforce("salesforce", Type.SOCIAL),
    SalesforceSandbox("salesforce-sandbox", Type.SOCIAL),
    Shopify("shopify", Type.SOCIAL),
    Soundcloud("soundcloud", Type.SOCIAL),
    TheCity("thecity", Type.SOCIAL),
    TheCitySandbox("thecity-sandbox", Type.SOCIAL),
    ThirtySevenSignals("thirtysevensignals", Type.SOCIAL),
    Twitter("twitter", Type.SOCIAL),
    VK("vkontakte", Type.SOCIAL),
    Weibo("weibo", Type.SOCIAL),
    WindowsLive("windowslive", Type.SOCIAL),
    Wordpress("wordpress", Type.SOCIAL),
    Yahoo("yahoo", Type.SOCIAL),
    Yammer("yammer", Type.SOCIAL),
    Yandex("yandex", Type.SOCIAL),

    ActiveDirectory("ad", Type.ENTERPRISE),
    ADFS("adfs", Type.ENTERPRISE),
    Auth0LDAP("auth0-adldap", Type.ENTERPRISE),
    Custom("custom", Type.ENTERPRISE),
    GoogleApps("google-apps", Type.ENTERPRISE),
    GoogleOpenId("google-openid", Type.ENTERPRISE),
    IP("ip", Type.ENTERPRISE),
    MSCRM("mscrm", Type.ENTERPRISE),
    Office365("office365", Type.ENTERPRISE),
    PingFederate("pingfederate", Type.ENTERPRISE),
    SAMLP("samlp", Type.ENTERPRISE),
    Sharepoint("sharepoint", Type.ENTERPRISE),
    Waad("waad", Type.ENTERPRISE),

    UnknownSocial("unknown-social", Type.SOCIAL);

    private String name;
    private Type type;

    Strategies(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public static Strategies fromName(String name) {
        Strategies strategy = null;
        for (Strategies str : values()) {
            if (str.getName().equals(name)) {
                strategy = str;
                break;
            }
        }

        // if strategy not found, assume it's a new social type
        if (strategy == null)
            strategy = UnknownSocial;

        return strategy;
    }

    public enum Type {
        DATABASE,
        SOCIAL,
        ENTERPRISE,
        PASSWORDLESS
    }
}