GKeepZoomInActivityAnimation
============================

GKeepZoomInActivityAnimation is an Open Source Android library project that allow developers to easily ceate activity with zoomIn animations like those made popular in the Google Keep.

Setup
-----
* In Eclipse, just import the library project.
* Then, just add library project as dependency to your existing project and you're good to go !

How to Integrate this Library into Your Projects
------------------------------------------------
In order to integrate GKeepZoomInActivityAnimation into your own projects you need do the following things.

(etc. when click A_activity's view to switch to B_activity )

__1.__      You need to set view's onClickListener

```java
view.setOnClickListener ( new OnClickListener() {
    @Override
    public void onClick ( View v ) {
        BitmapStorageManager.setCurrentBitmap ( v ) ;
        Bundle bundle = new Bundle () ;
        bundle.putInt ( "startX", (int) v.getX() ) ;
        bundle.putInt ( "startY", (int) v.getY() ) ;
        //judge if B_activity has ActionBar
        bundle.putBoolean( "hasActionBar", true ) ;
        bundle.putInt ( "animDirection",  ZoomActivityHelper.SCALE_DIRECT ) ;

        //then new Intent and putExtras
        //startActivity
    }
} ) ;
```

__2.__      You should add ImageView in B_activity's layout XML file

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >
    <TextView android:id="@+id/textView" 
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        android:background="#cccccc"
        android:text="@string/hello_world"
        android:textSize="57sp"/>
    <ImageView android:id="@+id/layer_image"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
</RelativeLayout>
```

__3.__      The B_activity need to extends ZoomInActivity

```java
public class B_activity extends ZoomInActivity{
    
    @Override
    protected void onCreate( Bundle bundle ) {
        super.onCreate ( bundle ) ;

        // TODO setContentView

        this.mAnimating = true ;    // set mAnimating
        // TODO find the targetView which need to zoomIn
        // TODO find the ImageView which the id is layer_image
        this.mZoomingActivityHelper = new ZoomActivityHelper ( this.getIntent().getExtras(), imageView, targetView, this, this ) ;
    }

    /**
     * when click the actionbar's icon , also show scale animations
     */
    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        switch ( item.getItemId () ) {
            case android.R.id.home :
                finish () ;
                return true ;
            default :
                return super.onOptionsItemSelected ( item ) ;
        }
    }
}
```

__4.__      You also need to set the AndroidManifest.xml, first the minSdkVersion is 14, and you'd better set the main activity: android:lauchMode="singleTop", then set the B_activity:
android:hardwareAccelerated="true", finally set the B_activity's background is transparent.
so B_activity should use custom theme may like that:

```xml
<resources>
    <style name="customTheme" parent="android:Theme.Light">
        <item name="android:windowActionBar">true</item>
        <item name="android:windowBackground">@color/transparent</item>
        <item name="android:windowIsFloating">false</item>
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowAnimationStyle">@null</item>
    </style>
</resources>
```

Sample
------

If you has other questions, you can see the sample project.

Developed By
------------
* Johnny Shieh - <johnnyshieh17@gmail.com>

License
-------

    Copyright 2013 Johnny Shieh
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

