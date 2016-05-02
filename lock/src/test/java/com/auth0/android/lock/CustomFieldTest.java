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

package com.auth0.android.lock;

import android.os.Bundle;

import com.auth0.android.lock.CustomField.FieldType;
import com.auth0.android.lock.views.ValidatedInputView;
import com.auth0.android.lock.views.ValidatedInputView.DataType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = android.support.v7.appcompat.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class CustomFieldTest {

    private static final String HINT = "hint";
    private static final String KEY = "key";
    private static final int TYPE = FieldType.TYPE_EMAIL;
    private static final String CUSTOM_FIELD_KEY = "custom_field";

    @Test
    public void testParcelable() throws Exception {
        CustomField field = new CustomField(TYPE, KEY, HINT);
        Bundle bundle = new Bundle();
        bundle.putParcelable(CUSTOM_FIELD_KEY, field);

        CustomField parcelableCustomField = bundle.getParcelable(CUSTOM_FIELD_KEY);
        assertThat(parcelableCustomField, is(notNullValue()));
        assertThat(parcelableCustomField.getType(), is(equalTo(TYPE)));
        assertThat(parcelableCustomField.getKey(), is(equalTo(KEY)));
        assertThat(parcelableCustomField.getHint(), is(equalTo(HINT)));
    }

    @Test
    public void shouldConfigureTheField(){
        ValidatedInputView input = Mockito.mock(ValidatedInputView.class);

        CustomField field = new CustomField(TYPE, KEY, HINT);
        field.configureField(input);

        Mockito.verify(input).setDataType(DataType.EMAIL);
        Mockito.verify(input).setHint(HINT);
        Mockito.verify(input).setTag(KEY);
    }
}