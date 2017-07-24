package com.alphagao.done365.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.User;
import com.alphagao.done365.greendao.gen.UserDao;
import com.alphagao.done365.utils.StreamUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alpha on 2017/4/15.
 */

public class AccountActivty extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.userNameView)
    TextView userNameView;
    @BindView(R.id.userNameLabel)
    TextView userNameLabel;
    @BindView(R.id.userNameLayout)
    RelativeLayout userNameLayout;
    @BindView(R.id.modifyPasswordView)
    TextView modifyPasswordView;
    @BindView(R.id.modifyPasswordLayout)
    RelativeLayout modifyPasswordLayout;
    @BindView(R.id.modifyEmailView)
    TextView modifyEmailView;
    @BindView(R.id.modifyEmailLabel)
    TextView modifyEmailLabel;
    @BindView(R.id.modifyEmailLayout)
    RelativeLayout modifyEmailLayout;
    private UserDao userDao;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        setupActionBar();
        initData();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.arrow_back_white_24dp);
        }
    }

    private void initData() {
        userDao = DaoUtil.generateUserDao(this);
        user = userDao.queryBuilder()
                .where(UserDao.Properties.Id.eq(userPrefs.getLong("user_id")))
                .build()
                .list()
                .get(0);

        userNameLabel.setText(userPrefs.getString("user_name"));
        modifyEmailLabel.setText(userPrefs.getString("user_email"));
    }

    @Override
    public void onNewMessage(Message msg) {

    }

    @OnClick({R.id.userNameLayout, R.id.modifyPasswordLayout, R.id.modifyEmailLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userNameLayout:
                modifyUserName();
                break;
            case R.id.modifyPasswordLayout:
                modifyPassword();
                break;
            case R.id.modifyEmailLayout:
                modifyEmail();
                break;
            default:
                break;
        }
    }

    private void modifyUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setText(userPrefs.getString("user_name"));
        editText.setSelection(editText.getText().length());
        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(100, 50, 100, 50);
        layout.addView(editText);
        builder.setTitle(getString(R.string.nic_name))
                .setView(layout)
                .setPositiveButton(getString(R.string.positive_sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString() != "") {
                            String userName = editText.getText().toString();
                            saveUserName(userName);
                            dialog.dismiss();
                        } else {
                            toast(getString(R.string.nic_name_null));
                        }
                    }
                })
                .show();
    }

    private void saveUserName(String userName) {
        user.setUserName(userName);
        userDao.update(user);
        userPrefs.putString("user_name", userName);
        userNameLabel.setText(userName);
    }

    private void modifyPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_modify_password, null);
        final EditText oldPwdView = (EditText) view.findViewById(R.id.oldPwdView);
        final EditText newPwdView = (EditText) view.findViewById(R.id.newPwdView);
        TextView button = (TextView) view.findViewById(R.id.confirm);
        final AlertDialog dialog = builder.setTitle(getString(R.string.modify_password))
                .setView(view)
                .create();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = oldPwdView.getText().toString();
                String newPwd = newPwdView.getText().toString();
                if (oldPwdIsCorrect(oldPwd)) {
                    saveNewPwd(newPwd);
                    dialog.dismiss();
                    requestNewLogin();
                } else {
                    oldPwdView.setError(getString(R.string.old_password_error));
                }
            }
        });
        dialog.show();
    }

    private void saveNewPwd(String newPwd) {
        user.setPassword(StreamUtils.password2MD5(newPwd));
        userDao.update(user);
    }

    private boolean oldPwdIsCorrect(String oldPwd) {
        return user.getPassword().equals(StreamUtils.password2MD5(oldPwd));
    }

    private void requestNewLogin() {
        userPrefs.putBoolean("login_succeed", false);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        toast(getString(R.string.new_pwd_new_login), Toast.LENGTH_LONG);
        finish();
    }

    private void modifyEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_modify_email, null);
        final EditText oldPwdView = (EditText) view.findViewById(R.id.oldPwdView);
        final EditText newEmailView = (EditText) view.findViewById(R.id.newEmailView);
        TextView button = (TextView) view.findViewById(R.id.confirm);
        final AlertDialog dialog = builder.setTitle(getString(R.string.modify_email))
                .setView(view)
                .create();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = oldPwdView.getText().toString();
                String newEmail = newEmailView.getText().toString();
                if (oldPwdIsCorrect(oldPwd)) {
                    if (isEmailValid(newEmail)) {
                        saveNewEmail(newEmail);
                        dialog.dismiss();
                        toast(getString(R.string.modify_email_success));
                    } else {
                        newEmailView.setError(getString(R.string.email_format_error));
                    }
                } else {
                    oldPwdView.setError(getString(R.string.password_error));
                }
            }
        });
        dialog.show();
    }

    private void saveNewEmail(String newEmail) {
        user.setEmail(newEmail);
        userDao.update(user);
        modifyEmailLabel.setText(newEmail);
    }

    private boolean isEmailValid(String newEmail) {
        return newEmail.contains("@") && userDao.queryBuilder()
                .where(UserDao.Properties.Email.eq(newEmail))
                .build()
                .list()
                .size() == 0;
    }
}
