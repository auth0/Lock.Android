package com.auth0.lock.validation;

import com.auth0.lock.R;
import com.auth0.lock.util.UsernameLengthParser;

public class UsernameOrEmailValidator extends BaseFragmentValidator {

    final UsernameValidator usernameValidator;
    final EmailValidator emailValidator;

    public UsernameOrEmailValidator(UsernameLengthParser lengthParser) {
        super(R.id.com_auth0_db_login_username_field, R.string.com_auth0_invalid_credentials_title, R.string.com_auth0_invalid_username_or_email_message);
        usernameValidator = new UsernameValidator(R.id.com_auth0_db_login_username_field, R.string.com_auth0_invalid_credentials_title, R.string.com_auth0_invalid_username_message, lengthParser);
        emailValidator = new EmailValidator(R.id.com_auth0_db_login_username_field, R.string.com_auth0_invalid_credentials_title, R.string.com_auth0_invalid_email_message);
    }

    @Override
    protected boolean doValidate(String value) {
        return emailValidator.doValidate(value) || usernameValidator.doValidate(value);
    }
}
