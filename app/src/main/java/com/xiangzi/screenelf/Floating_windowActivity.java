package com.xiangzi.screenelf;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;




public class Floating_windowActivity extends Activity{

	boolean go = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (go){
			dismiss();
		}else {
			go();
		}
		finish();
	}
	public void go(){
		Intent show = new Intent(this, FloatingWindowService.class);
		show.putExtra(PetElf.OPERATION, PetElf.OPERATION_SHOW);
		startService(show);
	}
	public void dismiss(){
		Intent hide = new Intent(this, FloatingWindowService.class);
		hide.putExtra(PetElf.OPERATION, PetElf.OPERATION_HIDE);
		startService(hide);
	}
}