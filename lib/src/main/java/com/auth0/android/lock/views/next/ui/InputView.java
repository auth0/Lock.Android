package com.auth0.android.lock.views.next.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.auth0.android.lock.R;
import com.auth0.android.lock.views.next.configuration.internal.IdentityStyle;
import com.auth0.android.lock.views.next.ui.validators.NonEmptyValidator;
import com.auth0.android.lock.views.next.ui.validators.RegexValidator;
import com.auth0.android.lock.views.next.ui.validators.Validator;

/**
 * Created by lbalmaceda on 24/11/2017.
 */

public class InputView extends LinearLayout {

    private AppCompatEditText input;
    private Validator textValidator;
    private AppCompatImageView icon;
    private AppCompatImageView button;


    public InputView(Context context) {
        super(context);
        init(null);
    }

    public InputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public InputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        //get view refs
        inflate(getContext(), R.layout.a0_input_view, this);
        input = (AppCompatEditText) findViewById(R.id.a0_input);
        icon = (AppCompatImageView) findViewById(R.id.a0_icon);
        button = (AppCompatImageView) findViewById(R.id.a0_show_password_button);
        button.setVisibility(View.GONE);

        int px48 = getResources().getDimensionPixelSize(R.dimen.a0_size_48);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px48));
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_view));

        if (isInEditMode()) {
            return;
        }

        //setup validation
        textValidator = new NonEmptyValidator();
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshViewStatus(isValid(true));
            }
        });
        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                refreshViewStatus(isValid(true));
            }
        });
    }

    private void refreshViewStatus(boolean textIsValid) {
        Drawable iconDrawable = icon.getDrawable();
        if (iconDrawable == null) {
            return;
        }
        if (textIsValid) {
            getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(), isFocused() ? R.color.a0ActiveFocus : R.color.a0Grey), PorterDuff.Mode.SCREEN);
            iconDrawable.mutate().setColorFilter(ContextCompat.getColor(getContext(), isFocused() ? R.color.a0ActiveFocus : R.color.a0Dark), PorterDuff.Mode.SRC_IN);
        } else {
            getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(), R.color.a0ErrorAlert), PorterDuff.Mode.SCREEN);
            iconDrawable.mutate().setColorFilter(ContextCompat.getColor(getContext(), R.color.a0ErrorAlert), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int px48 = getResources().getDimensionPixelSize(R.dimen.a0_size_48);
        setMeasuredDimension(getMeasuredWidth(), px48);
    }

    /////////////////////
    /////////////////////
    ////Public Methods///
    /////////////////////
    /////////////////////

    @CallSuper
    public boolean isValid(boolean allowEmptyText) {
        String text = input.getText().toString();
        boolean isValid = (allowEmptyText && text.isEmpty()) || textValidator.isValid(text);
        refreshViewStatus(isValid);
        return isValid;
    }

    @Override
    public boolean isFocused() {
        return input.isFocused();
    }

    public void setTextValidator(@NonNull Validator textValidator) {
        this.textValidator = textValidator;
    }

    public void setHint(@Nullable String hint) {
        input.setHint(hint);
    }

    public void setHint(@StringRes int resId) {
        input.setHint(resId);
    }

    public void setText(@Nullable String text) {
        input.setText("");
        if (text != null) {
            input.append(text);
        }
    }

    public String getText() {
        return input.getText().toString();
    }

    /**
     * Changes the input type of this view
     *
     * @param inputType one of the values defined by {@link InputType}.
     */
    public void setInputType(int inputType) {
        input.setInputType(inputType);
        boolean isMaskedPassword = (inputType & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD
                && (inputType & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        input.setTransformationMethod(isMaskedPassword ? PasswordTransformationMethod.getInstance() : null);
        //TODO: Depending on the typeface being applied in the theme, this value below might change
        input.setTypeface(Typeface.DEFAULT);
        input.setSelection(input.getText().length());
    }

    public void setIcon(@DrawableRes int resId) {
        this.icon.setImageResource(resId);
        refreshViewStatus(true);
    }

    public boolean isEmpty() {
        return input.getText().toString().isEmpty();
    }

    public void setButton(@DrawableRes int iconResId, @NonNull OnClickListener listener) {
        button.setVisibility(View.VISIBLE);
        button.setImageResource(iconResId);
        button.setOnClickListener(listener);
    }

    //Common InputViews setups

    @SuppressWarnings("SameParameterValue")
    public static InputView forPassword(Context context, boolean allowShowPassword) {
        final InputView inputView = new InputView(context);
        inputView.setHint(R.string.a0_hint_password);
        inputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputView.setIcon(R.drawable.ic_password);
        inputView.setTextValidator(new NonEmptyValidator());
        if (allowShowPassword) {
            inputView.setButton(R.drawable.ic_view_password, new OnClickListener() {
                boolean isPasswordVisible = false;

                @Override
                public void onClick(View button) {
                    isPasswordVisible = !isPasswordVisible;
                    if (isPasswordVisible) {
                        inputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        inputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                }
            });
        }
        return inputView;
    }

    public static InputView forIdentity(Context context, @IdentityStyle int style) {
        InputView inputView = new InputView(context);
        switch (style) {
            default:
            case IdentityStyle.USERNAME_AND_EMAIL:
                inputView.setHint(R.string.a0_hint_username_or_email);
                inputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                inputView.setIcon(R.drawable.ic_email);
                final String combinedRegex = String.format("(%s)|(%s)", RegexValidator.EMAIL_REGEX, RegexValidator.USERNAME_REGEX);
                inputView.setTextValidator(new RegexValidator(combinedRegex));
                break;
            case IdentityStyle.EMAIL:
                inputView.setHint(R.string.a0_hint_email);
                inputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                inputView.setIcon(R.drawable.ic_email);
                inputView.setTextValidator(new RegexValidator(RegexValidator.EMAIL_REGEX));
                break;
            case IdentityStyle.USERNAME:
                inputView.setHint(R.string.a0_hint_email);
                inputView.setInputType(InputType.TYPE_CLASS_TEXT);
                //FIXME: Add 'username' icon
                inputView.setIcon(R.drawable.ic_email);
                inputView.setTextValidator(new RegexValidator(RegexValidator.USERNAME_REGEX));
                break;
        }
        return inputView;
    }
}
