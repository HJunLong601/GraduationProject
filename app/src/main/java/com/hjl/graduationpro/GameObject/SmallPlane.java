package com.hjl.graduationpro.GameObject;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;


import com.hjl.graduationpro.R;
import com.hjl.graduationpro.sounds.GameSoundPool;

import java.util.Random;

public class SmallPlane extends EnemyPlane {

    public static final int GeneratedNum = 20; //生产速度比
    public static int sumCount = 6;

   // bitmap size : w is 102 h 84

    public SmallPlane (Resources resources){
        super(resources);
        alive = true;

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

    @Override
    public void initBiamap() {
        //PlaneBitmap =BitmapFactory.decodeResource(resources,R.drawable.enemy);
        PlaneBitmap =BitmapFactory.decodeResource(resources, R.drawable.small);
        //BitmapFactory.decodeResource(resources,R.drawable.enemy);
        width = PlaneBitmap.getWidth();			//
        height = PlaneBitmap.getHeight()/3;


    }

    @Override
    public boolean isCollide(GameObject obj) {
        if (super.isCollide(obj)){
            attacked(200);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void drawSelf(Canvas canvas) {
        if (alive){
            if (!isExplosion){
                //canvas.drawBitmap(PlaneBitmap, object_x,object_y,paint);
                canvas.save();
                canvas.clipRect(object_x,object_y,object_x + width,object_y + height);
                canvas.drawBitmap(PlaneBitmap, object_x, object_y,paint);
                canvas.restore();
                logic();

            }else {
                int y = currentFrame * height;
                if (!isPlayDieSound){
                    soundPool.playSound(GameSoundPool.SOUND_EXP,0);
                    task.addScore(10);
                    isPlayDieSound = true;
                }

                if (currentFrame < 3){
                    canvas.save();
                    canvas.clipRect(object_x,object_y,object_x + width,object_y + height);
                    canvas.drawBitmap(PlaneBitmap, object_x, object_y - y,paint);
                    canvas.restore();
                    currentFrame++;
                }else {
                    alive = false;
                    release();
                }

            }

        }
    }


}
