package com.hjl.graduationpro;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.hjl.graduationpro.Grade.MySQLiteHelper;
import com.hjl.graduationpro.View.MainView;

public class GameActivity extends AppCompatActivity {

    private MainView mainView;
    private MySQLiteHelper sqLiteHelper;
    private SQLiteDatabase db;

    private static final String TAG = "GameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqLiteHelper = new MySQLiteHelper(this,"GradeDB.db",null,1);
        db = sqLiteHelper.getWritableDatabase();


        if (mainView == null){
            mainView = new MainView(GameActivity.this);
        }
        setContentView(mainView);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){  //当按到返回键的时候

            mainView.setStopDraw(true);

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("是否确定退出游戏？");
            alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(GameActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            alert.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mainView.setStopDraw(false);                }
            });
            alert.show();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainView = null;
        Log.i(TAG,"onDestroy");
        if (mainView == null){
            Log.i(TAG,"mainView is null");
        }

    }


}
