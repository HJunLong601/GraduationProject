package com.hjl.graduationpro.View;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;


import com.hjl.graduationpro.Factory.GameObjectFactory;
import com.hjl.graduationpro.GameActivity;
import com.hjl.graduationpro.GameObject.BigPlane;
import com.hjl.graduationpro.GameObject.Bullet;
import com.hjl.graduationpro.GameObject.EnemyPlane;
import com.hjl.graduationpro.GameObject.GameObject;
import com.hjl.graduationpro.GameObject.MidPlane;
import com.hjl.graduationpro.GameObject.MyPlane;
import com.hjl.graduationpro.GameObject.SmallPlane;
import com.hjl.graduationpro.MainActivity;
import com.hjl.graduationpro.R;
import com.hjl.graduationpro.TaskSystem.Task;
import com.hjl.graduationpro.Util.BitmapUtil;
import com.hjl.graduationpro.sounds.GameSoundPool;

import java.util.ArrayList;

public class MainView extends BaseView implements MainActivity.MainViewListener {


    private static final String TAG = "MainView";

    private Bitmap background;  //背景
    private Bitmap background2;
    private Bitmap play;  //暂停开始按钮
    private Bitmap gameOver;

    private Bitmap cacheBitmap; //二级缓存
    private Canvas cacheCanvas;
    private Boolean threadState = true;
    private GameObjectFactory factory;
    private Task task;

    private int bg_y;
    private int bg_y2;
    private long GameFrameTime = 1;
    private long DrawStartTime = 1;
    private final int GameFrame = 30;  //帧率锁定 30
    private int GenerateCurrentCount;

    private boolean isStopDraw = false; //是否暂停绘制
    private boolean isGameOver = false; //是否被击中

    private ArrayList<EnemyPlane> enemyList;
    private ArrayList<Bullet> bullets;
    private ArrayList<Bitmap> myPlaneExp;

    private GameSoundPool soundPool;
    private MyPlane myPlane;
    private SQLiteDatabase db;


    public MainView(Context context) {
        super(context);

        gameActivity = (GameActivity) context;
        MainActivity mainActivity = MainActivity.sInstance;
        mainActivity.setListener(this);


        factory = GameObjectFactory.getInstance(getResources());
        task = Task.getInstance();
        soundPool = new GameSoundPool(gameActivity);
        enemyList = new ArrayList<>();
        bullets = new ArrayList<>();
        myPlaneExp = new ArrayList<>();
        myPlane = (MyPlane) factory.creareGameObject(GameObjectFactory.MY_PLANE);
        db = gameActivity.getDb();
        task.reStartAll();


    }


    public void initObject(){

        //初始化飞机对象
        for (EnemyPlane object : enemyList){

            if (object.isAlive()){

                //初始化小敌机
                if (object instanceof SmallPlane && object.isAlive()){
                    object.setScreenWH(screen_width,screen_height);
                    object.initial(task.getSmlSpeed(),200,0);  // speed blood null
                    object.setObjectList(enemyList);
//                    Log.i(TAG,"small speed is :"+task.getSmlSpeed());
                }
                //初始化中型机
                if (object instanceof MidPlane && object.isAlive()){
                    object.setScreenWH(screen_width,screen_height);
                    object.initial(task.getMidSpeed(),500,0); // speed blood null
                    object.setObjectList(enemyList);
//                    Log.i(TAG,"Mid speed is :"+task.getMidSpeed());
                }
                //初始化大型机
                if (object instanceof BigPlane && object.isAlive()){
                    object.setScreenWH(screen_width,screen_height);
                    object.initial(task.getBigSpeed(),800,0);// speed blood null
                    object.setObjectList(enemyList);
//                     Log.i(TAG,"Big speed is :"+task.getBigSpeed());
                }

                object.setSoundPool(soundPool);
            }

        }
        //初始化 子弹
        for (Bullet object : bullets){

            if (object.isAlive()){
                object.setScreenWH(screen_width,screen_height);
                object.initial(myPlane.getMiddle_x(),myPlane.getMiddle_y(),50);
               // Log.i(TAG,"y is " + myPlane.getMiddle_y());
                object.setObjectList(bullets);
            }

        }

        //初始化 主飞机  重要：并且实时更新 midY 的数据  /*已移动到logic（）函数 */
        if (myPlane.isAlive()&&!myPlane.isInitial()){
            myPlane.setScreenWH(screen_width,screen_height);
            myPlane.initial(0,0,0);
            myPlane.setExplodes(myPlaneExp);
        }


    }


