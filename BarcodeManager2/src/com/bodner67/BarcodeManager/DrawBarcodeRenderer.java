package com.bodner67.BarcodeManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;
import com.bodner67.BarcodeManager.BarcodeManagerActivity;


public class DrawBarcodeRenderer implements GLSurfaceView.Renderer {

	// FIX  display barcode name across top
	// FIX also draw numbers vertically along right side
	// ADD make redline move across screen, shifting color red/white, electrical sound.
	// ADD user can swipe the barcode and hear strummed guitar chords 
	private FloatBuffer triangleVB;
	private FloatBuffer redTriangleVB;
	float triangleCoords[] = new float[700];
	float redTriangleCoords[] = new float[18];
    private int numTriCoords = 0;			// number of triangle vertices coords
    float margin = 0.14f;					// fractions of screen width/height (2x) 
    float oneWidth = .0168f;				// width of a 1-bar  
	// float oneWidth = 2f / ((numCharInNumber)*4 + 5+3+3);
    private float x = -1 + margin;		  	// left start
	private float y = -1 + 1.4f*margin;	  	// bottom margin	


    // -------------------------------------------------------------------- Methods:

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {    	
    	gl.glClearColor(0.95f, 0.95f, 0.85f, 1.0f);  	 	// set background frame color to near white
    	initShapes ();      
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);   	// Enable use of vertex arrays
    }
    
	public void onSurfaceChanged(GL10 gl, int width, int height) {
	    gl.glViewport(0, 0, width, height);
	    // may also want to set your camera here, if it's a fixed camera 
	}
	   
    public void onDrawFrame(GL10 gl) {  // is called every frame, responsible for drawing the scene.
        // clear the framebuffer, & redraw background color
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	// white
 
        // OpenGL ES calls to draw the current scene.
        // Draw the bars (two triangles each)
        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);	// black
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleVB);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, numTriCoords/3);   
        
        gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);	// red
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, redTriangleVB);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);     // 2 triangles   

/*        // try to animate redBar sideways
        long currentTime;
        long startTime = SystemClock.uptimeMillis() % 400L;
        for (int ix=0; ix<400; ix++) {
        	currentTime = SystemClock.uptimeMillis() % 400L;
        	gl.glTranslatef(ix/1000, 0, 0);
        	triangleVB.position(0);
        	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, redTriangleVB);
        	gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);     // 2 red triangles   
      	}	          
*/        
/*        // toadd motion:   http://developer.android.com/resources/tutorials/opengl/opengl-es10.html#motion
        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);    // When using GL_MODELVIEW, you must set the view point    
    
        // Create a rotation for the triangle
        long time = SystemClock.uptimeMillis() % 400L;
        float angle = 0.090f * ((int) time);
        gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);       
        //gl.glTranslatef(angle, 0.0f, 0.0f, 1.0f);        
        
        // Draw the triangle ...    
*/    
    } // end onDrawFrame
    
