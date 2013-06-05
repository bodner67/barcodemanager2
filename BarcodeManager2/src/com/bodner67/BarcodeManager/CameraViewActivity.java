package com.bodner67.BarcodeManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.bodner67.BarcodeManager.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
import android.widget.Toast;

// NOTES DELETED FROM BarCodePASSActivity.java

/*        	  		// start CameraActivity
// 5th method:  http://stackoverflow.com/questions/5395674/camera-intent-android
		String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
String filename = "/pic5.jpg";     // ("/mnt/sdcard" + currentDateTimeString + ".jpg");
Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
File file = new File(Environment.getExternalStorageDirectory(), filename);
Uri outputFileUri = Uri.fromFile(file);
///	intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
startActivityForResult(intent, 1);   // TAKE_PICTURE);
//String filetype = "image/jpeg";
// Check out android-camera.parameters: Constants SCENE_MODE_BARCODE, EFFECT_MONO
*/  			
/*         	  		Toast.makeText(getApplicationContext(), "TAKE PICTURE OF BARCODE", Toast.LENGTH_LONG).show();
  
	// Start camera - 6th method
// http://stackoverflow.com/questions/6341329/built-in-camera-using-the-extra-mediastore-extra-output-stores-pictures-twice
Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
/// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
String path = Environment.getExternalStorageDirectory().getAbsolutePath();  // = /mnt/sdcard
path += "/pic10.jpg";		// "/mnt/sdcard"
File file = new File( path );
//file.mkdirs();
Uri outputFileUri = Uri.fromFile( file );
String absoluteOutputFileUri = file.getAbsolutePath();
intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
startActivityForResult(intent, 0);
*/
    			    				
	/*	// start CameraActivity
// 6th method:   http://www.tutorialforandroid.com/2010/10/take-picture-in-android-with.html
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(this)) );   
		startActivityForResult(intent, 1);  
	*/
/*	    // 6th method:  http://www.tutorialforandroid.com/2010/10/take-picture-in-android-with.html
 public File getTempFile (Context context) {  
 	//it will return /sdcard/image.tmp  
 	final File path = new File( Environment.getExternalStorageDirectory(), context.getPackageName() );  
 	if(!path.exists()){  
 	path.mkdir();  
 	}  
 	return new File(path, "image.tmp");  
 }  
*/ 	    


/* ???? This implementation is from marakana.com/forums/android/examples/39.html */
public class CameraViewActivity extends Activity implements SurfaceHolder.Callback,
		OnClickListener {
	static final int FOTO_MODE = 0;
	private static final String TAG = "CameraTest";
	Camera mCamera;
	boolean mPreviewRunning = false;
	private Context mContext = this;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    Toast.makeText(getApplicationContext(), "into CameraViewActivity", Toast.LENGTH_SHORT).show();    	 
		Log.e(TAG, "onCreate");

		Bundle extras = getIntent().getExtras();

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera_surface);
		mSurfaceView = (SurfaceView) findViewById(R.id.camera_surface);
		mSurfaceView.setOnClickListener(this);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {

			if (imageData != null) {

				// Intent mIntent = new Intent();
				//4th method:
				Intent mIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

				startActivityForResult(mIntent, 1);
				
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inSampleSize = 5;
				Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length,options);
				int ih = myImage.getHeight();
	///			StoreByteImage(mContext, imageData, 50, "ImageName");
				mCamera.startPreview();

				setResult(FOTO_MODE, mIntent);
				finish();
			}
		}
	};

	protected void onResume() {
		Log.e(TAG, "onResume");
		super.onResume();
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	protected void onStop() {
		Log.e(TAG, "onStop");
		super.onStop();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		mCamera = Camera.open();

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.e(TAG, "surfaceChanged");

		// XXX stopPreview() will crash if preview is not running
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}

		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(w, h);
		mCamera.setParameters(p);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;

	public void onClick(View arg0) {

		mCamera.takePicture(null, null, mPictureCallback);

	}
	
	public static boolean StoreByteImage(Context mContext, byte[] imageData,
			int quality, String expName) {

        File sdImageMainDirectory = new File("/sdcard");
		FileOutputStream fileOutputStream = null;
		String nameFile;
		try {

			// duplicate?
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize = 5;
			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length,options);
			
			fileOutputStream = new FileOutputStream(sdImageMainDirectory.toString() +"/image.jpg");
  
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

			myImage.compress(CompressFormat.JPEG, quality, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

}
