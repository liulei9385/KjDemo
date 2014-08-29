package com.example.kjdemo;

import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.activity.BaseActivity;

import com.example.kjdemo.utils.LogUtils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewActivity extends BaseActivity {

	@BindView(id = R.id.webView1)
	private WebView mWebView;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initWebView();
		initView();
		// webview操作
		operateWebView(mWebView);
	}

	private void operateWebView(WebView mWebView) {
		mWebView.loadUrl("http://tv.sohu.com/");
	}

	private void initView() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Loading....");
	}

	private void showLoading() {
		if (!mProgressDialog.isShowing())
			mProgressDialog.show();
	}

	private void dismissLoading() {
		if (mProgressDialog.isShowing())
			mProgressDialog.hide();
	}

	// 初始化webView
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// Webview 自适应屏幕
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setSupportZoom(true);
		// 本地存储
		String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
		webSettings.setDatabaseEnabled(true);
		webSettings.setDatabasePath(dir);// 设置数据库路径
		webSettings.setDomStorageEnabled(true);// 使用LocalStorage则必须打开
		// 离线存储
		webSettings.setAppCacheEnabled(true);
		String cache_dir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(cache_dir);// 设置应用缓存的路径
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setAllowFileAccess(true);// 可以读取文件缓存(manifest生效)
		mWebView.setWebChromeClient(webChromeClient);
		mWebView.setWebViewClient(webViewClient);
		mWebView.setOnKeyListener(onKeylistener);
	}

	// WebChromeClient
	private WebChromeClient webChromeClient = new WebChromeClient() {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			showLoading();
			if (newProgress > 95)
				dismissLoading();
			super.onProgressChanged(view, newProgress);
		}

		public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota,
				long estimatedDatabaseSize, long totalQuota, android.webkit.WebStorage.QuotaUpdater quotaUpdater) {

			super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota,
					quotaUpdater);
			quotaUpdater.updateQuota(estimatedDatabaseSize * 2); // 此方法扩展存储空间为默认的2倍
		};

	};
	// WebViewClient
	private WebViewClient webViewClient = new WebViewClient() {

		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
			showLoading();
		}

		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			dismissLoading();
		};

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			LogUtils.d(url);
			return super.shouldOverrideUrlLoading(view, url);
		}

	};

	// WebView的前进、后退、与刷新
	private View.OnKeyListener onKeylistener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) { // 表示按返回键 时的操作
					mWebView.goBack(); // 后退
					// webview.goForward();//前进
					return true; // 已处理
				}
			}
			return false;
		}
	};

	@Override
	public void setRootView() {
		setContentView(R.layout.acitvity_webview);
	}

}