    public void GenerateObject(){
        GenerateCurrentCount ++;
        if (GenerateCurrentCount % SmallPlane.GeneratedNum == 0 && myPlane.isAlive()){
            enemyList.add((SmallPlane)factory.creareGameObject(GameObjectFactory.SMALL_PLANE));
          // Log.i("GameView","Generate");
        }

        if (GenerateCurrentCount % MidPlane.GeneratedNum == 0 && myPlane.isAlive()){
            //Log.i("GameView","Generate Mid");
            enemyList.add((MidPlane)factory.creareGameObject(GameObjectFactory.MID_PLANE));
        }

        if (GenerateCurrentCount % BigPlane.GeneratedNum == 0 && myPlane.isAlive() ){
            //Log.i("GameView","Generate Big");
            enemyList.add((BigPlane)factory.creareGameObject(GameObjectFactory.BIG_PLANE));
        }

        if (GenerateCurrentCount % Bullet.GeneratedNum == 0 && isTouchPlane && myPlane.isAlive()){
            //Log.i("GameView","Generate Bullet");
            bullets.add((Bullet) factory.creareGameObject(GameObjectFactory.BULLET));
            soundPool.playSound(GameSoundPool.SOUND_SHOOT,0);
        }

        if (GenerateCurrentCount >= 20000000){
            GenerateCurrentCount = 0;
        }

    }


