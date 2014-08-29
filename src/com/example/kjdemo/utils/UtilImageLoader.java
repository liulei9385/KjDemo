package com.example.kjdemo.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.kjdemo.R;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class UtilImageLoader {
	static public ImageLoader initImageLoader(final Context context) {

		com.nostra13.universalimageloader.utils.L.writeDebugLogs(false);

		ImageLoader imageLoader = ImageLoader.getInstance();

		DisplayImageOptions options;
		ImageLoaderConfiguration loadoptions;

		if (android.os.Build.VERSION.SDK_INT >= 14) {
			options = new DisplayImageOptions.Builder()// 不发
					.showImageOnLoading(R.drawable.img_loading)// 帅哥照顾
					.showImageForEmptyUri(R.drawable.img_not_found)// 帅哥照顾
					.showImageOnFail(R.drawable.img_loadfail)//
					.bitmapConfig(Bitmap.Config.RGB_565)//
					.imageScaleType(ImageScaleType.EXACTLY)//
					.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
					.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
					.considerExifParams(true)// 淡入淡出
					.build(); // 创建配置过得DisplayImageOption对象
		} else {
			options = new DisplayImageOptions.Builder()// 不发
					.showImageOnLoading(R.drawable.img_loading)// 帅哥照顾
					.showImageForEmptyUri(R.drawable.img_not_found)// 帅哥照顾
					.showImageOnFail(R.drawable.img_loadfail)//
					.bitmapConfig(Bitmap.Config.RGB_565)//
					.imageScaleType(ImageScaleType.EXACTLY)//
					.cacheInMemory(false) // 设置下载的图片是否缓存在内存中
					.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
					.build(); // 创建配置过得DisplayImageOption对象
		}

		/** 20148-18日 内存模式变成为LRU的算法，设置为5M */
		loadoptions = new ImageLoaderConfiguration//
		.Builder(context)//
				.defaultDisplayImageOptions(options)//
				.threadPoolSize(4)//
				.threadPriority(Thread.NORM_PRIORITY - 2)//
				.denyCacheImageMultipleSizesInMemory()//
				.diskCache(new LruDiscCache(StorageUtils.getCacheDirectory(context), new Md5FileNameGenerator(), 50 << 20))//
				.tasksProcessingOrder(QueueProcessingType.LIFO)//
//				.memoryCache(new WeakMemoryCache())//
				.build();
		imageLoader.init(loadoptions);
		return imageLoader;
	}
}
