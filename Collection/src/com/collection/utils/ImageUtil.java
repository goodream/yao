package com.collection.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;

public class ImageUtil {

	/**
	 * 从本地文件获取图片,若从本地获取不到图片的话从网络断获取图片,若从网络端获取不到图片的话获取默认图片
	 * 
	 * @param path
	 *            本地图片 pathl
	 * @param url
	 *            网络路径 ur
	 * @param size
	 *            指定的大小
	 * @return bitmap or null
	 */
	public static Bitmap getImage(String path, String url, int size) {
		Bitmap bitmap = null;
		File file = new File(path);
		if (file.exists()) {
			bitmap = getBitmapToSize(path, size);
		} else {
			if (netWorkIsRight()) {
				GetNetWorkImage netWorkImage = new GetNetWorkImage(
						bitmap);
				netWorkImage.execute(url);
			}
		}
		return bitmap;
	}

	/**
	 * 保存图片 到本地指定文件夹
	 * 
	 * @param path
	 *            指定的文件路径
	 * @param bitmap
	 *            保存的图
	 */
	public static void saveImage(String path, Bitmap bitmap) {
		if (path.equals("") || bitmap == null) {
			return;
		}
		File file = new File(path);
		try {
			OutputStream outputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
			outputStream.close();
			bitmap.recycle();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 取本地压缩到一定的尺寸的图片
	 * 
	 * @param path
	 *            要压缩的图片路径
	 * @param size
	 *            尺寸
	 * @return bitmap or null
	 */
	public static Bitmap getBitmapToSize(String path, int size) {
		if (path.equals("") || size < 1) {
			return null;
		}
		Bitmap bitmap = null;
		Options options = new Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(path, options);
		int height = options.outHeight;
		int width = options.outWidth;
		boolean up = Math.min(height, width) >= size;
		if (up) {
			options.inSampleSize = Math.max(height, width) / size;
		} else {
			options.inSampleSize = 1;
		}
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(path, options);

		return bitmap;
	}

	/**
	 * 判断网络是否可用
	 * 
	 * 返回值 true or false
	 */
	public static boolean netWorkIsRight() {
		return false;
	}

	/**
	 * 从网络端获取图片
	 */
	public static class GetNetWorkImage extends AsyncTask<String, Void, Bitmap> {
		/**
		 * 从网络端获取的bitmap
		 */
		public Bitmap bitmap_ = null;

		public GetNetWorkImage(Bitmap bitmap) {
			bitmap_ = bitmap;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			URL url = null;
			Bitmap bitmap = null;
			InputStream is = null;
			HttpURLConnection connection = null;
			try {
				url = new URL(params[0]);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				is = connection.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
					if (connection != null) {
						connection.disconnect();
					}

				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				bitmap_ = result;
			}
		}
	}

	public static Bitmap AccordingToScreen(Activity activity, String imagePath) {
		Display currentDisplay = activity.getWindowManager().getDefaultDisplay();
		int dw = currentDisplay.getWidth();
		int dh = currentDisplay.getHeight();
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
			bitmapFactoryOptions.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(imagePath,bitmapFactoryOptions);

			int heightRatio = (int) Math.ceil(bitmapFactoryOptions.outHeight
					/ (float) dh);
			int widthRatio = (int) Math.ceil(bitmapFactoryOptions.outWidth
					/ (float) dw);

			Log.v("HEIGHRATIO", "" + heightRatio);
			Log.v("WIDTHRATIO", "" + widthRatio);

			if (heightRatio > 1 && widthRatio > 1) {
				bitmapFactoryOptions.inSampleSize = heightRatio > widthRatio ? heightRatio
						: widthRatio;
			}
			bitmapFactoryOptions.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(imagePath, bitmapFactoryOptions);
		} catch (Exception e) {
			Log.v("ERROR", e.toString());

		}
		return bitmap;
	}
	/*****************************************************************************************/
	/**
	 * 在指定的文件夹下创建多层文件夹
	 * 
	 * @param file
	 *            指定的文件夹
	 * @param dir
	 *            建立的子文件夹
	 * @return 新建文件夹成功 返回该多层文件夹对象 否则 返回 null
	 */
	public static File createDirs(File file, String dir) {
		if (file.exists() && file.isDirectory()) {
			String[] dirs = dir.split("/");
			int length = dirs.length;
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					if (dirs[i] != null && !dirs[i].equals("")) {
						File son = new File(file, "/" + dirs[i]);
						son.mkdirs();
						file = son;
					}
				}
			}
		}
		if (file.exists()) {
			return file;
		} else {
			return null;
		}
	}

	/**
	 * 在指定的文件夹下创建多层文件夹
	 * 
	 * @param file
	 *            指定的文件夹
	 * @param dir
	 *            建立的子文件夹
	 * @return 新建文件夹成功 返回该多层文件夹对象 否则 返回 null
	 */
	public static File createDirs(File file, String[] dirs) {
		if (file.exists() && file.isDirectory()) {
			int length = dirs.length;
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					if (dirs[i] != null && !dirs[i].equals("")) {
						File son = new File(file, "/" + dirs[i]);
						son.mkdirs();
						file = son;
					}
				}
			}
		}
		if (file.exists()) {
			return file;
		} else {
			return null;
		}
	}

	/**
	 * 删除指定目录下的所有文件
	 * 
	 * @param dir
	 *            指定的目录
	 */
	public static void deleteFiles(File dir) {
		if (dir.exists()) {
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				if (files.length > 0) {
					for (int i = 0; i < files.length; i++) {
						if (files[i].isFile()) {
							files[i].delete();
						} else {
							deleteFiles(files[i]);
						}
					}
				}
			}
		}
	}

	/**
	 * 只保留指定目录下的文件
	 * 
	 * @param dir
	 *            指定目录
	 * @param name
	 *            保留的文件名
	 */
	public static void deleteFiles(File dir, String name) {
		if (dir.exists() && (name != null && !name.equals(""))) {
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				if (files.length > 0) {
					for (int i = 0; i < files.length; i++) {
						if (files[i].isFile()&& !files[i].getName().equals(name)) {
							files[i].delete();
						} else {
							deleteFiles(files[i], name);
						}
					}
				}
			}
		}
	}
	
	public static boolean isMnueExists(File file,String[] dirs){
		int length = dirs.length;
		StringBuilder builder = new StringBuilder();
		if(file.exists()&&length>0){			
			for(int i=0;i<length;i++){
				if(dirs[i]!=null&&!TextUtils.isEmpty(dirs[i])){
					builder.append("/"+dirs[i]);
				}
			}
		}
		file = new File(file.getPath()+builder.toString());
		return file.exists();
	}
	/**
	 * 输入输出流写入
	 * @param input 输入流
	 * @param output 输出流
	 * @throws IOException 抛出异常
	 */
	public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
}
