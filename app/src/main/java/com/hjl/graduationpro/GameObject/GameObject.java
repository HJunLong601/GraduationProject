package com.hjl.graduationpro.GameObject;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

 public abstract class GameObject {

    protected Paint paint;
    protected int speed;
    protected int object_x, object_y;
    protected int width;
    protected int height;
    protected int currentFrame;
    protected float screen_width;
    protected float screen_height;
    protected boolean alive;
    protected Resources resources;

    public GameObject(Resources resources){
        this.resources = resources;
        paint = new Paint();
    }

    //释放资源
    public abstract void release();

    //绘制对象
    public abstract void drawSelf(Canvas canvas);

    //初始化图片资源
    public abstract void initBiamap();

    //初始化数据
    public abstract void initial(int arg0,int arg1,int arg2);

    //是否碰撞
    public boolean isCollide(GameObject object){
        return false;
    }

    //逻辑操作
    public abstract void logic();

    //设定屏幕的大小
    public void setScreenWH(float screen_width, float screen_height) {
        this.screen_width = screen_width;
        this.screen_height = screen_height;
    }

    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public float getObject_x() {
        return object_x;
    }
    public void setObject_x(int object_x) {
        this.object_x = object_x;
    }
    public float getObject_y() {
        return object_y;
    }
    public void setObject_y(int object_y) {
        this.object_y = object_y;
    }
    public float getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public float getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public boolean isAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
