package com.auth0.android.lock.views;

import android.view.ViewGroup;

import com.auth0.android.lock.utils.CustomField;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomFieldsFormViewTest {

    @Test
    public void shouldConvertCustomFieldsToMap() {
        ViewGroup container = mock(ViewGroup.class);

        //user_metadata attributes
        CustomField fieldMetadata = mock(CustomField.class);
        when(fieldMetadata.getKey()).thenReturn("company");
        when(fieldMetadata.findValue(container)).thenReturn("Auth0 INC");
        when(fieldMetadata.getStorage()).thenReturn(CustomField.Storage.USER_METADATA);


        //Root profile attributes
        CustomField fieldFamilyName = mock(CustomField.class);
        when(fieldFamilyName.getKey()).thenReturn("family_name");
        when(fieldFamilyName.findValue(container)).thenReturn("John");
        when(fieldFamilyName.getStorage()).thenReturn(CustomField.Storage.PROFILE_ROOT);

        CustomField fieldNickname = mock(CustomField.class);
        when(fieldNickname.getKey()).thenReturn("nickname");
        when(fieldNickname.findValue(container)).thenReturn("Johnnnny");
        when(fieldNickname.getStorage()).thenReturn(CustomField.Storage.PROFILE_ROOT);


        ArrayList<CustomField> list = new ArrayList<>();
        list.add(fieldMetadata);
        list.add(fieldFamilyName);
        list.add(fieldNickname);


        //Method under test
        Map<String, Object> map = CustomFieldsFormView.convertFieldsToMap(list, container);


        //Assertions
        assertThat(map, is(notNullValue()));

        assertThat(map, hasKey("user_metadata"));
        assertThat(map, hasEntry("family_name", (Object) "John"));
        assertThat(map, hasEntry("nickname", (Object) "Johnnnny"));

        Map<String, Object> resultMetadata = (Map<String, Object>) map.get("user_metadata");
        assertThat(resultMetadata, hasEntry("company", (Object) "Auth0 INC"));

    }
}