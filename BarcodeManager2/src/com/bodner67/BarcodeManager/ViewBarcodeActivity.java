package com.bodner67.BarcodeManager;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.app.Activity; 
import android.graphics.PixelFormat;


public class ViewBarcodeActivity extends Activity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
	        			 	   
	    Toast.makeText(getApplicationContext(), "into ViewBarcodeActivity", Toast.LENGTH_SHORT).show();    	 
	            
		// get and set content (barcode(position).pic     getItem(i) or something;
  	    // set backgrd_pic = barcode.getpic(active_barcode)
	    // set text = barcode.getname(active_barcode)
	    // get a company logo and display before name
	    // play short sound
	    // animate a red line scanning the screen
	    // (send barcode by wi-fi signal) 
	    // Have a RETURN onSwipe OR onTouchEvent(MotionEvent event);
	    //  try finish();?
 	  
	    // change name displayed in text:
	    //        textview1 (text) = toString(position) 
	    // menu.setHeaderTitle(((TextView)v.findViewById(R.id.textView1)).getText
	    // OR instead write text vertically
	    
	    // makes fullscreen
	    getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    setContentView(R.layout.view_barcode);       
		
/*	    Button button5 = (Button)findViewById(R.id.button5_cancel);
	   	button5.setOnClickListener(new OnClickListener() {
	   		public void onClick(View v) {
	   			finish();
	   		} 	
	   	});
*/	   	
   	}  // end onCreate
}
