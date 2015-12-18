package com.hangjiang.puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.hangjiang.puzzle.adapter.GameItemGridAdapter;
import com.hangjiang.puzzle.model.ItemBean;
import com.hangjiang.puzzle.util.GameUtil;
import com.hangjiang.puzzle.util.ImageUtil;
import com.hangjiang.puzzle.util.ScreenUtil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements OnClickListener{

	public static final String TAG = "hangjiang";
	public static Bitmap mLastBitmap;
	public static int TYPE = 2;
	private static final int TIME_CODE = 1;
	private static int TIME_COUNT = 0;

	private int mResId;
	private String mPicPath;
	private Bitmap mPicSelected;
	private Button mBtnRestart;
	private ImageView mIvPic;
	private boolean mIsShowImg;
	private GridView mGvMainGame;
	private TextView mTvTime;
	private boolean isWait = false;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TIME_CODE:
				TIME_COUNT += 1;
				mTvTime.setText("" + TIME_COUNT);
				
				break;

			default:
				break;
			}
		};
	};
	private List<Bitmap> mBitmapItemList = new ArrayList<Bitmap>();
	private GameItemGridAdapter mAdapter;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private TextView mTvBestScore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		Bitmap picSelectedTemp;
		mResId = getIntent().getExtras().getInt("picId");
		mPicPath = getIntent().getExtras().getString("mPicPath") == null ? 
				getIntent().getExtras().getString("picPath") : getIntent().getExtras()
				.getString("mPicPath");
		if (mResId != 0) {
			picSelectedTemp = BitmapFactory.decodeResource(getResources(),
					mResId);
		} else {
			picSelectedTemp = BitmapFactory.decodeFile(mPicPath);
			picSelectedTemp = ImageUtil.rotateImage(picSelectedTemp, mPicPath);
		}
		TYPE = getIntent().getExtras().getInt("mType", 2);
		resizeImage(picSelectedTemp);

		initView();
		initGame();
		
		mGvMainGame.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(GameUtil.isMove(position)){
					GameUtil.swapItems(GameUtil.mItemBeans.get(position), GameUtil.mBlankItemBean);
					flushData();
					mAdapter.notifyDataSetChanged();
					if(GameUtil.isSuccess()){
						mTimer.cancel();
						mTimerTask.cancel();
						SharedPreferences sp = getSharedPreferences("Best_Score", MODE_PRIVATE);
						int score = sp.getInt("Type" + TYPE, 65535);
						if(TIME_COUNT < score){
							mTvBestScore.setText(TIME_COUNT + "");
							Editor editor = sp.edit();
							editor.putInt("Type" + TYPE, TIME_COUNT);
							editor.commit();
						}
						
						flushData();
						mBitmapItemList.remove(TYPE * TYPE - 1);
						mBitmapItemList.add(mLastBitmap);
						mAdapter.notifyDataSetChanged();
						
						Toast.makeText(GameActivity.this, "拼图完成，时间: " + TIME_COUNT + "秒", Toast.LENGTH_LONG).show();
						mGvMainGame.setEnabled(false);
					}
				}
			}
		});
	}

	protected void flushData() {
		mBitmapItemList.clear();
		for(ItemBean temp : GameUtil.mItemBeans){
			mBitmapItemList.add(temp.getmBitmap());
		}
	}

	private void initGame() {
		new ImageUtil().createInitBitmaps(TYPE, mPicSelected, this);
		GameUtil.getCreateRandomItem();
		
		for(ItemBean tmp : GameUtil.mItemBeans){
			mBitmapItemList.add(tmp.getmBitmap());
		}
		
		mAdapter = new GameItemGridAdapter(this, mBitmapItemList);
		mGvMainGame.setAdapter(mAdapter);
		
		beginTimer();
	}

	private void beginTimer() {
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		};
		mTimer.schedule(mTimerTask, 0, 1000);
	}

	private void initView() {
		mBtnRestart = (Button) findViewById(R.id.btn_restart);
		mBtnRestart.setOnClickListener(this);
		
		mIvPic = (ImageView) findViewById(R.id.iv_main_img);
		mIvPic.setOnClickListener(this);
		mIvPic.setImageBitmap(mPicSelected);
		mIsShowImg = false;

		mGvMainGame = (GridView) findViewById(R.id.gv_game);
		LayoutParams gridParams = new LinearLayout.LayoutParams(
                mPicSelected.getWidth(),
                mPicSelected.getHeight() + 20);
		mGvMainGame.setLayoutParams(gridParams);
		mGvMainGame.setNumColumns(TYPE);
		mGvMainGame.setHorizontalSpacing(0);
		mGvMainGame.setVerticalSpacing(0);
		
		mTvTime = (TextView)findViewById(R.id.tv_time);
		mTvBestScore = (TextView)findViewById(R.id.tv_bestscore);
		
		SharedPreferences sp = getSharedPreferences("Best_Score", MODE_PRIVATE);
		int score = sp.getInt("Type" + TYPE, 65535);
		
		Log.d(TAG, "score: " + score);
		mTvBestScore.setText(score + "");
	}

	private void resizeImage(Bitmap picSelectedTemp) {
		int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
		int screenHeight = ScreenUtil.getScreenSize(this).heightPixels;

		Log.d(TAG, "screenWidth: " + screenWidth + " screenHeight: "
				+ screenHeight);

		mPicSelected = new ImageUtil().resizeBitmap(screenWidth * 0.8f,
				screenHeight * 0.6f, picSelectedTemp);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_main_img:
			
			break;
			
		case R.id.btn_restart:
			clearConfig();
			initGame();
			flushData();
			
			mTvTime.setText(TIME_COUNT + "");
			mAdapter.notifyDataSetChanged();
			mGvMainGame.setEnabled(true);
			
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		mTimer.cancel();
		mTimerTask.cancel();
		isWait = true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(isWait){
			beginTimer();
			isWait = false;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		clearConfig();
	}

	private void clearConfig() {
		GameUtil.mItemBeans.clear();
		mTimer.cancel();
		mTimerTask.cancel();
		TIME_COUNT = 0;
	}
}
