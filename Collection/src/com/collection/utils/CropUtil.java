package com.collection.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

public class CropUtil {
	public static final int TAKE_PICTURE=100;
	public static final int CROP_PICTURE=101;
	public static final int CHOOSE_PICTURE = 102;
	
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	public static Uri tempFileUri;
	public static File tempFile;
	public static File albumDir;
	/**
	 * 从图库中选择图片
	 * @param a Activity
	 */
	public static void chooseImage(Activity a){
		//TODO 从图库里选择选择图片 
		try {
			setUpPhotoFile("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		a.startActivityForResult(intent, CHOOSE_PICTURE);
	}
	/**
	 * 通过照相机获取图片
	 * @param a Activity
	 */
	public static void takePicture(Activity a){
		//TODO 调用摄像机获取图像
		try {
			setUpPhotoFile("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
		intent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
		intent.putExtra("return-data", false);
		a.startActivityForResult(intent, TAKE_PICTURE);
	}
	/**
	 * 调用系统的剪切功能剪切图像
	 * @param a Activity
	 * @param outputX 剪切的宽度
	 * @param outputY 剪切的高度
	 */
	public static void cropImageUri(Activity a,int outputX, int outputY){
		//TODO 剪切图像
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(tempFileUri, "image/*");
		intent.putExtra("crop", "true");
		if(outputX != 0||outputY != 0){
			if(outputX == outputY){
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
			}
			intent.putExtra("outputX", outputX);
			intent.putExtra("outputY", outputY);
		}
		intent.putExtra("scale", true);
		try {
			setUpPhotoFile("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		a.startActivityForResult(intent, CROP_PICTURE);
	}

	private static File createImageFile(String name) throws IOException {
		// Create an image file name
		if(TextUtils.isEmpty(name)){
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
			File albumF = albumDir;
			File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
			return imageF;
		}else{
			String timeStamp = "";
			String imageFileName = name + timeStamp + "_";
			File albumF = albumDir;
			File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
			return imageF;
		}
	}
	public static File setUpPhotoFile(String name) throws IOException {		
		tempFile = createImageFile(name);
		tempFileUri = Uri.fromFile(tempFile);
		return tempFile;
	}

}
