package com.example.kjdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kymjs.aframe.http.HttpCallBack;
import org.kymjs.aframe.http.KJHttp;
import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.activity.BaseActivity;
import org.kymjs.aframe.utils.CipherUtils;
import org.kymjs.aframe.utils.DensityUtils;
import org.kymjs.aframe.utils.FileUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.example.kjdemo.app.RequestManager;
import com.example.kjdemo.utils.LogUtils;

public class ParsePicureFromUrlActivity extends BaseActivity {

	@BindView(id = R.id.listView1)
	private ListView picListView;
	private PicPathAdapter adapter;
	private String imageHttpUrl;
	private ImageLoader mImageLoader;
	private com.nostra13.universalimageloader.core.ImageLoader mImageLoader2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new PicPathAdapter(ParsePicureFromUrlActivity.this, null);

		TextView textView = new TextView(this);
		Runtime runtime = Runtime.getRuntime();
		int maxSize = (int) (runtime.maxMemory() >> 20);
		int currentSize = (int) (runtime.totalMemory() >> 20);
		String text = "最大Heap值:" + maxSize + "m";
		text += "\n当前Heap值:" + currentSize + "m";
		textView.setText(text);

		android.view.ViewGroup.LayoutParams layoutParams = new android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		addContentView(textView, layoutParams);

		picListView.setDrawingCacheBackgroundColor(Color.TRANSPARENT);

