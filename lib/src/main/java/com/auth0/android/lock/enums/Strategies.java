package com.auth0.android.lock.enums;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.enums.Strategies.ADFS;
import static com.auth0.android.lock.enums.Strategies.AOL;
import static com.auth0.android.lock.enums.Strategies.ActiveDirectory;
import static com.auth0.android.lock.enums.Strategies.Amazon;
import static com.auth0.android.lock.enums.Strategies.Auth0;
import static com.auth0.android.lock.enums.Strategies.Auth0LDAP;
import static com.auth0.android.lock.enums.Strategies.Baidu;
import static com.auth0.android.lock.enums.Strategies.BitBucket;
import static com.auth0.android.lock.enums.Strategies.Box;
import static com.auth0.android.lock.enums.Strategies.Custom;
import static com.auth0.android.lock.enums.Strategies.Dropbox;
import static com.auth0.android.lock.enums.Strategies.Dwolla;
import static com.auth0.android.lock.enums.Strategies.EBay;
import static com.auth0.android.lock.enums.Strategies.Email;
import static com.auth0.android.lock.enums.Strategies.Evernote;
import static com.auth0.android.lock.enums.Strategies.EvernoteSandbox;
import static com.auth0.android.lock.enums.Strategies.Exact;
import static com.auth0.android.lock.enums.Strategies.Facebook;
import static com.auth0.android.lock.enums.Strategies.Fitbit;
import static com.auth0.android.lock.enums.Strategies.Github;
import static com.auth0.android.lock.enums.Strategies.GoogleApps;
import static com.auth0.android.lock.enums.Strategies.GoogleOpenId;
import static com.auth0.android.lock.enums.Strategies.GooglePlus;
import static com.auth0.android.lock.enums.Strategies.IP;
import static com.auth0.android.lock.enums.Strategies.Instagram;
import static com.auth0.android.lock.enums.Strategies.Linkedin;
import static com.auth0.android.lock.enums.Strategies.MSCRM;
import static com.auth0.android.lock.enums.Strategies.Miicard;
import static com.auth0.android.lock.enums.Strategies.Office365;
import static com.auth0.android.lock.enums.Strategies.Paypal;
import static com.auth0.android.lock.enums.Strategies.PingFederate;
import static com.auth0.android.lock.enums.Strategies.PlanningCenter;
import static com.auth0.android.lock.enums.Strategies.RenRen;
import static com.auth0.android.lock.enums.Strategies.SAMLP;
import static com.auth0.android.lock.enums.Strategies.SMS;
import static com.auth0.android.lock.enums.Strategies.Salesforce;
import static com.auth0.android.lock.enums.Strategies.SalesforceSandbox;
import static com.auth0.android.lock.enums.Strategies.Sharepoint;
import static com.auth0.android.lock.enums.Strategies.Shopify;
import static com.auth0.android.lock.enums.Strategies.Soundcloud;
import static com.auth0.android.lock.enums.Strategies.TheCity;
import static com.auth0.android.lock.enums.Strategies.TheCitySandbox;
import static com.auth0.android.lock.enums.Strategies.ThirtySevenSignals;
import static com.auth0.android.lock.enums.Strategies.Twitter;
import static com.auth0.android.lock.enums.Strategies.UnknownSocial;
import static com.auth0.android.lock.enums.Strategies.VK;
import static com.auth0.android.lock.enums.Strategies.Waad;
import static com.auth0.android.lock.enums.Strategies.Weibo;
import static com.auth0.android.lock.enums.Strategies.WindowsLive;
import static com.auth0.android.lock.enums.Strategies.Wordpress;
import static com.auth0.android.lock.enums.Strategies.Yahoo;
import static com.auth0.android.lock.enums.Strategies.Yammer;
import static com.auth0.android.lock.enums.Strategies.Yandex;

@StringDef({Auth0, Email, SMS, Amazon, AOL, Baidu, BitBucket, Box, Dropbox, Dwolla, EBay, Evernote, EvernoteSandbox, Exact, Facebook,
        Fitbit, Github, GooglePlus, Instagram, Linkedin, Miicard, Paypal, PlanningCenter, RenRen, Salesforce, SalesforceSandbox, Shopify,
        Soundcloud, TheCity, TheCitySandbox, ThirtySevenSignals, Twitter, VK, Weibo, WindowsLive, Wordpress, Yahoo, Yammer, Yandex, UnknownSocial,
        ActiveDirectory, ADFS, Auth0LDAP, Custom, GoogleApps, GoogleOpenId, IP, MSCRM, Office365, PingFederate, SAMLP, Sharepoint, Waad})
@Retention(RetentionPolicy.SOURCE)
public @interface Strategies {
    //Database
    String Auth0 = "auth0";

    //Passwordless
    String Email = "email";
    String SMS = "sms";

    //Social
    String Amazon = "amazon";
    String AOL = "aol";
    String Baidu = "baidu";
    String BitBucket = "bitbucket";
    String Box = "box";
    String Dropbox = "dropbox";
    String Dwolla = "dwolla";
    String EBay = "ebay";
    String Evernote = "evernote";
    String EvernoteSandbox = "evernote-sandbox";
    String Exact = "exact";
    String Facebook = "facebook";
    String Fitbit = "fitbit";
    String Github = "github";
    String GooglePlus = "google-oauth2";
    String Instagram = "instagram";
    String Linkedin = "linkedin";
    String Miicard = "miicard";
    String Paypal = "paypal";
    String PlanningCenter = "planningcenter";
    String RenRen = "renren";
    String Salesforce = "salesforce";
    String SalesforceSandbox = "salesforce-sandbox";
    String Shopify = "shopify";
    String Soundcloud = "soundcloud";
    String TheCity = "thecity";
    String TheCitySandbox = "thecity-sandbox";
    String ThirtySevenSignals = "thirtysevensignals";
    String Twitter = "twitter";
    String VK = "vkontakte";
    String Weibo = "weibo";
    String WindowsLive = "windowslive";
    String Wordpress = "wordpress";
    String Yahoo = "yahoo";
    String Yammer = "yammer";
    String Yandex = "yandex";
    String UnknownSocial = "unknown-social";

    //Enterprise
    String ActiveDirectory = "ad";
    String ADFS = "adfs";
    String Auth0LDAP = "auth0-adldap";
    String Custom = "custom";
    String GoogleApps = "google-apps";
    String GoogleOpenId = "google-openid";
    String IP = "ip";
    String MSCRM = "mscrm";
    String Office365 = "office365";
    String PingFederate = "pingfederate";
    String SAMLP = "samlp";
    String Sharepoint = "sharepoint";
    String Waad = "waad";
}
