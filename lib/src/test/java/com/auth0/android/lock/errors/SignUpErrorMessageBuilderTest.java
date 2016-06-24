package com.auth0.android.lock.errors;

import com.auth0.android.lock.BuildConfig;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class SignUpErrorMessageBuilderTest {

    @Before
    public void setUp() throws Exception {

    }
}