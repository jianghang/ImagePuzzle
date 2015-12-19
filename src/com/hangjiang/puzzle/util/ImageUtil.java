package com.hangjiang.puzzle.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hangjiang.puzzle.GameActivity;
import com.hangjiang.puzzle.R;
import com.hangjiang.puzzle.model.ItemBean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class ImageUtil {
	private ItemBean itemBean;

	/**   
	* @Title: createInitBitmaps   
	* @Description: 生成游戏界面所需要的小图片
	* @param type
	* @param picSelected
	* @param context      
	* @return: void      
	* @throws   
	*/  
	public void createInitBitmaps(int type, Bitmap picSelected, Context context) {
		Bitmap bitmap = null;
		List<Bitmap> bitmapItems = new ArrayList<Bitmap>();

		int bpItemWidth = picSelected.getWidth() / type;
		int bpItemHeight = picSelected.getHeight() / type;

		//根据游戏难度切割图片，同时打上标签
		for (int i = 1; i <= type; i++) {
			for (int j = 1; j <= type; j++) {
				bitmap = Bitmap.createBitmap(picSelected,
						(j - 1) * bpItemWidth, (i - 1) * bpItemHeight,
						bpItemWidth, bpItemHeight);
				bitmapItems.add(bitmap);
				itemBean = new ItemBean((i - 1) * type + j, (i - 1) * type + j,
						bitmap);
				GameUtil.mItemBeans.add(itemBean);
			}
		}

		//移除最后一张图片，并同时添加空白图片
		GameActivity.mLastBitmap = bitmapItems.get(type * type - 1);
		bitmapItems.remove(type * type - 1);
		GameUtil.mItemBeans.remove(type * type - 1);
		Bitmap blankBitmap = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.blank);
		blankBitmap = Bitmap.createBitmap(blankBitmap, 0, 0, bpItemWidth,
				bpItemHeight);

		bitmapItems.add(blankBitmap);
		GameUtil.mItemBeans.add(new ItemBean(type * type, 0, blankBitmap));
		GameUtil.mBlankItemBean = GameUtil.mItemBeans.get(type * type - 1);
	}

	//缩放到指定的宽度
	public Bitmap resizeBitmap(float newWidth, float newHeight, Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.setScale(newWidth / bitmap.getWidth(),
				newHeight / bitmap.getHeight());
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		
		return newBitmap;
	}
	
	//检测从相册加载的图片是否是正方向的，如果不是则调整
	public static Bitmap rotateImage(Bitmap bm, String filepath) {
		try {
			ExifInterface exifInterface = new ExifInterface(filepath);
			int result = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);
			int rotate = 0;
			switch (result) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;

				break;

			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;

				break;

			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;

				break;

			default:
				break;
			}

			if (rotate > 0) {
				Matrix matrix = new Matrix();
				matrix.setRotate(rotate);
				Bitmap rotateBitmap = Bitmap.createBitmap(bm, 0, 0,
						bm.getWidth(), bm.getHeight(), matrix, true);
				if (rotateBitmap != null) {
					bm.recycle();
					bm = rotateBitmap;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bm;
	}
}
