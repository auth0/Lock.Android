/*
 * CustomFieldTest.java
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

package com.auth0.android.lock.utils;

import android.os.Bundle;

import com.auth0.android.lock.R;
import com.auth0.android.lock.utils.CustomField.FieldType;
import com.auth0.android.lock.views.ValidatedInputView;
import com.auth0.android.lock.views.ValidatedInputView.DataType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.auth0.android.lock.utils.CustomField.Storage;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class CustomFieldTest {

    private static final int ICON = R.drawable.com_auth0_lock_ic_email;
    private static final int TYPE = FieldType.TYPE_EMAIL;
    private static final String KEY = "key";
    private static final int HINT = R.string.com_auth0_lock_hint_email;
    private static final String CUSTOM_FIELD_KEY = "custom_field";
    private static final int STORAGE = Storage.PROFILE_ROOT;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldThrowIfKeyIsEmpty() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The key cannot be empty!");
        new CustomField(ICON, TYPE, "", HINT, STORAGE);
    }

    @Test
    public void shouldThrowIfKeyIsUserMetadataAndStorageIsRoot() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Update the user_metadata root profile attributes by using Storage.USER_METADATA as storage location.");
        new CustomField(ICON, TYPE, "user_metadata", HINT, Storage.PROFILE_ROOT);
    }

    @Test
    public void shouldCreateWithDefaultValues() {
        CustomField field = new CustomField(ICON, TYPE, KEY, HINT);
        assertThat(field.getIcon(), is(ICON));
        assertThat(field.getType(), is(TYPE));
        assertThat(field.getKey(), is(KEY));
        assertThat(field.getHint(), is(HINT));
        //Default values
        assertThat(field.getStorage(), is(Storage.USER_METADATA));
    }

    @Test
    public void shouldCreate() {
        CustomField field = new CustomField(ICON, TYPE, KEY, HINT, STORAGE);
        assertThat(field.getIcon(), is(ICON));
        assertThat(field.getType(), is(TYPE));
        assertThat(field.getKey(), is(KEY));
        assertThat(field.getHint(), is(HINT));
        assertThat(field.getStorage(), is(STORAGE));
    }

    @Test
    public void shouldBeParcelable() {
        CustomField field = new CustomField(ICON, TYPE, KEY, HINT, STORAGE);
        Bundle bundle = new Bundle();
        bundle.putParcelable(CUSTOM_FIELD_KEY, field);

        CustomField parcelableCustomField = bundle.getParcelable(CUSTOM_FIELD_KEY);
        assertThat(parcelableCustomField, is(notNullValue()));
        assertThat(parcelableCustomField.getIcon(), is(ICON));
        assertThat(parcelableCustomField.getType(), is(TYPE));
        assertThat(parcelableCustomField.getKey(), is(KEY));
        assertThat(parcelableCustomField.getHint(), is(HINT));
        assertThat(parcelableCustomField.getStorage(), is(STORAGE));
    }

    @Test
    public void shouldConfigureTheField() {
        ValidatedInputView input = Mockito.mock(ValidatedInputView.class);

        CustomField field = new CustomField(ICON, TYPE, KEY, HINT);
        field.configureField(input);

        Mockito.verify(input).setIcon(ICON);
        Mockito.verify(input).setDataType(DataType.EMAIL);
        Mockito.verify(input).setHint(HINT);
        Mockito.verify(input).setTag(KEY);
    }
}