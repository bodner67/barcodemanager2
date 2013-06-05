package com.bodner67.BarcodeManager;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.net.Uri;
import java.lang.String;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import com.bodner67.BarcodeManager.R;
import com.bodner67.BarcodeManager.NewBarcodeActivity;

//import java.lang.Object;
//import java.lang.Byte;
//import java.text.DateFormat;
//import java.util.Date;
//import android.view.View.OnLongClickListener;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.util.Log;
//import android.opengl.GLU;
//import android.os.SystemClock;
//import android.provider.Settings.System;
//import android.view.MotionEvent;
//import android.graphics.Bitmap.Config;
//import android.graphics.PorterDuff.Mode;
//import android.hardware.Camera;


public class BarcodeManagerActivity extends ListActivity implements OnClickListener  {

	// ADD add camera flash when taking picture (scanning)
	// ADD ability to scan from an image file
	// make app title textsize bigger
	
	//-------------------------------------------------------------- FIELDS (OBJECTS)
    				 			
	public static Barcode[] mBarcode = new Barcode[100];
	
	public static int numBarcodes = 0;
	public static int currentBarcode = -1;	  // first is '0	

	public static String foundNumberString= "xxxxx"; 
	public static String bType;		// STANDARD, NONSTANDARD, UNKNOWN
    int iflag;
    static int setSaveFlag = 0;	// 1= call saveBarcodesToPhone after deleting one

    public static Bitmap mbitmap; 		// for camera barcode pic
	public static Bitmap plotBitmap;	// for decode plot
	
	// ListView Method 2: w/button   Ref: http://stackoverflow.com/questions/4540754/add-dynamically-elements-to-a-listview-android       
	public static ArrayList<String> listItems=new ArrayList<String>();   	// list item array strings
    public static ArrayAdapter<String> adapter;  
	public final static String BARCODE_FILEPATH="barcodemanager.bar";   
	public final static String BARCODE_PICTUREPATH = "/mnt/sdcard/DCIM/Camera/barcodemanager.jpg";
	static final String[] INITIAL_BARCODES = new String[] {"A", "B"};
		
	public static final int SETTINGS 	= 1;
	public static final int WEB_ID 		= 2;
	public static final int EDIT_ID 	= 1;
	public static final int DELETE_ID 	= 2;

	public static Menu mMenu;  // ???
	
	// ------------------------------------------------------------------ METHODS 

	public BarcodeManagerActivity () {  // constructor????
//		FIX    try to move here initial readBarcodes, etc?? 
	}
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                	
    	// Toast.makeText(getApplicationContext(), "1 : into onCreate", Toast.LENGTH_SHORT).show();      
 	    // test_method();
    
        setContentView(R.layout.main);
       
        // set up ListView stuff
        ListView lv = getListView();
        adapter = new ArrayAdapter<String>(this,
        		  android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);
        lv.setTextFilterEnabled(true);    
        registerForContextMenu(lv);		// ORIG: getListView());
   
        if (savedInstanceState == null) {   // first time thru only
            Toast.makeText(getApplicationContext(), R.string.hello, Toast.LENGTH_LONG).show();              
	      	// read barcodes
        	BarcodeManagerActivity.numBarcodes = 0;
        	try {
        		listItems.clear();	//  zero out ArrayAdapter     	
        		readBarcodesFromPhone ();
        	} catch (IOException e) {
        		// TODO Auto-generated catch block
                Toast.makeText(getApplicationContext(), "problem reading barcodes file", Toast.LENGTH_LONG).show();         		
                e.printStackTrace();
        	}            		
		} // end if
              
        // color 'Add' button green
        Button addButton = (Button)findViewById(R.id.addBtn);
        addButton.getBackground().setColorFilter(0xFF44BB44, android.graphics.PorterDuff.Mode.MULTIPLY);

/*        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //----------- LONG CLICK
        	
        	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
 
        		// bring up menu:  EDIT, DELETE, WEB
        		////onCreateOptionsMenu(mMenu); 
           	  	Toast.makeText(getApplicationContext(), "into onItemLongClick", Toast.LENGTH_SHORT).show();
        		  	
        		return true;
        		
        	} // end onItemLongClick
        });
*/  
        
