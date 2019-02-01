package com.hjl.graduationpro.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hjl.graduationpro.GameActivity;
import com.hjl.graduationpro.MainActivity;

public class BaseView extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    protected int screen_width;
    protected int screen_height;
    protected MainActivity mainActivity;
    protected GameActivity gameActivity;
    protected Paint paint;
    protected Canvas canvas;
    protected Thread thread;
    protected Boolean threadFlag;
    protected SurfaceHolder holder;


    public BaseView(Context context) {
        super(context);
        paint = new Paint();
        holder = this.getHolder();
        holder.addCallback(this);

    }

    public void drawSelf(){

    }

    public void initBitmap(){

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screen_height = this.getHeight();
        screen_width = this.getWidth();
        threadFlag = true;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        threadFlag = false;
    }

    @Override
    public void run() {

    }
}
