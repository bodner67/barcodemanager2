package com.bodner67.BarcodeManager;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Toast;

import com.bodner67.BarcodeManager.BarcodeManagerActivity;
 

public class DecodeBarcode {
	static int width	 = 0;
	static int height	 = 0;
	static int midHeight = 0;
	static int redVal[] 	= new int[4100];		// 0-255  
	static int greenVal[]	= new int[4100];		
	static int blueVal[] 	= new int[4100];
	static int pixBW[] 		= new int[4100];	   	//  0000111110000000011100010000001111
	static float pixSwitches[]  = new float[300];
	static float condensedPixSwitches[] = new float[300];   
	static float lineWidthf[] 	= new float[300];   
	static int 	 lineWidth[] 	= new int[300];     //  24 1 1 1 2 1 4 1 3 1 2 1 4 2 3 1 1 1

	static int redAverage 	= 0;	// number between 0-255
	static int greenAverage	= 0;
	static int blueAverage	= 0;		

	static float averWidthOffset  = 1.08f;    	// multiplier makes averWidth a little bigger, lineWidths a little smaller 	
	static float lineWidthOffset = 0.30f;   	// partial linewidth added to calculated lineWidth
	
	
//=================================================================================== METHODS =============

	static public String calcBarcodeNumberRepeater (Bitmap image) {		
	
		// this routine cycles thru calling "calcBarcodeNumber" with different factors
		String s;
//		minWidthOffset = 3; 
		lineWidthOffset = 0.40f;
		s = calcBarcodeNumber (image);
		
		if (BarcodeManagerActivity.bType == "STANDARD") {		// change to UPC-12
			return "STANDARD"; 
		}
		
//		minWidthOffset = 3; 
		lineWidthOffset = 0.40f;
		s = calcBarcodeNumber (image);
				
		return "x";
		
	// if FAILED:
	//   	1) rotate bitmap 180 deg  (handles reverses)
	//		2) rotate 90 and 270  (handles picture rotated wrong)
	//		3) recalc w/ diff factors:   lineWidthOffset, RGBcutoffOffset?, etc  (minWidthOffset) 
	//
	//		or pick a scan height above and below midheight?			
	}
//=====================================================================================	
	
