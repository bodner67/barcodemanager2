package com.bodner67.BarCodePASS;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
//import java.awt.Flowlayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;

public class HOLD_BarCodePASSActivity_DroidPg246 extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);             
               
        registerButtonListenersAndSetDefaultText();
        
        setContentView(R.layout.change_barcode); 
        for (int i=0;i<100;i++) {
        	// draw line
            }
    	int ii=1;
 
        }
    int j =1;
    Barcode barcode = new Barcode();
    barcode.change_barcode_name();
    
    private Button mDateButton;
    
    private void registerButtonListenersAndSetDefaultText() {
		// TODO Auto-generated method stub: Android DUMMIES P.246    
    	
    	button1_enter_new_barcode.setOnClickListener (new View.OnClickListener() {
    	
    			@Override
    			public void onClick(View v) {
    				showDialog(DATE_PICKER_DIALOG);
    			}
    	});
    	upDateDateButtonText();
    	upDateTimeButtonText();
    	}
    
    
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.button1_enter_new_barcode:
            	System.out.println("into enter_new_barcode");
            	// play sound
            	setContentView(R.layout.take_picture);
                return true;
                
            case R.id.button2_display_barcode:
            	System.out.println("into display_barcode");
            	// set backgrd_pic = barcode.getpic(active_barcode)
            	// set text = barcode.getname(active_barcode)
            	// play short sound
            	// animate a red line scanning the screen
            	// (send barcode by wi-fi signal) 
                setContentView(R.layout.display_barcode);
                return true;
                
            case R.id.button3_save:
            	System.out.println("into save");
            	System.out.flush();
            	int active_barcode = 1;
            	String s = "picpath";
            	// instead get string from TextView?
            	barcode.change_barcode_picpath (s);
            	barcode.change_barcode_name (s);
            	// play sound
            	setContentView(R.layout.change_barcode);
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
                                
                                      
        }  
       
    }
  
}