package com.itheima.qqslidemenu78;

import com.itheima.qqslidemenu78.SlideMenu.MenuState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout{

	
	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		SlideMenu slideMenu = (SlideMenu)this.getParent();
		if(slideMenu.getCurrentState() == MenuState.OPEN){//当slidemenu打开时，拦截触摸事件
			return true;
		}else{//否则不拦截
			return super.onInterceptTouchEvent(ev);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SlideMenu slideMenu = (SlideMenu)this.getParent();
		if(slideMenu.getCurrentState() == MenuState.OPEN){
			if(event.getAction() == MotionEvent.ACTION_UP){//当手指抬起时，关闭slidemenu
				slideMenu.closeSlideMenu();
			}
			return true;
		}else{
			return super.onInterceptTouchEvent(event);
		}
	}
}
