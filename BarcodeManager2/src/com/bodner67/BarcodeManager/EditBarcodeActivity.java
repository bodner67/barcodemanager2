package com.bodner67.BarcodeManager;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.Toast;
import android.app.Activity; 
import java.lang.String;
import com.bodner67.BarcodeManager.R;
import com.bodner67.BarcodeManager.BarcodeManagerActivity;
import com.bodner67.BarcodeManager.Barcode;
import com.bodner67.BarcodeManager.DecodeBarcode;

public class EditBarcodeActivity extends Activity {

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	        	   
		setContentView(R.layout.new_barcode);
		
        // color 'Save' button green
        Button saveButton = (Button)findViewById(R.id.button3_save);
        saveButton.getBackground().setColorFilter(0xFF44BB44, android.graphics.PorterDuff.Mode.MULTIPLY);       
        // color 'Cancel' button grey
        Button cancelButton = (Button)findViewById(R.id.button5_cancel);
        cancelButton.getBackground().setColorFilter(0xFFAAAAAA, android.graphics.PorterDuff.Mode.MULTIPLY);       
        
        // display camera picture just taken
///		ImageView image = (ImageView) findViewById(R.id.imageView2); 
///		image.setImageBitmap(BarcodeManagerActivity.mbitmap);

		// calculate barcode number
		BarcodeManagerActivity.foundNumberString = DecodeBarcode.calcBarcodeNumber(BarcodeManagerActivity.mbitmap);   			

		//  TEMP- TEST ONLY to plot decode data
///		DecodeBarcode.drawDecodeBarcode();		
///     ImageView plotImage = (ImageView) findViewById(R.id.imageView2);   // plot replaces barcode pic
///		plotImage.setImageBitmap(BarcodeManagerActivity.plotBitmap);
		
		// show barcode name in editText 
		int ebn = BarcodeManagerActivity.currentBarcode;  // ??
		EditText myEditText1 = (EditText)findViewById(R.id.editText1);
		myEditText1.setText(BarcodeManagerActivity.mBarcode[ebn].barcodeName);
		// show barcode# in editText 
		EditText myEditText2 = (EditText)findViewById(R.id.editText2);
		myEditText2.setText(BarcodeManagerActivity.mBarcode[ebn].barcodeNumberString);

		
		Button button3 = (Button)findViewById(R.id.button3_save);
	   	button3.setOnClickListener(new OnClickListener() {  //---------------------SAVE BUTTON
	   		public void onClick(View v) {
   	    		   			
	   	    	/// ADD play sound	   				   			
	   			
	   		   	// get EditText strings
	   			EditText myEditText1 = (EditText)findViewById(R.id.editText1);
	   			String sname = myEditText1.getText().toString();
	   			EditText myEditText2 = (EditText)findViewById(R.id.editText2);
	   			String snumber = myEditText2.getText().toString();

	   			// set new values
  	 			int position = BarcodeManagerActivity.currentBarcode;
  	 		 	BarcodeManagerActivity.mBarcode[position].barcodeName = sname;
	   			BarcodeManagerActivity.mBarcode[position].barcodeNumberString = snumber;
	   			
	   			// verify
   	 			BarcodeManagerActivity.mBarcode[position].barcodeType = "UNKNOWN";
	   	 		int vvflag = Barcode.verifyBarcode(snumber);
	   	 		if (vvflag == 1) {
	   	 			BarcodeManagerActivity.mBarcode[position].barcodeType = "STANDARD";
	   	 		}
	   	 		    		
	   	 		// set new item (sname) into ListView/ArrayAdapter...
	   			BarcodeManagerActivity.listItems.set(position, sname);	// WATCH IT: sname needs to be E-object
	   	 		BarcodeManagerActivity.adapter.notifyDataSetChanged();
	   	 		
//	   	 		iflag = BarcodeManagerActivity.saveBarcodesToPhone ();				
/*	   		   	if (iflag == 1) {	Toast.makeText(getApplicationContext(), "SAVED: "+ sname, Toast.LENGTH_LONG).show();    	 
	   		   	}
	   		   	else {	Toast.makeText(getApplicationContext(), "SAVE FAILED: " + sname, Toast.LENGTH_LONG).show();    	 
	   		   	}
*/
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
	
	
/*	static void editBarcode (int position) {		// NEED TO DEBUG
		// get newname from user somehow
		String newName = "new";
		
		// edit ListView item
		BarcodeManagerActivity.mBarcode[position].barcodeName = newName;
		BarcodeManagerActivity.listItems.set(position, newName); 		// WATCH IT: newName needs to be E-object
 		BarcodeManagerActivity.adapter.notifyDataSetChanged();
 		}
*/
}		