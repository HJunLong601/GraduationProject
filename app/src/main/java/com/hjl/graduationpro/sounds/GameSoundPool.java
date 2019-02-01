package com.hjl.graduationpro.sounds;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


import com.hjl.graduationpro.GameActivity;
import com.hjl.graduationpro.R;

import java.util.HashMap;

public class GameSoundPool {
	private GameActivity gameActivity;
	private SoundPool soundPool;
	private HashMap<Integer,Integer> map;

    public static int SOUND_SHOOT = 1;
    public static int SOUND_EXP = 2;
    public static int SOUND_EXP2 = 3;
    public static int SOUND_EXP3 = 4;
    public static int SOUND_BIG_EXP = 5;
    public static int SOUND_GET_GOODS = 6;
	public static int SOUND_BUTTON = 7;

	@SuppressLint("UseSparseArrays")
	public GameSoundPool(GameActivity gameActivity){
		this.gameActivity = gameActivity;
		map = new HashMap<Integer,Integer>();
		soundPool = new SoundPool(8,AudioManager.STREAM_MUSIC,0);
	}
	public void initGameSound(){
		map.put(SOUND_SHOOT, soundPool.load(gameActivity, R.raw.shoot, 1));
		map.put(SOUND_EXP, soundPool.load(gameActivity, R.raw.explosion, 1));
		map.put(SOUND_EXP2, soundPool.load(gameActivity, R.raw.explosion2, 1));
		map.put(SOUND_EXP3, soundPool.load(gameActivity, R.raw.explosion3, 1));
		map.put(SOUND_BIG_EXP, soundPool.load(gameActivity, R.raw.bigexplosion, 1));
		map.put(SOUND_GET_GOODS, soundPool.load(gameActivity, R.raw.get_goods, 1));
		map.put(SOUND_BUTTON, soundPool.load(gameActivity, R.raw.button, 1));
	}

	public void playSound(int sound,int loop){
		AudioManager am = (AudioManager)gameActivity.getSystemService(Context.AUDIO_SERVICE);
		float stramVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float stramMaxVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float volume = stramVolumeCurrent/stramMaxVolumeCurrent;
		soundPool.play(map.get(sound), volume, volume, 1, loop, 1.0f);	
	}
}
