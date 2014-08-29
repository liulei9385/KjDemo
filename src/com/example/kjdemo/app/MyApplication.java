package com.example.kjdemo.app;

import org.kymjs.aframe.KJLoger;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.example.kjdemo.utils.UtilImageLoader;

import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		KJLoger.openActivityState(false);
		KJLoger.openDebutLog(false);

		UtilImageLoader.initImageLoader(this);
		RequestManager.init(this);

	}

}