	static public String calcBarcodeNumber (Bitmap image) {
		
		if (image == null) {
			return "null image";
		}
		
		width  = image.getWidth();
		height = image.getHeight();
		midHeight = height/2;
		
		if (width > 4099) {    // too many pixels in picture width
			return "too many pixels wide";
		}
		
		int color		= 0;
		int imin = width / 4;
		int imax = 3*width / 4;
				
		// ================================ get all pixel data		

		for (int i=0; i<width; i++) {
			int redSum 		= 0;
			int greenSum 	= 0;
			int blueSum 	= 0;			
			for (int j=midHeight-2; j<midHeight+3; j++) {   // FIX spread the pixels out vertically
				color = image.getPixel(i,j);
				redSum 	= redSum 	+ Color.red(color); 
				greenSum= greenSum 	+ Color.green(color); 
				blueSum = blueSum 	+ Color.blue(color); 
			}
			redVal[i]   = redSum / 5;
			greenVal[i] = greenSum / 5;
			blueVal[i]  = blueSum / 5;
		}
		
		// ================================ calc average RGB values at mid-area 
		int redSum 		= 0;
		int greenSum 	= 0;
		int blueSum 	= 0;
		for (int i=imin; i<imax; i++) {
			redSum = redSum + redVal[i];
			greenSum = greenSum + greenVal[i];
			blueSum = blueSum + blueVal[i];
		}	
		redAverage 	= redSum / (imax-imin);
		greenAverage= greenSum / (imax-imin);
		blueAverage = blueSum / (imax-imin); 			
				
		// ================================ build up array pixBW of 0 (black) & 1(white):		
		for (int i=0; i<width; i++) {	   // any 2 of 3 above cutoff = white
			pixBW[i] = 0;  // black
			if (   (redVal[i]>redAverage && greenVal[i]>greenAverage)   
				|| (redVal[i]>redAverage && blueVal[i]>blueAverage) 
				|| (greenVal[i]>greenAverage && blueVal[i]>blueAverage) ) {
				pixBW[i] = 1;  // white  
			}
		}		
	
		// ============================================= build up pixSwitches array
		//    scan thru pixBW array & note how many pixels till change in B/W:
		int count = 1;
		int numSwitches = 0;
		int previousPix = pixBW[0];
		for (int ii=1; ii<width-1; ii++) {			
			if (pixBW[ii] == previousPix) {
				count++;
			}
			else {
				pixSwitches[numSwitches] = count;
				count = 1;
				previousPix = pixBW[ii];
				numSwitches++;
			}
		pixSwitches[numSwitches] = count;         // get last one
		}
				
		// return if bad picture of barcode 
		if (numSwitches>130) {    	 
			return ("too many lines " + numSwitches);
		}		
		if (numSwitches<30) {   	 
			return ("too few lines " + numSwitches);
		}
		
		// =================================================== eliminate any small changes (1-5) in pixSwitches
		int numberCondensed = 1;
		condensedPixSwitches[0] = pixSwitches[0];    // set 1st one
		for (int i=1; i<numSwitches-1; i++) {	// skip 1st & last
			if (pixSwitches[i] < 6) {			
				condensedPixSwitches[numberCondensed-1] = pixSwitches [i-1] + pixSwitches[i] + pixSwitches[i+1];
				i++;
				i++;
			}
			else {
				condensedPixSwitches[numberCondensed] = pixSwitches[i]; 
				numberCondensed++;
			}
		}		
		condensedPixSwitches[numberCondensed] = pixSwitches[numberCondensed];    // set last one
		numberCondensed++;
		numSwitches = numberCondensed;
		
		// ============================================ calculate minWidth number by averaging middle 4 numbers
		float averWidth=999;
		int midNumSwitch = numSwitches/2;
		float tempf = 0;
		for (int i=midNumSwitch-8; i<midNumSwitch+8; i++) {
			tempf = tempf + condensedPixSwitches[i];
		}
		tempf = tempf / 28f; 	// middle 4 numbers (= 4 * 7 = 28 linewidths)
		tempf = tempf * averWidthOffset;		// 1st small correction (about 1.06)
		averWidth = (int) tempf;
		
		// normalize pixSwitch widths: 
		for (int iii=0; iii<numSwitches; iii++) { 							// 2nd small correction (about +0.2)
			lineWidthf[iii] =        ((condensedPixSwitches[iii] / averWidth) + lineWidthOffset);
			lineWidth [iii] = (int)  ((condensedPixSwitches[iii] / averWidth) + lineWidthOffset);
		}
		
		// ======================================================== CLEAN-UP LINEWIDTHS ARRAY:
		//  delete last large block and all after 
		//    (currently assume only 1 large block at begin and end.) 
		int midSwitch = numSwitches/2;
		for (int i=midSwitch; i<numSwitches; i++) {
			if (lineWidth[i] > 4) {
				numSwitches = i;
			}
		}
			
		//strip out all up to & including 1st large block, 
		int deleteNum = -1;
		for (int i=midSwitch; i>=0; i--) {   // start in middle and move in reverse
			if (lineWidth[i] > 4) {
				deleteNum = i+1;
				i=-1;
			}
		}		
		// shift all lineWidths upward by deleteNum
		numSwitches = numSwitches - deleteNum;
		for (int i=0; i<numSwitches; i++) {
			lineWidth[i] = lineWidth[i+deleteNum];
		}
		
		//  check for standard intro 1-1-1, middle 1-1-1-1-1, & ending 1-1-1:
		if ((lineWidth[0] ==1) && (lineWidth[1] ==1) && (lineWidth[2] ==1) &&
			(lineWidth[27]==1) && (lineWidth[28]==1) && (lineWidth[29]==1) && (lineWidth[30]==1) && (lineWidth[31]==1) &&
			(lineWidth[56]==1) && (lineWidth[57]==1) && (lineWidth[58]==1)) {

			BarcodeManagerActivity.bType = "STANDARD";
			// shift all lineWidths upward by 3
			numSwitches = numSwitches - 3;
			for (int i=0; i<numSwitches; i++) {
				lineWidth[i] = lineWidth[i+3];
			}

			// shift 29-52 upward by 5 to 24-47
			numSwitches = numSwitches - 5;
			for (int i=24; i<numSwitches; i++) {
				lineWidth[i] = lineWidth[i+5];
			}
			
			numSwitches = numSwitches - 3;   // drop last 3 lines
		}  // end STANDARD
		else {
			BarcodeManagerActivity.bType = "NONSTANDARD";
			// maybe return NONSTANDARD;   or  call method  decodeNonStandard
		}
				
		// ================================== convert pixSwitches to standard Barcode 12-digits:
		//	0 = 3-2-1-1		5 = 1-2-3-1
		//	1 = 2-2-2-1		6 = 1-1-1-4
		//	2 = 2-1-2-2		7 = 1-3-1-2
		//	3 = 1-4-1-1		8 = 1-2-1-3
		//	4 = 1-1-3-2		9 = 3-1-1-2 		
		int d[] = new int[20];   // final digits array		
		int digitNum = 0;
		int fourDigit=0;
		
		// get every four linewidths at 0,4,8,12,16,20,24,28,32,36,40,44.
		for (int i=0; i<numSwitches; i++) {
			fourDigit = lineWidth[i+0]*1000 + lineWidth[i+1]*100 + lineWidth[i+2]*10 + lineWidth[i+3];
			switch (fourDigit)  {
			case 3211:
				d[digitNum] = 0;
				break;
			case 2221:
				d[digitNum] = 1;
				break;
			case 2122:
				d[digitNum] = 2;
				break;
			case 1411:
				d[digitNum] = 3;
				break;
			case 1132:
				d[digitNum] = 4;
				break;
			case 1231:
				d[digitNum] = 5;
				break;
			case 1114:
				d[digitNum] = 6;
				break;
			case 1312:
				d[digitNum] = 7;
				break;
			case 1213:
				d[digitNum] = 8;
				break;
			case 3112:
				d[digitNum] = 9;
				break;
			default:
				d[digitNum] = -1;
				break;			
			}
			digitNum++;
			i++;
			i++;
			i++;
		} // end for loop
		
										
		// convert barcode numbers to a 12-char string
		String barcodeString = "";
		for (int i=0; i<numSwitches/4; i++) { 
			if (d[i] == -1) {
				barcodeString = barcodeString + "x";  
			}
			else {
				barcodeString = barcodeString + "" + Integer.toString(d[i]);  		
			}
		}
		
		// QUALITY CHECKS:

		// verify barcodeString has no negative sign (x's) and is 12 char long
		for (int i=0; i<numSwitches/4; i++) {
			if (d[i] == -1) {
				return "Can't decipher barcode " + barcodeString;
			}
		}

		// verify number
		int iflag =Barcode.verifyBarcode (barcodeString);	
		if (iflag == 1) {
			// supposedly a good valid barcode
			// FIX play a good sound!  (a C strum)
			// set type = STANDARD?
			return barcodeString;
		}
					
// FIX		// swap number and re-verify
		String reversedBarcodeString = new StringBuilder(barcodeString).reverse().toString();
		int iflag2 = Barcode.verifyBarcode (reversedBarcodeString);
		if (iflag2 == 1) {
			// supposedly a good valid barcode
			// FIX play a good sound!  (a C strum)
			// set type = STANDARD?
			return reversedBarcodeString;
		}
		
		return "Checkdigit "+d[11]+" invalid for " + barcodeString;
			 
	}  // end calcBarcodeNumber
	
//=============================================================================================	

