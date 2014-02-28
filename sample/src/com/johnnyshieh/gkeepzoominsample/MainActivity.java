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
package com.johnnyshieh.gkeepzoominsample;

import com.johnnyshieh.gkeepzoominsample.R;
import com.johnnyshieh.zoominanimation.BitmapStorageManager;
import com.johnnyshieh.zoominanimation.ZoomActivityHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @ClassName:  MainActivity
 * @Description:TODO
 * @author  Johnny Shieh
 * @date    December 26, 2013
 */
public class MainActivity extends Activity {

	private TextView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println(" new a main activity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		view = (TextView) findViewById(R.id.text);
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				BitmapStorageManager.setCurrentBitmap(v);
				Bundle bundle = new Bundle();
				bundle.putInt("startX", (int) v.getX());
				bundle.putInt("startY", (int) v.getY());
				bundle.putBoolean("hasActionBar", true ) ;
				bundle.putInt("animDirection", ZoomActivityHelper.SCALE_DIRECT );

				Intent intent = new Intent(MainActivity.this,
						DemoActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
