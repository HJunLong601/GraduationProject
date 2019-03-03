package com.hjl.graduationpro.GameObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;


import com.hjl.graduationpro.TaskSystem.Task;
import com.hjl.graduationpro.sounds.GameSoundPool;

import java.util.ArrayList;
import java.util.Random;

public class EnemyPlane extends GameObject {

    protected Bitmap PlaneBitmap;
    protected boolean isExplosion;
    protected boolean isInitial = false;
    protected int blood;   //当前血量
    protected int bloodVolume; // 总血量
    protected Task task;
    protected GameSoundPool soundPool;
    protected boolean isPlayDieSound = false;
    public ArrayList<EnemyPlane> objectList;



    public EnemyPlane(Resources resources) {
        super(resources);
        currentFrame = 0;
        task = Task.getInstance();
    }

    @Override
    public boolean isCollide(GameObject obj) {

        if (object_x <= obj.getObject_x() && object_x + width <= obj.getObject_x()){
            return false;  // obj 在 EP的右边
        }else if (obj.getObject_x()<= object_x &&obj.getObject_x() + obj.getWidth() <= object_x){
            return false;  // obj 在 EP的左边
        }else if ( obj.getObject_y() <= object_y && obj.getObject_y() + obj.getHeight() <= object_y){
            return false; //obj 在EP 的上方
        }else if (object_y <= obj.getObject_y() && object_y + height<= obj.getObject_y() ){
            return false; //obj 在EP 的下方
        }

        return true;
//        if (object.getObject_x() > object_x && object.getObject_y() > object_y
//                 && object.getObject_x() < object_x + width && object.getObject_y() < object_y + height ){
//
//            isExplosion = true; // 进入爆炸状态
//
//            return true;
//        }
//
//        return false;
    }

    @Override
    public void logic() {
        if (object_y < screen_height){
            object_y += speed;
        }else {
            alive = false;
            release();
        }
    }

    @Override
    public void release() {
        if (!PlaneBitmap.isRecycled()){
            PlaneBitmap.recycle();
            PlaneBitmap = null;
        }
        paint = null;
        objectList.remove(this);
    }

    @Override
    public void drawSelf(Canvas canvas) {

    }

    @Override
    public void initBiamap() {

    }

    @Override
    public void initial(int speed,int bloodVolume,int arg1) {

        if (!isInitial){
            this.speed = speed;
            this.bloodVolume = bloodVolume;
            initBiamap();
            blood = bloodVolume;

            Random ran = new Random();
            object_x = ran.nextInt( (int)screen_width-width);
            object_y = -height*2;
            isExplosion = false;
            isInitial = true;
        }

    }

    protected void attacked(int harm) {
        blood -= harm;
        if (blood <= 0){
            isExplosion = true; // 进入爆炸状态
        }
    }


    public Bitmap getPlaneBitmap() {
        return PlaneBitmap;
    }


    public boolean isExplosion() {
        return isExplosion;
    }

    public void setObjectList(ArrayList<EnemyPlane> objectList) {
        this.objectList = objectList;
    }

    public void setSoundPool(GameSoundPool soundPool) {
        this.soundPool = soundPool;
    }
}
