package com.hjl.graduationpro.GameObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.hjl.graduationpro.R;
import com.hjl.graduationpro.sounds.GameSoundPool;

import java.util.ArrayList;

public class MyPlane extends GameObject {

    private Bitmap myPlane;

    private int middle_x;
    private int middle_y;


    private boolean isInitial = false;
    private boolean isExplosion;
    private boolean playDieSound = false;

    private ArrayList<Bitmap> explodes;

    // MyPlane: h is :240 w is : 210
    public MyPlane(Resources resources) {
        super(resources);
        alive = true;
        isExplosion = false;
        currentFrame = 0;

    }

    @Override
    public void release() {
        if (!myPlane.isRecycled()){
            myPlane.recycle();
        }
    }

    @Override
    public void drawSelf(Canvas canvas) {

        if (alive){
            middle_x = object_x + width/2;
            middle_y = object_y + height/2;
            if (!isExplosion){
                canvas.save();
                canvas.clipRect(object_x,object_y,object_x+width,object_y+height);
                canvas.drawBitmap(myPlane,object_x,object_y,paint);
                canvas.restore();

            }else {

                if (currentFrame < explodes.size()){
                    canvas.save();
                    canvas.clipRect(object_x,object_y,object_x + explodes.get(currentFrame).getWidth(),
                            object_y + explodes.get(currentFrame).getHeight());
                    canvas.drawBitmap(explodes.get(currentFrame), object_x, object_y,paint);
                    canvas.restore();
                    currentFrame++;
                }else {
                    alive = false;
                    release();
                }
            }
        }
    }


    @Override
    public void initBiamap() {
        myPlane = BitmapFactory.decodeResource(resources, R.drawable.myplane2);
        width = myPlane.getWidth() / 2;
        height = myPlane.getHeight();

    }

    @Override
    public void initial(int arg0, int arg1, int arg2) {
        if (!isInitial){
            initBiamap();

            object_x = ((int) screen_width - width) /2;
            object_y = (int)screen_height - height;

            isExplosion = false;
            isInitial = true;
        }
    }

    @Override
    public void setScreenWH(float screen_width, float screen_height) {
        super.setScreenWH(screen_width, screen_height);


    }

    @Override
    public void logic() {
        middle_x = object_x + width/2;
        middle_y = object_y + height/2;
    }

    @Override
    public boolean isCollide(GameObject obj) {

        if (object_x <= obj.getObject_x() && object_x + width <= obj.getObject_x()){
            return false;  // obj 在Plane的右边
        }else if (obj.getObject_x()<= object_x &&obj.getObject_x() + obj.getWidth() <= object_x){
            return false;  // obj 在Plane的左边
        }else if ( obj.getObject_y() <= object_y && obj.getObject_y() + obj.getHeight() <= object_y){
            return false; //obj 在Plane 的上方
        }else if (object_y <= obj.getObject_y() && object_y + height<= obj.getObject_y() ){
            return false; //obj 在Plane 的下方
        }
        isExplosion = true; // 进入爆炸状态

        return true;


//        if (object.getObject_x() > object_x && object.getObject_y() > object_y
//                && object.getObject_x() < object_x + width && object.getObject_y() < object_y + height ){
//
//            isExplosion = true; // 进入爆炸状态
//
//            return true;
//        }
//        return false;

    }

    public int getMiddle_x() {
        return middle_x;
    }
    public int getMiddle_y() {
        return middle_y;
    }

    public boolean isInitial() {
        return isInitial;
    }
    public void setInitial(boolean initial) {
        isInitial = initial;
    }

    public boolean isExplosion() {
        return isExplosion;
    }
    public void setExplosion(boolean explosion) {
        isExplosion = explosion;
    }

    public void setExplodes(ArrayList<Bitmap> explodes) {
        this.explodes = explodes;
    }


    public boolean isPlayDieSound() {
        return playDieSound;
    }
    public void setPlayDieSound(boolean playDieSound) {
        this.playDieSound = playDieSound;
    }
}
