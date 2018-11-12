package com.example.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android_serialport_api.SerialPort;


public class PrintUtil
{
	private static SerialPort mSerialPortOp=null;
	
    public static SerialPort getSerialPort()
    		throws SecurityException, IOException, InvalidParameterException
	{
	    if (mSerialPortOp == null)
	      mSerialPortOp = new SerialPort(new File("/dev/ttyS1"), 115200, 0, true);
	    return mSerialPortOp;
	}
    
    public static void closeSerialPort()
    {
      if (mSerialPortOp != null)
      {
    	  mSerialPortOp.close();
    	  mSerialPortOp = null;
      }
    }
    
    public static byte[] getGbk(String paramString)
    {
		byte[] arrayOfByte = null;
		try 
		{
			arrayOfByte = paramString.getBytes("GBK");  //必须放在try内才可以
		}
		catch (Exception   ex) {
			;
		}
		return arrayOfByte;
    	
    }

    public static byte[] CutPaper()   //切纸； GS V 66D 0D
    {
      byte[] arrayOfByte =  new byte[] {0x1D, 0x56, 0x42, 0x00}; 
      return arrayOfByte;
    }

    public static byte[] PrintBarcode(String paramString) //条码 GS k
    {
      byte[] arrayOfByte = new byte[13 + paramString.length()];
      //设置条码高度
      arrayOfByte[0] = 0x1D;
      arrayOfByte[1] = 'h';
      arrayOfByte[2] = 0x60; //1到255
     
      //设置条码宽度
      arrayOfByte[3] = 0x1D;
      arrayOfByte[4] = 'w';
      arrayOfByte[5] = 2; //2到6
      
      //设置条码文字打印位置
      arrayOfByte[6] = 0x1D;
      arrayOfByte[7] = 'H';
      arrayOfByte[8] = 2; //0到3
      
      //打印39条码
      arrayOfByte[9] = 0x1D;
      arrayOfByte[10] = 'k';
      arrayOfByte[11] = 0x45;
      arrayOfByte[12] = ((byte)paramString.length());
      System.arraycopy(paramString.getBytes(), 0, arrayOfByte, 13, paramString.getBytes().length);
      return arrayOfByte;
    }
    
    public static byte[] setAlignCenter(char paramChar) //对齐 ESC a
    {
		byte[] arrayOfByte = new byte[3]; 
		arrayOfByte[0] = 0x1B;
		arrayOfByte[1] = 0x61;
		
		switch(paramChar)	//1-左对齐；2-居中对齐；3-右对齐
		{
			case '2':
				arrayOfByte[2] = 0x01;
				break;
			case '3':
				arrayOfByte[2] = 0x02;
				break;
			default:
				arrayOfByte[2] = 0x00;
				break;
		}
		return arrayOfByte;
    }

    public static byte[] setBold(boolean paramBoolean)  //加粗 ESC E
    {
      byte[] arrayOfByte = new byte[3]; 
      arrayOfByte[0] = 0x1B;
      arrayOfByte[1] = 0x45;
      if (paramBoolean)				//表示加粗
      {  
    	  arrayOfByte[2] = 0x01;
      }
      else
      {
    	  arrayOfByte[2] = 0x00;  
      }
      return arrayOfByte;
     }
    
    public static byte[] setLineH(int h)  //设置行高，h 0-255
    {
      byte[] arrayOfByte = new byte[3]; 
      arrayOfByte[0] = 0x1B;
      arrayOfByte[1] = 0x33;
      arrayOfByte[2] = (byte)(h & 255);  
      return arrayOfByte;
     }

    public static byte[] setWH(char paramChar) //GS ! 设置打印字符大小
    {
		byte[] arrayOfByte = new byte[3]; //GS ! 11H 倍宽倍高		
		arrayOfByte[0] = 0x1D;
		arrayOfByte[1] = 0x21;
		
		switch(paramChar)	//1-无；2-倍宽；3-倍高； 4-倍宽倍高
		{
			case '2':
				arrayOfByte[2] = 0x10;
				break;
			case '3':
				arrayOfByte[2] = 0x01;
				break;
			case '4':
				arrayOfByte[2] = 0x11;
				break;
			default:
				arrayOfByte[2] = 0x00;
				break;
		}
				
		return arrayOfByte;
    }
	 /***************************************************************************
		    add by yidie   2012-01-10     
		         功能：设置打印绝对位置
		         参数：
		         int				在当前行，定位光标位置，取值范围0至576点
		         说明：
				在字体常规大小下，每汉字24点，英文字符12点
				如位于第n个汉字后，则position=24*n
				如位于第n个半角字符后，则position=12*n
	  ****************************************************************************/   
    
