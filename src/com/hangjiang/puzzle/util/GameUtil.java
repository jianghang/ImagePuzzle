package com.hangjiang.puzzle.util;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.hangjiang.puzzle.GameActivity;
import com.hangjiang.puzzle.model.ItemBean;

public class GameUtil {

	public static final String TAG = "GameUtil";
	public static List<ItemBean> mItemBeans = new ArrayList<ItemBean>();
	public static ItemBean mBlankItemBean = new ItemBean();

	//生成随机的图片
	public static void getCreateRandomItem() {
		int index = 0;
		for (int i = 0; i < mItemBeans.size(); i++) {
			index = (int) (Math.random() * GameActivity.TYPE * GameActivity.TYPE);

			Log.d(TAG, "index: " + index + " RandomNumber: " + Math.random());

			swapItems(mItemBeans.get(index), mBlankItemBean);
		}
		List<Integer> data = new ArrayList<Integer>();
		for (int i = 0; i < mItemBeans.size(); i++) {
			data.add(mItemBeans.get(i).getmBitmapId());
		}

		if (canSolve(data)) {
			return;
		} else {
			getCreateRandomItem();
		}
	}

	//检测随机出来的图片是否可解，使用倒置变量值得算法
	private static boolean canSolve(List<Integer> data) {
		int blankId = GameUtil.mBlankItemBean.getmItemId();
		if (data.size() % 2 == 1) {
			return getInversions(data) % 2 == 0;
		} else {
			if (((blankId - 1) / GameActivity.TYPE) % 2 == 1) {
				return getInversions(data) % 2 == 0;
			} else {
				return getInversions(data) % 2 == 1;
			}
		}
	}

	private static int getInversions(List<Integer> data) {
		int inversion = 0;
		int inversionCount = 0;

		for (int i = 0; i < data.size(); i++) {
			for (int j = i + 1; j < data.size(); j++) {
				int index = data.get(i);
				if (data.get(j) != 0 && data.get(j) < index) {
					inversionCount++;
				}
			}
			inversion += inversionCount;
			inversionCount = 0;
		}

		return inversion;
	}

	public static void swapItems(ItemBean item, ItemBean blank) {
		ItemBean tempItemBean = new ItemBean();

		tempItemBean.setmBitmapId(item.getmBitmapId());
		item.setmBitmapId(blank.getmBitmapId());
		blank.setmBitmapId(tempItemBean.getmBitmapId());

		tempItemBean.setmBitmap(item.getmBitmap());
		item.setmBitmap(blank.getmBitmap());
		blank.setmBitmap(tempItemBean.getmBitmap());

		GameUtil.mBlankItemBean = item;
	}

	public static boolean isMove(int position) {
		int type = GameActivity.TYPE;
		int blankId = GameUtil.mBlankItemBean.getmItemId() - 1;

		if (Math.abs(blankId - position) == type) {
			return true;
		}

		if ((blankId / type == position / type)//检测空白格和所触碰的格子是否在同一行
				&& Math.abs(blankId - position) == 1) {
			return true;
		}

		return false;
	}

	public static boolean isSuccess() {
		for (ItemBean temp : mItemBeans) {
			if (temp.getmBitmapId() != 0
					&& (temp.getmItemId() == temp.getmBitmapId())) {
				continue;
			} else if (temp.getmBitmapId() == 0
					&& temp.getmItemId() == GameActivity.TYPE
							* GameActivity.TYPE) {
				continue;
			}else{
				return false;
			}
		}

		return true;
	}
}
