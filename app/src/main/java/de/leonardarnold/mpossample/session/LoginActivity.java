package de.leonardarnold.mpossample.session;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.leonardarnold.mpossample.R;
import de.leonardarnold.mpossample.injection.BaseActivity;
import de.leonardarnold.mpossample.session.listener.LoginListener;
import de.payleven.payment.PaylevenError;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int VIEW_MODE_LOGGING_IN = 0; // show progress bar; disable login button
    private static final int VIEW_MODE_ERROR = 1;      // disable progress bar; enable login button

    @Inject
        LoginController loginController;

    @InjectView(R.id.login_button)
        Button loginButton;
    @InjectView(R.id.login_load)
        ProgressBar progressBar;

    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    /**
     * Login button "OnClickListener" (see activity_login.xml)
     */
    public void onLoginClicked(View view) {
        final EditText userNameField = (EditText) findViewById(R.id.username_field);
        final EditText passwordField = (EditText) findViewById(R.id.password_field);
        setViewMode(VIEW_MODE_LOGGING_IN);

        if (checkOrShowError(userNameField, "User name is empty")
                && checkOrShowError(passwordField, "Password is empty")) {

            //creating valid payleven instance
            configurePaylevenApi(
                    userNameField.getText().toString(),
                    passwordField.getText().toString(), new LoginListener() {
                        /*
                            onRegistered - when you successfully logged in with your payleven credentials
                         */
                        @Override
                        public void onLoginSuccessful() {
                            // set result
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            activity.finish();
                        }

                        /*
                            onError - e.g. you use the wrong api key or don't have internet connection
                         */
                        @Override
                        public void onLoginError(PaylevenError e) {
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                            setViewMode(VIEW_MODE_ERROR);
                        }
                    });
        }
    }

    /**
     * log in with your payleven credentials
     *
     * @param userName        payleven username
     * @param password        payleven password
     * @param loginListener   your listener to know when logged in or when an error occurs
     */
    private void configurePaylevenApi(String userName, String password,
                                        LoginListener loginListener) {
        loginController.loginAsync(userName, password, loginListener);
    }

    /**
     * @param viewMode set different view modes
     */
    protected void setViewMode(int viewMode) {
        switch (viewMode) {
            case VIEW_MODE_LOGGING_IN:
                loginButton.setEnabled(false);
                progressBar.setIndeterminate(true);
                break;
            case VIEW_MODE_ERROR:
                loginButton.setEnabled(true);
                progressBar.setIndeterminate(false);
                break;
            default:

        }
    }

    /**
     * Checks that the specified field contains valid data. Otherwise displays an error message
     * if specified
     *
     * @param textField     Text field to check
     * @param errorMessage  Message to be displayed. If null, the message won't be displayed
     * @return              true if the field contains valid data
     */
    private boolean checkOrShowError(TextView textField, String errorMessage) {
        if (textField == null) {
            throw new IllegalArgumentException("textField can not be null");
        }

        CharSequence text = textField.getText();
        if (TextUtils.isEmpty(text)) {
            if (errorMessage != null) {
                textField.setError(errorMessage);
            }
            return false;
        }

        return true;
    }

    /**
     * send a broadcast to close all activities
     * in BaseActivity we registered a broadcast receiver
     */
    @Override
    public void onBackPressed() {
        loginController.loginCanceled();
    }

}
