/*
 * DemoActivity.java
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
package com.auth0.android.lock.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.lock.*
import com.auth0.android.provider.WebAuthProvider.login
import com.auth0.android.provider.WebAuthProvider.logout
import com.auth0.android.result.Credentials
import com.google.android.material.snackbar.Snackbar

class DemoActivity : AppCompatActivity() {

    // Configured instances
    private var lock: Lock? = null
    private var passwordlessLock: PasswordlessLock? = null

    // Views
    private lateinit var rootLayout: View
    private lateinit var groupSubmitMode: RadioGroup
    private lateinit var checkboxClosable: CheckBox
    private lateinit var groupPasswordlessChannel: RadioGroup
    private lateinit var groupPasswordlessMode: RadioGroup
    private lateinit var checkboxConnectionsDB: CheckBox
    private lateinit var checkboxConnectionsEnterprise: CheckBox
    private lateinit var checkboxConnectionsSocial: CheckBox
    private lateinit var checkboxConnectionsPasswordless: CheckBox
    private lateinit var checkboxHideMainScreenTitle: CheckBox
    private lateinit var groupDefaultDB: RadioGroup
    private lateinit var groupUsernameStyle: RadioGroup
    private lateinit var checkboxLoginAfterSignUp: CheckBox
    private lateinit var checkboxScreenLogIn: CheckBox
    private lateinit var checkboxScreenSignUp: CheckBox
    private lateinit var checkboxScreenReset: CheckBox
    private lateinit var groupInitialScreen: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_activity)
        rootLayout = findViewById(R.id.scrollView)

        //Basic
        groupSubmitMode = findViewById(R.id.group_submitmode)
        checkboxClosable = findViewById(R.id.checkbox_closable)
        checkboxHideMainScreenTitle = findViewById(R.id.checkbox_hide_title)
        checkboxConnectionsDB = findViewById(R.id.checkbox_connections_db)
        checkboxConnectionsEnterprise = findViewById(R.id.checkbox_connections_enterprise)
        checkboxConnectionsSocial = findViewById(R.id.checkbox_connections_social)
        checkboxConnectionsPasswordless = findViewById(R.id.checkbox_connections_Passwordless)
        groupPasswordlessChannel = findViewById(R.id.group_passwordless_channel)
        groupPasswordlessMode = findViewById(R.id.group_passwordless_mode)

        //Advanced
        groupDefaultDB = findViewById(R.id.group_default_db)
        groupUsernameStyle = findViewById(R.id.group_username_style)
        checkboxLoginAfterSignUp = findViewById(R.id.checkbox_login_after_signup)
        checkboxScreenLogIn = findViewById(R.id.checkbox_enable_login)
        checkboxScreenSignUp = findViewById(R.id.checkbox_enable_signup)
        checkboxScreenReset = findViewById(R.id.checkbox_enable_reset)
        groupInitialScreen = findViewById(R.id.group_initial_screen)

        //Buttons
        val advancedContainer = findViewById<LinearLayout>(R.id.advanced_container)
        val checkboxShowAdvanced = findViewById<CheckBox>(R.id.checkbox_show_advanced)
        checkboxShowAdvanced.setOnCheckedChangeListener { _, b -> advancedContainer.visibility = if (b) View.VISIBLE else View.GONE }
        val btnShowLockClassic = findViewById<Button>(R.id.btn_show_lock_classic)
        btnShowLockClassic.setOnClickListener { showClassicLock() }
        val btnShowLockPasswordless = findViewById<Button>(R.id.btn_show_lock_passwordless)
        btnShowLockPasswordless.setOnClickListener { showPasswordlessLock() }
        val btnShowUniversalLogin = findViewById<Button>(R.id.btn_show_universal_login)
        btnShowUniversalLogin.setOnClickListener { showWebAuth() }
        val btnClearSession = findViewById<Button>(R.id.btn_clear_session)
        btnClearSession.setOnClickListener { clearSession() }
    }

    private fun showWebAuth() {
        login(account)
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                .start(this, loginCallback)
    }

    private fun clearSession() {
        logout(account)
                .withScheme("demo")
                .start(this, logoutCallback)
    }

    private fun showClassicLock() {
        val builder = Lock.newBuilder(account, callback)
                .withScheme("demo")
                .closable(checkboxClosable.isChecked)
                .useLabeledSubmitButton(groupSubmitMode.checkedRadioButtonId == R.id.radio_use_label)
                .loginAfterSignUp(checkboxLoginAfterSignUp.isChecked)
                .allowLogIn(checkboxScreenLogIn.isChecked)
                .allowSignUp(checkboxScreenSignUp.isChecked)
                .allowForgotPassword(checkboxScreenReset.isChecked)
                .allowedConnections(generateConnections())
                .hideMainScreenTitle(checkboxHideMainScreenTitle.isChecked)

        when (groupUsernameStyle.checkedRadioButtonId) {
            R.id.radio_username_style_email -> {
                builder.withUsernameStyle(UsernameStyle.EMAIL)
            }
            R.id.radio_username_style_username -> {
                builder.withUsernameStyle(UsernameStyle.USERNAME)
            }
        }
        when (groupInitialScreen.checkedRadioButtonId) {
            R.id.radio_initial_reset -> {
                builder.initialScreen(InitialScreen.FORGOT_PASSWORD)
            }
            R.id.radio_initial_signup -> {
                builder.initialScreen(InitialScreen.SIGN_UP)
            }
            else -> {
                builder.initialScreen(InitialScreen.LOG_IN)
            }
        }
        if (checkboxConnectionsDB.isChecked) {
            when (groupDefaultDB.checkedRadioButtonId) {
                R.id.radio_default_db_policy -> {
                    builder.setDefaultDatabaseConnection("with-strength")
                }
                R.id.radio_default_db_mfa -> {
                    builder.setDefaultDatabaseConnection("mfa-connection")
                }
                else -> {
                    builder.setDefaultDatabaseConnection("Username-Password-Authentication")
                }
            }
        }

        // For demo purposes because options change dynamically, we release the resources of Lock here.
        // In a real app, you will have a single instance and release its resources in Activity#OnDestroy.
        lock?.onDestroy(this)
        // Create a new instance with the updated configuration
        lock = builder.build(this)
        startActivity(lock!!.newIntent(this))
    }

    private fun showPasswordlessLock() {
        val builder = PasswordlessLock.newBuilder(account, callback)
                .withScheme("demo")
                .closable(checkboxClosable.isChecked)
                .allowedConnections(generateConnections())
                .hideMainScreenTitle(checkboxHideMainScreenTitle.isChecked)

        if (groupPasswordlessMode.checkedRadioButtonId == R.id.radio_use_link) {
            builder.useLink()
        } else {
            builder.useCode()
        }

        // For demo purposes because options change dynamically, we release the resources of Lock here.
        // In a real app, you will have a single instance and release its resources in Activity#OnDestroy.
        passwordlessLock?.onDestroy(this)
        // Create a new instance with the updated configuration
        passwordlessLock = builder.build(this)
        startActivity(passwordlessLock!!.newIntent(this))
    }

    private val account: Auth0 by lazy {
        Auth0(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain))
    }

    private fun generateConnections(): List<String> {
        val connections: MutableList<String> = ArrayList()
        if (checkboxConnectionsDB.isChecked) {
            connections.add("Username-Password-Authentication")
            connections.add("mfa-connection")
            connections.add("with-strength")
        }
        if (checkboxConnectionsEnterprise.isChecked) {
            connections.add("ad")
            connections.add("another")
            connections.add("fake-saml")
            connections.add("contoso-ad")
        }
        if (checkboxConnectionsSocial.isChecked) {
            connections.add("google-oauth2")
            connections.add("twitter")
            connections.add("facebook")
            connections.add("paypal-sandbox")
        }
        if (checkboxConnectionsPasswordless.isChecked) {
            connections.add(if (groupPasswordlessChannel.checkedRadioButtonId == R.id.radio_use_sms) "sms" else "email")
        }
        if (connections.isEmpty()) {
            connections.add("no-connection")
        }
        return connections
    }

    public override fun onDestroy() {
        super.onDestroy()
        lock?.onDestroy(this)
        passwordlessLock?.onDestroy(this)
    }

    internal fun showResult(message: String) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show()
    }

    private val callback: LockCallback = object : AuthenticationCallback() {
        override fun onAuthentication(credentials: Credentials) {
            showResult("OK > " + credentials.accessToken)
        }

        override fun onError(error: AuthenticationException) {
            if (error.isCanceled) {
                showResult("User pressed back.")
            } else {
                showResult(error.getDescription())
            }
        }
    }
    private val loginCallback: Callback<Credentials, AuthenticationException> = object : Callback<Credentials, AuthenticationException> {
        override fun onFailure(error: AuthenticationException) {
            showResult("Failed > " + error.getDescription())
        }

        override fun onSuccess(result: Credentials) {
            showResult("OK > " + result.accessToken)
        }
    }
    private val logoutCallback: Callback<Void?, AuthenticationException> = object : Callback<Void?, AuthenticationException> {
        override fun onFailure(error: AuthenticationException) {
            showResult("Log out cancelled")
        }

        override fun onSuccess(result: Void?) {
            showResult("Logged out!")
        }
    }
}