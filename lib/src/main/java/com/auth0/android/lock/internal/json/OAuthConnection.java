package com.auth0.android.lock.internal.json;

import android.support.annotation.NonNull;

import com.auth0.android.lock.internal.AuthType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OAuthConnection extends Connection {

    /**
     * Creates a new OAuthConnection instance
     *
     * @param strategy the OAuth strategy to use.
     * @param values   Connection values
     */
    OAuthConnection(@NonNull String strategy, Map<String, Object> values) {
        super(strategy, values);
    }

    /**
     * Returns if this Connection can use Resource Owner to authenticate
     *
     * @return if the Active Flow (Resource Owner) is enabled.
     */
    public boolean isActiveFlowEnabled() {
        return "ad".equals(getStrategy()) || "adfs".equals(getStrategy()) || "waad".equals(getStrategy());
    }

    /**
     * If this connection is of type Enterprise it will return the configured Domains.
     *
     * @return a set with all domains configured
     */
    public Set<String> getDomainSet() {
        Set<String> domains = new HashSet<>();
        String domain = getValueForKey("domain");
        if (domain != null) {
            domains.add(domain.toLowerCase());
            List<String> aliases = getValueForKey("domain_aliases");
            if (aliases != null) {
                for (String alias : aliases) {
                    domains.add(alias.toLowerCase());
                }
            }
        }
        return domains;
    }

    @Override
    public int getType() {
        switch (getStrategy()) {
            case "ad":
            case "adfs":
            case "auth0-adldap":
            case "custom":
            case "google-apps":
            case "google-openid":
            case "ip":
            case "mscrm":
            case "office365":
            case "pingfederate":
            case "samlp":
            case "sharepoint":
            case "waad":
                return AuthType.ENTERPRISE;
            default:
                return AuthType.SOCIAL;
        }
    }
}
