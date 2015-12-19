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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements OnClickListener {

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
	private Handler handler = new Handler() {
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
	private Button mBtnOperateUp;
	private Button mBtnOperateDown;
	private Button mBtnOperateLeft;
	private Button mBtnOperateRight;
	private RelativeLayout mRlOperateMain;
	private boolean isShowOperateMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		Bitmap picSelectedTemp;
		mResId = getIntent().getExtras().getInt("picId");
		mPicPath = getIntent().getExtras().getString("mPicPath") == null ? getIntent()
				.getExtras().getString("picPath") : getIntent().getExtras()
				.getString("mPicPath");
		if (mResId != 0) {
			picSelectedTemp = BitmapFactory.decodeResource(getResources(),
					mResId);
		} else {
			picSelectedTemp = BitmapFactory.decodeFile(mPicPath);
			picSelectedTemp = ImageUtil.rotateImage(picSelectedTemp, mPicPath);
		}
		TYPE = getIntent().getExtras().getInt("mType", 2);
		resizeImage(picSelectedTemp);//按照指定的宽度缩放图片

		initView();
		initGame();

		mGvMainGame.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (GameUtil.isMove(position)) {
					GameUtil.swapItems(GameUtil.mItemBeans.get(position),
							GameUtil.mBlankItemBean);
					flushData();
					mAdapter.notifyDataSetChanged();
					isFinish();
				}
			}
		});
	}

	// 刷新列表中的数据
	protected void flushData() {
		mBitmapItemList.clear();
		for (ItemBean temp : GameUtil.mItemBeans) {
			mBitmapItemList.add(temp.getmBitmap());
		}
	}

	private void initGame() {
		new ImageUtil().createInitBitmaps(TYPE, mPicSelected, this);
		GameUtil.getCreateRandomItem();

		for (ItemBean tmp : GameUtil.mItemBeans) {
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
		mIvPic.setClickable(false);
		mIvPic.setImageBitmap(mPicSelected);
		mIsShowImg = false;

		mGvMainGame = (GridView) findViewById(R.id.gv_game);
		LayoutParams gridParams = new LinearLayout.LayoutParams(
				mPicSelected.getWidth(), mPicSelected.getHeight());
		Log.d(TAG, "gridParamsWdith: " + gridParams.width
				+ " gridParamsHeight: " + gridParams.height);

		mGvMainGame.setLayoutParams(gridParams);
		mGvMainGame.setNumColumns(TYPE);
		mGvMainGame.setHorizontalSpacing(0);
		mGvMainGame.setVerticalSpacing(0);

		mTvTime = (TextView) findViewById(R.id.tv_time);
		mTvBestScore = (TextView) findViewById(R.id.tv_bestscore);

		SharedPreferences sp = getSharedPreferences("Best_Score", MODE_PRIVATE);
		int score = sp.getInt("Type" + TYPE, 65535);

		Log.d(TAG, "score: " + score);
		mTvBestScore.setText(score + "");

		mBtnOperateUp = (Button) findViewById(R.id.btn_operate_up);
		mBtnOperateDown = (Button) findViewById(R.id.btn_operate_down);
		mBtnOperateLeft = (Button) findViewById(R.id.btn_operate_left);
		mBtnOperateRight = (Button) findViewById(R.id.btn_operate_right);
		mBtnOperateUp.setOnClickListener(this);
		mBtnOperateDown.setOnClickListener(this);
		mBtnOperateLeft.setOnClickListener(this);
		mBtnOperateRight.setOnClickListener(this);
		mRlOperateMain = (RelativeLayout) findViewById(R.id.rl_operate_main);
		isShowOperateMain = sp.getBoolean("isShowOperateMain", false);
		if (!isShowOperateMain) {
			mRlOperateMain.setVisibility(RelativeLayout.GONE);
		}
	}

	private void resizeImage(Bitmap picSelectedTemp) {
		int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
		int screenHeight = ScreenUtil.getScreenSize(this).heightPixels;

		mPicSelected = new ImageUtil().resizeBitmap(screenWidth * 0.9f,
				screenHeight * 0.7f, picSelectedTemp);

		Log.d(TAG,"screenWidth: " + screenWidth + " screenHeight: "
						+ screenHeight + " mPicSelectedWidth: "
						+ mPicSelected.getWidth() + " mPicSelectedHeight: "
						+ mPicSelected.getHeight());
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

			mBtnOperateDown.setClickable(true);
			mBtnOperateRight.setClickable(true);
			mTvTime.setText(TIME_COUNT + "");
			mAdapter.notifyDataSetChanged();
			mGvMainGame.setEnabled(true);

			break;

		case R.id.btn_operate_up:
			int itemPosUp = GameUtil.mBlankItemBean.getmItemId() - 1 + TYPE;
			if (itemPosUp < TYPE * TYPE) {
				GameUtil.swapItems(GameUtil.mItemBeans.get(itemPosUp),
						GameUtil.mBlankItemBean);
				flushData();
				mAdapter.notifyDataSetChanged();
				isFinish();
			}

			break;

		case R.id.btn_operate_down:
			int blankPos = GameUtil.mBlankItemBean.getmItemId() - 1;
			int itemPosDown = blankPos - TYPE;
			if (itemPosDown >= 0) {
				GameUtil.swapItems(GameUtil.mItemBeans.get(itemPosDown),
						GameUtil.mBlankItemBean);
				flushData();
				mAdapter.notifyDataSetChanged();
				isFinish();
			}

			break;

		case R.id.btn_operate_left:
			int blankItemLeft = GameUtil.mBlankItemBean.getmItemId() - 1;
			int itemPosLeft = blankItemLeft + 1;
			if ((itemPosLeft / TYPE == blankItemLeft / TYPE)
					&& (itemPosLeft < TYPE * TYPE)) {
				GameUtil.swapItems(GameUtil.mItemBeans.get(itemPosLeft),
						GameUtil.mBlankItemBean);
				flushData();
				mAdapter.notifyDataSetChanged();
				isFinish();
			}

			break;

		case R.id.btn_operate_right:
			int blankItemRight = GameUtil.mBlankItemBean.getmItemId() - 1;
			int itemPosRight = blankItemRight - 1;
			if ((itemPosRight / TYPE == blankItemRight / TYPE)
					&& (itemPosRight >= 0)) {
				GameUtil.swapItems(GameUtil.mItemBeans.get(itemPosRight),
						GameUtil.mBlankItemBean);
				flushData();
				mAdapter.notifyDataSetChanged();
				isFinish();
			}

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

		if (isWait) {
			beginTimer();
			isWait = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		clearConfig();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.setting:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			if (!isShowOperateMain) {

				builder.setTitle("提示");
				builder.setMessage("是否开启操作小键盘？");
				builder.setCancelable(false);
				builder.setPositiveButton("是",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								isShowOperateMain = true;
								SharedPreferences sp = getSharedPreferences(
										"Best_Score", MODE_PRIVATE);
								Editor editor = sp.edit();
								editor.putBoolean("isShowOperateMain",
										isShowOperateMain);
								editor.commit();
								mRlOperateMain
										.setVisibility(RelativeLayout.VISIBLE);
							}
						});
				builder.setNegativeButton("否",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				builder.show();
			} else {
				builder.setTitle("提示");
				builder.setMessage("是否关闭操作小键盘？");
				builder.setCancelable(false);
				builder.setPositiveButton("是",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								isShowOperateMain = false;
								SharedPreferences sp = getSharedPreferences(
										"Best_Score", MODE_PRIVATE);
								Editor editor = sp.edit();
								editor.putBoolean("isShowOperateMain",
										isShowOperateMain);
								editor.commit();
								mRlOperateMain
										.setVisibility(RelativeLayout.GONE);
							}
						});
				builder.setNegativeButton("否",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								return;
							}
						});
				builder.show();
			}

			break;

		default:
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void clearConfig() {
		GameUtil.mItemBeans.clear();
		mTimer.cancel();
		mTimerTask.cancel();
		TIME_COUNT = 0;
	}

	private void isFinish() {
		if (GameUtil.isSuccess()) {
			mTimer.cancel();
			mTimerTask.cancel();
			SharedPreferences sp = getSharedPreferences("Best_Score",
					MODE_PRIVATE);
			int score = sp.getInt("Type" + TYPE, 65535);
			if (TIME_COUNT < score) {
				mTvBestScore.setText(TIME_COUNT + "");
				Editor editor = sp.edit();
				editor.putInt("Type" + TYPE, TIME_COUNT);
				editor.commit();
			}

			flushData();
			mBitmapItemList.remove(TYPE * TYPE - 1);
			mBitmapItemList.add(mLastBitmap);
			mAdapter.notifyDataSetChanged();

			Toast.makeText(GameActivity.this, "拼图完成，时间: " + TIME_COUNT + "秒",
					Toast.LENGTH_LONG).show();
			mGvMainGame.setEnabled(false);

			mBtnOperateDown.setClickable(false);
			mBtnOperateRight.setClickable(false);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		Log.d(TAG, "GvMainWdith: " + mGvMainGame.getWidth() + " GvMainHeight: "
				+ mGvMainGame.getHeight());
	}
}
