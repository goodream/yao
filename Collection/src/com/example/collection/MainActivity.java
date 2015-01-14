package com.example.collection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.collection.utils.CropUtil;
import com.collection.utils.ImageUtil;


public class MainActivity extends Activity implements OnClickListener{

	private ImageView iv_show;
	private PopupWindow popWindow;
	
	private Button btn_photo;
	private Button btn_camera;
	private Button btn_cancel;
	private LinearLayout layout_pop_dismiss_poph;
	private LinearLayout layout_below;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_show = (ImageView)findViewById(R.id.main_iv_show);
        iv_show.setOnClickListener(this);
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CropUtil.TAKE_PICTURE:
				CropUtil.cropImageUri(this,200,200);
				break;
			case CropUtil.CROP_PICTURE:
				String imagePath = CropUtil.tempFileUri.toString();
				System.out.println(imagePath);				
				iv_show.setImageBitmap(ImageUtil.getBitmapToSize(CropUtil.tempFile.getPath(), 300));	
				break;
			case CropUtil.CHOOSE_PICTURE:
				try {
					InputStream inputStream = getContentResolver().openInputStream(data.getData());
					FileOutputStream fileOutputStream = new FileOutputStream(CropUtil.tempFile);
					ImageUtil.copyStream(inputStream, fileOutputStream);
					fileOutputStream.flush();
					fileOutputStream.close();
					inputStream.close();
					CropUtil.cropImageUri(this,200,200);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
		if (popWindow != null) {
			popWindow.dismiss();
			popWindow = null;
		}
	}
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btn_headericon_photo:
				// 判断图库是否有图
				CropUtil.chooseImage(this);
				break;	
			case R.id.btn_headericon_camera:
				// 判断摄像机是否可用？
				CropUtil.takePicture(this);
				break;
			case R.id.main_iv_show:
				PopupWindowSet(v);
				break;
			default :
				break;
		}
		
	}
	private void PopupWindowSet(View v) {
		String[] dirs = new String[]{"BXGNEW","ICON","COLLECTION"};
		File file = null;
		if(ImageUtil.isMnueExists(Environment.getExternalStorageDirectory(), dirs)){
			file = ImageUtil.createDirs(Environment.getExternalStorageDirectory(), dirs);
		}		
		CropUtil.albumDir = file;
		if (popWindow != null) {
			popWindow.dismiss();
			popWindow = null;
		}
		if (popWindow == null) {
			View popLayout = LayoutInflater.from(getApplicationContext())
					.inflate(R.layout.icon_change, null);
			btn_camera = (Button) popLayout
					.findViewById(R.id.btn_headericon_camera);
			btn_photo = (Button) popLayout
					.findViewById(R.id.btn_headericon_photo);
			btn_cancel = (Button) popLayout
					.findViewById(R.id.btn_headericon_cancel);

			layout_pop_dismiss_poph = (LinearLayout) popLayout
					.findViewById(R.id.layout_pop_dismiss_poph);
			layout_below = (LinearLayout) popLayout
					.findViewById(R.id.layout_below);

			btn_cancel.setOnClickListener(this);
			btn_camera.setOnClickListener(this);
			btn_photo.setOnClickListener(this);
			layout_pop_dismiss_poph.setOnClickListener(this);

			popWindow = new PopupWindow(popLayout, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, true);
			popWindow.setTouchable(true);

			ColorDrawable dw = new ColorDrawable(99000000);
			popWindow.setBackgroundDrawable(dw);
			popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
		}
	}
	
	
}
