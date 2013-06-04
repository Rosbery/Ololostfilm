package com.tryrosberry.ololostfilm.logic.storage;

import android.content.SharedPreferences;

import com.tryrosberry.ololostfilm.LostFilmApp;

/**
 * Store most important configuration values
 */
public class Settings {
	
	private LostFilmApp mApp = null;
	
	private final static String PREFS_NAME = "settings";
	
	public Settings(LostFilmApp app) {
		mApp = app;
	}
	
	public void saveUid(String uid) {
		SharedPreferences.Editor spe = mApp.getSharedPreferences(PREFS_NAME, 0).edit();
		spe.putString("uid", uid);
		spe.commit();
	}

	public String loadUid() {
		return loadStringValue("uid", "");
	}

    public void saveUserName(String userName) {
        SharedPreferences.Editor spe = mApp.getSharedPreferences(PREFS_NAME, 0).edit();
        spe.putString("username", userName);
        spe.commit();
    }

    public String loadUserName() {
        return loadStringValue("username", "");
    }

    public void saveCrash(String crash) {
        SharedPreferences.Editor spe = mApp.getSharedPreferences(PREFS_NAME, 0).edit();
        spe.putString("crash", crash);
        spe.commit();
    }

    public String loadCrash() {
        return loadStringValue("crash", "");
    }

    public void deleteLastCrash(){
        saveCrash("");
    }

    public void saveCords(String lat,String lng) {
        SharedPreferences.Editor spe = mApp.getSharedPreferences(PREFS_NAME, 0).edit();
        spe.putString("myLat", lat);
        spe.putString("myLng", lng);
        spe.commit();
    }

    public double getLat() {
        return Double.valueOf(loadStringValue("myLat", "0.000000"));
    }

    public double getLng() {
        return Double.valueOf(loadStringValue("myLng", "0.000000"));
    }

    private void putBoolean(String name, boolean value){
        mApp.getSharedPreferences(PREFS_NAME, 0).edit().putBoolean(name, value).commit();
    }

    private boolean getBoolean(String name,Boolean standartValue){
        return mApp.getSharedPreferences(PREFS_NAME, 0).getBoolean(name.toString(), standartValue);
    }



    private String loadStringValue(String key, String def) {
		return mApp.getSharedPreferences(PREFS_NAME, 0).getString(key, def);
	}
}
