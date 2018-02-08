package com.xiangzi.screenelf;


import java.io.IOException;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiangzi.screenelf.Util.ElfUtil;

import pl.droidsonroids.gif.GifDrawable;

public class PetElf extends View{
	final Point size = new Point();
	private int petW = 0;
	public static final String OPERATION = "operation";
	public static final int OPERATION_SHOW = 100;
	public static final int OPERATION_HIDE = 101;
	private static final int HANDLE_CHECK_ACTIVITY = 200;

	public static final float g = 9800f;
	public static final float m = 1000f;
	public static final float k = 5f;
	public static final float fs = 4000f;

	private boolean isAdded = false;

	public static final int SPEECH_START = 10000;
	public static final int RECOGNIZE_RESULT = 10001;
	public static final int RECOGNIZE_START = 10002;
	public static final int RECOGNIZE_DISMISS = 10003;
	public static final int TIMER_START = 10004;
	public static final int TIMER_STOP = 10005;
	public static final int RUN_LEFT = 10006;
	public static final int RUN_RIGHT = 10007;
	public static final int SLEEP = 10008;
	public static final int FLY = 10009;

	private View talkview ,elfView;
	ImageView elfImView ;
	private boolean isPushing=false;
	private Context context;
	private WindowManager wm;
	private WindowManager.LayoutParams params,talkParams,sampleParams;
	private SharedPreferences preferences;
	private  Editor edit;
	private String walkToLeftAnimationPath;
	private String walkToRightAnimationPath;
	private String talkAnimationPath;
	private String pushAnimationPath;
	private String flyAnimationPath;
	private String successAnimationPath;
	private TextView text;
	private String[] stayAnimationPath;
	private String speak;
	private LineView sampleView;
	private float elasticX ,elasticY;
	private GifDrawable hangUpDrawable ,
			walkLeftGifDrawable ,
			stayAnimation ,
			walkRightGifDrawable ,
			flyDrawable,
			successDrawable;

	public PetElf(final Context context ,ImageView elfImView,View elfView,View talkview, TextView text){
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();
		talkParams = new WindowManager.LayoutParams();
		sampleParams = new WindowManager.LayoutParams();
		wm.getDefaultDisplay().getSize(size);
		preferences = context.getSharedPreferences("pet", 0);
		edit = preferences.edit();
		this.elfView = elfView;
		this.elfImView = elfImView;
		this.text=text;
		this.talkview= talkview;
		sampleView = new LineView(context);

	}

