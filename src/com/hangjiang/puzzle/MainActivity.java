package com.hangjiang.puzzle;

import java.io.File;
import java.util.ArrayList;

import com.hangjiang.puzzle.adapter.ImageGridAdapter;
import com.hangjiang.puzzle.util.ScreenUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**

* <p>Title: MainActivity</p>

* <p>Description: 此游戏的设计方法，封装一个ItemBean类，初始化时纪录每个拼图小图片的ItemId、BitmapId、Bitmap，之后
* 随机打乱小图片的BitmapId和Bitmap,
* 游戏玩的过程中不断交换BitmapId、Bitmap,完成的条件是ItemId和BitmapId相等</p>

* <p>Company: LTGames</p>

* @author    hang

* @date       2015年12月19日

*/
public class MainActivity extends Activity implements OnClickListener {

	private static final int RESULT_IMAGE = 100;//本地图库
	private static final int RESULT_CAMERA = 200;//相机
	private static final String IMAGE_TYPE = "image/*";
	public static String TEMP_IMAGE_PATH;//图片路径
	
	private GridView mGridList;
	private int[] mResPicId;
	private ArrayList<Bitmap> mPicList;
	private TextView tvSelect;
	private String[] mSelectType = new String[] { "2x2", "3x3", "4x4","6x6"};
	private String[] mCustomItems = new String[] { "本地图册", "相机拍照" };
	protected int mType = 2;
	private TextView tvSelectMain;
	private int mScreenWidth;
	private int mScreenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TEMP_IMAGE_PATH = Environment.getExternalStorageDirectory().getPath()
				+ "/temp.png";

		initData();
		mGridList = (GridView) findViewById(R.id.gv_main_pic_list);
		mGridList.setAdapter(new ImageGridAdapter(this, mPicList));
		mGridList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == mResPicId.length - 1) {
					showDialogCustom();
				} else {
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, GameActivity.class);
					intent.putExtra("picId", mResPicId[position]);
					intent.putExtra("mType", mType);
					startActivity(intent);
				}
			}
		});

		tvSelect = (TextView) findViewById(R.id.tv_main_type_select);
		tvSelect.setOnClickListener(this);
		tvSelectMain = (TextView)findViewById(R.id.tv_main_select);
		tvSelectMain.setOnClickListener(this);
	}

	private void initData() {
		
		//初始化图片数据
		mPicList = new ArrayList<Bitmap>();
		mResPicId = new int[] { R.drawable.pic1, R.drawable.pic2,
				R.drawable.pic3, R.drawable.pic4, R.drawable.pic5,
				R.drawable.pic6, R.drawable.pic7, R.drawable.pic8,
				R.drawable.pic9, R.drawable.pic10, R.drawable.pic11,
				R.drawable.pic12, R.drawable.pic13, R.drawable.pic14,
				R.drawable.pic15, R.drawable.add_128 };
		Bitmap[] bitmaps = new Bitmap[mResPicId.length];
		for (int i = 0; i < bitmaps.length; i++) {
			bitmaps[i] = BitmapFactory.decodeResource(getResources(),
					mResPicId[i]);
			mPicList.add(bitmaps[i]);
		}
		
		SharedPreferences sp = getSharedPreferences("Best_Score", MODE_PRIVATE);
		if(!sp.contains("Type2")){
			Editor editor = sp.edit();
			editor.putInt("Type" + 2, 65535);
			editor.putInt("Type" + 3, 65535);
			editor.putInt("Type" + 4, 65535);
			editor.putInt("Type" + 6, 65535);
			editor.commit();
		}
		if(!sp.contains("isShowOperateMain")){
			Editor editor = sp.edit();
			editor.putBoolean("isShowOperateMain", false);
			editor.commit();
		}
		
		mScreenWidth = ScreenUtil.getScreenSize(this).widthPixels;
		mScreenHeight = ScreenUtil.getScreenSize(this).heightPixels;
	}

	private void showSelectDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("选择难度：");
		builder.setItems(mSelectType, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (0 == which) {
					mType = 2;
					tvSelect.setText("2 X 2");
				} else if (1 == which) {
					mType = 3;
					tvSelect.setText("3 X 3");
				} else if (2 == which) {
					mType = 4;
					tvSelect.setText("4 X 4");
				} else if (3 == which){
					mType = 6;
					tvSelect.setText("6 X 6");
				}
			}
		});
		builder.create().show();
	}

	private void showDialogCustom() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("选择：");
		builder.setItems(mCustomItems, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (0 == which) {
					// 本地图册
					Intent intent = new Intent(Intent.ACTION_PICK, null);
					intent.putExtra("outputX", (int)mScreenWidth * 0.9f);  
					intent.putExtra("outputY", (int)mScreenHeight * 0.7f);  
					intent.putExtra("scale", true);
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							IMAGE_TYPE);
					startActivityForResult(intent, RESULT_IMAGE);
				} else if (1 == which) {
					// 系统相机
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					Uri photoUri = Uri.fromFile(new File(TEMP_IMAGE_PATH));
					intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
					startActivityForResult(intent, RESULT_CAMERA);
				}
			}
		});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == RESULT_IMAGE && data != null) {
				//对本地图库返回的图片进行处理
				Cursor cursor = this.getContentResolver().query(data.getData(),
						null, null, null, null);
				cursor.moveToFirst();
				String imagePath = cursor.getString(cursor
						.getColumnIndex("_data"));
				Intent intent = new Intent(MainActivity.this, GameActivity.class);
				intent.putExtra("picPath", imagePath);
				intent.putExtra("mType", mType);
				cursor.close();
				startActivity(intent);
			} else if (requestCode == RESULT_CAMERA) {
				//对相机返回的图片进行处理
				Intent intent = new Intent(MainActivity.this, GameActivity.class);
				intent.putExtra("mPicPath", TEMP_IMAGE_PATH);
				intent.putExtra("mType", mType);
				startActivity(intent);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_main_select:
			showSelectDialog();
			
			break;
			
		case R.id.tv_main_type_select:
			showSelectDialog();
			
			break;

		default:
			break;
		}
	}
}
