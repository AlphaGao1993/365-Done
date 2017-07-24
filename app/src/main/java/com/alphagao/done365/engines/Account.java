package com.alphagao.done365.engines;

import android.content.Context;

import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.User;
import com.alphagao.done365.greendao.gen.UserDao;

import java.util.List;

/**
 * Created by Alpha on 2017/4/11.
 */

public class Account {

    public static final int EMIAL_NOT_EXIST = 101;
    public static final int PASSWORD_ERROR = 102;
    public static final int LOGIN_SUCCEED = 105;
    public static final int REGISTER_EMAIL_EXIST = 106;
    public static final int REGISTER_SUCCEED = 109;
    public static final int REGISTER_FAIL = 110;
    public static final int HEAD_IMAGE_UPDATE = 111;

    private UserDao userDao;

    public Account(Context context) {
        userDao = DaoUtil.generateUserDao(context);
    }

    public int login(String email, String enPassword) {

        if (isEmailExist(email)) {
            if (isLoginSucceed(email, enPassword)) {
                return LOGIN_SUCCEED;
            } else {
                return PASSWORD_ERROR;
            }
        }
        return EMIAL_NOT_EXIST;
    }

    private boolean isLoginSucceed(String email, String enPassword) {
        return userDao.queryBuilder()
                .where(userDao.queryBuilder().and(
                        UserDao.Properties.Email.eq(email),
                        UserDao.Properties.Password.eq(enPassword)))
                .build().list().size() > 0;
    }

    private boolean isEmailExist(String emial) {
        return userDao.queryBuilder()
                .where(UserDao.Properties.Email.eq(emial))
                .build().list().size() > 0;
    }

    public int Register(String email, String password) {
        if (isEmailExist(email)) {
            return Account.REGISTER_EMAIL_EXIST;
        }
        User user = new User(null, email, password);
        if (userDao.insert(user) > 0) {
            return REGISTER_SUCCEED;
        }
        return REGISTER_FAIL;
    }

    public long getUserId(String email) {
        List<User> users = userDao
                .queryBuilder()
                .where(UserDao.Properties.Email.eq(email))
                .build()
                .list();
        if (users.size() > 0) {
            return users.get(0).getId();
        } else {
            return 0;
        }
    }
}
