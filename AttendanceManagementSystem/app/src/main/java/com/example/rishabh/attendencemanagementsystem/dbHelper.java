package com.example.rishabh.attendencemanagementsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rishabh on 15-02-2017.
 */

public class dbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "studentrecord.db";
    public static final String TABLE = "attendence";
    public static final String COL_1 = "studId";
    public static final String COL_2 = "courseId";
    public static final String COL_3 = "data";
    public ArrayList<String> student;
    Context ctx;


    public dbHelper(Context context, ArrayList<String> al) {
        super(context, DATABASE_NAME, null, 9);
        student = al;
        ctx = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE + " (" + COL_1 + " TEXT ," + COL_2 + " TEXT , " + COL_3 + " INTEGER )");
        Toast.makeText(ctx, "in on create", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Toast.makeText(ctx, "in upgrade", Toast.LENGTH_SHORT).show();

        db.execSQL("DROP TABLE IF EXIST " + TABLE);
        onCreate(db);
    }


    public void markAttendence(HashMap<String, String> totalStudent, ArrayList<String> marked, String CourseId) {
        long r = 0;
        int totalAttendence=0;


        if (marked.isEmpty())
            Toast.makeText(ctx, "Empty list ...mark student first!!", Toast.LENGTH_SHORT).show();
        else {
            for (int i = 0; i < marked.size(); i++) {
                String studentname=marked.get(i);
                String  studentid= totalStudent.get(studentname);
                SQLiteDatabase db = this.getWritableDatabase();
                Cursor res = db.rawQuery("select * from attendence where studId = '"+studentid+"' and courseId ='"+CourseId+"' ",null);

                if(!res.moveToNext())
                {
                    ContentValues contentValues =new ContentValues();
                    contentValues.put(COL_1,studentid);
                    contentValues.put(COL_2,CourseId);
                    contentValues.put(COL_3,totalAttendence+1);
                   // Toast.makeText(ctx, "in if", Toast.LENGTH_SHORT).show();
                    db.insertOrThrow(TABLE,null,contentValues);


                }
                else
                {
                    ContentValues contentValues =new ContentValues();
                    //Toast.makeText(ctx, "in else", Toast.LENGTH_SHORT).show();
                    totalAttendence = res.getInt(2)+1;

                   contentValues.put(COL_1,studentid);
                    contentValues.put(COL_2,CourseId);
                    contentValues.put(COL_3,totalAttendence);
                     db.update(TABLE, contentValues,"studId = ? AND courseId = ?", new String[]{studentid,CourseId});

                }
                Toast.makeText(ctx,"Saved On Sqlite!!!", Toast.LENGTH_SHORT).show();

            }
        }
    }




    public String createJsonResopse() throws Exception {

        JSONArray jsonArray = new JSONArray();
        int i = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from attendence ", null);

        while (res.moveToNext()) {

            JSONObject obj = new JSONObject();
            obj.put("studentId", res.getString(0));
            obj.put("courseId", res.getString(1));
            obj.put("data", res.getInt(2));

            jsonArray.put(obj);

            //  test= test+res.getString(0)+" ";

        }


        return jsonArray.toString();
    }
    public void clearDataBase()
    {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from attendence ", null);
        while(res.moveToNext())
        {
           // Toast.makeText(ctx,Integer.toString(res.getInt(2)), Toast.LENGTH_SHORT).show();
            ContentValues contentValues=new ContentValues();
            String id=res.getString(0),c=res.getString(1);
            contentValues.put(COL_3,0);
            db.update(TABLE, contentValues,"studId = ? AND courseId = ?", new String[]{id,c});

        }
    }
}