		picListView.setAdapter(adapter);
		mImageLoader = RequestManager.getImageLoader();
		mImageLoader2 = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
		// imageHttpUrl = "http://image.baidu.com";
		imageHttpUrl = "http://appartment.sundsdiginc.com/index.php?stype=Art";
		initListAdapter();

	}

	// 放在主线程
	@SuppressLint("HandlerLeak")
	private Handler refreshListDatahandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (Looper.myLooper() != null) {

				if (msg.what == 0)
					((BaseAdapter) msg.obj).notifyDataSetChanged();
			}
		};

	};

	private void notifyDataSetChanged(BaseAdapter adapter) {
		refreshListDatahandler.obtainMessage(0, adapter).sendToTarget();
	}

	private void initListAdapter() {
		// downloadHtmlToCache(imageHttpUrl);

		downloadHtmlToCache(imageHttpUrl, new DownloadSuccess() {

			@Override
			public void onSuccess(String filePath) {
				List<String> listData = parseHtmlUseStringApi(filePath);
				adapter.setListData(listData);
				if (listData.size() > 0)
					notifyDataSetChanged(adapter);
			}

			@Override
			public void onFailue() {
			}

		});

	}

	// 使用jsoup解析
	@SuppressWarnings("unused")
	private List<String> initListAdapterUseJsoup(String httpUrl) {
		List<String> listData = new ArrayList<String>();
		try {
			Document doc = Jsoup.connect(httpUrl).timeout(5000).post();
			Elements ele = doc.select("[src$=.png]");
			for (Element e : ele)
				listData.add(e.attr("abs:src"));

			LogUtils.d("listData.size(): " + listData.size());

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return listData;
	}

	// 下载成功的回调
	private interface DownloadSuccess {
		public void onSuccess(String filePath);

		public void onFailue();
	}

	// 将指定url的html网页缓存到sd卡
	private void downloadHtmlToCache(final String httpUrl, final DownloadSuccess success) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				KJHttp doPost = new KJHttp();
				doPost.post(httpUrl, new HttpCallBack() {

					@Override
					public void onSuccess(Object t) {
						if (t instanceof String) {
							String str = String.valueOf(t);
							Context context = ParsePicureFromUrlActivity.this;
							String folderPath = context.getExternalCacheDir().getPath();
							FileUtils.saveFileCache(str.getBytes(), folderPath, CipherUtils.md5(httpUrl));
							success.onSuccess(folderPath + "/" + CipherUtils.md5(httpUrl));
						}

					}

					@Override
					public void onLoading(long count, long current) {
					}

					@Override
					public void onFailure(Throwable t, int errorNo, String strMsg) {
						success.onFailue();
					}
				});

			}
		}).start();

	}

	private boolean checkIsPngOrJpg(String str) {
		if (str != null && str.length() > 0 && str.startsWith("http://")) {
			int index = str.lastIndexOf(".");
			if (index != -1) {
				String picStr = str.substring(index);
				if (picStr.equalsIgnoreCase(".jpg") || picStr.equalsIgnoreCase(".png"))
					return true;
			}
		}
		return false;
	}

	// 使用普通的string的api解析字符串
	private List<String> parseHtmlUseStringApi(String filePath) {
		List<String> list = new ArrayList<String>();

		try {
			InputStream is = new FileInputStream(filePath);
			byte[] data = new byte[1024];
			int len = 0;
			String tempStr = "";

			while ((len = is.read(data)) > 0) {
				String str = new String(data, 0, len);
				tempStr = StringUtils.trim(str);
				String picStr = "";
				int firstIndex = 0, lastIndex = 0;
				// src="http://img0.bdstatic.com/static/common/widget/search_box_search/logo/logo_3b6de4c.png"
				for (; tempStr.length() > 0;) {

					firstIndex = tempStr.indexOf("src=\"");
					if (firstIndex != -1) {
						firstIndex += "src=\"".length();
						lastIndex = tempStr.substring(firstIndex).indexOf("\"");
						if (lastIndex == -1) {
							tempStr = tempStr.substring(firstIndex);
							continue;
						}
						lastIndex = firstIndex + lastIndex;
						if (firstIndex < lastIndex) {
							picStr = tempStr.substring(firstIndex, lastIndex);
							if (checkIsPngOrJpg(picStr) && !list.contains(picStr))
								list.add(picStr);
						}
						lastIndex = Math.max(firstIndex, lastIndex);
						tempStr = tempStr.substring(lastIndex);
					} else
						break;

				}
				tempStr = StringUtils.trim(str);
				// solve data-src="http://img0.bdstatic.com/img/image/shouye/hjdbtg.jpg"
				for (; tempStr.length() > 0;) {
					firstIndex = tempStr.indexOf("data-src=\"");
					if (firstIndex != -1) {
						firstIndex += "data-src=\"".length();
						lastIndex = tempStr.substring(firstIndex).indexOf("\"");
						if (lastIndex == -1) {
							tempStr = tempStr.substring(firstIndex);
							continue;
						}
						lastIndex = firstIndex + lastIndex;
						if (firstIndex < lastIndex) {
							picStr = tempStr.substring(firstIndex, lastIndex);
							if (checkIsPngOrJpg(picStr) && !list.contains(picStr))
								list.add(picStr);
						}
						lastIndex = Math.max(firstIndex, lastIndex);
						tempStr = tempStr.substring(lastIndex);
					} else
						break;

				}
				tempStr = StringUtils.trim(str);
				// "src":"http:\/\/img0.bdstatic.com\/img\/image\/shouye\/mnyhxznllll.jpg"
				for (; tempStr.length() > 0;) {

					firstIndex = tempStr.indexOf("\"src\":\"");
					if (firstIndex != -1) {
						firstIndex += "\"src\":\"".length();
						lastIndex = tempStr.substring(firstIndex).indexOf("\"");
						if (lastIndex == -1) {
							tempStr = tempStr.substring(firstIndex);
							continue;
						}
						lastIndex = firstIndex + lastIndex;
						if (firstIndex < lastIndex) {
							picStr = tempStr.substring(firstIndex, lastIndex);
							if (checkIsPngOrJpg(picStr) && !list.contains(picStr))
								list.add(StringUtils.replace(picStr, "\\", ""));
						}
						lastIndex = Math.max(firstIndex, lastIndex);
						tempStr = tempStr.substring(lastIndex);
					} else
						break;

				}

			}

			is.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		LogUtils.d(list.size() + "");
		return list;
	}

	// 解析assets中的html文件
	@SuppressWarnings("unused")
	private List<String> parseHtmlFromAssets(String assetsFileName) {
		List<String> list = new ArrayList<String>();

		try {
			byte[] data = FileUtils.input2byte(getAssets().open(assetsFileName));
			String folderPath = this.getExternalCacheDir().getPath();
			FileUtils.saveFileCache(data, folderPath, assetsFileName);
			File docFile = new File(folderPath, assetsFileName);
			// Document document = Jsoup.connect(httpUrl).timeout(5000).post();
			Document doc = Jsoup.parse(docFile, "UTF-8");
			// Elements img = doc.getElementsByTag("img");
			//
			Elements src = doc.select("[src]");
			Elements src_data = doc.select("[data-src]");

			for (Element ele : src) {
				if (ele.tagName().equals("img"))
					list.add(ele.attr("abs:src"));
			}

			for (Element ele2 : src_data) {
				if (ele2.tagName().equals("img"))
					list.add(ele2.attr("abs:data-src"));
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		LogUtils.d(list.size() + "");
		return list;
	}

	@Override
	public void setRootView() {
		setContentView(R.layout.activity_parse_html);
	}

	class PicPathAdapter extends BaseAdapter {
		private List<String> listData;
		private Context context;
		private ViewHolder viewHolder;
		private ImageListener listener;

		public PicPathAdapter(Context context, List<String> listData) {
			this.listData = listData;
			this.context = context;
			listener = new ImageListener();
		}

		public void setListData(List<String> listData) {
			this.listData = listData;
		}

		@Override
		public int getCount() {
			return listData != null ? listData.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return listData != null ? listData.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public ViewHolder getViewHolder() {
			return viewHolder;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				RelativeLayout relRootView = new RelativeLayout(context);
				int px = DensityUtils.dip2px(ParsePicureFromUrlActivity.this, 2);
				relRootView.setPadding(0, px, 0, px);
				relRootView.setBackgroundResource(R.drawable.imagevie_bg);

				TextView textView = new TextView(context);
				textView.setId(R.integer.button01);
				textView.setTextColor(Color.BLUE);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				relRootView.addView(textView, layoutParams);

				ImageView imageView = new ImageView(context);
				imageView.setId(R.integer.imageView01);
				layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.addRule(RelativeLayout.BELOW, R.integer.button01);
				layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageView.setPadding(px, 0, px, 0);
				imageView.setAdjustViewBounds(true);
				imageView.setDrawingCacheEnabled(false);
				imageView.setDuplicateParentStateEnabled(false);
				relRootView.addView(imageView, layoutParams);

				TextView showPosition = new TextView(context);
				showPosition.setId(R.integer.button02);
				showPosition.setTextColor(Color.RED);
				layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.addRule(RelativeLayout.BELOW, R.integer.button01);
				showPosition.setTextSize(24);
				showPosition.getPaint().setFakeBoldText(true);
				relRootView.addView(showPosition, layoutParams);

				viewHolder = new ViewHolder();
				viewHolder.textView = textView;
				viewHolder.imageView = imageView;
				viewHolder.showPositionText = showPosition;

				convertView = relRootView;
				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();

			String imageUrl = String.valueOf(getItem(position));
			viewHolder.textView.setText(imageUrl);
			viewHolder.showPositionText.setText(position + "");

			// mImageLoader2.displayImage(imageUrl, viewHolder.imageView);
			mImageLoader.get(imageUrl, listener);

			return convertView;
		}

		class ImageListener implements com.android.volley.toolbox.ImageLoader.ImageListener {

			public ImageListener() {
			}

			@Override
			public void onErrorResponse(VolleyError error) {
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				Bitmap bitmap = response.getBitmap();
				WeakReference<Bitmap> bitmapReference = new WeakReference<Bitmap>(bitmap);
				if (bitmapReference.get() != null) {
					viewHolder.imageView.setImageBitmap(null);
					viewHolder.imageView.setImageBitmap(bitmapReference.get());
				}

			}
		}

		class ViewHolder {
			TextView textView;
			TextView showPositionText;
			ImageView imageView;
		}

	}
}
