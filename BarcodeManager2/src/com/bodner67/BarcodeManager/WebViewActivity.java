package com.bodner67.BarcodeManager;

import com.bodner67.BarcodeManager.R;

import android.os.Bundle;
import android.webkit.WebView;
//import android.widget.Toast;
import android.app.Activity; 
import android.content.Intent;

public class WebViewActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	        
	   
	   	// Toast.makeText(getApplicationContext(), "into WebViewActivity", Toast.LENGTH_SHORT).show();    	 
		// set webView's Google textbox to "barcode"  
	    setContentView(R.layout.webview);        
	        
	   	// Ref:   http://mobile.tutsplus.com/tutorials/android/android-listview/
	   	Intent launchingIntent = getIntent();
	   	String content = launchingIntent.getData().toString();
	   	WebView viewer = (WebView) findViewById(R.id.barcodeWebView);
	   	viewer.loadUrl(content);
    
   		// play sound       
	}
}