        lv.setOnItemClickListener(new OnItemClickListener() { //-------------------------- SHORT CLICK
       	
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	// When a list item is clicked, show a toast with the TextView text
        	  	// Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
    	    	// Toast.makeText(getApplicationContext(), "Position="+Integer.toString(position), Toast.LENGTH_SHORT).show();
        	          	      	    	         	    	
         	   	// Start Activity to display barcode: ---------------------------------DRAW BARCODE         	    	
       	    	BarcodeManagerActivity.currentBarcode = position;
       	    	if (position >=0 && position < BarcodeManagerActivity.numBarcodes) {   	    	
       	    		Intent showContentDisplay = new Intent(getApplicationContext(), DrawBarcodeActivity.class);
       	    		String notUsed = "x";   //BarcodeManagerActivity.mBarcode[position].barcodeNumberString;
       	    		showContentDisplay.setData(Uri.parse(notUsed));
       	    		startActivity(showContentDisplay);    // DrawBarcode
       	    	}
         } // end OnItemClick 

        }); 
    } // end onCreate
    
//======================================================================================================
	public void saveBarcodesToPhone () throws IOException {  // Using STRINGS
		// save barcode data to phone memory:  "/data/data/(package)/barcodes.bar" :  barcodeName[i], barcodeNumberString[i]	
   		
       	//Toast.makeText(getApplicationContext(), "Saving barcodes",Toast.LENGTH_SHORT).show();
        int nb = BarcodeManagerActivity.numBarcodes;
		try {
			OutputStreamWriter out=	new OutputStreamWriter(openFileOutput(BarcodeManagerActivity.BARCODE_FILEPATH, 0));
						
			for (int i=0; i<nb; i++) {
	    		String s = 	BarcodeManagerActivity.mBarcode[i].barcodeName 		+ '\n' + 
	    					BarcodeManagerActivity.mBarcode[i].barcodeNumberString + '\n';
	    		out.write (s);  		// outputs:  name+\n+number+\n
	    	}        		
			out.close();
		}
		catch (Throwable t) {
		Toast.makeText(getApplicationContext(), "Exception: "+ t.toString(), 2000).show();
		}
		
	} // end saveBarcodesToPhone
	
//===========================================================================================================
	public void readBarcodesFromPhone() throws IOException {	// Using STRINGS
		// Method 5  Ref  http://grail.cba.csuohio.edu/~matos/notes/cis-493/lecture-notes/Android-Chapter15-Files.pdf
		try {
		InputStream in = openFileInput(BARCODE_FILEPATH);
		if (in != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String nameString = "";
			String numString = "";
			/// ? StringBuffer buf=new StringBuffer();
			while ((nameString = reader.readLine()) != null) {
				/// ? buf.append(namestring+"\n");			
				/// ?nameString = buf.toString();
				numString = reader.readLine();
				NewBarcodeActivity.createBarcode (nameString, numString);						
			}
			in.close();
			///String name = buf.toString();  // ??
			
			}//end if EOF
       		Toast.makeText(getApplicationContext(), BarcodeManagerActivity.numBarcodes +" barcodes read",Toast.LENGTH_SHORT).show();
		}
		catch (java.io.FileNotFoundException e) {
		// that's OK, we probably haven't created it yet
		}
		catch (Throwable t) {
		Toast.makeText(this, "Exception: "+ t.toString(), 2000).show();
		}
	}	// end readBarcodesFromPhone	
			
	 //=================================================================================================================
	public void addItems(View v) {		//------------------------------------------- NEW BARCODE  
		// Called when user clicks "ADD BARCODE" button
  		Toast.makeText(getApplicationContext(), "TAKE PICTURE OF BARCODE", Toast.LENGTH_LONG).show();
 			
		// Start camera - 6th method.     		 
		// 	http://stackoverflow.com/questions/6341329/built-in-camera-using-the-extra-mediastore-extra-output-stores-pictures-twice
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);		// "android.media.action.IMAGE_CAPTURE");	
		///String path = Environment.getExternalStorageDirectory().getAbsolutePath();  // = /mnt/sdcard
		String path = BARCODE_PICTUREPATH;		// "/mnt/sdcard/DCIM/Camera/barcodemanager.jpg";
		File file = new File( path );
		Uri outputFileUri = Uri.fromFile( file );
		///String absoluteOutputFileUri = file.getAbsolutePath();
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);   	// = file:///mnt...
		startActivityForResult(cameraIntent, 0);		// start camera

		// try to set camera effects: android-camera.parameters: Constants SCENE_MODE_BARCODE, EFFECT_MONO
