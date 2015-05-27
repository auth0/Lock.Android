package com.auth0.core;

import com.auth0.BaseTestCase;
import com.auth0.android.BuildConfig;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class UserProfileTest extends BaseTestCase {

    public static final String USER_ID = "IOU a user id";
    public static final String NAME = "Somebody Someone";
    public static final String NICKNAME = "somebody";
    public static final String EMAIL = "somebody@somwhere.com";
    public static final String CREATED_AT = "2014-07-06T18:33:49.005Z";
    public static final String PICTURE_URL = "http://somewhere.com/pic.jpg";
    public static final Object EXTRA_VALUE = "extra_value";
    public static final long CREATED_AT_TIMESTAMP = 1404671629005l;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldRaiseExceptionWithNullValuesWhenCreatingInstance() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("must supply non-null values"));
        new UserProfile((HashMap<String, Object>)null);
    }

    @Test
    public void shouldRaiseExceptionWithNoUserIdWhenCreatingInstance() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("profile must have a user id"));
        new UserProfile(new HashMap<String, Object>());
    }

    @Test
    public void shouldInstantiateProfileWithId() throws Exception {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("user_id", USER_ID);
        UserProfile profile = new UserProfile(values);
        assertValidProfile(profile);
    }

    @Test
    public void shouldInstantiateWithBasicProfileInfo() throws Exception {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("user_id", USER_ID);
        values.put("name", NAME);
        values.put("nickname", NICKNAME);
        values.put("email", EMAIL);
        values.put("picture", PICTURE_URL);
        values.put("created_at", CREATED_AT);
        UserProfile profile = new UserProfile(values);
        assertValidProfile(profile);
        assertThat(profile.getName(), equalTo(NAME));
        assertThat(profile.getNickname(), equalTo(NICKNAME));
        assertThat(profile.getEmail(), equalTo(EMAIL));
        assertThat(profile.getPictureURL(), equalTo(PICTURE_URL));
        assertThat(profile.getCreatedAt().getTime(), equalTo(CREATED_AT_TIMESTAMP));
        assertThat(profile.getExtraInfo().size(), equalTo(0));
    }

    @Test
    public void shouldHandleExtraProfileInfo() throws Exception {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("user_id", USER_ID);
        values.put("extra_key", EXTRA_VALUE);
        UserProfile profile = new UserProfile(values);
        assertValidProfile(profile);
        assertThat(profile.getExtraInfo(), hasEntry("extra_key", EXTRA_VALUE));
    }

    @Test
    public void shouldHandleIdentities() throws Exception {
        Map<String, Object> values = new HashMap<String, Object>();
        UserIdentity identity = mock(UserIdentity.class);
        values.put("user_id", USER_ID);
        values.put("identities", Arrays.asList(identity));
        UserProfile profile = new UserProfile(values);
        assertValidProfile(profile);
        assertThat(profile.getIdentities(), hasItem(identity));
    }

    private void assertValidProfile(UserProfile profile) {
        assertThat(profile, is(notNullValue()));
        assertThat(profile.getId(), equalTo(USER_ID));
    }
}
