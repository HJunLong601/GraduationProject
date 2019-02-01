package com.hjl.graduationpro.GameObject;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.hjl.graduationpro.R;
import com.hjl.graduationpro.sounds.GameSoundPool;

import java.util.Random;

public class BigPlane extends EnemyPlane {

    public static final int GeneratedNum = 120; //生产速度比 越大生产越慢
    public static int sumCount = 2;

    public BigPlane(Resources resources) {
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

        PlaneBitmap = BitmapFactory.decodeResource(resources, R.drawable.big);
        width = PlaneBitmap.getWidth();			//
        height = PlaneBitmap.getHeight()/5;

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
                    soundPool.playSound(GameSoundPool.SOUND_EXP3,0);
                    task.addScore(40);
                    isPlayDieSound = true;
                }
                if (currentFrame < 5){
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
