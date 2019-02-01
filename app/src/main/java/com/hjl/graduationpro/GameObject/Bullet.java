package com.hjl.graduationpro.GameObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.hjl.graduationpro.R;

import java.util.ArrayList;

public class Bullet extends GameObject {

    private Bitmap bullet;
    private boolean isInitial = false;
    private ArrayList<Bullet> objectList;
    public static final int GeneratedNum = 5; //生产速度比



    public Bullet(Resources resources) {
        super(resources);

        alive = true;
    }

    @Override
    public void release() {
        if (!bullet.isRecycled()){
            bullet.recycle();
            bullet = null;
        }
        objectList.remove(this); //移出列表
        //Log.i("Bullet","releasereleasereleasereleasereleasereleasereleasereleaserelease");
    }

    @Override
    public void drawSelf(Canvas canvas) {

        if (alive){
            canvas.save();
            canvas.clipRect(object_x,object_y,object_x+width,object_y+height);
            canvas.drawBitmap(bullet,object_x,object_y,paint);
            canvas.restore();
            logic();
        }

    }

    @Override
    public void initBiamap() {
        bullet = BitmapFactory.decodeResource(resources, R.drawable.bullet);
        width = bullet.getWidth();
        height = bullet.getHeight();
    }

    @Override
    public void initial(int x, int y, int speed) {


        if (!isInitial){
            initBiamap();

            this.object_x = x - 10;
            this.object_y = y-100;
            this.speed = speed;
            isInitial = true;

        }

    }

    @Override
    public void logic() {

        if (object_y >= 0){
            object_y -= speed;
        }else {
            Log.i("Bullet","remove ");
            Log.i("Bullet","y is " + object_y );
            release();
        }

    }

    public void setObjectList(ArrayList<Bullet> objectList) {
        this.objectList = objectList;
    }

}
