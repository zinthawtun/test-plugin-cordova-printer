package com.example.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.example.citaqminipostest.R;
import com.example.util.MessageUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PaintView extends View {

	 private float mov_x;//�����������
	 private float mov_y;
	 private float mov_x2=0;//�����������
	 private float mov_y2=0;
	 private Paint paint,paint2,bgPaint;//��������
	 private Canvas canvas;//����
	 private Bitmap bitmap;//λͼ
	 private int mScreenWidth, mScreenHeight, mBarHeight;
	 
	public PaintView(Context context) {
		super(context);
		init();
	}

	public PaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	 public PaintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	 //�����¼�
	 @Override
	 public boolean onTouchEvent(MotionEvent event) {
	
	  float x1,y1,x2=mov_x2,y2=mov_y2;
	  if(event.getPointerCount()==2){
		  x2=event.getX(1);
		  y2=event.getY(1);
	  }
	  x1=event.getX(0);
	  y1=event.getY(0);
	  
	  if (event.getAction()==MotionEvent.ACTION_MOVE) {//����϶�
	   canvas.drawLine(mov_x, mov_y, x1, y1, paint);//����
	   canvas.drawLine(mov_x2, mov_y2, x2, y2, paint2);//���ڶ�����
	   invalidate();
	  }
	  if (event.getAction()==MotionEvent.ACTION_DOWN) {//������
	   mov_x= x1;
	   mov_y= y1;
	   canvas.drawPoint(mov_x, mov_y, paint);//����
	   mov_x2= x2;
	   mov_y2= y2;
	   canvas.drawPoint(mov_x2, mov_y2, paint2);//���ڶ���
	   invalidate();
	  }
	   mov_x= x1;
	   mov_y= y1;
	   
	   mov_x2= x2;
	   mov_y2= y2;
	  return true;
	 }
	 
	 //��ջ���
	 public void clear()  
	 {  	     
		 bitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888); //����λͼ�Ŀ��
		 canvas.setBitmap(bitmap);
		 
		 DrawBackGround();  
		    
	     
	     invalidate();  
	 }  
	 
	 //����λͼ
    public boolean storeImageToFile(String name){
    	if(bitmap == null){
    		return false;         
    	}         
    	File file = null;         
    	RandomAccessFile accessFile = null;         
    	ByteArrayOutputStream steam = new ByteArrayOutputStream();
    	bitmap.compress(Bitmap.CompressFormat.PNG, 100, steam); 
    	byte[] buffer = steam.toByteArray();          
    	try 
    	{             
    		file = new File(name);             
    		accessFile = new RandomAccessFile(file, "rw");             
    		accessFile.write(buffer);         
    	} 
    	catch (Exception e) 
    	{             
    		MessageUtil.toast(getContext(), e.toString());
    		return false;         
    	}         
    	try 
    	{             
    		steam.close();             
    		accessFile.close();         
    	} 
    	catch (IOException e) 
    	{             
    		MessageUtil.toast(getContext(), e.toString());
    		return false;         
    	}
    	MessageUtil.toast(getContext(),getContext().getString(R.string.save_info));
    	return true;     
    	} 
    
    //��λͼ
 		 @Override
	protected void onDraw(Canvas canvas) {
		//  super.onDraw(canvas);
		canvas.drawBitmap(bitmap,0,0,null);
	}
	
	private void init() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
	    mScreenWidth = dm.widthPixels;   //�����Ļ�Ŀ�
	    mScreenHeight = dm.heightPixels;  //�����Ļ�ĸ�
	    mBarHeight=0;
	    
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);//����һ������
		paint.setStyle(Style.FILL_AND_STROKE);//�����������
		paint.setStrokeWidth(4);//�ʿ�4����
		paint.setColor(Color.RED);//����Ϊ���
		paint.setAntiAlias(false);//�����ʾ
		
		paint2=new Paint(Paint.ANTI_ALIAS_FLAG);//����һ������
		paint2.setStyle(Style.FILL_AND_STROKE);//�����������
		paint2.setStrokeWidth(4);//�ʿ�4����
		paint2.setColor(Color.BLUE);//����Ϊ����
		paint2.setAntiAlias(false);//�����ʾ
		
		bgPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setColor(Color.GRAY);
		bgPaint.setStyle(Style.STROKE);
		bgPaint.setStrokeWidth(2);
		
	    canvas=new Canvas();
	    clear();
	}
	 
	private void DrawBackGround(){
		int i;
		for(i=60;i<mScreenWidth;i+=60){
			canvas.drawLine(i, 0, i, 1024, bgPaint);
		}
		for(i=0;i<mScreenHeight;i+=60){
			canvas.drawLine(0, i, 768, i, bgPaint);
		}
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.toolbar);
	    canvas.drawBitmap(bmp, 0, mScreenHeight-mBarHeight-bmp.getHeight(),null);
	}
}
