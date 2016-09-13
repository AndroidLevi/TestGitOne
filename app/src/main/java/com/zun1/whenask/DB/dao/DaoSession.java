package com.zun1.whenask.DB.dao;

import android.database.sqlite.SQLiteDatabase;

import com.zun1.whenask.DB.entity.UserEntity;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;


public class DaoSession extends AbstractDaoSession {
    private final DaoConfig userDaoConfig;

    private final UserDao userDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);



        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);

        registerDao(UserEntity.class, userDao);

    }

    public void clear() {
        userDaoConfig.getIdentityScope().clear();
    }

    public UserDao getUserDao() {
        return userDao;
    }

}