/*   // from http://www.droidnova.com/android-3d-game-tutorial-part-i,312.html
    public boolean onTouchEvent(final MotionEvent event) {
        queueEvent(new Runnable() {
            public void run() {
                // renderer.setColor(event.getX() / getWidth(), event.getY() / getHeight(), 1.0f);
                gl.glColor4f(1.0f, 0.0f, 0.0f, 0.0f);	// red
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, redTriangleVB);
                gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);     // 2 triangles   
                }
        });
        return true;
    }  */
    
    public void initShapes() {        
    	int cb = BarcodeManagerActivity.currentBarcode;		// first is '0'
    	String numStr = BarcodeManagerActivity.mBarcode[cb].barcodeNumberString;
    	/// ALTERNATE  String numStr = Barcode.getCurrentBarcodeNumberString();
  
    	if (BarcodeManagerActivity.mBarcode[cb].barcodeType == "STANDARD") {
    		drawReddot();
    		drawStandardBarcode(numStr);
    	} else {
    		drawRedbar();
    		drawNonStandardBarcode(numStr);
    	}   	
    }  // end initShapes
    
 //================================================================================================   
	public void drawRedbar () {
        final float redTriangleCoords[] = {   // X Y Z - 6 pts
     		-0.15f, -0.897f, 0,   -.1f, -0.9f, 0,     0f,  0.9f, 0, 
     		-0.15f, -0.9f, 0,   0f, 0.9f, 0,     -0.05f,  0.903f, 0  };  
        // initialize vertex Buffer for triangle  
        ByteBuffer vbbRed = ByteBuffer.allocateDirect(redTriangleCoords.length * 4);	 // (# of coordinate values * 4 bytes per float)
        vbbRed.order(ByteOrder.nativeOrder());		// use the device hardware's native byte order
        redTriangleVB = vbbRed.asFloatBuffer();  	// create a floating point buffer from the ByteBuffer
        redTriangleVB.put(redTriangleCoords);    	// add the coordinates to the FloatBuffer
        redTriangleVB.position(0);            		// set the buffer to read the fi	
	}
	
	public void drawReddot () {
        final float redTriangleCoords[] = {   // X Y Z - 6 pts
     		-0.95f, -0.95f, 0,     -0.95f, -0.94f, 0,     -0.94f, -0.94f, 0, 
     		-0.95f, -0.95f, 0,     -0.94f, -0.95f, 0,     -0.94f, -0.94f, 0  };  
        // initialize vertex Buffer for triangle  
        ByteBuffer vbbRed = ByteBuffer.allocateDirect(redTriangleCoords.length * 4);
        vbbRed.order(ByteOrder.nativeOrder());		
        redTriangleVB = vbbRed.asFloatBuffer(); 
        redTriangleVB.put(redTriangleCoords);    
        redTriangleVB.position(0);            		
	}
		
    public void drawBar(float width, float x, float y) {  
		// draws a horizontal rectangle rightward and upward from x,y (screen -1 to +1).
		float length = 2 - 2.1f*margin;
		triangleCoords [numTriCoords+0] = x;					// Pt1 	x
		triangleCoords [numTriCoords+1] = y;					// 		y
		triangleCoords [numTriCoords+2] = 0;					// 		z
		triangleCoords [numTriCoords+3] = x + length;			// Pt2	
		triangleCoords [numTriCoords+4] = y;	 		
		triangleCoords [numTriCoords+5] = 0;		 		
		triangleCoords [numTriCoords+6] = x + length;			// Pt3	
		triangleCoords [numTriCoords+7] = y + width*oneWidth;	 		
		triangleCoords [numTriCoords+8] = 0;		 		

		triangleCoords [numTriCoords+9]  = x;					// Pt4
		triangleCoords [numTriCoords+10] = y;			
		triangleCoords [numTriCoords+11] = 0;			
		triangleCoords [numTriCoords+12] = x + length;		// Pt5
		triangleCoords [numTriCoords+13] = y + width*oneWidth;		
		triangleCoords [numTriCoords+14] = 0;				
		triangleCoords [numTriCoords+15] = x;					// Pt6
		triangleCoords [numTriCoords+16] = y + width*oneWidth;		
		triangleCoords [numTriCoords+17] = 0;				
		numTriCoords = numTriCoords + 18;
	} 

    public void drawNonStandardBarcode(String barcodeString) {    		     
    	for (int i=0; i<10; i++) {
    		y = y + (4f+4f)*oneWidth;
    		drawBar(4f, x, y);
    	}
        // initialize vertex Buffer for triangle  
        ByteBuffer vbb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                triangleCoords.length * 4); 
        vbb.order(ByteOrder.nativeOrder());	// use the device hardware's native byte order
        triangleVB = vbb.asFloatBuffer();  	// create a floating point buffer from the ByteBuffer
        triangleVB.put(triangleCoords);    	// add the coordinates to the FloatBuffer
        triangleVB.position(0);            	// set the buffer to read the first coordinate  
        }
 
    //====================================================================================================
    public void drawStandardBarcode(String barcodeString) {    	
    	//	0 = 3-2-1-1		5 = 1-2-3-1
		//	1 = 2-2-2-1		6 = 1-1-1-4
		//	2 = 2-1-2-2		7 = 1-3-1-2
		//	3 = 1-4-1-1		8 = 1-2-1-3
		//	4 = 1-1-3-2		9 = 3-1-1-2 		
        
    	// assume STANDARD barcode.  calc triangleCoords data  (-1 to +1)
       		///s = barcode.barcodeNumberString(idigit);
    		///String temp = "038000035302";    	// kellog crispix
    		///String temp = "123456789012";    	// test
        	///String number = mBarcode[currentlyBarcode];
    	char[] ch = barcodeString.toCharArray();
        	
    	// start in bottom-left corner   	

			// opening 1-1-1
        	margin = margin -.04f;
        	drawBar(1f, x, y);
			y = y + (1+1)*oneWidth;
			drawBar(1f, x, y);
			y = y + (1)*oneWidth;
        	margin = margin +.04f;
        	
			//chars 0-5
			for (int idigit=0; idigit<6; idigit++) { 
        	switch (ch[idigit])  {
			case '0':		// 3211     
				y = y + (3f)*oneWidth;
				drawBar(2f, x, y);
				y = y + (2f+1f)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1f)*oneWidth;
				break;
			case '1':		// 2221     
				y = y + (2f)*oneWidth;
				drawBar(2f, x, y);
				y = y + (2f+2f)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1f)*oneWidth;
				break;
			case '2':		// 2122     
				y = y + (2f)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1f+2f)*oneWidth;
				drawBar(2f, x, y);
				y = y + (2f)*oneWidth;
				break;
			case '3':		// 1411     
				y = y + (1f)*oneWidth;
				drawBar(4f, x, y);
				y = y + (4f+1f)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1f)*oneWidth;
				break;			
			case '4':		// 1132    
				y = y + (1f)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1f+3f)*oneWidth;
				drawBar(2f, x, y);
				y = y + (2f)*oneWidth;
				break;
			case '5':		// 1231     
				y = y + (1f)*oneWidth;
				drawBar(2f, x, y);
				y = y + (2f+3f)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1f)*oneWidth;
				break;
			case '6':		// 1114     
				y = y + (1f)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1f+1f)*oneWidth;
				drawBar(4f, x, y);
				y = y + (4f)*oneWidth;
				break;
			case '7':		// 1312     
				y = y + (1f)*oneWidth;
				drawBar(3f, x, y);
				y = y + (3f+1f)*oneWidth;
				drawBar(2f, x, y);
				y = y + (2f)*oneWidth;
				break;	
			case '8':		// 1213       
				y = y + (1f)*oneWidth;
				drawBar(2f, x, y);
				y = y + (2f+1f)*oneWidth;
				drawBar(3f, x, y);
				y = y + (3f)*oneWidth;
				break;
			case '9':		// 3112     
				y = y + (3f)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1f+1f)*oneWidth;
				drawBar(2f, x, y);
				y = y + (2f)*oneWidth;
				break;	
			case 'x':		// xxxx     
				y = y + (3f)*oneWidth;
				drawBar(3f, x, y);
				y = y + (3f+3f)*oneWidth;
				drawBar(3f, x, y);
				y = y + (3f)*oneWidth;
				break;
			default:
				break;
        	}
        }
        	
        // middle 1-1-1-1-1
        margin = margin -.04f;
        y = y + (1f)*oneWidth;
		drawBar(1f, x, y);
    	y = y + (1f+1f)*oneWidth;
    	drawBar(1f, x, y);
    	y = y + (1f+1f)*oneWidth;
        margin = margin +.04f;
        	
		//chars 6-11
		for (int idigit1=6; idigit1<12; idigit1++) { 
        	switch (ch[idigit1])  {
			case '0':		// 3211     
				drawBar(3f, x, y);
				y = y + (3+2)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1+1)*oneWidth;
				break;
			case '1':		// 2221     
				drawBar(2f, x, y);
				y = y + (2+2)*oneWidth;
				drawBar(2f, x, y);
				y = y + (2+1)*oneWidth;
				break;
			case '2':		// 2122     
				drawBar(2f, x, y);
				y = y + (2+1)*oneWidth;
				drawBar(2f, x, y);
				y = y + (2+2)*oneWidth;
				break;
			case '3':		// 1411     
				drawBar(1f, x, y);
				y = y + (1+4)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1+1)*oneWidth;
				break;			
			case '4':		// 1132     
				drawBar(1f, x, y);
				y = y + (1+1)*oneWidth;
				drawBar(3f, x, y);
				y = y + (3+2)*oneWidth;
				break;
			case '5':		// 1231     
				drawBar(1f, x, y);
				y = y + (1+2)*oneWidth;
				drawBar(3f, x, y);
				y = y + (3+1)*oneWidth;
				break;
			case '6':		// 1114     
				drawBar(1f, x, y);
				y = y + (1+1)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1+4)*oneWidth;
				break;
			case '7':		// 1312     
				drawBar(1f, x, y);
				y = y + (1+3)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1+2)*oneWidth;
				break;	
			case '8':		// 1213     
				drawBar(1f, x, y);
				y = y + (1+2)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1+3)*oneWidth;
				break;
			case '9':		// 3112     
				drawBar(3f, x, y);
				y = y + (3+1)*oneWidth;
				drawBar(1f, x, y);
				y = y + (1+2)*oneWidth;
				break;	
			case 'x':		// xxxx     
				drawBar(3f, x, y);
				y = y + (3+3)*oneWidth;
				drawBar(3f, x, y);
				y = y + (3+3)*oneWidth;
				break;
			default:
				break; 
        	}        	        	
        }  // end for loop       

		// ending 1-1-1
       	margin = margin -.04f;
       	drawBar(1f, x, y);
		y = y + (1+1)*oneWidth;
		drawBar(1f, x, y);
		y = y + (1)*oneWidth;
       	margin = margin +.04f;

       	// verify all triangleCoords are between -1 to +1
    	for (int i=0; i<numTriCoords; i++) {
    		if (triangleCoords[i] < -1 || triangleCoords[i] > 1) {
    			// FIX  ERROR
    	///  		Toast.makeText(getApplicationContext(), "triangleCoords >+-1", Toast.LENGTH_SHORT).show();
    	  		triangleCoords[i] = .999f;
    		}
    	}
    	
        // initialize vertex Buffer for triangle  
        ByteBuffer vbb = ByteBuffer.allocateDirect(triangleCoords.length * 4);  // (# of coordinate values * 4 bytes per float)
        vbb.order(ByteOrder.nativeOrder());	// use the device hardware's native byte order
        triangleVB = vbb.asFloatBuffer();  	// create a floating point buffer from the ByteBuffer
        triangleVB.put(triangleCoords);    	// add the coordinates to the FloatBuffer
        triangleVB.position(0);            	// set the buffer to read the first coordinate  

    }  // end drawStandardBarcode
}  // end DrawBarcodeRenderer