	public static byte[] setCusorPosition(int position)
	{
		byte[] returnText = new byte[4]; //当前行，设置绝对打印位置 ESC $ bL bH
		returnText[0] = 0x1B;
		returnText[1] = 0x24;
		returnText[2]=(byte)(position%256);
		returnText[3]=(byte)(position/256);
		return returnText;
	}
   
    public static boolean printBytes(byte[] printText)
    {
    	boolean returnValue=true;
    	try
    	{
    		OutputStream mOutputStream=getSerialPort().getOutputStream();
    		
    		//printText=CutPaper();
    		mOutputStream.write(printText);
    	}
    	catch(Exception ex)
    	{
    		returnValue=false;
    	}
    	return returnValue;
    }
   
    public static boolean printString(String paramString)
    {
    	return printBytes(getGbk(paramString));
    }
    
    public static boolean printTest()
    {
    	boolean returnValue=true;
    	try
    	{
    		OutputStream mOutputStream=getSerialPort().getOutputStream();
			
			byte[] printText = new byte[4];
			
			printText[0] = 0x1F;
			printText[1] = 0x1B;
			printText[2] = 0x1F;
			printText[3] = 0x53;
    		mOutputStream.write(printText, 0, 4);
    	}
    	catch(Exception ex)
    	{
    		returnValue=false;
    	}
    	return returnValue;
    }

    public static boolean printDemo()
    {
    	boolean returnValue=true;
    	try
    	{
    		OutputStream mOutputStream=getSerialPort().getOutputStream();

			int iNum = 0;
			
			byte[] printText = new byte[1024];
			String strTmp="";
			
			byte[] oldText = setAlignCenter('1');
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText =setWH('3');
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;
					
			oldText=getGbk("订单号：");
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText = setWH('4'); 
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText=getGbk("0032");
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText = setWH('1'); 
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText = setCusorPosition(324); 
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			strTmp=new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE).format(new Date());
			oldText=getGbk(strTmp+"打印\n\n");
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setAlignCenter('2');
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setWH('4'); 
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setBold(true);
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = getGbk("川田体验店");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText = setWH('1'); 
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;		
			
			oldText = getGbk("\n\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setAlignCenter('1');
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setBold(false);
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setWH('3'); 
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			strTmp=new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE).format(new Date());
			oldText = getGbk("外送时间："+strTmp+" 尽快送达\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText = getGbk("外送地址：高新区科技中路6号创业大厦\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = getGbk("称呼电话：张三丰先生 13501234567\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setWH('1'); 
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText = getGbk("        品名            单价    数量    金额\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = getGbk("----------------------------------------------\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setWH('3'); 
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			//菜品名最多10个汉字（20个字符）；单价最多8个字符；数量最多4个字符；金额最多8个字符；中间分隔各2个空格
			
			strTmp="草莓酸奶布甸";			
			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			strTmp="12.00     1     12.00\n";
			oldText = setCusorPosition(552-12*strTmp.length()); 
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			strTmp="酸奶水果夹心面包";
			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			strTmp="12.00     2     24.00\n";
			oldText = setCusorPosition(552-12*strTmp.length()); 
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			strTmp="酥皮芝士面包";			
			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			strTmp="4.00     4     16.00\n";
			oldText = setCusorPosition(552-12*strTmp.length()); 
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			strTmp="香橙软桃蛋糕";			
			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			strTmp="8.00     3     24.00\n";
			oldText = setCusorPosition(552-12*strTmp.length()); 
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setWH('1'); 
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = getGbk("----------------------------------------------\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
		
			oldText = setWH('3'); 
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			strTmp="送餐费：";			
			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			strTmp="3.00\n";
			oldText = setCusorPosition(552-12*strTmp.length()); 
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			strTmp="总计：";			
			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			strTmp="10     79.00\n";
			oldText = setCusorPosition(552-12*strTmp.length()); 
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText=getGbk(strTmp);
			System.arraycopy(oldText, 0,  printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = getGbk("备注：请加送一次性刀叉\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setWH('1'); 
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setAlignCenter('2');
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = getGbk("\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			strTmp=new SimpleDateFormat("yyyyMMdd", Locale.SIMPLIFIED_CHINESE).format(new Date())+"0032";
			oldText = PrintBarcode(strTmp);
	        System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setAlignCenter('1');
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText = getGbk("\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = CutPaper();
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
    		
    		mOutputStream.write(printText);
    	}
    	catch(Exception ex)
    	{
    		returnValue=false;
    	}
    	return returnValue;
    }
    
}
