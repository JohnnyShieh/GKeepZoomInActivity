/*
 * Copyright (C) 2013 Johnny Shieh
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

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;

/**
 * @ClassName:  ZoomInActivity
 * @Description:TODO
 * @author  Johnny Shieh
 * @date    December 26, 2013
 */
public class ZoomInActivity extends Activity implements BitmapStorageInterface{

	protected boolean mAnimating ;
	protected ZoomActivityHelper mZoomingActivityHelper ;
	
	@Override
	public void freeBitmap() {
		BitmapStorageManager.freeBitmap() ;
	}
	
	@Override
	public Bitmap getBitmap() {
		return BitmapStorageManager.getCurrentBitmap() ;
	}

	@Override
	public Rect getBitmapDrawingRect() {
		return BitmapStorageManager.getCurrentDrawingRect() ;
	}
	
	@Override
    public void finish () {
        if ( ( this.mAnimating ) && ( !this.mZoomingActivityHelper.isFinished() ) ) {
            this.mZoomingActivityHelper.finish() ;
            return ;
        }
        super.finish() ;
    }
	
	@Override
	public void onPause () {
        if ( this.mAnimating )
            this.mZoomingActivityHelper.onPause() ;
        super.onPause() ;
    }
	
	@Override
	public void onSaveInstanceState(Bundle bundle)
    {
      if (this.mAnimating)
        this.mZoomingActivityHelper.onSaveInstanceState(bundle);
      super.onSaveInstanceState(bundle);
    }
}
