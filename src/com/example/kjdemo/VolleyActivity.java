package com.example.kjdemo;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kjdemo.app.RequestManager;

public class VolleyActivity extends Activity {

	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volley);

		initView();

		// showImageByNetworkImageView();

		loadImageByVolley();
	}

	private void initView() {
		mImageView = (ImageView) findViewById(R.id.imageView);
	}

	/**
	 * 利用Volley获取JSON数据
	 */
	@SuppressWarnings("unused")
	private void getJSONByVolley() {
		RequestQueue requestQueue = Volley.newRequestQueue(this);
		String JSONDataUrl = "http://pipes.yahooapis.com/pipes/pipe.run?_id=giWz8Vc33BG6rQEQo_NLYQ&_render=json";
		final ProgressDialog progressDialog = ProgressDialog.show(this, "This is title", "...Loading...");

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				System.out.println("response=" + response);
				if (progressDialog.isShowing() && progressDialog != null) {
					progressDialog.dismiss();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				System.out.println("sorry,Error");
			}
		});
		requestQueue.add(jsonObjectRequest);
	}

	private void loadImageByVolley() {
		String imageUrl = "http://c.hiphotos.baidu.com/image/w%3D1280%3Bcrop%3D0%2C0%2C1280%2C800/sign=2abcf809eb24b899de3c7d3a563626f6/43a7d933c895d143afcf362a71f082025aaf0779.jpg";
		RequestManager.getRequestQueue();
		ImageLoader imageLoader = RequestManager.getImageLoader();
		ImageListener listener = ImageLoader.getImageListener(mImageView, R.drawable.ic_launcher, R.drawable.ic_launcher);
		imageLoader.get(imageUrl, listener);
	}

	/**
	 * 利用NetworkImageView显示网络图片
	 */
	@SuppressWarnings("unused")
	private void showImageByNetworkImageView() {
		// String imageUrl = "http://avatar.csdn.net/6/6/D/1_lfdfhl.jpg";
		// RequestQueue requestQueue = Volley.newRequestQueue(ctx,new Https)
		// RequestQueue requestQueue = Volley.newRequestQueueInDisk(ctx, "/sdcard/demo/", null);
		// ImageLoader imageLoader = new ImageLoader(requestQueue, new BitmapCache());
		//
		// mNetworkImageView.setTag("url");
		// mNetworkImageView.setImageUrl(imageUrl, imageLoader);
	}
}
