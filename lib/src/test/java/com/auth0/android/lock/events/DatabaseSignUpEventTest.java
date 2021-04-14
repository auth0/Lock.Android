/*
 * DatabaseSignUpEventTest.java
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

package com.auth0.android.lock.events;

import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.request.Request;
import com.auth0.android.request.SignUpRequest;
import com.auth0.android.result.DatabaseUser;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class DatabaseSignUpEventTest {

    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String CONNECTION = "connection";

    @Test
    public void shouldSetAllValues() {
        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, USERNAME);

        assertThat(event.getEmail(), is(equalTo(EMAIL)));
        assertThat(event.getPassword(), is(equalTo(PASSWORD)));
        assertThat(event.getUsername(), is(equalTo(USERNAME)));
    }

    @Test
    public void shouldSetNullUsername() {
        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, null);

        assertThat(event.getEmail(), is(equalTo(EMAIL)));
        assertThat(event.getPassword(), is(equalTo(PASSWORD)));
        assertThat(event.getUsername(), is(nullValue()));
    }

    @Test
    public void shouldGetSignUpRequestWithUsername() {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, USERNAME);
        event.getSignUpRequest(client, CONNECTION);
        Mockito.verify(client).signUp(EMAIL, PASSWORD, USERNAME, CONNECTION);
    }

    @Test
    public void shouldGetSignUpRequestWithoutUsername() {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, null);
        event.getSignUpRequest(client, CONNECTION);
        Mockito.verify(client).signUp(EMAIL, PASSWORD, CONNECTION);
    }

    @Test
    public void shouldGetSignUpRequestWithUserMetadata() {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        final Map<String, String> metadata = createMetadata();
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

        SignUpRequest requestMock = mock(SignUpRequest.class);
        Mockito.when(client.signUp(EMAIL, PASSWORD, CONNECTION)).thenReturn(requestMock);
        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, null);
        event.setExtraFields(metadata);
        event.getSignUpRequest(client, CONNECTION);
        Mockito.verify(requestMock).addSignUpParameters(mapCaptor.capture());
        Map<String, String> metadataMap = (Map<String, String>) mapCaptor.getValue().get("user_metadata");
        assertValidMetadata(metadataMap);

        SignUpRequest usernameRequestMock = mock(SignUpRequest.class);
        Mockito.when(client.signUp(EMAIL, PASSWORD, USERNAME, CONNECTION)).thenReturn(usernameRequestMock);
        DatabaseSignUpEvent usernameEvent = new DatabaseSignUpEvent(EMAIL, PASSWORD, USERNAME);
        usernameEvent.setExtraFields(metadata);
        usernameEvent.getSignUpRequest(client, CONNECTION);
        Mockito.verify(usernameRequestMock).addSignUpParameters(mapCaptor.capture());
        metadataMap = (Map<String, String>) mapCaptor.getValue().get("user_metadata");
        assertValidMetadata(metadataMap);
    }

    @Test
    public void shouldGetSignUpRequestWithRootProfileAttributes() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        final Map<String, String> attrs = createRootProfileAttributes();
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

        SignUpRequest requestMock = mock(SignUpRequest.class);
        Mockito.when(client.signUp(EMAIL, PASSWORD, CONNECTION)).thenReturn(requestMock);
        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, null);
        event.setRootAttributes(attrs);
        event.getSignUpRequest(client, CONNECTION);
        Mockito.verify(requestMock).addSignUpParameters(mapCaptor.capture());
        assertValidRootProfileAttributes(mapCaptor.getValue());

        SignUpRequest usernameRequestMock = mock(SignUpRequest.class);
        Mockito.when(client.signUp(EMAIL, PASSWORD, USERNAME, CONNECTION)).thenReturn(usernameRequestMock);
        DatabaseSignUpEvent usernameEvent = new DatabaseSignUpEvent(EMAIL, PASSWORD, USERNAME);
        usernameEvent.setRootAttributes(attrs);
        usernameEvent.getSignUpRequest(client, CONNECTION);
        Mockito.verify(usernameRequestMock).addSignUpParameters(mapCaptor.capture());
        assertValidRootProfileAttributes(mapCaptor.getValue());
    }

    @Test
    public void shouldGetCreateUserRequestWithoutRootProfileAttributes() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, USERNAME);

        SignUpRequest signUpRequestMock = mock(SignUpRequest.class);
        Mockito.when(client.signUp(EMAIL, PASSWORD, USERNAME, CONNECTION)).thenReturn(signUpRequestMock);
        event.getSignUpRequest(client, CONNECTION);
        Mockito.verify(signUpRequestMock, never()).addSignUpParameters(anyMap());

        SignUpRequest createRequestMock = mock(SignUpRequest.class);
        Mockito.when(client.signUp(EMAIL, PASSWORD, USERNAME, CONNECTION)).thenReturn(createRequestMock);
        event.getCreateUserRequest(client, CONNECTION);
        Mockito.verify(createRequestMock, never()).addSignUpParameters(anyMap());
    }

    @Test
    public void shouldGetCreateUserRequestWithUsername() {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, USERNAME);
        event.getCreateUserRequest(client, CONNECTION);
        Mockito.verify(client).createUser(EMAIL, PASSWORD, USERNAME, CONNECTION);
    }

    @Test
    public void shouldGetCreateUserRequestWithoutUsername() {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, null);
        event.getCreateUserRequest(client, CONNECTION);
        Mockito.verify(client).createUser(EMAIL, PASSWORD, CONNECTION);
    }

    @Test
    public void shouldGetCreateUserRequestWithUserMetadata() {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        final Map<String, String> metadata = createMetadata();

        Request<DatabaseUser, AuthenticationException> requestMock = mock(Request.class);
        Mockito.when(client.createUser(EMAIL, PASSWORD, CONNECTION)).thenReturn(requestMock);
        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, null);
        event.setExtraFields(metadata);
        event.getCreateUserRequest(client, CONNECTION);
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(requestMock).addParameters(mapCaptor.capture());
        Map<String, String> metadataMap = (Map<String, String>) mapCaptor.getValue().get("user_metadata");
        assertValidMetadata(metadataMap);

        Request<DatabaseUser, AuthenticationException> usernameRequestMock = mock(Request.class);
        Mockito.when(client.createUser(EMAIL, PASSWORD, USERNAME, CONNECTION)).thenReturn(usernameRequestMock);
        DatabaseSignUpEvent eventUsername = new DatabaseSignUpEvent(EMAIL, PASSWORD, USERNAME);
        eventUsername.setExtraFields(metadata);
        eventUsername.getCreateUserRequest(client, CONNECTION);
        Mockito.verify(usernameRequestMock).addParameters(mapCaptor.capture());
        metadataMap = (Map<String, String>) mapCaptor.getValue().get("user_metadata");
        assertValidMetadata(metadataMap);
    }

    @Test
    public void shouldGetCreateUserRequestWithRootProfileAttributes() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        final Map<String, String> attrs = createRootProfileAttributes();

        Request<DatabaseUser, AuthenticationException> requestMock = mock(Request.class);
        Mockito.when(client.createUser(EMAIL, PASSWORD, CONNECTION)).thenReturn(requestMock);
        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, null);
        event.setRootAttributes(attrs);
        event.getCreateUserRequest(client, CONNECTION);
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(requestMock).addParameters(mapCaptor.capture());
        assertValidRootProfileAttributes(mapCaptor.getValue());

        Request<DatabaseUser, AuthenticationException> usernameRequestMock = mock(Request.class);
        Mockito.when(client.createUser(EMAIL, PASSWORD, USERNAME, CONNECTION)).thenReturn(usernameRequestMock);
        DatabaseSignUpEvent eventUsername = new DatabaseSignUpEvent(EMAIL, PASSWORD, USERNAME);
        eventUsername.setRootAttributes(attrs);
        eventUsername.getCreateUserRequest(client, CONNECTION);
        Mockito.verify(usernameRequestMock).addParameters(mapCaptor.capture());
        assertValidRootProfileAttributes(mapCaptor.getValue());
    }

    private Map<String, String> createRootProfileAttributes() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "Nicholas");
        map.put("nickname", "Nick");
        map.put("lastname", "Fury");
        return map;
    }

    private Map<String, String> createMetadata() {
        Map<String, String> map = new HashMap<>();
        map.put("country", "Argentina");
        map.put("preferred_color", "blue");
        return map;
    }

    private void assertValidRootProfileAttributes(Map<String, Object> map) {
        assertThat(map, is(notNullValue()));
        assertThat(map, IsMapContaining.hasEntry("name", "Nicholas"));
        assertThat(map, IsMapContaining.hasEntry("nickname", "Nick"));
        assertThat(map, IsMapContaining.hasEntry("lastname", "Fury"));
        assertThat(map, IsMapContaining.hasEntry("user_metadata", createMetadata()));
    }

    private void assertValidMetadata(Map<String, String> map) {
        assertThat(map, is(notNullValue()));
        assertThat(map, IsMapContaining.hasEntry("country", "Argentina"));
        assertThat(map, IsMapContaining.hasEntry("preferred_color", "blue"));
    }

}