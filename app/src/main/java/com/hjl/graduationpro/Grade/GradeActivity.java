package com.hjl.graduationpro.Grade;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;


import com.hjl.graduationpro.R;

import java.util.ArrayList;
import java.util.List;

public class GradeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Long> gradelist;
    private MySQLiteHelper SQLhelper;
    private SQLiteDatabase db;
    private static final String TAG = "GradeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        gradelist = new ArrayList<>();

        recyclerView = findViewById(R.id.recycle_view);

        SQLhelper = new MySQLiteHelper(this,"GradeDB.db",null,1);
        db = SQLhelper.getWritableDatabase();
        LinearLayoutManager layoutManager = new LinearLayoutManager(GradeActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); //设置上下滑动
        recyclerView.setLayoutManager(layoutManager);

        Cursor cursor = db.query("grade",null,null,null,
                null,null,"grade desc","10");
        while (cursor.moveToNext()){
            long grade_temp = cursor.getLong(cursor.getColumnIndex("grade"));
            gradelist.add(grade_temp);
//            Log.i(TAG,String.valueOf(gradelist.size()));
        }
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(gradelist);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
