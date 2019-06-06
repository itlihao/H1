package com.hospital.s1m.lib_base.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hospital.s1m.lib_base.entity.Patient;

import java.util.ArrayList;

/**
 *
 * @author Lihao
 * @date 2018-12-10
 * Email heaolihao@163.com
 */
public class PatientDao {
    private static final String DATABASE_NAME = "zy_ai_registration.db";
    private static final String TABLE_DEV = "patient";
    private static final int DATABASE_VERSION = 1000;

    private DBHelper mDBHelper;
    private Context mContext;

    private static final String CREATE_TABLE_FAVORITE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_DEV + "(" +
                    "_id INTEGER PRIMARY KEY, " +
                    "idS VARCHAR, " +
                    "userName TEXT, " +
                    "userShortName TEXT, " +
                    "birthday TEXT, " +
                    "sex INT, " +
                    "phone TEXT, " +
                    "base_version INT64, " +
                    "clinicId TEXT " +
                    ")";

    public PatientDao(Context context) {
        this.mContext = context;
        this.mDBHelper = new DBHelper(mContext);
    }

    public void addPatient(String userName, String birthday, int sex, String phone, String version, String clinicId, String idS) {
        if (!patientAlreadyExist(idS)) {
            String sql = "insert into " + TABLE_DEV
                    + " (_id,userName,birthday,sex,phone,base_version,clinicId,idS)"
                    + " values(null,?,?,?,?,?,?,?)";
            try (SQLiteDatabase db = mDBHelper.getWritableDatabase()) {
                db.execSQL(sql, new Object[]{userName, birthday, sex, phone, version, clinicId, idS});
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String sql = "update " + TABLE_DEV + " set userName=?, birthday=?, sex=?, phone=?, base_version=?, clinicId=? where idS=? ";
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            try {
                db.execSQL(sql, new Object[]{userName, birthday, sex, phone, version, clinicId, idS});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void updatePatient(String userName, String birthday, int sex, String phone, String version, String idS) {
        if (!patientAlreadyExist(idS)) {
            return;
        }
        String sql = "update " + TABLE_DEV + " set userName=?, birthday=?, sex=?, phone=?, base_version=? where idS=? ";
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try {
            db.execSQL(sql, new Object[]{userName, birthday, sex, phone, version, idS});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean patientAlreadyExist(String idS) {
        boolean isExist = true;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        try {
            String sql = "select * from " + TABLE_DEV + " where idS = ?";
            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idS)});

            if (cursor != null && cursor.getCount() == 0) {
                isExist = false;
            }
            assert cursor != null;
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isExist;
    }

    public ArrayList<Patient> getDeviceByClinicId(String clinicId) {
        ArrayList<Patient> patients = new ArrayList<>();
        Patient patient;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String sql = "select * from " + TABLE_DEV + " where clinicId = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{ String.valueOf(clinicId) });
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                patient = new Patient();
                patient.setIdS(cursor.getString(1));
                patient.setUserName(cursor.getString(2));
                patient.setUserShortName(cursor.getString(3));
                patient.setBirthday(cursor.getString(4));
                patient.setSex(cursor.getInt(5));
                patient.setPhone(cursor.getString(6));
                patient.setBaseVersion(cursor.getString(7));
                patients.add(patient);
//                Logger.d("dao", PATIENT.getUserName());
            }
        }
        assert cursor != null;
        cursor.close();
        return patients;
    }

    public String getMaxVersion(String clinicId) {
//        select * from table where id=(select MAX(id) from table )

        String ver = "";
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        try {
            String sql = "select MAX(base_version) from patient where clinicId=?";
            Cursor cursor = db.rawQuery(sql, new String[]{clinicId});

            if (cursor.moveToNext()) {
                ver = cursor.getString(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ver;
    }

    private class DBHelper extends SQLiteOpenHelper {
        private DBHelper instance = null;

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public synchronized DBHelper getInstance(Context context) {
            if (instance == null) {
                instance = new DBHelper(context);
            }
            return instance;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_FAVORITE);

            // 若不是第一个版本安装，直接执行数据库升级
            // 请不要修改FIRST_DATABASE_VERSION的值，其为第一个数据库版本大小
            final int firstDatabaseVersion = 1000;
            onUpgrade(db, firstDatabaseVersion, DATABASE_VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // 使用for实现跨版本升级数据库
            for (int i = oldVersion; i < newVersion; i++) {
                String sql;
                switch (i) {

                    default:
                        break;
                }
            }
        }
    }
}
