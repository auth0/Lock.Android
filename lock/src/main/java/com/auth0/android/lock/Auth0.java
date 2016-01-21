package com.auth0.android.lock;

/**
 * Created by nikolaseu on 1/21/16.
 */
public class Auth0  implements Parcelable {

    private static final String AUTH0_US_CDN_URL = "https://cdn.auth0.com";
    private static final String DOT_AUTH0_DOT_COM = ".auth0.com";

    private String clientId;
    private String domainUrl;
    private String configurationUrl;

    public Auth0Account(String clientId, String domainUrl, String configurationUrl) {
        this.clientId = clientId;
        this.domainUrl = ensureUrlString(domainUrl);
        this.configurationUrl = resolveConfiguration(configurationUrl, this.domainUrl);
    }

    public Auth0Account(String clientId, String domain) {
        this(clientId, domain, null);
    }

    private String resolveConfiguration(String configurationDomain, String domainUrl) {
        String url = ensureUrlString(configurationDomain);
        if (configurationDomain == null && domainUrl != null) {
            final Uri domainUri = Uri.parse(domainUrl);
            final String host = domainUri.getHost();
            if (host.endsWith(DOT_AUTH0_DOT_COM)) {
                String[] parts = host.split("\\.");
                if (parts.length > 3) {
                    url = "https://cdn." + parts[parts.length-3] + DOT_AUTH0_DOT_COM;
                } else {
                    url = AUTH0_US_CDN_URL;
                }
            } else {
                url = domainUrl;
            }
        }
        return url;
    }

    private String ensureUrlString(String url) {
        String safeUrl = null;
        if (url != null) {
            safeUrl = url.startsWith("http") ? url : "https://" + url;
        }
        return safeUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public String getConfigurationUrl() {
        return configurationUrl;
    }

    // PARCELABLE
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(clientId);
        dest.writeString(domainUrl);
        dest.writeString(configurationUrl);
    }

    public static final Parcelable.Creator<Auth0Account> CREATOR
            = new Parcelable.Creator<Auth0Account>() {
        public Auth0Account createFromParcel(Parcel in) {
            return new Auth0Account(in);
        }

        public Auth0Account[] newArray(int size) {
            return new Auth0Account[size];
        }
    };

    private Auth0Account(Parcel in) {
        clientId = in.readString();
        domainUrl = in.readString();
        configurationUrl = in.readString();
    }
}
