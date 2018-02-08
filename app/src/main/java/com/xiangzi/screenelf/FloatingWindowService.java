package com.xiangzi.screenelf;
import java.util.List;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiangzi.screenelf.Util.ElfUtil;

public class FloatingWindowService extends Service {

//	private static WindowManager wm;
//	private static WindowManager.LayoutParams params;
//	private static WindowManager.LayoutParams talkParams;
	private PetElf petElf;
	private View myElfView,talkview ;
	private List<String> homeList; // 桌面应用程序包名列表

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		homeList = ElfUtil.getHomes(this);
		
		myElfView =LayoutInflater.from(this).inflate( R.layout.petelf, null);
		talkview = LayoutInflater.from(this).inflate( R.layout.talkwindow, null);
		TextView talkrighttop_tx = (TextView) talkview.findViewById(R.id.talkrighttop_tx);
		ImageView elfbody = (ImageView) myElfView.findViewById(R.id.elfbody);

		petElf = new PetElf(getApplicationContext(),elfbody,myElfView,talkview,talkrighttop_tx);
		petElf.setPushAnimationPath("a80_3.gif");
		petElf.setStayAnimationPath(new String[]{"a80_1.gif","a80_2.gif","a80_5.gif","a80_6.gif","a80_8.gif","a80_9.gif"});
		petElf.setTalkAnimationPath("a80_3.gif");
		petElf.setSpeak("雅蠛蝶");
		petElf.setWalkToLeftAnimationPath("runL.gif");
		petElf.setWalkToRightAnimationPath("runR.gif");
		petElf.setFlyAnimationPath("fly.gif");
		petElf.setSuccessAnimationPath("success.gif");
		petElf.Go();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	




}





























