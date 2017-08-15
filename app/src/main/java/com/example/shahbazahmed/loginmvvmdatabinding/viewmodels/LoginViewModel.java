package com.example.shahbazahmed.loginmvvmdatabinding.viewmodels;

import android.databinding.BaseObservable;
import android.util.Log;

import com.example.shahbazahmed.loginmvvmdatabinding.entities.User;
import com.example.shahbazahmed.loginmvvmdatabinding.repositories.UserRepository;
import com.example.shahbazahmed.loginmvvmdatabinding.validators.EmailValidator;
import com.example.shahbazahmed.loginmvvmdatabinding.validators.PasswordValidator;
import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * Created by shahbazahmed on 15/08/17.
 */

public class LoginViewModel extends BaseObservable {

    private String email, password;
    private boolean loginEnabled;
    private ViewListener mListener;
    private EmailValidator mEmailValidator;
    private PasswordValidator mPasswordValidator;

    private UserRepository mUserRepository;

    public LoginViewModel(UserRepository userRepository) {
        this.mUserRepository = userRepository;
        email = "";
        password = "";
        mEmailValidator = new EmailValidator("Invalid Email");
        mPasswordValidator = new PasswordValidator("Password should be between 6 to 15 characters");
    }

    public void setViewListener(ViewListener listener) {
        this.mListener = listener;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyChange();
        setLoginEnabled(isInputValid());
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyChange();
        setLoginEnabled(isInputValid());
    }

    public boolean isLoginEnabled() {
        return loginEnabled;
    }

    public void setLoginEnabled(boolean loginEnabled) {
        this.loginEnabled = loginEnabled;
        notifyChange();
    }

    private boolean isInputValid() {
        return mEmailValidator.isValid(email, email.length() == 0) &&
                mPasswordValidator.isValid(password, password.length() == 0);
    }

    public void onLoginClick() {
        if (isInputValid()) {
            setLoginEnabled(false);
            try {
                User user = mUserRepository.fetchByEmail(email);
                if (user != null && user.getEmail().equals(email)) {
                    // User exists in local DB, check for password
                    if (user.getPassword().equals(password)) {
                        // Login successful
                        mListener.onMessage("Login Success.");
                        mListener.onLoginSuccess();
                    } else {
                        // Wrong password
                        mListener.onMessage("Wrong password. Please retry.");
                    }
                } else {
                    // User not found
                    mListener.onMessage("Email not Registered .Please Register first.");
                }
            } catch (Exception e) {
                Log.d("LoginViewModel", "Error while saving: " + e.getMessage());
            } finally {
                setLoginEnabled(true);
            }
        }
    }


    public METValidator getEmailValidator() {
        return mEmailValidator;
    }

    public PasswordValidator getPasswordValidator() {
        return mPasswordValidator;
    }

    public interface ViewListener {

        void onLoginSuccess();

        void onMessage(String message);
    }
}
