package com.hjl.graduationpro.View;

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
import com.hjl.graduationpro.GameObject.MidPlane;
import com.hjl.graduationpro.GameObject.MyPlane;
import com.hjl.graduationpro.GameObject.MySecPlane;
import com.hjl.graduationpro.GameObject.SmallPlane;
import com.hjl.graduationpro.MainActivity;
import com.hjl.graduationpro.R;
import com.hjl.graduationpro.TaskSystem.Task;
import com.hjl.graduationpro.Util.Util;
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
    private ArrayList<Bullet> secBullets;
    private ArrayList<Bitmap> myPlaneExp;

    private GameSoundPool soundPool;
    private MyPlane myPlane;
    private MySecPlane mySecPlane;
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
        secBullets = new ArrayList<>();
        myPlaneExp = new ArrayList<>();
        myPlane = (MyPlane) factory.creareGameObject(GameObjectFactory.MY_PLANE);
        mySecPlane = (MySecPlane) factory.creareGameObject(GameObjectFactory.MY_SEC_PLANE);
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

        //初始化 第二飞机的子弹
        for (Bullet object : secBullets){

            if (object.isAlive()){
                object.setScreenWH(screen_width,screen_height);
                object.initial(mySecPlane.getMiddle_x(),mySecPlane.getMiddle_y(),50);
                // Log.i(TAG,"y is " + myPlane.getMiddle_y());
                object.setObjectList(secBullets);
            }

        }

        //初始化 主飞机  重要：并且实时更新 midY 的数据  /*已移动到logic（）函数 */
        if (myPlane.isAlive()&&!myPlane.isInitial()){
            myPlane.setScreenWH(screen_width,screen_height);
            myPlane.initial(0,0,0);
            myPlane.setExplodes(myPlaneExp);
        }

        //初始化 第二架飞机  重要：并且实时更新 midY 的数据  /*已移动到logic（）函数 */
        if (mySecPlane.isAlive()&&!mySecPlane.isInitial()){
            mySecPlane.setScreenWH(screen_width,screen_height);
            mySecPlane.initial(0,0,0);
            mySecPlane.setExplodes(myPlaneExp);
        }



    }


    public void GenerateObject(){
        GenerateCurrentCount ++;
        if (GenerateCurrentCount % SmallPlane.GeneratedNum == 0 && !isGameOver){
            enemyList.add((SmallPlane)factory.creareGameObject(GameObjectFactory.SMALL_PLANE));
          // Log.i("GameView","Generate");
        }

        if (GenerateCurrentCount % MidPlane.GeneratedNum == 0 && !isGameOver){
            //Log.i("GameView","Generate Mid");
            enemyList.add((MidPlane)factory.creareGameObject(GameObjectFactory.MID_PLANE));
        }

        if (GenerateCurrentCount % BigPlane.GeneratedNum == 0 && !isGameOver ){
            //Log.i("GameView","Generate Big");
            enemyList.add((BigPlane)factory.creareGameObject(GameObjectFactory.BIG_PLANE));
        }

        if (GenerateCurrentCount % Bullet.GeneratedNum == 0 && myPlane.isAlive()){
            //Log.i("GameView","Generate Bullet");
            bullets.add((Bullet) factory.creareGameObject(GameObjectFactory.BULLET));
            soundPool.playSound(GameSoundPool.SOUND_SHOOT,0);
        }

        if (GenerateCurrentCount % Bullet.GeneratedNum == 0 && mySecPlane.isAlive()){
            //Log.i("GameView","Generate Bullet");
            secBullets.add((Bullet) factory.creareGameObject(GameObjectFactory.BULLET));
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


        if (isGameOver){  //绘制游戏结束界面
            paint.setTextSize(70);
            paint.setColor(Color.BLACK);
            cacheCanvas.drawBitmap(gameOver,
                    100,
                    screen_height/3,
                    paint);
            cacheCanvas.drawText(""+task.getGrade(),screen_width/2,screen_height/3 +
                    Util.dip2px(45,getContext().getResources().getDisplayMetrics()) ,paint);
            enemyList.clear();

        }

//        Log.i(TAG,"wid is " + gameOver.getWidth() + "h is :"  + gameOver.getHeight());

//        Log.i(TAG,"enemyList size is " + enemyList.size());
//        Log.i(TAG,"bulletList size is " + bullets.size());

        //遍历敌机 画敌机 判断敌机是否被击中
        for (EnemyPlane object :(ArrayList<EnemyPlane>) enemyList.clone()){
            if (object.isAlive() && !isGameOver){
                //判断是否击中
                for (Bullet bullet : bullets){
                    if (bullet.isAlive()){ // 子弹是否存活
                        if (object.isCollide(bullet)){ // 是否击中 击中敌机扣血
                            bullet.setAlive(false);

                        }
                    }
                }

                //判断是否击中 第二架飞机的子弹
                for (Bullet bullet :secBullets){
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
        if (myPlane.isAlive() | mySecPlane.isAlive()){
             //遍历敌机 判断飞机是否与敌机碰撞
            for (EnemyPlane object :(ArrayList<EnemyPlane>) enemyList.clone()){
                if (object.isAlive()){
                    if (myPlane.isCollide(object) & !mySecPlane.isAlive()){ //两架飞机都爆炸了游戏才结束
                        isGameOver = true;
                    }

                    if (mySecPlane.isCollide(object) & !myPlane.isAlive()){
                        isGameOver = true;
                    }
                }
            }

            if (myPlane.isExplosion() && !myPlane.isPlayDieSound()){
                soundPool.playSound(GameSoundPool.SOUND_BIG_EXP,0);
                myPlane.setPlayDieSound(true);
            }

            if (mySecPlane.isExplosion() && !mySecPlane.isPlayDieSound()){
                soundPool.playSound(GameSoundPool.SOUND_BIG_EXP,0);
                mySecPlane.setPlayDieSound(true);
            }

            myPlane.drawSelf(cacheCanvas);
            mySecPlane.drawSelf(cacheCanvas);
        }else{
            isGameOver = true;
        }

        //画子弹
        for (Bullet object : (ArrayList<Bullet>) bullets.clone() ){

            if (object.isAlive()){
                object.drawSelf(cacheCanvas);
            }else {
                object.release();
            }

        }

        for (Bullet object : (ArrayList<Bullet>) secBullets.clone() ){

            if (object.isAlive()){
                object.drawSelf(cacheCanvas);
            }else {
                object.release();
            }

        }

        //
        if (!myPlane.isAlive() && !mySecPlane.isAlive()){
            isGameOver = true;
        }


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
                Log.i("GameFrame","锁定前帧率 : " + String.valueOf((int)(1000/(GameFrameTime+0.001))));  //锁定前帧率
                if (GameFrameTime<period){
                    try {
                        Thread.sleep(sleepTime);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                GameFrameTime = System.nanoTime()/1000000L - DrawStartTime; //锁定帧率后实际帧率
                Log.i("GameFrame","锁定后帧率 : " + String.valueOf((int) (1000/(GameFrameTime+0.001))));  //锁定后帧率

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initBitmap() {

        cacheBitmap = Bitmap.createBitmap(screen_width,screen_height,Bitmap.Config.ARGB_8888);
        background = Util.decodeSampleBitmapFromResources(getResources(), R.drawable.bg_01,screen_width,screen_height);
        background2 = Util.decodeSampleBitmapFromResources(getResources(), R.drawable.bg_02,screen_width,screen_height);
        play = Util.decodeSampleBitmapFromResources(getResources(),R.drawable.play,40,80);
        gameOver = Util.decodeSampleBitmapFromResources(getResources(),R.drawable.over_grade,screen_width-200,screen_height/3);
        gameOver = Util.changeBitmapSize(gameOver,screen_width-200,screen_height/3);
        bg_y = -screen_height;
        bg_y2 = 0;

        myPlaneExp.add(Util.changeBitmapSize(BitmapFactory.decodeResource(getResources(), R.drawable.explode1),210,240));
        myPlaneExp.add(Util.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode3),210,240));
        myPlaneExp.add(Util.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode4),210,240));
        myPlaneExp.add(Util.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode5),210,240));
        myPlaneExp.add(Util.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode6),210,240));
        myPlaneExp.add(Util.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode7),210,240));
        myPlaneExp.add(Util.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode8),210,240));
        myPlaneExp.add(Util.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode9),210,240));
        myPlaneExp.add(Util.changeBitmapSize(BitmapFactory.decodeResource(getResources(),R.drawable.explode12),210,240));
    }

    //背景图逻辑
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
        Log.i("Receive",data);
        for (int i=0;i < data.length(); i++){
            if (!isStopDraw){
                analyzeData(data.charAt(i));
            }
        }

    }

    /**
     * 解析信号，改变飞机位置
     * @param data
     */
    private void analyzeData(char data){
        switch (data){
            case 'a':
                if ((int) myPlane.getObject_x() - 10 > 0){
                    myPlane.setObject_x((int) myPlane.getObject_x() - 10);
                }else {
                    myPlane.setObject_x(0); // 若超过屏幕左边边界 则设为0
                }
                break;
            case 'd':
                if ((int) myPlane.getObject_x() + 10 < screen_width - myPlane.getWidth()){
                    myPlane.setObject_x((int) myPlane.getObject_x() + 10);
                }else {
                    myPlane.setObject_x( (int) (screen_width - myPlane.getWidth()) );
                }
                break;
            case 'w':
                if ((int) myPlane.getObject_y() - 10 > 0 ){
                    myPlane.setObject_y((int) myPlane.getObject_y() - 10);
                }else {
                    myPlane.setObject_y(0);
                }
                break;
            case 's':
                if ((int) myPlane.getObject_y() + 10 < screen_height - myPlane.getHeight() ){
                    myPlane.setObject_y((int) myPlane.getObject_y() + 10);
                }else {
                    myPlane.setObject_y((int)(screen_height - myPlane.getHeight()));
                }
                break;
            case 'j':
                if ((int) mySecPlane.getObject_x() - 10 > 0){
                    mySecPlane.setObject_x((int) mySecPlane.getObject_x() - 10);
                }else {
                    mySecPlane.setObject_x(0); // 若超过屏幕左边边界 则设为0
                }
                break;
            case 'l':
                if ((int) mySecPlane.getObject_x() + 10 < screen_width - mySecPlane.getWidth()){
                    mySecPlane.setObject_x((int) mySecPlane.getObject_x() + 10);
                }else {
                    mySecPlane.setObject_x( (int) (screen_width - mySecPlane.getWidth()) );
                }
                break;
            case 'i':
                if ((int) mySecPlane.getObject_y() - 10 > 0 ){
                    mySecPlane.setObject_y((int) mySecPlane.getObject_y() - 10);
                }else {
                    mySecPlane.setObject_y(0);
                }
                break;
            case 'k':
                if ((int) mySecPlane.getObject_y() + 10 < screen_height - mySecPlane.getHeight() ){
                    mySecPlane.setObject_y((int) mySecPlane.getObject_y() + 10);
                }else {
                    mySecPlane.setObject_y((int)(screen_height - mySecPlane.getHeight()));
                }
                break;

        }
    }


    // 是否点击飞机
    // 测试蓝牙 即使不点击 也为true 即可以改变飞机的位置
    private boolean isTouchPlane = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_DOWN){

            float x = event.getX();
            float y = event.getY();

            // 判定是否点击飞机
            if (x > myPlane.getObject_x() && x < myPlane.getObject_x() + myPlane.getWidth()
                    && y > myPlane.getObject_y() && y < myPlane.getObject_y() + myPlane.getHeight()){
                //isTouchPlane = true;
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

            float paddingPx = Util.dip2px(45,getContext().getResources().getDisplayMetrics());

            if (isGameOver){
                if (event.getX()> 100+paddingPx &&
                        event.getY() > screen_height/2 &&
                        event.getX() < screen_width - 100 - paddingPx &&
                        event.getY() < screen_height/2 + screen_height/12 )
                {    //点击了重新开始
                    Intent toSelf = new Intent(getContext(),GameActivity.class);
                    gameActivity.startActivity(toSelf);

                    gameActivity.finish();
                }else if (event.getX()> 100+paddingPx &&
                        event.getY() > screen_height/2 + screen_height/12 &&
                        event.getX() < screen_width - 100 - paddingPx &&
                        event.getY() < screen_height/2 + screen_height/6)
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
//                myPlane.setObject_x((int) x - (int) myPlane.getWidth()/2);
//                myPlane.setObject_y((int) y  - (int) myPlane.getHeight()/2);
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP){
            //isTouchPlane = false;
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
