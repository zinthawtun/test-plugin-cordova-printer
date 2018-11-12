package hev.cordova.plugin.PrintUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import hev.cordova.plugin.android_serialport_api.SerialPort;

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
			arrayOfByte = paramString.getBytes("GBK");  //�������try�ڲſ���
		}
		catch (Exception   ex) {
			;
		}
		return arrayOfByte;
    	
    }

    public static byte[] CutPaper()   //��ֽ�� GS V 66D 0D
    {
      byte[] arrayOfByte =  new byte[] {0x1D, 0x56, 0x42, 0x00}; 
      return arrayOfByte;
    }

    public static byte[] PrintBarcode(String paramString) //���� GS k
    {
      byte[] arrayOfByte = new byte[13 + paramString.length()];
      //��������߶�
      arrayOfByte[0] = 0x1D;
      arrayOfByte[1] = 'h';
      arrayOfByte[2] = 0x60; //1��255
     
      //����������
      arrayOfByte[3] = 0x1D;
      arrayOfByte[4] = 'w';
      arrayOfByte[5] = 2; //2��6
      
      //�����������ִ�ӡλ��
      arrayOfByte[6] = 0x1D;
      arrayOfByte[7] = 'H';
      arrayOfByte[8] = 2; //0��3
      
      //��ӡ39����
      arrayOfByte[9] = 0x1D;
      arrayOfByte[10] = 'k';
      arrayOfByte[11] = 0x45;
      arrayOfByte[12] = ((byte)paramString.length());
      System.arraycopy(paramString.getBytes(), 0, arrayOfByte, 13, paramString.getBytes().length);
      return arrayOfByte;
    }
    
    public static byte[] setAlignCenter(char paramChar) //���� ESC a
    {
		byte[] arrayOfByte = new byte[3]; 
		arrayOfByte[0] = 0x1B;
		arrayOfByte[1] = 0x61;
		
		switch(paramChar)	//1-����룻2-���ж��룻3-�Ҷ���
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

    public static byte[] setBold(boolean paramBoolean)  //�Ӵ� ESC E
    {
      byte[] arrayOfByte = new byte[3]; 
      arrayOfByte[0] = 0x1B;
      arrayOfByte[1] = 0x45;
      if (paramBoolean)				//��ʾ�Ӵ�
      {  
    	  arrayOfByte[2] = 0x01;
      }
      else
      {
    	  arrayOfByte[2] = 0x00;  
      }
      return arrayOfByte;
     }
    
    public static byte[] setLineH(int h)  //�����иߣ�h 0-255
    {
      byte[] arrayOfByte = new byte[3]; 
      arrayOfByte[0] = 0x1B;
      arrayOfByte[1] = 0x33;
      arrayOfByte[2] = (byte)(h & 255);  
      return arrayOfByte;
     }

    public static byte[] setWH(char paramChar) //GS ! ���ô�ӡ�ַ���С
    {
		byte[] arrayOfByte = new byte[3]; //GS ! 11H ������		
		arrayOfByte[0] = 0x1D;
		arrayOfByte[1] = 0x21;
		
		switch(paramChar)	//1-�ޣ�2-����3-���ߣ� 4-������
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
		         ���ܣ����ô�ӡ����λ��
		         ������
		         int				�ڵ�ǰ�У���λ���λ�ã�ȡֵ��Χ0��576��
		         ˵����
				�����峣���С�£�ÿ����24�㣬Ӣ���ַ�12��
				��λ�ڵ�n�����ֺ���position=24*n
				��λ�ڵ�n������ַ�����position=12*n
	  ****************************************************************************/   
    
	public static byte[] setCusorPosition(int position)
	{
		byte[] returnText = new byte[4]; //��ǰ�У����þ��Դ�ӡλ�� ESC $ bL bH
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
					
			oldText=getGbk("�����ţ�");
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
			oldText=getGbk(strTmp+"��ӡ\n\n");
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
			
			oldText = getGbk("���������");
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
			oldText = getGbk("����ʱ�䣺"+strTmp+" �����ʹ�\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText = getGbk("���͵�ַ���������Ƽ���·6�Ŵ�ҵ����\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = getGbk("�ƺ�绰������������ 13501234567\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setWH('1'); 
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;

			oldText = getGbk("        Ʒ��            ����    ����    ���\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = getGbk("----------------------------------------------\n");
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			
			oldText = setWH('3'); 
			System.arraycopy(oldText, 0, printText,  iNum,  oldText.length);
			iNum += oldText.length;
			//��Ʒ�����10�����֣�20���ַ������������8���ַ����������4���ַ���������8���ַ����м�ָ��2���ո�
			
			strTmp="��ݮ���̲���";			
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
			
			strTmp="����ˮ���������";
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
			
			strTmp="��Ƥ֥ʿ���";			
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
			
			strTmp="������ҵ���";			
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
			
			strTmp="�Ͳͷѣ�";			
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
			
			strTmp="�ܼƣ�";			
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
			
			oldText = getGbk("��ע�������һ���Ե���\n");
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
