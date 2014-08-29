package com.example.kjdemo;

import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.activity.BaseActivity;
import org.kymjs.aframe.utils.SystemTool;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends BaseActivity {

	@BindView(id = R.id.button1, click = true)
	private Button clean;
	@BindView(id = R.id.button2, click = true)
	private Button parsePic;
	@BindView(id = R.id.button3, click = true)
	private Button webView;
	@BindView(id = R.id.button4, click = true)
	private Button playVideo;
	@BindView(id = R.id.button5, click = true)
	private Button volley;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setRootView() {
		setContentView(R.layout.activity_main);
	}

	private final String mVidepPath = "http://124.95.143.7/5b3fb550000001F4-1409219155-2079006872/data5/newflv.sohu.ccgslb.net/sohu/s26h23eab6/230/116/NVrikxXWS3eHGr0oJa4a9D.mp4?key=44twB_TmVOJNNgAsLn4qnkhrkDD-ndYo&n=1&a=50&cip=72.46.156.29";

	@Override
	public void widgetClick(View v) {
		super.widgetClick(v);
		switch (v.getId()) {
		case R.id.button1:
			SystemTool.gc(MainActivity.this);
			break;

		case R.id.button2:
			showActivity(MainActivity.this, ParsePicureFromUrlActivity.class);
			break;

		case R.id.button3:
			showActivity(MainActivity.this, WebviewActivity.class);
			break;

		case R.id.button4:
			Intent playVideoIntent = new Intent(Intent.ACTION_VIEW);
			playVideoIntent.setComponent(new ComponentName("tv.danmaku.ijk.media.demo",
					"tv.danmaku.ijk.media.demo.VideoPlayerActivity"));
			playVideoIntent.setData(Uri.parse(mVidepPath));
			startActivity(playVideoIntent);
			break;

		case R.id.button5:
			showActivity(MainActivity.this, VolleyActivity.class);
			break;
		}
	}
}