	static public void drawDecodeBarcode () {
		// ADD vert text along left :   "BARS     PICT     RGB"
		
		if (BarcodeManagerActivity.mbitmap == null) {
			return;
		}
		int 	cWidth  = BarcodeManagerActivity.mbitmap.getWidth();
		int 	cHeight = BarcodeManagerActivity.mbitmap.getHeight();
		float 	cWidthf  = cWidth; 
		float 	cHeightf = cHeight;
		float 	cmidHeight = cHeightf/2f;
		float 	xx, yy, yy2, temp;
		float 	size = 12;		// was 3     #pixels square to color
		float 	shiftUp = 0.20f * cHeightf;	// shifts things uo 20% to allow room for pic and B/W at bottom
		float	averWidthDraw = 12;			// width of averageRGB lines 
		
		try { 
			System.gc();
			BarcodeManagerActivity.plotBitmap = Bitmap.createBitmap(cWidth, cHeight, Config.RGB_565);  // 16-bit pixel format
		}
		catch (Throwable t) {
			///Toast.makeText(getApplicationContext(), "Error opening plotBitmap", 2000).show();
			return;
		}
		Canvas canvas = new Canvas(BarcodeManagerActivity.plotBitmap);
			
		//  ADD  randomly blink pixels white for a while
		//
		//  canvas goes from 0 to 'bitmapsize'(float), and starts at top-left (0,0).
		//
		// ------------------- Plots to draw:
		//		Raw		locally		B/W		B/W		linewidth
		//				normalized	comps	
		//		R,G,B	R,G,B		R,G,B	x		x
		
		// Ref http://android.nakatome.net/2010/04/graphics-basics.html	
		Paint redPaint   = new Paint();
		Paint greenPaint = new Paint();
		Paint bluePaint  = new Paint();
		Paint blackPaint = new Paint();
		Paint whitePaint = new Paint();
		Paint grayPaint  = new Paint();
		Paint ltgrayPaint= new Paint();
		Paint mydarkgrayPaint= new Paint();
		Paint BWPaint	 = new Paint();
		Paint transparentPaint = new Paint();

		redPaint.setColor	(Color.RED);
		greenPaint.setColor	(Color.GREEN);
		bluePaint.setColor	(0xFF5555FF);		// light BLUE);
		blackPaint.setColor	(Color.BLACK);
		whitePaint.setColor	(Color.WHITE);
		grayPaint.setColor	(Color.GRAY);
		ltgrayPaint.setColor(Color.LTGRAY);
		mydarkgrayPaint.setColor(0xFF222222);
		transparentPaint.setColor (Color.TRANSPARENT);

		// set background black
		canvas.drawColor(Color.BLACK);	// Fill canvas 0xFFFF0000
		
		// 'draw' partial picture of barcode
		float left = 0;
		float top =  cmidHeight * 0.80f;
		canvas.drawBitmap (BarcodeManagerActivity.mbitmap, left, top, blackPaint);

		// mask off all of barcode picture except middle
		canvas.drawRect(0, 0,  cWidthf-1f, cHeightf * 0.80f, blackPaint);  	// (left, top, right, bottom)  all floats
		///canvas.drawRect(0, cmidHeight+0, cWidth-1, cHeightf,	 	 blackPaint);
		/// canvas.drawRect(0,0,   cWidth,100, transparentPaint);
							
		// draw all R,G,B,B-W data
		for (int i=0; i<cWidth-4; i++) {				
			xx = (float) i;
			// red
			temp = DecodeBarcode.redVal[i];		// redVal is 0-255
			yy = (1f - temp / 255f) * cHeightf - shiftUp;					
			canvas.drawRect(xx, yy, xx+size, yy+size, redPaint);
			// green
			temp = DecodeBarcode.greenVal[i];
			yy = (1f - temp / 255f) * cHeightf - shiftUp;				
			canvas.drawRect(xx, yy, xx+size, yy+size, greenPaint);
			// blue
			temp = DecodeBarcode.blueVal[i];
			yy = (1f - temp / 255f) * cHeightf - shiftUp;				
			canvas.drawRect(xx, yy, xx+size, yy+size, bluePaint);
			
			// black/white
			temp = DecodeBarcode.pixBW[i];
			yy 	= 0.90f * cHeightf;
			yy2 = 0.999f * cHeightf;	
			if (temp == 0) {
				canvas.drawRect(xx, yy, xx+1, yy2, mydarkgrayPaint);
				} else {
				canvas.drawRect(xx, yy, xx+1, yy2, whitePaint);
			}	
		} // end for loop

		// ADD  drawBarcode bars from .95 to .999 of canvas
		//   need to calc where drawBarcode starts and stops in x-direction 
		
		// draw RGB average lines
		temp = DecodeBarcode.redAverage;
		yy = (1f - temp/255f) * cHeightf - shiftUp;
		canvas.drawRect (1f, yy, cWidthf-2f, yy+averWidthDraw,	redPaint);		// (left, top, right, bottom, paint)
		//canvas.drawLine (1f, yy, cWidthf-2f, yy+5f,	redPaint);		// (float startX, float startY, float stopX, float stopY, Paint paint)

		temp = DecodeBarcode.greenAverage;
		yy = (1f - temp/255f) * cHeightf - shiftUp;
		canvas.drawRect (1f, yy, cWidthf-2f, yy+averWidthDraw,	greenPaint);

		temp = DecodeBarcode.blueAverage;
		yy = (1f - temp/255f) * cHeightf - shiftUp;
		canvas.drawRect (1f, yy, cWidthf-2f, yy+averWidthDraw,	bluePaint);				

		yy = 0;
		canvas.drawRect (1f, yy, cWidthf-2f, yy+averWidthDraw,	whitePaint);	// top line			
	
		// draw side vertical B-W continuum
		for (int j=0; j<cHeight-1; j++) {			
			yy = j;
			int icolor = ((cHeight-j) *255) / cHeight;			
			int BWcolor = Color.rgb (icolor, icolor, icolor);	// RGB
			BWPaint.setColor(BWcolor);
			canvas.drawLine(0, yy, 25, yy, BWPaint);
			canvas.drawLine(cWidthf-25f, yy, cWidthf-1f, yy, BWPaint);
		}
	}  // end drawDecodeBarcode
}	