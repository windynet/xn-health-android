package com.chengdai.ehealthproject.uitls.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.chengdai.ehealthproject.model.common.model.CityModel;
import com.chengdai.ehealthproject.uitls.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DBManager {
    private static final String ASSETS_NAME = "china_cities.db";
    private static final String DB_NAME = "china_cities.db";
    private static final String TABLE_NAME = "city";
    private static final String NAME = "name";
    private static final String PINYIN = "pinyin";
    private static final int BUFFER_SIZE = 1024;
    private String DB_PATH;
    private Context mContext;

//    public static DBManager init(){
//        if (mInstance == null){
//            synchronized (DBManager.class){
//                if (mInstance != null){
//                    mInstance = new DBManager();
//                }
//            }
//        }
//        return mInstance;
//    }

    public DBManager(Context context) {
        this.mContext = context;
        DB_PATH = File.separator + "data"
                + Environment.getDataDirectory().getAbsolutePath() + File.separator
                + context.getPackageName() + File.separator + "MyDatabases" + File.separator;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void copyDBFile(){
        File dir = new File(DB_PATH);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File dbFile = new File(DB_PATH + DB_NAME);
        if (!dbFile.exists()){
            InputStream is;
            OutputStream os;
            try {
                is = mContext.getResources().getAssets().open(ASSETS_NAME);
                os = new FileOutputStream(dbFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                while ((length = is.read(buffer, 0, buffer.length)) > 0){
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("数据库已存在");
        }
    }

    /**
     * 读取所有城市
     * @return
     */
    public List<CityModel> getAllCities(){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        List<CityModel> result = new ArrayList<>();
        CityModel city;
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String pinyin = cursor.getString(cursor.getColumnIndex(PINYIN));
            city = new CityModel(name, pinyin);
            result.add(city);
        }
        cursor.close();
        db.close();
        Collections.sort(result, new CityComparator());
        return result;
    }

    /**
     * 通过名字或者拼音搜索
     * @param keyword
     * @return
     */
    public List<CityModel> searchCity(final String keyword){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME +" where name like \"%" + keyword
                + "%\" or pinyin like \"%" + keyword + "%\"", null);
        List<CityModel> result = new ArrayList<>();
        CityModel city;
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String pinyin = cursor.getString(cursor.getColumnIndex(PINYIN));
            city = new CityModel(name, pinyin);
            result.add(city);
        }
        cursor.close();
        db.close();
        Collections.sort(result, new CityComparator());
        return result;
    }

    /**
     * a-z排序
     */
    private class CityComparator implements Comparator<CityModel> {
        @Override
        public int compare(CityModel lhs, CityModel rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);

            lhs.setFirstChar(a.toUpperCase());
            rhs.setFirstChar(b.toUpperCase());

            return a.compareTo(b);
        }
    }
}