///		Camera mcamera=null;
///		Camera.Parameters parameters = mcamera.getParameters();
///		parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);	
	}
	
///    @Override   // after taking picture.  4th method: http://pilhuhn.blogspot.com/2010/11/using-camera-on-android-is-easy.html
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);  //---------------------------- SAVE
    	if (resultCode==RESULT_OK) {    //  && requestCode==1 && if(data.hasExtra("data")) {
    		
    		///bombs: mbitmap = (Bitmap) data.getExtras().get("data");  	// gets picture just taken from camera 	   		

    		// get picture from file   		
    		String path = BARCODE_PICTUREPATH;    // "/mnt/sdcard/DCIM/Camera/barcodemanager.jpg";
    		File file = new File( path );
    		Uri inFileUri = Uri.fromFile( file );   		
    		try {
				System.gc();	// garbage collection to free up system memory 
				BarcodeManagerActivity.mbitmap = Media.getBitmap(getContentResolver(), inFileUri );
				file.delete();
			} catch (FileNotFoundException e) {
		       	Toast.makeText(getApplicationContext(), "Image file not found",Toast.LENGTH_LONG).show();
		        //e.printStackTrace();
				return;
			} catch (IOException e) {
		       	Toast.makeText(getApplicationContext(), "Image file error",Toast.LENGTH_LONG).show();
		        //e.printStackTrace();
				return;
			}
    		
/*    		// free up camera resources ????
    		if (mCamera != null) {
    	        mCamera.setPreviewCallback(null);
    	        mCamera.release();
    	        mCamera = null;
    	    }
 */  		
       	 	String content2 = "starting new barcode";  
	  		Intent showContent2 = new Intent (getApplicationContext(), NewBarcodeActivity.class);
	        showContent2.setData(Uri.parse(content2));	        
	        startActivity(showContent2);   // start NewBarcode 
	    }
        else if (resultCode == RESULT_CANCELED) {
        	Toast.makeText(getApplicationContext(), "Image canceled",Toast.LENGTH_LONG).show();
        }
    } // end onActivityResult
