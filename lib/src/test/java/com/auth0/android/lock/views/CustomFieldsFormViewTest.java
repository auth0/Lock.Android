package com.auth0.android.lock.views;

import android.view.ViewGroup;

import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.utils.CustomField;
import com.auth0.android.lock.utils.HiddenField;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CustomFieldsFormViewTest {

    @Test
    public void shouldConvertCustomFieldsToMap() {
        DatabaseSignUpEvent event = mock(DatabaseSignUpEvent.class);
        ViewGroup container = mock(ViewGroup.class);

        //user_metadata attributes
        CustomField fieldMetadata = mock(CustomField.class);
        when(fieldMetadata.getKey()).thenReturn("company");
        when(fieldMetadata.findValue(container)).thenReturn("Auth0 INC");
        when(fieldMetadata.getStorage()).thenReturn(CustomField.Storage.USER_METADATA);

        HiddenField fieldHidden1 = mock(HiddenField.class);
        when(fieldHidden1.getKey()).thenReturn("referral_id");
        when(fieldHidden1.getValue()).thenReturn("123456789");
        when(fieldHidden1.getStorage()).thenReturn(CustomField.Storage.USER_METADATA);


        //Root profile attributes
        CustomField fieldFamilyName = mock(CustomField.class);
        when(fieldFamilyName.getKey()).thenReturn("family_name");
        when(fieldFamilyName.findValue(container)).thenReturn("John");
        when(fieldFamilyName.getStorage()).thenReturn(CustomField.Storage.PROFILE_ROOT);

        CustomField fieldNickname = mock(CustomField.class);
        when(fieldNickname.getKey()).thenReturn("nickname");
        when(fieldNickname.findValue(container)).thenReturn("Johnnnny");
        when(fieldNickname.getStorage()).thenReturn(CustomField.Storage.PROFILE_ROOT);

        HiddenField fieldHidden2 = mock(HiddenField.class);
        when(fieldHidden2.getKey()).thenReturn("partnership");
        when(fieldHidden2.getValue()).thenReturn("bookar");
        when(fieldHidden2.getStorage()).thenReturn(CustomField.Storage.PROFILE_ROOT);


        ArrayList<CustomField> visibleList = new ArrayList<>();
        visibleList.add(fieldMetadata);
        visibleList.add(fieldFamilyName);
        visibleList.add(fieldNickname);

        ArrayList<HiddenField> hiddenList = new ArrayList<>();
        hiddenList.add(fieldHidden1);
        hiddenList.add(fieldHidden2);

        //Method under test
        CustomFieldsFormView.setEventRootProfileAttributes(event, visibleList, hiddenList, container);


        //Assertions
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

        verify(event).setExtraFields(mapCaptor.capture());
        Map<String, String> metadataFields = mapCaptor.getValue();
        assertThat(metadataFields, is(notNullValue()));
        assertThat(metadataFields, hasEntry("company", "Auth0 INC"));
        assertThat(metadataFields, hasEntry("referral_id", "123456789"));

        verify(event).setRootAttributes(mapCaptor.capture());
        Map<String, Object> rootAttributes = mapCaptor.getValue();
        assertThat(rootAttributes, is(notNullValue()));
        assertThat(rootAttributes, hasEntry("family_name", (Object) "John"));
        assertThat(rootAttributes, hasEntry("nickname", (Object) "Johnnnny"));
        assertThat(rootAttributes, hasEntry("partnership", (Object) "bookar"));
    }
}