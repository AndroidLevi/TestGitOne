package com.zun1.whenask.DB.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.zun1.whenask.DB.entity.UserEntity;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


public class UserDao extends AbstractDao<UserEntity,Long> {


    public static final String TABLENAME = "UserInfo";

    public static class Properties{
        public final static Property Id = new Property(0,int.class,"id",true,"id");
        public final static Property username = new Property(1,String.class,"username",false,"username");
        public final static Property realname = new Property(2,String.class,"realname",false,"realname");
        public final static Property phone = new Property(3,String.class,"phone",false,"phone");
        public final static Property session = new Property(4,String.class,"session",false,"session");
        public final static Property email = new Property(5,String.class,"email",false,"email");
        public final static Property address = new Property(6,String.class,"address",false,"address");
    }

    public UserDao(DaoConfig daoConfig){
        super(daoConfig);
    }
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    @Override
    protected UserEntity readEntity(Cursor cursor, int offset) {
        return null;
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {
        return null;
    }

    @Override
    protected void readEntity(Cursor cursor, UserEntity entity, int offset) {

    }

    @Override
    protected void bindValues(SQLiteStatement stmt, UserEntity entity) {

    }

    @Override
    protected Long updateKeyAfterInsert(UserEntity entity, long rowId) {
        return null;
    }

    @Override
    protected Long getKey(UserEntity entity) {
        return null;
    }

    @Override
    protected boolean isEntityUpdateable() {
        return false;
    }
}
