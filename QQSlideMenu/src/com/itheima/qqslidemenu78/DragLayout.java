package com.itheima.qqslidemenu78;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class DragLayout extends ViewGroup {

	
	
	private View redView;//红色子view
	private View blueView;//蓝色子view

	private ViewDragHelper mDragHelper;//处理父view控制子view移动的核心类
	
	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DragLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DragLayout(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		//1.实例化一个ViewDragHelper对象
		mDragHelper = ViewDragHelper.create(this, callback);
	}
	
	//2.将处理触摸事件的方法，交给ViewDragHelper处理
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
	//3.创建一个监听器，注册给ViewDragHelper
	ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

		/**
		 * 是否处理手指在子view上的触摸事件。返回true的时候标识处理     false则不处理
		 * 
		 * child：手指触摸到的那个view
		 */
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child == redView || child == blueView;
		}
		/**
		 * 控制子view的水平方向移动
		 * left ： 是子View的新的left值
		 * dx ：每次手指移动的增量值。
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(left < 0 ){//控制view移动左侧边界
				left = 0;
			}else if(left > DragLayout.this.getMeasuredWidth() - child.getMeasuredWidth()){//控制view移动右侧边界
				left = DragLayout.this.getMeasuredWidth() - child.getMeasuredWidth();
			}
			return left ;
		}
		/**
		 * 控制子view的垂直方向的移动
		 */
		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			if(top < 0 ){
				top = 0;
			}else if(top >  DragLayout.this.getMeasuredHeight() - child.getMeasuredHeight()){
				top = DragLayout.this.getMeasuredHeight() - child.getMeasuredHeight();
			}
			return top;
		}
		/**
		 * 当一个view移动时候，回调此方法
		 * left ：这个view的新的left
		 * dx ： 手指每次移动增量值
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			if(changedView == blueView){
				
				int newLeft = redView.getLeft()+dx;
				
				if(newLeft < 0 ){
					newLeft = 0;
				}else if(newLeft >  DragLayout.this.getMeasuredWidth() - redView.getMeasuredWidth()){
					newLeft = DragLayout.this.getMeasuredWidth() - redView.getMeasuredWidth();
				}
				redView.layout(newLeft, redView.getTop()+dy,
						redView.getLeft()+dx + redView.getMeasuredWidth(), 
						redView.getTop()+dy + redView.getMeasuredHeight());
			}
			
			//处理动画基于的变化值   范围 0---1
			float percent = redView.getLeft() * 1.0f / (DragLayout.this.getMeasuredWidth() - redView.getMeasuredWidth());
			excuteAnima(percent);
			
		}
		
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			int centerX = DragLayout.this.getMeasuredWidth()/2 - redView.getMeasuredWidth()/2;
			
			if(redView.getLeft() <= centerX){//向左缓缓移动
				
//				Log.e("tag","=============向左缓缓移动===========");
				mDragHelper.smoothSlideViewTo(releasedChild, 0, releasedChild.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			}else if(redView.getLeft() > centerX){//向右缓缓移动
				
//				Log.e("tag","=============向右缓缓移动===========");
				mDragHelper.smoothSlideViewTo(releasedChild, 
								DragLayout.this.getMeasuredWidth() - releasedChild.getMeasuredWidth(), 
								releasedChild.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			}
			
			
		}
	};
	
	public void computeScroll() {
		if(mDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(DragLayout.this);
		}	
	}
	
	
	/**
	 * 跟随手指移动，动态执行一些属性动画 
	 */
	protected void excuteAnima(float percent) {//percent 范围 0---1
//		Log.e("tag","========================"+percent);
//		ViewHelper.setTranslationX(redView, 50f * percent);
//		ViewHelper.setAlpha(redView, 1-percent);
//		ViewHelper.setRotationY(redView, 360f*percent);
//		ViewHelper.setScaleX(redView, 1f + 0.5f*percent);
		redView.setBackgroundColor((Integer) ColorUtil.evaluateColor(percent, Color.RED, Color.BLUE));
	}

	/**
	 * 在系统解析到DragLayout对应配置文件的标签（</com.itheima.qqslidemenu78.DragLayout>）
	 * 回调此方法
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		redView = this.getChildAt(0);
		blueView = this.getChildAt(1);
	}
	
	/**
	 * 这个方法即设置DragLayout的宽高规格，同时设置子view的宽高规格
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int redViewWidthSpec = MeasureSpec.makeMeasureSpec(redView.getLayoutParams().width, MeasureSpec.EXACTLY);
		int redViewHeightSpec = MeasureSpec.makeMeasureSpec(redView.getLayoutParams().height, MeasureSpec.EXACTLY);
		redView.measure(redViewWidthSpec, redViewHeightSpec);
		
		
		blueView.measure(redViewWidthSpec, redViewHeightSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int left = 0;//this.getMeasuredWidth()/2 - redView.getMeasuredWidth()/2;
		int top = 0;
		//redView.getWidth()在执行完onMeasure和onLayout方法之后，才能获得到值
		//redView.getMeasuredWidth()在onMeasure方法执行完后，就可以获得值
		redView.layout(left, top, 
				left + redView.getMeasuredWidth(), top+redView.getMeasuredHeight());
		
		blueView.layout(left, top + redView.getMeasuredHeight(), 
				left + blueView.getMeasuredWidth(), top + redView.getMeasuredHeight() + blueView.getMeasuredHeight());

	}
	

}