	public void Go(){
		try {
			hangUpDrawable = new GifDrawable( context.getAssets(), getPushAnimationPath());
			walkLeftGifDrawable = new GifDrawable( context.getAssets(), getWalkToLeftAnimationPath());
			walkRightGifDrawable = new GifDrawable( context.getAssets(), getWalkToRightAnimationPath() );
			flyDrawable = new GifDrawable( context.getAssets(), getFlyAnimationPath() );
			successDrawable= new GifDrawable( context.getAssets(), getSuccessAnimationPath() );
		} catch (IOException e) {
			e.printStackTrace();
		}
		touch();
		mHandler.sendEmptyMessage(PetElf.TIMER_START);


		createSampleView();
		createBodyView();
		createTalkView();


	}
	private void touch(){
		elfView.setOnTouchListener(new OnTouchListener() {
			int lastX, lastY ,dx ,dy;
			int paramX, paramY;
			long downTime ,upTime;
			boolean isUpToLine = false;

			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						mHandler.sendEmptyMessage(PetElf.TIMER_STOP);
						downTime=System.currentTimeMillis();
						lastX = (int) event.getRawX();
						lastY = (int) event.getRawY();
						paramX = params.x;
						paramY = params.y;
						sampleView.setVisibility(VISIBLE);

						break;
					case MotionEvent.ACTION_MOVE:

						HangUp();
						isPushing=true;
						dx = (int) event.getRawX() - lastX;
						dy = (int) event.getRawY() - lastY;
						params.x = paramX + dx;
						params.y = paramY + dy;
						float controlX = (params.x-params.width*1.1f)-sampleParams.x+size.x*0.5f;
						float controlY = (params.y+params.height*1.3f)-sampleParams.y;
						sampleView.setControlPoint(controlX,controlY);
						wm.updateViewLayout(elfView, params);
						break;
					case  MotionEvent.ACTION_UP:
						sampleView.setVisibility(GONE);
						isPushing=false;
						upTime=System.currentTimeMillis();
						elasticX = (ElfUtil.getV(params.x));
						elasticY = - ElfUtil.getV((params.y+params.height*1.3f)-sampleParams.y);
						Log.e("elf",""+elasticY);
						if(elasticY<0&&((params.y+params.height*1.3f)-sampleParams.y)>0){
							mHandler.sendEmptyMessage(PetElf.FLY);
						}else {
							mHandler.sendEmptyMessage(PetElf.TIMER_START);
						}

						break;
				}
				return true;
			}
		});
	}

	public void Happend(){
		mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
		mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
	}

	public void Dismiss(){
		mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
		elfView.setVisibility(GONE);
		talkview.setVisibility(GONE);
		wm.removeView(elfView);
		wm.removeView(talkview);
		isAdded = false;
	}

	private void setImageHWbyGifDrawable(GifDrawable gifDrawable){
		elfImView.setImageDrawable(gifDrawable);
		float hangUpDrawableW = hangUpDrawable.getIntrinsicWidth();
		params.width = (int) (petW*(gifDrawable.getIntrinsicWidth()/hangUpDrawableW));
		params.height = params.width;
		elfView.setVisibility(VISIBLE);
		wm.updateViewLayout(elfView, params);
		RelativeLayout.LayoutParams linearParams =(RelativeLayout.LayoutParams) elfImView.getLayoutParams();
		linearParams.width = params.width;
		linearParams.height = params.height;
		elfImView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
	}


	public void WalkToLeft(){
		walkLeftGifDrawable.getIntrinsicHeight();
		setImageHWbyGifDrawable(walkLeftGifDrawable);
		mHandler.sendEmptyMessage(PetElf.RUN_LEFT);

	}

	public void WalkToRight(){
		setImageHWbyGifDrawable(walkRightGifDrawable);
		mHandler.sendEmptyMessage(PetElf.RUN_RIGHT);
	}

	public void Sleep(){
		int radom = new Random().nextInt(getStayAnimationPath().length);
		try {
			stayAnimation = new GifDrawable( context.getAssets(), getStayAnimationPath()[radom]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setImageHWbyGifDrawable(stayAnimation);
		mHandler.sendEmptyMessage(PetElf.SLEEP);
	}

	public void HangUp(){
		if(!isPushing){
			setImageHWbyGifDrawable(hangUpDrawable);
		}
	}
	public void Fly(){
		setImageHWbyGifDrawable(flyDrawable);
	}
	public void Success(){
		setImageHWbyGifDrawable(successDrawable);
	}
	public void Talk(final String s ,TextView text){
		if(params.x<0/2&&params.y<0){
			talkParams.x = params.x + params.width/2;
			talkParams.y = params.y + params.width/2;
			wm.updateViewLayout(talkview, talkParams);
		}else if(params.x>0/2&&params.y<0){
			talkParams.x = params.x - params.width/2;
			talkParams.y = params.y + params.width/2;
			wm.updateViewLayout(talkview, talkParams);
		}else if(params.x>0/2&&params.y>0){
			talkParams.x = params.x - params.width/2;
			talkParams.y = params.y - params.width/2;
			wm.updateViewLayout(talkview, talkParams);
		}else if(params.x<0/2&&params.y>0){
			talkParams.x = params.x + params.width/2;
			talkParams.y = params.y - params.width/2;
			wm.updateViewLayout(talkview, talkParams);
		}
		if(s!=null){
			(talkview).setVisibility(View.VISIBLE);
			text.setText(s);
			int textSize = (int)(text).getPaint().getTextSize();
			talkParams.width =textSize*8+10;
			talkParams.height =((text).getText().toString().toCharArray().length/8+5)*(talkParams.width-10)/8 +10;
			wm.updateViewLayout(talkview, talkParams);
		}

	}

	public void dismissTalk(){
		System.out.println("说完了");
		(talkview).setVisibility(View.GONE);
		Sleep();
	}


	public String getSpeak() {
		return speak;
	}

	public void setSpeak(String speak) {
		this.speak = speak;
	}

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi") private void createBodyView() {
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		params.format = PixelFormat.RGBA_8888; // 设置图片
		// 格式，效果为背景透明
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;


		petW = size.x/5;
		params.width = petW;
		params.height = petW;
		params.x = 0;
		params.y = 0;
		elfView.setVisibility(VISIBLE);
		wm.addView(elfView, params);
		isAdded = true;
	}

	@SuppressLint("NewApi") private void createTalkView(){

		talkParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		talkParams.format = PixelFormat.RGBA_8888;
		talkParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		talkParams.width =1;
		talkParams.height =1;
		talkview.setVisibility(VISIBLE);
		wm.addView(talkview, talkParams);
	}

	@SuppressLint("NewApi") private void createSampleView(){

		sampleParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		sampleParams.format = PixelFormat.RGBA_8888;

		sampleParams.x = 0;
		sampleParams.y = (int) (size.y*0.25f)+size.x/5;
		sampleParams.width =size.x;
		sampleParams.height = (int) (size.y*0.25f);
		sampleView.setVisibility(GONE);
		wm.addView(sampleView, sampleParams);
	}
	@SuppressLint("HandlerLeak") private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case HANDLE_CHECK_ACTIVITY:
					if(ElfUtil.isHome(context)) {
						if(!isAdded) {
							wm.addView(elfView, params);
							wm.addView(talkview, talkParams);
							isAdded = true;
						}
					} else {
						if(isAdded) {
							wm.removeView(elfView);
							wm.removeView(talkview);
							isAdded = false;
						}
					}
					mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
					break;
				case PetElf.SPEECH_START:
					Talk((String) msg.obj,text);

					break;
				case PetElf.RECOGNIZE_RESULT:

					break;
				case PetElf.RECOGNIZE_START:

					break;
				case PetElf.RECOGNIZE_DISMISS:
					dismissTalk();
					break;
				case PetElf.TIMER_START:
					mHandler.removeMessages(PetElf.TIMER_START);
					int j = (int)(Math.random()*(3));
					switch (j){
						case 0:
							Sleep();
							break;
						case 1:
							mHandler.removeMessages(PetElf.RUN_LEFT );
							WalkToRight();
							break;
						case 2:
							mHandler.removeMessages(PetElf.RUN_RIGHT);
							WalkToLeft();
							break;
					}
//					Talk("c++"+j,text);
					mHandler.sendEmptyMessageDelayed(PetElf.TIMER_START, 5000+(int)Math.random()*3000);
					break;
				case PetElf.TIMER_STOP:
					mHandler.removeMessages(PetElf.TIMER_START);
					mHandler.removeMessages(PetElf.RUN_LEFT);
					mHandler.removeMessages(PetElf.RUN_RIGHT);
					break;
				case PetElf.RUN_LEFT:
					mHandler.removeMessages(PetElf.RUN_LEFT);
					params.x = params.x - ((int)Math.random()*2+1);
					wm.updateViewLayout(elfView, params);
					if(params.x-petW/2 < (- 400)){
						WalkToRight();
					}else {
						mHandler.sendEmptyMessageDelayed(PetElf.RUN_LEFT, 50);
					}
					break;
				case PetElf.RUN_RIGHT:
					mHandler.removeMessages(PetElf.RUN_RIGHT);
					params.x = params.x + ((int)Math.random()*2+1);
					wm.updateViewLayout(elfView, params);
					if(params.x >(400-petW/2)){
						WalkToLeft();
					}else {
						mHandler.sendEmptyMessageDelayed(PetElf.RUN_RIGHT, 50);
					}

					break;
				case PetElf.SLEEP:
					mHandler.removeMessages(PetElf.RUN_LEFT);
					mHandler.removeMessages(PetElf.RUN_RIGHT);
					break;
				case PetElf.FLY:
					Fly();
					int b = -1;

					params.x = (int) (params.x - (elasticX*0.02));
					params.y = (int) (params.y + (elasticY*0.02));
					if(Math.abs(elasticX)<30){
						elasticX = 0;
					}else {
						if(elasticX>0){
							elasticX = elasticX - fs*0.02f;
						}else {
							elasticX = elasticX + fs*0.02f;
						}
					}
					elasticY = elasticY  +g*0.02f;

					if((params.x)<(-400)||(params.x)>(400)){
						elasticX = -elasticX;
					}


					if(params.y<-800){
						if(petW + (params.y+800)>0){
							elfView.setAlpha((params.y+800f)/petW);
						}else {
							b = 0;
							params.x = 0;
							params.y = 0;
							params.height = petW;
						}
					}
					if(params.y+petW/2>800){
						if(Math.abs(elasticY)< 600){
							b = 1;
						}else {
							elasticY = -elasticY*0.5f;
						}

					}

					if (b==0){
						mHandler.removeMessages(PetElf.FLY);
						elfView.setRotation(0);
						elfView.setAlpha(1);
						Success();
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mHandler.sendEmptyMessage(TIMER_START);
							}
						},2000);
					}else if(b==1){
						elfView.setRotation(0);
						elfView.setAlpha(1);
						elfView.setVisibility(VISIBLE);
						wm.updateViewLayout(elfView, params);
						mHandler.sendEmptyMessage(TIMER_START);
					}else {
//						Log.e("elf","elasticX:"+elasticX+"...elasticY:"+elasticY+"...atan2"+ (float) (Math.atan2(elasticY,elasticX)*180/Math.PI)+
//								"。。。getRotation+"+elfView.getRotation());
						elfView.setRotation((float) (Math.atan2(elasticY,-elasticX)*180/Math.PI)+90);
						wm.updateViewLayout(elfView, params);
						mHandler.sendEmptyMessageDelayed(PetElf.FLY, 20);

					}

					break;
			}
		}
	};

	public static String getOPERATION() {
		return OPERATION;
	}

	public String getWalkToLeftAnimationPath() {
		return walkToLeftAnimationPath;
	}

	public void setWalkToLeftAnimationPath(String walkToLeftAnimationPath) {
		this.walkToLeftAnimationPath = walkToLeftAnimationPath;
	}

	public String getWalkToRightAnimationPath() {
		return walkToRightAnimationPath;
	}

	public void setWalkToRightAnimationPath(String walkToRightAnimationPath) {
		this.walkToRightAnimationPath = walkToRightAnimationPath;
	}

	public String getTalkAnimationPath() {
		return talkAnimationPath;
	}

	public void setTalkAnimationPath(String talkAnimationPath) {
		this.talkAnimationPath = talkAnimationPath;
	}

	public String getPushAnimationPath() {
		return pushAnimationPath;
	}

	public void setPushAnimationPath(String pushAnimationPath) {
		this.pushAnimationPath = pushAnimationPath;
	}

	public String getFlyAnimationPath() {
		return flyAnimationPath;
	}

	public void setFlyAnimationPath(String flyAnimationPath) {
		this.flyAnimationPath = flyAnimationPath;
	}

	public String[] getStayAnimationPath() {
		return stayAnimationPath;
	}

	public void setStayAnimationPath(String[] stayAnimationPath) {
		this.stayAnimationPath = stayAnimationPath;
	}
	public String getSuccessAnimationPath() {
		return successAnimationPath;
	}

	public void setSuccessAnimationPath(String successAnimationPath) {
		this.successAnimationPath = successAnimationPath;
	}

}
