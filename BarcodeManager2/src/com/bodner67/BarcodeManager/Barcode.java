package com.bodner67.BarcodeManager;

import android.widget.Toast;
import android.content.Context;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.bodner67.BarcodeManager.*;

// github test 1

public class Barcode {

	//------------------------------------------------ FIELDS
	String barcodeName;
	String barcodeNumberString;
	String barcodeType; 		// UNKNOWN, STANDARD, NONSTANDARD

	//------------------------------------------------ METHODS
	
	public Barcode () {   // Constructor
		barcodeName = "no name";
		barcodeNumberString = "xxxxxxxxxxxx";
		barcodeType = "UNKNOWN";
	}
	
   	
   	public static String getCurrentBarcodeNumberString () {	 
		int ic = BarcodeManagerActivity.currentBarcode;
		return BarcodeManagerActivity.mBarcode[ic].barcodeNumberString;		
	}	
	
	public void setBarcodeName (String newName) {
		barcodeName = newName;
	}
		

	public static int verifyBarcode (String s) {   // 1=STANDARD; 0=NONSTANDARD
		int checkDigit = -1;
		int dd[] = new int[12];   // digits array			
		
		// verify barcode is 12 digits long
		int leng = s.length();
		if (leng != 12) {
			return 0;
		}
		
		// convert 12-string to int array & verify digits are 0-9 
		for (int i=0; i< 12; i++) {
			//String ssub = s.substring(i,i+1);
			char mChar = s.charAt(i);
			if ((mChar >='0' && mChar <= '9')) {
				dd[i] = Integer.valueOf (mChar) - 48;    // ASCII?
			} else {
				return 0;
			}
		}
	
		// verify checkdigit(last) is good
		int evens =	dd[1]+dd[3]+dd[5]+dd[7]+dd[9];
		int odds  = dd[0]+dd[2]+dd[4]+dd[6]+dd[8]+dd[10];
		int temp = odds*3 + evens;	
		while (temp > 10) {
			temp = temp - 10;
		}
		checkDigit = 10 - temp;	
		if (checkDigit == 10) {
			checkDigit = 0;
		}
		
		if (checkDigit == dd[11]) {			
			return 1;	// valid barcode
		} else {
			return 0;	// not a valid barcode
		}
	}
}	
