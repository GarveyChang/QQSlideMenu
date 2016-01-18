package com.itheima.qqslidemenu78;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SlideMenu extends FrameLayout {

	
	private ViewDragHelper mDragHelper;
	private View mainView;//主界面
	private View menuView;//菜单界面
	private float dragRange;//右侧边界值
	private FloatEvaluator fe;//插值器
	
	public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		 init();
	}

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideMenu(Context context) {
		super(context);
		init();
	}

	private void init(){
		mDragHelper = ViewDragHelper.create(this, callback);
		fe = new FloatEvaluator();
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = mDragHelper.shouldInterceptTouchEvent(ev);
		return result;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDragHelper.processTouchEvent(event);
		return true;
	}
	
	ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
		
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child == mainView || child == menuView;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(child == mainView){
				if(left < 0 ){//控制左边界
					left = 0;
				}else if(left > dragRange){//控制右边界
					left = (int) dragRange;
				}
			}
			return left;
		}

		/**
		 * 拦击子view的触目事件
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return (int) dragRange;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			if(changedView == menuView){
				menuView.layout(0,0, menuView.getMeasuredWidth(),menuView.getMeasuredHeight());
				
				int newLeft = mainView.getLeft() + dx;
				
				if(newLeft < 0 ){//控制左边界
					newLeft = 0;
				}else if(newLeft > dragRange){//控制右边界
					newLeft = (int) dragRange;
				}
				
				mainView.layout(newLeft, mainView.getTop() + dy, 
						mainView.getLeft() + dx + mainView.getMeasuredWidth(), 
						 mainView.getTop() + dy + mainView.getMeasuredHeight());
			}
			
			float percent = mainView.getLeft() * 1.0f / dragRange;
			excuteAnima(percent);
			
			if(mainView.getLeft() == 0){//关闭状态
				if(slideMenuStateChangeListener!=null){
					slideMenuStateChangeListener.onClosed();
					currentState = MenuState.CLOSED;//记录当前状态
				}
			}else if(mainView.getLeft() == dragRange){//打开状态
				if(slideMenuStateChangeListener!=null){
					slideMenuStateChangeListener.onOpen();
					currentState = MenuState.OPEN;
				}
			}else{
				if(slideMenuStateChangeListener!=null){//滑动状态
					slideMenuStateChangeListener.onSliding(percent);
					currentState = MenuState.SLIDING;
				}
			}
		}

		/**
		 * 手指离开触摸的子view的时候，回调此方法
		 * xvel：水平方向的速度
		 * yvel：垂直方向的速度
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if(mainView.getLeft() <= dragRange/2){//往左缓缓移动
				closeSlideMenu();
			}else if(mainView.getLeft() > dragRange/2){//往右缓缓移动
				mDragHelper.smoothSlideViewTo(mainView, (int) dragRange, mainView.getTop());
				ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
			}
		}
	};
	
	//处理继续滑动的回调方法
	public void computeScroll() {
		if(mDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		}
	}
	/**
	 * 关闭slideMenu
	 */
	public void closeSlideMenu(){
		//启动执行缓缓滑动的方法
		mDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
		//刷新的方法，底层触发onDraw方法执行
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}
	
	protected void excuteAnima(float percent) {
		//水平方向和垂直方向缩放mainView
		ViewHelper.setScaleX(mainView, fe.evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleY(mainView, fe.evaluate(percent, 1.0f, 0.8f));
		
		//处理menuView的水平移动
		ViewHelper.setTranslationX(menuView, fe.evaluate(percent, -menuView.getMeasuredWidth()/2,0));
		//处理menuView的透明效果
		ViewHelper.setAlpha(menuView, fe.evaluate(percent, 0, 1.0f));
		
		//水平方向和垂直方向缩放mainView
		ViewHelper.setScaleX(menuView, fe.evaluate(percent, 0.6f, 1.0f));
		ViewHelper.setScaleY(menuView, fe.evaluate(percent, 0.6f, 1.0f));
	}

	/**
	 * 当执行完onMeasure方法后，立即执行此方法。
	 * 因此在此方法中，调用.getMeasuredHeight()正合适。
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		dragRange = this.getMeasuredWidth() * 0.6f;
	}
	
	protected void onFinishInflate() {
		mainView = getChildAt(1);
		menuView = getChildAt(0);
	}
	
	
	
	/** 
	 * slideMenu的打开，关闭状态监听器
	 */
	public interface SlideMenuStateChangeListener{
		public void onOpen();
		public void onClosed();
		public void onSliding(float percent);
	}
	
	private SlideMenuStateChangeListener slideMenuStateChangeListener;
	
	private MenuState currentState = MenuState.CLOSED;//初始情况下currentState是关闭状态
	
	
	/**
	 * 获得当前SlideMenu的状态
	 * @return
	 */
	public MenuState getCurrentState() {
		return currentState;
	}

	public void setSlideMenuStateChangeListener(SlideMenuStateChangeListener slideMenuStateChangeListener){
		this.slideMenuStateChangeListener = slideMenuStateChangeListener;
	}
	public enum MenuState{
		OPEN,CLOSED,SLIDING;
	}

}
























