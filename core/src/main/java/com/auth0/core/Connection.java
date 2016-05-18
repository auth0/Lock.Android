package com.auth0.core;

import android.os.Parcel;
import android.os.Parcelable;


import com.auth0.util.moshi.MapOfObjects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import static com.auth0.util.CheckHelper.checkArgument;

/**
 * Class with a Auth0 connection info
 */
public class Connection implements Parcelable {

    private transient String name;

    private Map<String, Object> values;

    @SuppressWarnings("unused") // Moshi uses this!
    private  Connection() {
        name=null;
    }
    /**
     * Creates a new connection instance
     * @param values Connection values
     */
    public Connection(Map<String, Object> values) {
        this.values = values;
        init();
    }

    private void init() {
        checkArgument(values != null && values.size() > 0, "Must have at least one value");
        final String name = (String) values.remove("name");
        checkArgument(name != null, "Must have a non-null name");
        this.name = name;
    }

    /**
     * Returns all the connection values
     * @return connection values
     */
    public Map<String, Object> getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns a value using its key
     * @param key a key
     * @param <T> type of value to return
     * @return a value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueForKey(String key) {
        return (T) this.values.get(key);
    }

    /**
     * Returns a boolean value using its key
     * @param key a key
     * @return the value of the flag
     */
    public boolean booleanForKey(String key) {
        Boolean value = getValueForKey(key);
        if (value == null) {
            return false;
        }
        return value;
    }

    /**
     * Get set of domain if the connection is Enterprise
     * @return a set with all domains configured
     */
    public Set<String> getDomainSet() {
        Set<String> domains = new HashSet<>();
        String domain = getValueForKey("domain");
        if (domain != null) {
            domains.add(domain.toLowerCase());
            List<String> aliases = getValueForKey("domain_aliases");
            if (aliases != null) {
                for (String alias: aliases) {
                    domains.add(alias.toLowerCase());
                }
            }
        }
        return domains;
    }

    @SuppressWarnings("unchecked")
    protected Connection(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            values = (Map<String, Object>) in.readSerializable();
        } else {
            values = new HashMap<>();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (values == null || values.isEmpty()) {
            dest.writeByte((byte) 0x00);
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeSerializable(new HashMap<>(values));
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Connection> CREATOR = new Parcelable.Creator<Connection>() {
        @Override
        public Connection createFromParcel(Parcel in) {
            return new Connection(in);
        }

        @Override
        public Connection[] newArray(int size) {
            return new Connection[size];
        }
    };

    public MapOfObjects toMap() {
        //NOT IMPLEMENTED
        return null;
    }
}
