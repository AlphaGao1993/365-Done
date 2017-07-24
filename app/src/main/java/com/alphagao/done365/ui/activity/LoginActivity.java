package com.alphagao.done365.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alphagao.done365.R;
import com.alphagao.done365.engines.Account;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.User;
import com.alphagao.done365.greendao.gen.UserDao;
import com.alphagao.done365.utils.MyToast;
import com.alphagao.done365.utils.Prefs;
import com.alphagao.done365.utils.StreamUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alpha on 2017/4/10.
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_email)
    EditText userEmail;
    @BindView(R.id.user_password)
    EditText userPassword;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.loginLayout)
    LinearLayout loginLayout;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.bt_register)
    Button btRegister;

    private UserLoginTask mAuthTask;
    private UserRegisterTask mSignTask;
    private View focusView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setBackgroundFit();
    }

    @Override
    public void onNewMessage(Message msg) {

    }

    /**
     * 在 5.0 以上的系统中设置全屏背景
     */
    private void setBackgroundFit() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorLightBackground));
    }

    @OnClick({R.id.bt_login, R.id.bt_register})
    public void onViewClicked(View view) {
        String mEmail = userEmail.getText().toString();
        String mPassword = userPassword.getText().toString();
        switch (view.getId()) {
            case R.id.bt_login:
                requestLogin(mEmail, mPassword);
                break;
            case R.id.bt_register:
                requestSign(mEmail, mPassword);
                break;
            default:
                break;
        }
    }

    /**
     * 注册前的合法性检测
     *
     * @param mEmail    注册邮箱
     * @param mPassword 注册密码
     */
    private void requestSign(String mEmail, String mPassword) {
        userEmail.setError(null);
        userPassword.setError(null);
        if (isEmailValid(mEmail, false) && isPasswordValid(mPassword)) {
            if (mSignTask != null) {
                return;
            }
            showProgress(true);
            mSignTask = new UserRegisterTask(mEmail, mPassword);
            mSignTask.execute();
        }
    }

    /**
     * 登录前的合法性检测
     *
     * @param mEmail    登录邮箱
     * @param mPassword 登录密码
     */
    private void requestLogin(String mEmail, String mPassword) {
        if (attemptLogin(mEmail, mPassword)) {
            if (mAuthTask != null) {
                return;
            }
            showProgress(true);
            mAuthTask = new UserLoginTask(mEmail, mPassword);
            mAuthTask.execute();
        }
    }

    private boolean attemptLogin(String mEmail, String mPassword) {
        focusView = null;
        if (isEmailValid(mEmail, true) && isPasswordValid(mPassword)) {
            return true;
        }
        if (focusView != null) {
            focusView.requestFocus();
        }
        return false;
    }

    private boolean isEmailValid(String mEmail, boolean focus) {
        boolean isValid = false;
        if (mEmail.contains("@")) {
            isValid = true;
        } else if (focus) {
            userEmail.setError(getString(R.string.error_invalid_email));
            focusView = userEmail;
        }
        return isValid;
    }

    private boolean isPasswordValid(String mPassword) {
        if (mPassword.length() >= 4) {
            return true;
        }
        userPassword.setError(getString(R.string.error_invalid_password));
        focusView = userPassword;
        return false;
    }

    private void showProgress(final boolean isShow) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
            int duration = getResources().getInteger(android.R.integer.config_shortAnimTime);
            loginLayout.setVisibility(isShow ? View.GONE : View.VISIBLE);
            loginLayout.animate()
                    .setDuration(duration)
                    .alpha(isShow ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loginLayout.setVisibility(isShow ? View.GONE : View.VISIBLE);
                        }
                    });

            progress.setVisibility(isShow ? View.VISIBLE : View.GONE);
            progress.animate()
                    .alpha(isShow ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progress.setVisibility(isShow ? View.VISIBLE : View.GONE);
                        }
                    });
        } else {
            loginLayout.setVisibility(isShow ? View.GONE : View.VISIBLE);
            progress.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private String email;
        private String password;

        public UserLoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int resultCode = 0;
            try {
                Account account = new Account(LoginActivity.this);
                resultCode = account.login(email, StreamUtils.password2MD5(password));
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return resultCode;
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            mAuthTask = null;
            showProgress(false);
            if (resultCode == Account.LOGIN_SUCCEED) {

                UserDao userDao = DaoUtil.generateUserDao(getApplicationContext());
                User user = userDao.queryBuilder()
                        .where(UserDao.Properties.Email.eq(email)).build().list().get(0);

                saveLoginState(user);
                loginSucceed();
            } else {
                if (resultCode == Account.PASSWORD_ERROR) {
                    userPassword.setError(getString(R.string.error_incorrect_password));
                    userPassword.requestFocus();
                } else if (resultCode == Account.EMIAL_NOT_EXIST) {
                    userEmail.setError(getString(R.string.error_not_exist_email));
                    userEmail.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void loginSucceed() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Integer> {

        private String email;
        private String password;

        public UserRegisterTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int resultCode = 0;
            try {
                Account register = new Account(LoginActivity.this);
                resultCode = register.Register(email, StreamUtils.password2MD5(password));
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return resultCode;
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            showProgress(false);
            mSignTask = null;
            if (resultCode == Account.REGISTER_EMAIL_EXIST) {
                userEmail.setError(getString(R.string.error_exist_email));
                userEmail.requestFocus();
            } else if (resultCode == Account.REGISTER_SUCCEED) {
                UserDao userDao = DaoUtil.generateUserDao(getApplicationContext());
                User user = userDao.queryBuilder()
                        .where(UserDao.Properties.Email.eq(email))
                        .build().list().get(0);

                saveLoginState(user);
                loginSucceed();
            } else {
                MyToast.toast(getString(R.string.sign_out_error));
            }
        }
    }

    private void saveLoginState(User user) {
        prefs.putLong("current_login_user", user.getId());

        //该 Activity 的第一次实例化 userPrefs
        userPrefs = Prefs.getUserInstance(getApplicationContext(), user.getId());

        userPrefs.putLong("user_id", user.getId());
        String oldName = userPrefs.getString("user_name");
        userPrefs.putString("user_name", oldName == "" ? "user" + user.getId() : oldName);
        userPrefs.putBoolean("login_succeed", true);
        userPrefs.putString("user_email", user.getEmail());
    }
}
