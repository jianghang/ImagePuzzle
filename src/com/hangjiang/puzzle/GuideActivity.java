package com.hangjiang.puzzle;

import com.hangjiang.puzzle.util.ScreenUtil;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class GuideActivity extends Activity {
	
	private static final String TAG = "GuideActivity";

	private ImageView ivUp;
	private ImageView ivDown;
	private ImageView ivLeft;
	private ImageView ivRight;
	private TextView tvGuide;
	private int mScreenWidth;
	private int mScreenHeight;
	private double mMoveX;
	private double mMoveY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);
		
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		Point point = new Point();
		wm.getDefaultDisplay().getSize(point);
		mScreenWidth = point.x;
		mScreenHeight = point.y;

		if(mScreenWidth == 1080){
			mMoveX = mScreenWidth / 2 * 0.42f;
			mMoveY = mScreenHeight / 2 * 0.62f;
		}else{
			mMoveX = mScreenWidth / 2 * 0.44f;
			mMoveY = mScreenHeight / 2 * 0.64f;
		}
		
		
		Log.d(TAG, "mMoveX: " + mMoveX + "mScreenWidth: " + mScreenWidth);
		
		ivUp = (ImageView) findViewById(R.id.iv_up);
		ivDown = (ImageView)findViewById(R.id.iv_down);
		ivLeft = (ImageView)findViewById(R.id.iv_left);
		ivRight = (ImageView)findViewById(R.id.iv_right);
		tvGuide = (TextView)findViewById(R.id.guide_tv);
		
		AnimationSet asUp = createAnimation(0, 0, 0, (float)mMoveY, 1000);
		ivUp.startAnimation(asUp);
		
		AnimationSet asDown = createAnimation(0, 0, 0, (float)-mMoveY, 1000);
		ivDown.startAnimation(asDown);
		
		AnimationSet asLeft = createAnimation(0, (float)mMoveX, 0, 0, 1000);
		ivLeft.startAnimation(asLeft);
		
		AnimationSet asRight = createAnimation(0, (float)-mMoveX, 0, 0, 1000);
		ivRight.startAnimation(asRight);
		
		AlphaAnimation alpha = new AlphaAnimation(0, (float) 1.0);
		alpha.setDuration(1500);
		alpha.setFillAfter(true);
		tvGuide.startAnimation(alpha);
		
		alpha.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	private AnimationSet createAnimation(float fromX,float toX,float fromY,float toY,long duration){
		AnimationSet as = new AnimationSet(true);
		as.setDuration(duration);
		as.setFillAfter(true);
		
		TranslateAnimation ta = new TranslateAnimation(fromX, toX, fromY, toY);
		ta.setDuration(duration);
		ta.setFillAfter(true);
		as.addAnimation(ta);
		
		AlphaAnimation aa = new AlphaAnimation(0, 1);
		aa.setDuration(duration);
		aa.setFillAfter(true);
		as.addAnimation(aa);
		
		return as;
	}
}
