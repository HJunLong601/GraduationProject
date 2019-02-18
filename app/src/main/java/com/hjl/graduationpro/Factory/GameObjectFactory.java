package com.hjl.graduationpro.Factory;

import android.content.res.Resources;
import com.hjl.graduationpro.GameObject.BigPlane;
import com.hjl.graduationpro.GameObject.Bullet;
import com.hjl.graduationpro.GameObject.GameObject;
import com.hjl.graduationpro.GameObject.MidPlane;
import com.hjl.graduationpro.GameObject.MyPlane;
import com.hjl.graduationpro.GameObject.MySecPlane;
import com.hjl.graduationpro.GameObject.SmallPlane;

public class GameObjectFactory {

    private volatile static GameObjectFactory factory;
    private static Resources resource;

    public static final int SMALL_PLANE =1;
    public static final int MID_PLANE =2;
    public static final int BIG_PLANE =3;
    public static final int MY_PLANE =4;
    public static final int BULLET =5;
    public static final int MY_SEC_PLANE =6;


    private GameObjectFactory(Resources resources){
        this.resource = resources;
    }

    public static GameObjectFactory getInstance(Resources resources){

        if (factory == null){
            synchronized (GameObjectFactory.class){
                if (factory == null){
                    factory = new GameObjectFactory(resources);
                }
            }
        }

        return factory;
    }


    public GameObject creareGameObject(Integer type){

        switch (type){
            case SMALL_PLANE: return new SmallPlane(resource);
            case MID_PLANE:return new MidPlane(resource);
            case BIG_PLANE: return new BigPlane(resource);
            case MY_PLANE:return new MyPlane(resource);
            case BULLET: return new Bullet(resource);
            case MY_SEC_PLANE:return new MySecPlane(resource);
        }

        return null;

    }


}
