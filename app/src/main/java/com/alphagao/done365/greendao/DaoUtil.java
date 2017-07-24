package com.alphagao.done365.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.alphagao.done365.greendao.gen.DaoMaster;
import com.alphagao.done365.greendao.gen.DaoSession;
import com.alphagao.done365.greendao.gen.UserDao;

/**
 * Created by Alpha on 2017/3/17.
 */

public class DaoUtil {
    public static DaoSession daoSession;
    private static DaoMaster daoMaster;
    private static SQLiteDatabase db;
    public static UserDao userDao;

    public static void generateDatabase(Context context, String userId) {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster
                .DevOpenHelper(context, "user_" + userId, null);
        db = openHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        if (daoSession == null) {
            daoSession = daoMaster.newSession();
        }
    }

    public static UserDao generateUserDao(Context context) {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context, "user", null);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        userDao = master.newSession().getUserDao();
        return userDao;
    }
}