    @Override
    public void drawSelf() {

        cacheCanvas = new Canvas(cacheBitmap);

        cacheCanvas.drawBitmap(background,0,bg_y,paint);
        cacheCanvas.drawBitmap(background2,0,bg_y2,paint);
        cacheCanvas.drawBitmap(play,screen_width-80,10,paint);

        //分数 关卡 画笔属性
        paint.setColor(Color.GRAY);
        paint.setTextSize(50);
        paint.setDither(true);
        paint.setAntiAlias(true); //抗锯齿

        cacheCanvas.drawText("分数："+task.getGrade(),0,50,paint);
        cacheCanvas.drawText("关卡："+ task.getRank() ,0,100,paint);
        cacheCanvas.drawText("下一关："+(task.getRank()+1),0,150,paint);


        if (isGameOver){
            paint.setTextSize(70);
            paint.setColor(Color.BLACK);
            cacheCanvas.drawBitmap(gameOver,100,screen_height/3,paint);
            cacheCanvas.drawText(""+task.getGrade(),screen_width/2,screen_height/3 + 100 ,paint);
            enemyList.clear();

        }

//        Log.i(TAG,"wid is " + gameOver.getWidth() + "h is :"  + gameOver.getHeight());

        Log.i(TAG,"enemyList size is " + enemyList.size());
        Log.i(TAG,"bulletList size is " + bullets.size());

        //遍历敌机 画敌机
        for (EnemyPlane object :(ArrayList<EnemyPlane>) enemyList.clone()){
            if (object.isAlive() && !isGameOver){
                //判断是否击中
                for (Bullet bullet : bullets){
                    if (bullet.isAlive()){ // 子弹是否存活
                        if (object.isCollide(bullet)){ // 是否击中 击中飞机扣血
                            bullet.setAlive(false);

                        }
                    }
                }
                object.drawSelf(cacheCanvas);
            }

        }

        //画飞机
        if (myPlane.isAlive()){

            for (EnemyPlane object :(ArrayList<EnemyPlane>) enemyList.clone()){
                if (object.isAlive()){
                    if (myPlane.isCollide(object)){
                        isGameOver = true;
                    }
                }
            }

            if (myPlane.isExplosion() && !myPlane.isPlayDieSound()){
                soundPool.playSound(GameSoundPool.SOUND_BIG_EXP,0);
                myPlane.setPlayDieSound(true);
            }

            myPlane.drawSelf(cacheCanvas);
        }

        //画子弹
        for (Bullet object : (ArrayList<Bullet>) bullets.clone() ){

            if (object.isAlive()){
                object.drawSelf(cacheCanvas);
            }else {
                object.release();
            }

        }
        //


        //最后画二级缓存
        canvas = holder.lockCanvas();
        canvas.drawBitmap(cacheBitmap,0,0,paint);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void run() {

        long period = 1000/GameFrame;  //对应帧率执行时间
        long sleepTime; //休眠时间

        try {
            while (threadState){
                DrawStartTime = System.nanoTime()/1000000L; // 开始时间 转换成毫秒

                if (!isStopDraw){
                    viewLogic();
                    GenerateObject();
                    initObject();

                    drawSelf();
                }

                GameFrameTime = System.nanoTime()/1000000L - DrawStartTime;  //实际执行时间
                sleepTime = period - GameFrameTime;
                if (sleepTime <= 0 ){
                    sleepTime = 2;
                }
                Log.i("GameFrame","Frame Before Lock : " + String.valueOf(1000/(GameFrameTime+0.001)));  //锁定前帧率
                if (GameFrameTime<period){
                    try {
                        Thread.sleep(sleepTime);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                GameFrameTime = System.nanoTime()/1000000L - DrawStartTime; //锁定帧率后实际帧率
                Log.i("GameFrame","Frame After Lock : " + String.valueOf(1000/(GameFrameTime+0.001)));  //锁定前帧率

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initBitmap() {

        cacheBitmap = Bitmap.createBitmap(screen_width,screen_height,Bitmap.Config.ARGB_8888);
        background = BitmapUtil.decodeSampleBitmapFromResources(getResources(), R.drawable.bg_01,screen_width,screen_height);
        background2 = BitmapUtil.decodeSampleBitmapFromResources(getResources(), R.drawable.bg_02,screen_width,screen_height);
        play = BitmapUtil.decodeSampleBitmapFromResources(getResources(),R.drawable.play,40,80);
        gameOver = BitmapUtil.decodeSampleBitmapFromResources(getResources(),R.drawable.over_grade,screen_width-200,screen_height/3);
        gameOver = BitmapUtil.changeBitmapSize(gameOver,screen_width-200,screen_height/3);
        bg_y = -screen_height;
        bg_y2 = 0;

        myPlaneExp.add(BitmapUtil.changeBitmapSize(BitmapFactory.decodeResource(getResources(), R.drawable.explode1),210,240));
        myPlaneExp.add(BitmapUtil.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode3),210,240));
        myPlaneExp.add(BitmapUtil.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode4),210,240));
        myPlaneExp.add(BitmapUtil.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode5),210,240));
        myPlaneExp.add(BitmapUtil.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode6),210,240));
        myPlaneExp.add(BitmapUtil.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode7),210,240));
        myPlaneExp.add(BitmapUtil.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode8),210,240));
        myPlaneExp.add(BitmapUtil.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode9),210,240));
        myPlaneExp.add(BitmapUtil.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode12),210,240));
    }
    public void viewLogic(){

        //第一轮循环 拼接 y = -screen_height;
        //                y2 = 0;
        if (bg_y < bg_y2 ){ //当 y 在上 y2 在下时
            bg_y2 += 10; // 移动y2
            bg_y = bg_y2 - background.getHeight(); //y 随着 y2的移动而移动
        }else {    //当 y2 在上 y 在下时
            bg_y += 10;   //移动y1
            bg_y2 = bg_y - background.getHeight();   //y2随着 y 的移动而移动
        }

        // 当在下面的 y2 溢出屏幕大小时 y开始加
        if (bg_y2 >= background.getHeight()){
            bg_y2 = bg_y - background.getHeight();  // y - y2 = h
            //将y2移到 y 上面 移动后 y - y2 = h 则 y2 = y - h（负数）
        }else if (bg_y >= background.getHeight()){
            bg_y = bg_y2 - background.getHeight();
        }

        //背景完

    }

    /**
     * 接收从MainActivity传过来的蓝牙数据 根据数据改变飞机的位置
     * @param data
     */

    @Override
    public void onReceiveBluData(String data) {
        Log.i("MainView",data);
        char fistCode = data.charAt(0);
        if (fistCode == 'a'){
            if ((int) myPlane.getObject_x() + 10 < screen_width - myPlane.getWidth()){
                myPlane.setObject_x((int) myPlane.getObject_x() + 10);
            }
        }else if (fistCode == 'b'){
            if ((int) myPlane.getObject_x() - 10 > 0){
                myPlane.setObject_x((int) myPlane.getObject_x() - 10);
            }

        }

    }

    // 是否点击飞机
    private boolean isTouchPlane = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_DOWN){

            float x = event.getX();
            float y = event.getY();

            // 判定是否点击飞机
            if (x > myPlane.getObject_x() && x < myPlane.getObject_x() + myPlane.getWidth()
                    && y > myPlane.getObject_y() && y < myPlane.getObject_y() + myPlane.getHeight()){
                isTouchPlane = true;
            }
            // 判定是否点击了开始暂停
            if (x > screen_width - 80 && x < screen_width ){
                if (y > 0 && y < 90){
                    //点击了 暂停
                    isStopDraw = true;
                }else if (y> 90 && y < 170){
                    //点击了 开始
                    isStopDraw = false;
                }
            }

            //结束点击判断

            if (isGameOver){
                if (event.getX()> 100+135 &&
                        event.getY() > screen_height/3+290 &&
                        event.getX() < 100 + 880 -170 &&
                        event.getY() < screen_height/3 + 290 + 145 )
                {    //点击了重新开始
                    Intent toSelf = new Intent(getContext(),GameActivity.class);
                    gameActivity.startActivity(toSelf);

                    gameActivity.finish();
                }else if (event.getX()> 100+135 &&
                        event.getY() > screen_height/3 + 290 + 145 &&
                        event.getX() < 100 + 880 - 170 &&
                        event.getY() < screen_height/3 + 580 - 10)
                {  //点击了提交分数
                    if (task.getGrade() != 0){
                        ContentValues values = new ContentValues();
                        values.put("grade", task.getGrade());
                        db.insert("Grade", null, values);

                        Toast.makeText(getContext(),"成绩提交成功！",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(),"成绩不能为零！",Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent(getContext(),MainActivity.class);
                    getContext().startActivity(intent);

                    gameActivity.finish();
                }
            }

            return true;
        }else if (event.getAction() == MotionEvent.ACTION_MOVE){
            float x = event.getX();
            float y = event.getY();

            if (isTouchPlane && !isStopDraw){
                myPlane.setObject_x((int) x - (int) myPlane.getWidth()/2);
                myPlane.setObject_y((int) y  - (int) myPlane.getHeight()/2);
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP){
            isTouchPlane = false;
        }

        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);

        soundPool.initGameSound();
        initBitmap();

        if (thread != null && thread.isAlive()){
            thread.start();
        }else {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        isStopDraw = true;

        myPlaneExp = null;
        bullets = null;
        enemyList = null;
        background = null;  //背景
        background2 = null;
        play = null;  //暂停开始按钮
        gameOver = null;
        cacheBitmap = null; //二级缓存
        System.gc();
    }

    public boolean isStopDraw() {
        return isStopDraw;
    }
    public void setStopDraw(boolean stopDraw) { isStopDraw = stopDraw; }

}
