package com.bodner67.BarcodeManager;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
//import android.widget.Toast;
import android.app.Activity; 
//import android.content.Context;

import java.lang.String;
//import java.io.IOException;

import com.bodner67.BarcodeManager.R;
import com.bodner67.BarcodeManager.BarcodeManagerActivity;
import com.bodner67.BarcodeManager.Barcode;
import com.bodner67.BarcodeManager.DecodeBarcode;


public class NewBarcodeActivity extends Activity   {
    		
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	        	   
		setContentView(R.layout.new_barcode);

		// FIX bug:  on resume the drawn barcode shifts upward a lot (but not redline)
		// ADD/FIX   add onPause, onResume, etc
		
		// draw redline on barcode picture   
		// 	FIX  draw redline before taking picture (plus transparent mask)
		int picWidth = BarcodeManagerActivity.mbitmap.getWidth(); 
		int picMidHeight = BarcodeManagerActivity.mbitmap.getHeight() / 2; 
		int redColor = 0xFFFF0000;
		if (BarcodeManagerActivity.mbitmap.isMutable()) {
			for (int ix=0; ix<picWidth; ix++) {
				BarcodeManagerActivity.mbitmap.setPixel(ix, picMidHeight, redColor);  	// RED = 0xFFFF0000 = -65536
			}
		}
		
        // color 'Save' button green
        Button saveButton = (Button)findViewById(R.id.button3_save);
        saveButton.getBackground().setColorFilter(0xFF44BB44, android.graphics.PorterDuff.Mode.MULTIPLY);       
        // color 'Cancel' button grey
        Button cancelButton = (Button)findViewById(R.id.button5_cancel);
        cancelButton.getBackground().setColorFilter(0xFFAAAAAA, android.graphics.PorterDuff.Mode.MULTIPLY);       
        
        // display camera picture just taken
		ImageView image = (ImageView) findViewById(R.id.imageView2); 
		image.setImageBitmap(BarcodeManagerActivity.mbitmap);

		// calculate barcode number
		BarcodeManagerActivity.foundNumberString = DecodeBarcode.calcBarcodeNumber(BarcodeManagerActivity.mbitmap);   			

		//  TEMP- TEST ONLY to plot decode data
		DecodeBarcode.drawDecodeBarcode();		
        ImageView plotImage = (ImageView) findViewById(R.id.imageView2);   // plot replaces barcode pic
		plotImage.setImageBitmap(BarcodeManagerActivity.plotBitmap);
		
		// show barcode# in edit text 
		EditText myEditText = (EditText)findViewById(R.id.editText2);
		myEditText.setText(BarcodeManagerActivity.foundNumberString);
			   	    
		Button button3 = (Button)findViewById(R.id.button3_save);
	   	button3.setOnClickListener(new OnClickListener() {  //---------------------SAVE BUTTON
	   		public void onClick(View v) {
   	    		   			
	   	    	/// ADD play sound	   				   			
	   			
	   		   	// get EditText strings
	   			EditText myEditText1 = (EditText)findViewById(R.id.editText1);
	   			String sname = myEditText1.getText().toString();
	   			EditText myEditText2 = (EditText)findViewById(R.id.editText2);
	   			String snumber = myEditText2.getText().toString();

				NewBarcodeActivity.createBarcode (sname, snumber);						

	   		   	// ADD  goto web with barcode number to get company "name" and "icon"
	   			
/*	   		   	// save barcodes - WON'T COMPILE
	   		   	try {
					BarcodeManagerActivity.saveBarcodesToPhone ();
					// TEST:  BarcodeManagerActivity.readBarcodesFromPhone (); 		   	
	   		   	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	   		   	if (iflag == 1) {	Toast.makeText(getApplicationContext(), "SAVED: "+ sname, Toast.LENGTH_LONG).show();    	 
	   		   	}
	   		   	else {	Toast.makeText(getApplicationContext(), "SAVE FAILED: " + sname, Toast.LENGTH_LONG).show();    	 
	   		   	} */
	   			finish();	   			
	   		}
	   		
	   	});  // end button3 save
	   	
	   	Button button5 = (Button)findViewById(R.id.button5_cancel);
	   	button5.setOnClickListener(new OnClickListener() {	//-------------------CANCEL BUTTON
	   		public void onClick(View v) {
	   			finish();
	   		} 	
	   	});    
	}  // end onCreate

	
	static void createBarcode (String name, String numString) {
		// ADD  goto web with barcode number to get company "name" and "icon"   
		
		BarcodeManagerActivity.numBarcodes++;
		int nb = BarcodeManagerActivity.numBarcodes - 1;
    	
    	BarcodeManagerActivity.mBarcode[nb] = new Barcode();		// create new barcode
		BarcodeManagerActivity.mBarcode[nb].barcodeName = name;
		BarcodeManagerActivity.mBarcode[nb].barcodeNumberString = numString;
		BarcodeManagerActivity.mBarcode[nb].barcodeType = BarcodeManagerActivity.bType;

		// verify
 		int vvflag = Barcode.verifyBarcode(numString);
 		if (vvflag == 1) {
 			BarcodeManagerActivity.mBarcode[nb].barcodeType = "STANDARD";
 		}
 		    		
 		// set new item (name) into ListView/ArrayAdapter...
 		BarcodeManagerActivity.listItems.add(name);
 		BarcodeManagerActivity.adapter.notifyDataSetChanged();
	}
	
	
	static void editBarcode (int position) {		// NEED TO DEBUG
/*		// get newname from user somehow
		String newName = "new";
		
		// edit ListView item
		BarcodeManagerActivity.mBarcode[position].barcodeName = newName;
		BarcodeManagerActivity.listItems.set(position, newName); 		// WATCH IT: newName needs to be E-object
 		BarcodeManagerActivity.adapter.notifyDataSetChanged();
*/ 		}
		
	 static void deleteBarcode (int position) {

		int nb = BarcodeManagerActivity.numBarcodes;
		 if (position < 0 || position >= nb) {
			return;
		}
		 
		 // delete position from myBarcode and shift all data up 1
		for (int i=position; i<(BarcodeManagerActivity.numBarcodes - 1); i++) {
			BarcodeManagerActivity.mBarcode[i].barcodeName 			= BarcodeManagerActivity.mBarcode[i+1].barcodeName;
			BarcodeManagerActivity.mBarcode[i].barcodeNumberString 	= BarcodeManagerActivity.mBarcode[i+1].barcodeNumberString;
			BarcodeManagerActivity.mBarcode[i].barcodeType			= BarcodeManagerActivity.mBarcode[i+1].barcodeType;
		}
		BarcodeManagerActivity.mBarcode[BarcodeManagerActivity.numBarcodes-1].barcodeName		 = null;  	//"no name";
		BarcodeManagerActivity.mBarcode[BarcodeManagerActivity.numBarcodes-1].barcodeNumberString= null;  	//"xxxxxxxxxxxx";
		BarcodeManagerActivity.mBarcode[BarcodeManagerActivity.numBarcodes-1].barcodeType 		 = null;	//"UNKNOWN";
		BarcodeManagerActivity.numBarcodes--;
				
		// remove from ListView
		BarcodeManagerActivity.listItems.remove(position); 
	   	BarcodeManagerActivity.adapter.notifyDataSetChanged();
	}

}
