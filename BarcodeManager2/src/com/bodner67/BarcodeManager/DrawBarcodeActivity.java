package com.bodner67.BarcodeManager;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView; 
import android.os.Bundle;


public class DrawBarcodeActivity extends Activity { 
//  based on the Android Tutorial GL1.0
  
    private GLSurfaceView mGLView; 
    
    // ------------------------------------------------------------- Methods:
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create a GLSurfaceView instance and set it as the ContentView for this Activity.
        mGLView = new DrawBarcodeSurfaceView (this);
        
        // FIX turn off screen rotation
		
		setContentView(mGLView);
		
		// set app title to:  "name  number"
		int cb = BarcodeManagerActivity.currentBarcode;		// first is '0'
		setTitle (BarcodeManagerActivity.mBarcode[cb].barcodeName + "          # "
		        + BarcodeManagerActivity.mBarcode[cb].barcodeNumberString);
		///setTitleColor (0xFF0000FF);
	}

    @Override  					 // I added this
	public void onBackPressed() {
        super.onBackPressed();
    	// do something on 'back'.
    	////  finish(); ??
	}

  
    @Override
    protected void onPause() {
        super.onPause();        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive, you should consider
        // de-allocating objects that consume significant memory here.
        mGLView.onPause();
 ///    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);      // doesn't compile
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread. If you de-allocated graphic 
        //  objects for onPause() this is a good place to re-allocate them.
        mGLView.onResume();
    }
 
} // end DrawBarcodeActivity


class DrawBarcodeSurfaceView extends GLSurfaceView {
    public DrawBarcodeSurfaceView(Context context){
        super(context);
     // Turn on error-checking and logging
     //   setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);

        setRenderer(new DrawBarcodeRenderer());   // Set the Renderer for drawing on the GLSurfaceView
    }
}