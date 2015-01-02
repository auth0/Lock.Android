package com.auth0.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static com.auth0.util.CheckHelper.checkArgument;

/**
 * Created by hernan on 11/27/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application {

    private String id;
    private String tenant;
    private String authorizeURL;
    private String callbackURL;
    private String subscription;
    private boolean hasAllowedOrigins;
    private List<Strategy> strategies;
    private List<Strategy> socialStrategies;
    private List<Strategy> enterpriseStrategies;
    private Strategy databaseStrategy;

    @JsonCreator
    public Application(@JsonProperty(value = "id") String id,
                       @JsonProperty(value = "tenant") String tenant,
                       @JsonProperty(value = "authorize") String authorizeURL,
                       @JsonProperty(value = "callback") String callbackURL,
                       @JsonProperty(value = "subscription") String subscription,
                       @JsonProperty(value = "hasAllowedOrigins") boolean hasAllowedOrigins,
                       @JsonProperty(value = "strategies") List<Strategy> strategies) {
        checkArgument(id != null, "id must be non-null");
        checkArgument(tenant != null, "tenant must be non-null");
        checkArgument(authorizeURL != null, "authorize must be non-null");
        checkArgument(callbackURL != null, "callback must be non-null");
        checkArgument(strategies != null && strategies.size() > 0, "Must have at least 1 strategy");
        this.id = id;
        this.tenant = tenant;
        this.authorizeURL = authorizeURL;
        this.callbackURL = callbackURL;
        this.subscription = subscription;
        this.hasAllowedOrigins = hasAllowedOrigins;
        this.strategies = strategies;
        this.socialStrategies = new ArrayList<Strategy>();
        this.enterpriseStrategies = new ArrayList<Strategy>();
        for(Strategy strategy: strategies) {
            if (Strategies.Auth0.getName().equals(strategy.getName())) {
                this.databaseStrategy = strategy;
            } else {
                switch (strategy.getType()) {
                    case SOCIAL:
                        this.socialStrategies.add(strategy);
                        break;
                    case ENTERPRISE:
                        this.enterpriseStrategies.add(strategy);
                        break;
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getTenant() {
        return tenant;
    }

    public String getAuthorizeURL() {
        return authorizeURL;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public String getSubscription() {
        return subscription;
    }

    public boolean isHasAllowedOrigins() {
        return hasAllowedOrigins;
    }

    public List<Strategy> getStrategies() {
        return new ArrayList<Strategy>(strategies);
    }

    public Strategy getDatabaseStrategy() {
        return databaseStrategy;
    }

    public List<Strategy> getSocialStrategies() {
        return new ArrayList<Strategy>(socialStrategies);
    }

    public List<Strategy> getEnterpriseStrategies() {
        return new ArrayList<Strategy>(enterpriseStrategies);
    }

    public Strategy strategyForName(String name) {
        for (Strategy strategy: this.strategies) {
            if (strategy.getName().equals(name)) {
                return strategy;
            }
        }
        return null;
    }
}
