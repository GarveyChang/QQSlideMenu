package com.itheima.qqslidemenu78;

import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.qqslidemenu78.SlideMenu.SlideMenuStateChangeListener;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class MainActivity extends Activity {

	private ListView mainLv,menuLv;
	
	private SlideMenu slideMenu;
	private ImageView head;
	private FloatEvaluator fe;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		fe = new FloatEvaluator();
		mainLv = (ListView)findViewById(R.id.main_listview);
		
		menuLv = (ListView)findViewById(R.id.menu_listview);
		
		mainLv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.NAMES));
		menuLv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.sCheeseStrings){
					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						TextView tv = (TextView) super.getView(position, convertView, parent);
						tv.setTextColor(Color.WHITE);
						return tv;
					}
				});
		
		head = (ImageView)findViewById(R.id.iv_head);
		slideMenu = (SlideMenu) findViewById(R.id.slidemenu);
		slideMenu.setSlideMenuStateChangeListener(new SlideMenuStateChangeListener() {
			
			@Override
			public void onSliding(float percent) {
				Log.e("tag","================onSliding===========");
				//slideMenu滑动时，让head这个ImageView 有透明的动画效果
				ViewHelper.setAlpha(head, fe.evaluate(percent, 1.0f, 0));
			}
			
			@Override
			public void onOpen() {
				Log.e("tag","================onOpen===========");
				//slideMenu打开让menuListView 随机滚动
				menuLv.smoothScrollToPosition(new Random().nextInt(menuLv.getCount()));
			}
			
			@Override
			public void onClosed() {
				Log.e("tag","===============onClosed============");
				//slideMenu关闭时，让head这个ImageView 有一个水平来回移动效果
				ViewPropertyAnimator.animate(head)
				.translationX(10.0f)//让head水平方向移动10像素
				.setInterpolator(new CycleInterpolator(6))//让这个动画循环执行6次
				.setDuration(800)//动画的执行时间
				.start();
			}
		});
		
		
	}

	

}
