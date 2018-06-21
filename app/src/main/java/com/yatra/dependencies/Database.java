package com.yatra.dependencies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteOpenHelper {
    // All Static variables
    // Database Name
    private static final String DB_NAME = "yatra";
    // Database Version
    private static final int DB_VERSION =2;
    // Table name
    public static final String TABLE_NAME = "dependencies";

    // Drop table query
    public static final String DROP_QUERY = "DROP TABLE IF EXIST " + TABLE_NAME;
    public static final String GET_DEP_QUERY = "SELECT * FROM " + TABLE_NAME;


    // image table column names
    public static final String Id = "id";
    public static final String Dep_Id = "Dep_Id";
    public static final String Name = "name";
    public static final String Type = "type";
    public static final String SizeInBytes = "size";
    public static final String Cdn_Path = "path";
    public static final String PHOTO = "photo";


    // Create table
    public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "" +
            "(" +Id + " integer primary key autoincrement, " +
            Dep_Id + " TEXT not null, " +
            Name + " TEXT not null, " +
            Type + " TEXT not null, " +
            Cdn_Path + " TEXT not null, " +
            SizeInBytes + " INTEGER DEFAULT 0, " +
            PHOTO + " blob not null)";


    public Database(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    // Creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(
                    "CREATE TABLE " + TABLE_NAME +
                            "(" + Id + " INTEGER PRIMARY KEY, " +
                            Dep_Id + " TEXT, " +
                            Name + " TEXT, " +
                            Type + " TEXT, " +
                            Cdn_Path + " TEXT, " +
                            SizeInBytes + " INTEGER DEFAULT 0, " +
                            PHOTO + " blob not null)"
            );
        //    db.execSQL(CREATE_TABLE_QUERY);
        }

        catch (Exception e){

        }
    }

    // Upgrading tables
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        // Drop older table if existed
        db.execSQL(DROP_QUERY);

        // Create tables again
        this.onCreate(db);
    }
   //get poses count from particular column by value
    public int getCountByValue() {

        int cnt = 0;
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
            cursor.moveToFirst();
            cnt = Integer.parseInt(cursor.getString(0));
            // cursor.close();
            // db.close();

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();

            }


        }
        catch (Exception e){
            e.printStackTrace();
        }
        return cnt;
    }

    //TOTAL  Count
    public int getCount() {

        SQLiteDatabase db = this.getWritableDatabase();
        int cnt = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        cnt = Integer.parseInt(cursor.getString(0));
        cursor.close();
        db.close();
        return cnt;
    }
    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public String Exist(String user) {
        String username="";
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor c = db.query(TABLE_NAME, null, Dep_Id + "=?", new String[]{String.valueOf(user)},null, null, null);

            if (c == null) {
                return username;
            }
            else {

                if(c != null && c.moveToFirst()){
                    username = c.getString(c.getColumnIndex(Dep_Id));
                    c.close();
                }

            }
        }

        catch(Exception e){
            e.printStackTrace();
        }

        return username;
    }




    //adding into local db
    public void addData(dependencies dataModel) {
        //  Log.e("Values Got ", dataModel.poses.toString());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

             String storedUser = Exist(dataModel.getId().toString());
             if (storedUser.equalsIgnoreCase(dataModel.getId().toString())) {

                 Log.e("exits", "Exits Alredy");

             } else {

                 values.put(Dep_Id, dataModel.getSno());
                 values.put(Name, dataModel.getName());

                 if(dataModel.getPicture()!=null)
                 values.put(PHOTO, NetworkUtility.getPictureByteOfArray(dataModel.getPicture()));

                 values.put(Cdn_Path, dataModel.getCdn_path());
                 values.put(SizeInBytes, dataModel.getSizeInBytes());
                 values.put(Type, dataModel.getType());

                 try {

                    // db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);


                      db.insert(TABLE_NAME,null,values);
                 } catch (Exception e) {
                     Log.e("Error", e.getMessage());
                 }

                 db.close();
             }

       }

    //fetching all  data in local db data
    public void fetchData(DataFetchListner listener) {
        DataFetcher fetcher = new DataFetcher(listener, this.getReadableDatabase());
        fetcher.start();
    }

    public class DataFetcher extends Thread {

        private final DataFetchListner mListener;
        private final SQLiteDatabase mDb;

        public DataFetcher(DataFetchListner listener, SQLiteDatabase db) {
            mListener = listener;
            mDb = db;

        }

        @Override
        public void run() {


            Cursor cursor = mDb.rawQuery(GET_DEP_QUERY, null);

              Log.e("cursor count", ""+cursor.getCount());

              final List<dependencies> dataList = new ArrayList<>();

            try {


                if (cursor != null && cursor.getCount() > 0 && cursor.getColumnCount() != 0) {

                    if (cursor.moveToFirst()) {

                        do {

                                dependencies data = new dependencies();

                                data.setFromDatabase(true);

                                data.setName(cursor.getString(cursor.getColumnIndex(Name)));
                                data.setSno(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Dep_Id))));
                                data.setType(cursor.getString(cursor.getColumnIndex(Type)));
                                data.setSizeInBytes(String.valueOf(cursor.getDouble(cursor.getColumnIndex(SizeInBytes))));
                                data.setPicture(NetworkUtility.getBitmapFromByte(cursor.getBlob(cursor.getColumnIndex(Cdn_Path))));

                                dataList.add(data);
                                publishFlower(data);


                        }

                        while (cursor.moveToNext());
                    }

                    cursor.close();

                }
            }
            catch(Exception ec)
            {
                Log.e("error", "Exception:" + ec.getMessage());
                cursor.close();

            }
                   Handler handler = new Handler(Looper.getMainLooper());
                   handler.post(new Runnable() {
                   @Override
                   public void run() {

                    mListener.onHideDialog();

                   }
                });

        }

        public void publishFlower(final dependencies data) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                     mListener.onDeliverData(data);
                }
            });
        }
    }


}
