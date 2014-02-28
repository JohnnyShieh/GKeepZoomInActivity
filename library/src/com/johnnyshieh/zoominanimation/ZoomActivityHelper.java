/*
 * Copyright (C) 2013 JohnnyShieh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.johnnyshieh.zoominanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

/**
 * @ClassName:  ZoomActivityHelper
 * @Description:help to create ZoomIn and ZoomOut animations
 * @author  JohnnyShieh
 * @date    December 26, 2013
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ZoomActivityHelper implements View.OnLayoutChangeListener {

	private static int sAnimationTime = -1;
	private static int sCloseAnimationTime = -1;
	private static int sShortAnimationTime = -1;
	private final Activity mActivity;
	private final int mAnimationDirection;
	private float mAnimationHeightScaleFactor = 1.0F;
	private float mAnimationWidthScaleFactor = 1.0F;
	private AnimatorSet mCurrentAnimation;
	private final Interpolator mDecelerateQuadInterpolator;
	private boolean mAnimatedIn;
	private boolean mFinished;
	private boolean mFinishing;
	private boolean mOpened;
	private boolean hasActionBar;
	private final Handler mHandler = new Handler();
	private final BitmapStorageInterface mStorage;
	private final ImageView mImageView; // the last activity's view convert to
										// bitmap, then use imageView to show
										// animation
	private final View mTargetView; // the current activity's view which need to
									// show zoomIn animation

	// the following four param stand for last view's Rect
	private int mStartHeight; // last view's start height
	private int mStartWidth; // last view's start width
	private int mStartX; // last view's start X
	private int mStartY; // last view's start Y
	private float mTargetStartScaleX;
	private float mTargetStartScaleY;
	private int mTargetStartX;
	private int mTargetStartY;

	public static final int SCALE_DIRECT = 1;

	public ZoomActivityHelper(Bundle paramBundle, ImageView paramImageView,
			View paramView, Activity paramActivity,
			BitmapStorageInterface bitmapStorage) {
		this.mStorage = bitmapStorage;
		this.mStartX = paramBundle.getInt("startX");
		this.mStartY = paramBundle.getInt("startY");
		this.mStartWidth = this.mStorage.getBitmap().getWidth();
		this.mStartHeight = this.mStorage.getBitmap().getHeight();

		this.mAnimationDirection = paramBundle.getInt("animDirection",
				SCALE_DIRECT);
		this.mAnimatedIn = paramBundle.getBoolean("animationRunYet", false);
		this.hasActionBar = paramBundle.getBoolean("hasActionBar", false);

		this.mImageView = paramImageView;
		this.mImageView.setVisibility(View.GONE);
		this.mTargetView = paramView;
		this.mTargetView.setVisibility(View.INVISIBLE);
		this.mTargetView.addOnLayoutChangeListener(this);

		this.mActivity = paramActivity;

		this.mDecelerateQuadInterpolator = new DecelerateInterpolator();
		if (-1 == sAnimationTime) {
			sAnimationTime = this.mActivity.getResources().getInteger(
					R.integer.activity_custom_animation_duration);
			sCloseAnimationTime = this.mActivity.getResources().getInteger(
					R.integer.activity_custom_animation_close_duration);
			sShortAnimationTime = this.mActivity.getResources().getInteger(
					R.integer.activity_custom_animation_duration_short);
		}
	}

	/**
	 * 
	 * @Description: get the starting animation's bounds
	 * @return Rect the starting Rect
	 * 
	 */
	private Rect getStartingBounds() {
		return new Rect(this.mStartX, this.mStartY, this.mStartX
				+ this.mStartWidth, this.mStartY + this.mStartHeight);
	}

	/**
	 * 
	 * @Description: get the ending animation's bounds
	 * @return Rect the ending Rect
	 * 
	 */
	private Rect getEndingBounds() {
		Display localDisplay = this.mActivity.getWindowManager()
				.getDefaultDisplay();
		Point localPoint = new Point();
		localDisplay.getSize(localPoint);
		return new Rect(0, 0, localPoint.x, localPoint.y);
	}

	private AnimatorSet getTargetOpenAnimations() {
		Rect localRect1 = getStartingBounds();
		Rect localRect2 = getEndingBounds();

		this.mTargetStartX = localRect1.left;
		this.mTargetStartY = localRect1.top;
		this.mTargetStartScaleX = (float) localRect1.width()
				/ localRect2.width();
		this.mTargetStartScaleY = (float) localRect1.height()
				/ localRect2.height();

		ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(
				this.mTargetView, View.X, new float[] { this.mTargetStartX,
						localRect2.left });
		ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(
				this.mTargetView, View.Y, new float[] { this.mTargetStartY,
						localRect2.top });
		ObjectAnimator localObjectAnimator3 = ObjectAnimator.ofFloat(
				this.mTargetView, View.SCALE_X, new float[] {
						this.mTargetStartScaleX, 1.0F });
		ObjectAnimator localObjectAnimator4 = ObjectAnimator.ofFloat(
				this.mTargetView, View.SCALE_Y, new float[] {
						this.mTargetStartScaleY, 1.0F });
		AnimatorSet localAnimatorSet = new AnimatorSet();

		switch (this.mAnimationDirection) {
		case SCALE_DIRECT:
			this.mTargetView.setPivotY(0.0F);
			break;
		default:
			this.mTargetView.setPivotX(0.0F);
			this.mTargetView.setPivotY(0.0F);
		}

		localAnimatorSet.playTogether(new Animator[] { localObjectAnimator1,
				localObjectAnimator2, localObjectAnimator3,
				localObjectAnimator4 });
		localAnimatorSet.setDuration(sAnimationTime);
		return localAnimatorSet;
	}

	private AnimatorSet getTargetCloseAnimations() {
		int i = this.mTargetStartX;
		int j = this.mTargetStartY;
		if ((this.mStartX == -1) || (this.mStartY == -1)) {
			Rect localRect = getEndingBounds();
			i = localRect.centerX();
			j = localRect.centerY();
		}
		Rect localRect2 = getEndingBounds();
		ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofFloat(
				this.mTargetView, View.X, new float[] { localRect2.left, i });
		ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofFloat(
				this.mTargetView, View.Y, new float[] { localRect2.top, j });
		ObjectAnimator localObjectAnimator3 = ObjectAnimator.ofFloat(
				this.mTargetView, View.SCALE_X, new float[] { 1.0F,
						this.mTargetStartScaleX });
		ObjectAnimator localObjectAnimator4 = ObjectAnimator.ofFloat(
				this.mTargetView, View.SCALE_Y, new float[] { 1.0F,
						this.mTargetStartScaleY });
		AnimatorSet localAnimatorSet = new AnimatorSet();
		System.out.println(" the scaleX is " + mTargetStartScaleX); // TODO
		switch (this.mAnimationDirection) {
		case SCALE_DIRECT:
			this.mTargetView.setPivotY(0.0F);
			break;
		default:
			this.mTargetView.setPivotX(0.0F);
			this.mTargetView.setPivotY(0.0F);
		}

		localAnimatorSet.playTogether(new Animator[] { localObjectAnimator1,
				localObjectAnimator2, localObjectAnimator3,
				localObjectAnimator4 });
		localAnimatorSet.setDuration(sCloseAnimationTime);
		return localAnimatorSet;
	}

	private float getBitmapScaleFactorX() {
		Rect localRect1 = getEndingBounds();
		Rect localRect2 = getStartingBounds();
		if (localRect2.width() == 0) {
			return 0.0F;
		}
		return (localRect1.width() * this.mAnimationWidthScaleFactor)
				/ localRect2.width();
	}

	private float getBitmapScaleFactorY() {
		Rect localRect1 = getEndingBounds();
		Rect localRect2 = getStartingBounds();
		if (localRect2.height() == 0) {
			return 0.0F;
		}
		return (localRect1.height() * this.mAnimationHeightScaleFactor)
				/ localRect2.height();
	}

	private AnimatorSet getBitmapCloseAnimations() {
		Rect localRect = getEndingBounds();
		int i = this.mStartX, j = this.mStartY;
		float f1 = 1.0F, f2 = 1.0F;
		if ((this.mStartX == -1) && (this.mStartY == -1)) {
			i = localRect.centerX();
			j = localRect.centerY();
			f1 = 0.0F;
			f2 = 0.0F;
		}

		ObjectAnimator localObjectAinmator1 = ObjectAnimator.ofFloat(
				this.mImageView, View.X, new float[] { localRect.left, i });
		ObjectAnimator localObjectAinmator2 = ObjectAnimator.ofFloat(
				this.mImageView, View.Y, new float[] { localRect.top, j });
		ObjectAnimator localObjectAinmator3 = ObjectAnimator.ofFloat(
				this.mImageView, View.SCALE_X, new float[] {
						getBitmapScaleFactorX(), f1 });
		ObjectAnimator localObjectAinmator4 = ObjectAnimator.ofFloat(
				this.mImageView, View.SCALE_Y, new float[] {
						getBitmapScaleFactorY(), f2 });
		AnimatorSet localAnimatorSet = new AnimatorSet();
		switch (this.mAnimationDirection) {
		case 1:
			this.mImageView.setPivotY(0.0F);
			break;
		default:
			this.mImageView.setPivotX(0.0F);
			this.mImageView.setPivotY(0.0F);
		}
		localAnimatorSet.playTogether(new Animator[] { localObjectAinmator1,
				localObjectAinmator2, localObjectAinmator3,
				localObjectAinmator4 });
		localAnimatorSet.setDuration(sCloseAnimationTime);
		return localAnimatorSet;
	}

	/**
	 * @Description: firstly,show the bitmap's zoomIn animations and then show
	 *               the target view's zoomIn animations
	 */
	public void animateOpen() {
		if (this.mCurrentAnimation != null) {
			this.mCurrentAnimation.cancel();
		}
		AnimatorSet localAnimatorSet1 = getTargetOpenAnimations();
		ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(
				this.mImageView, View.ALPHA, new float[] { 1.0F, 0.0F });
		localObjectAnimator.setDuration(sShortAnimationTime);
		localObjectAnimator.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animator) {
				ZoomActivityHelper.this.mImageView.setImageBitmap(null);
				ZoomActivityHelper.this.mImageView.setVisibility(View.GONE);
				ZoomActivityHelper.this.mImageView.setLayerType(
						View.LAYER_TYPE_NONE, null);
			}
		});
		AnimatorSet localAnimatorSet2 = new AnimatorSet();
		localAnimatorSet2.playTogether(new Animator[] { localAnimatorSet1,
				localObjectAnimator });
		localAnimatorSet2.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animator) {
				ZoomActivityHelper.this.mCurrentAnimation = null;
				ZoomActivityHelper.this.mAnimatedIn = true;
				ZoomActivityHelper.this.mTargetView.setLayerType(
						View.LAYER_TYPE_NONE, null);
			}

			public void onAnimationStart(Animator animator) {
				if (ZoomActivityHelper.this.hasActionBar) {
					ZoomActivityHelper.this.mActivity.getActionBar().show();
				}
				ZoomActivityHelper.this.mImageView
						.setImageBitmap(ZoomActivityHelper.this.mStorage
								.getBitmap());
				ZoomActivityHelper.this.mImageView.setAlpha(1.0F);
				ZoomActivityHelper.this.mImageView
						.setX(ZoomActivityHelper.this.mStartX);
				ZoomActivityHelper.this.mImageView
						.setY(ZoomActivityHelper.this.mStartY);
				ZoomActivityHelper.this.mImageView.setLayerType(
						View.LAYER_TYPE_HARDWARE, null);
				ZoomActivityHelper.this.mTargetView
						.setX(ZoomActivityHelper.this.mStartX);
				ZoomActivityHelper.this.mTargetView
						.setY(ZoomActivityHelper.this.mStartY);
				ZoomActivityHelper.this.mTargetView.setLayerType(
						View.LAYER_TYPE_HARDWARE, null);
				ZoomActivityHelper.this.mTargetView
						.setScaleX(ZoomActivityHelper.this.mTargetStartScaleX);
				ZoomActivityHelper.this.mTargetView
						.setScaleY(ZoomActivityHelper.this.mTargetStartScaleY);
				ZoomActivityHelper.this.mTargetView.setVisibility(View.VISIBLE);
			}
		});
		localAnimatorSet2.setInterpolator(this.mDecelerateQuadInterpolator);
		localAnimatorSet2.start();
		this.mCurrentAnimation = localAnimatorSet2;
	}

	private void animateClose() {
		if (this.mCurrentAnimation != null) {
			this.mCurrentAnimation.cancel();
		}
		AnimatorSet localAnimatorSet1 = getTargetCloseAnimations();
		AnimatorSet localAnimatorSet2 = getBitmapCloseAnimations();
		ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(
				this.mImageView, View.ALPHA, new float[] { 0.0F, 1.0F });
		localObjectAnimator.setDuration(sCloseAnimationTime);
		AnimatorSet localAnimatorSet = new AnimatorSet();
		localAnimatorSet.playTogether(new Animator[] { localAnimatorSet1,
				localAnimatorSet2, localObjectAnimator });
		localAnimatorSet.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animator) {
				if (ZoomActivityHelper.this.hasActionBar) {
					ZoomActivityHelper.this.mActivity.getActionBar().hide();
				}
				ZoomActivityHelper.this.mCurrentAnimation = null;
				ZoomActivityHelper.this.mAnimatedIn = false;
				ZoomActivityHelper.this.mImageView.setVisibility(View.GONE);
				ZoomActivityHelper.this.mImageView.setImageBitmap(null);
				ZoomActivityHelper.this.mTargetView.setVisibility(View.GONE);
				ZoomActivityHelper.this.reallyFinish();
			}

			public void onAnimationStart(Animator animator) {
				ZoomActivityHelper.this.mImageView.setVisibility(View.VISIBLE);
				ZoomActivityHelper.this.mImageView
						.setImageBitmap(ZoomActivityHelper.this.mStorage
								.getBitmap());
				ZoomActivityHelper.this.mImageView.setLayerType(
						View.LAYER_TYPE_HARDWARE, null);
				ZoomActivityHelper.this.mTargetView.setLayerType(
						View.LAYER_TYPE_SOFTWARE, null);
			}
		});
		localAnimatorSet.setInterpolator(this.mDecelerateQuadInterpolator);
		this.mCurrentAnimation = localAnimatorSet;
		this.mFinishing = true;
		localAnimatorSet.start();
	}

	private void reallyFinish() {
		this.mFinished = true;
		this.mActivity.finish();
	}

	public boolean isFinished() {
		return this.mFinished;
	}

	/**
	 * 
	 * @Description: run activity zoomIn animation when initialize
	 * 
	 */
	public void openActivity() {
		// judge if the animation is run yet
		if ((this.mAnimatedIn) && (null == this.mStorage.getBitmap())) {
			this.mTargetView.setVisibility(View.VISIBLE);
			this.mTargetView.setAlpha(1.0F);
			this.mImageView.setVisibility(View.GONE);
		}
		while (this.mOpened)
			return;
		this.mOpened = true;
		this.mHandler.postDelayed(new Runnable() {
			public void run() {
				ZoomActivityHelper.this.animateOpen();
			}
		}, 1L);
	}

	public void finish() {
		if (!this.mFinishing)
			animateClose();
	}

	@Override
	public void onLayoutChange(View arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5, int arg6, int arg7, int arg8) {
		if ((arg1 != arg5) || (arg2 != arg6) || (arg4 != arg8)
				|| (arg3 != arg7))
			openActivity();
	}

	public void onPause() {
		if ((this.mCurrentAnimation != null) && (!this.mActivity.isFinishing())) {
			this.mCurrentAnimation.end();
		}
	}

	public void onSaveInstanceState(Bundle bundle) {
		if (null != bundle && this.mActivity.getChangingConfigurations() != 0) {
			bundle.putInt("startX", -1);
			bundle.putInt("startY", -1);

			resetAnimationProperties();
		}
		bundle.putBoolean("animationRunYet", this.mAnimatedIn);
		bundle.putBoolean("hasActionBar", this.hasActionBar);
		bundle.putInt("animDirection", this.mAnimationDirection);
	}

	public void resetAnimationProperties() {
		this.mStartX = -1;
		this.mStartY = -1;
		this.mTargetStartX = 0;
		this.mTargetStartY = 0;
		this.mTargetStartScaleX = 0;
		this.mTargetStartScaleY = 0;
		this.mImageView.setImageBitmap(null);
		this.mStorage.freeBitmap();
	}

}