//=======================================================================================================   
    
    protected void onPause() {
        super.onPause();
        //Toast.makeText(getApplicationContext(), "into onPause", Toast.LENGTH_SHORT).show();    	 
        	// could add code here to store the user's preferred persistent settings:
        try {
  			saveBarcodesToPhone ();
  		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    } // end onPause
    
    
    protected void onResume() {
        super.onResume();
        //Toast.makeText(getApplicationContext(), R.string.hello, Toast.LENGTH_SHORT).show();

/*		try {
  			readBarcodesFromPhone ();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
    }

    @Override
    protected void onRestart() {
    	super.onRestart();   	
    }
    
    public void onClick(View v) {
        int ibutton = v.getId();
        // int i = (int)v.findViewByID(i);	
        Toast.makeText(getApplicationContext(), "into main onClick, Button="+ibutton, Toast.LENGTH_SHORT).show();
		} 
    
    public void onLongClick(View v) {
        int ibutton = v.getId();
        Toast.makeText(getApplicationContext(), "into main onLongClick, Button="+ibutton, Toast.LENGTH_SHORT).show();
		} 

    public void test_method () {
     	Toast.makeText(getApplicationContext(), "into test method", Toast.LENGTH_SHORT).show();
    	return;
    }
  
    //=========================================================================== MENUS
    @Override		// sets up Menu button menu
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
    	
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.barcode_menu, menu); 

        //menu.add(0, EDIT_ID, 0, R.string.menu_edit);
        //menu.add(0, DELETE_ID, 1, "delete?").setIcon(R.drawable.icon_barcode_snip7);
        return result;
    }

    @Override		// when user selects a menu item
    public boolean onOptionsItemSelected(MenuItem item) { 
 
    	switch (item.getItemId()) {	
		case R.id.menu_settings:		// EDIT_ID:			
		   	Toast.makeText(getApplicationContext(), "Settings menu selected", Toast.LENGTH_SHORT).show();      
			return true;
 			
		case R.id.menu_web:			// WEB_ID
			// Start a Activity WebView: ---------------------WEBVIEW  	        	  	
  			String content = "http://www.google.com";  
  			Intent showContent = new Intent(getApplicationContext(), WebViewActivity.class);
  			showContent.setData(Uri.parse(content));
  			startActivity(showContent);
			return true;
		}  // end switch
	
        return super.onOptionsItemSelected(item);
    }  // end onOptionsItemSelected
//=====================================================================================
 
    @Override	// when user double clicks, sets up menu for ListItem context menu
    public void onCreateContextMenu(ContextMenu menu, View v,
        // Ref:  http://www.mikeplate.com/2010/01/21/show-a-context-menu-for-long-clicks-in-an-android-listview/
    	ContextMenuInfo menuInfo) {
    	if (v.getId() == android.R.id.list) {
    		
 //   		MenuInflater inflater = getMenuInflater();
 //         inflater.inflate(R.menu.context_menu, menu); 
            
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
    		int bnum = info.position;
    		menu.setHeaderTitle(BarcodeManagerActivity.mBarcode[bnum].barcodeName);
    		menu.add(0, EDIT_ID, 	0, R.string.menu_edit);
    	    menu.add(0, DELETE_ID,	0, R.string.menu_delete);
    	}
    }
	
    @Override	// called after user selects an item from context menu
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
      
    	//int menuItemIndex = item.getItemId();
    	//String[] menuItems = getResources().getStringArray(R.array.menu);
    	//String menuItemName = menuItems[menuItemIndex];
    	//TextView text = (TextView)findViewById(R.id.footer);
    	//text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));

      	int currnum = info.position;		// list item selected
      	int inum = item.getItemId();	// context menu item selected
  		//Toast.makeText(getApplicationContext(), bnum +" + "+ delnum, Toast.LENGTH_LONG).show();
  
      switch (inum) {	
		case EDIT_ID:			
			BarcodeManagerActivity.currentBarcode = currnum;
			//Toast.makeText(getApplicationContext(), "for now delete and Add", Toast.LENGTH_LONG).show();
      	 	String content1 = "starting edit barcode";  
	  		Intent showContent1 = new Intent (getApplicationContext(), EditBarcodeActivity.class);
	        showContent1.setData(Uri.parse(content1));	        
	        startActivity(showContent1);   // start EditBarcode 
	        return true;
			
		case DELETE_ID:
			int nb = BarcodeManagerActivity.numBarcodes;
			
			if (currnum < 0 || currnum >= nb) {
				return true;   // delnum out of range
			}
			Toast.makeText(getApplicationContext(), "Deleting " + mBarcode[currnum].barcodeName, Toast.LENGTH_LONG).show();
			NewBarcodeActivity.deleteBarcode (currnum);	 
			try {
				saveBarcodesToPhone ();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
			
		}  // end switch

  	return true;
    }	// onContextItemSelected
    
}  // end BarcodeManagerActivity