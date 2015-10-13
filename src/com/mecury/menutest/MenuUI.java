package com.mecury.menutest;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class MenuUI extends RelativeLayout{
	private Context context;
	private FrameLayout leftMenu;
	private FrameLayout middleMenu;
	private FrameLayout rightMenu;
	private FrameLayout middleMask;
	private Scroller mScroller;
	
	
	public MenuUI(Context context) {
		super(context);
		initView(context);
		
	}
	
	public MenuUI(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	//初始化
	private void initView(Context context){
		this.context=context;
		mScroller = new Scroller(context, new DecelerateInterpolator());    //第二个参数为渲染器
		leftMenu = new FrameLayout(context);
		middleMenu = new FrameLayout(context);
		rightMenu = new FrameLayout(context);
		middleMask = new FrameLayout(context);
		leftMenu.setBackgroundColor(Color.YELLOW);
		middleMenu.setBackgroundColor(Color.GREEN);
		rightMenu.setBackgroundColor(Color.YELLOW);
		middleMask.setBackgroundColor(0x88000000);
		addView(leftMenu);
		addView(middleMenu);
		addView(rightMenu);
		addView(middleMask);
		//设置透明度
		middleMask.setAlpha(0);  
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		middleMenu.measure(widthMeasureSpec, heightMeasureSpec);
		middleMask.measure(widthMeasureSpec, heightMeasureSpec);
		//得到菜单的宽的大小
		int realWidth = MeasureSpec.getSize(widthMeasureSpec);
		//令tempRealtempwidth等于菜单0.8倍的宽，用于设置左右菜单的宽度
		int tempRealWidth = MeasureSpec.makeMeasureSpec((int)(realWidth*0.8f),
				MeasureSpec.EXACTLY);
		//设置左右菜单的大小
		leftMenu.measure(tempRealWidth, heightMeasureSpec);
		rightMenu.measure(tempRealWidth, heightMeasureSpec);
	}
	
	public float onMiddleMask(){
		System.out.println("透明度："+middleMask.getAlpha());
		return middleMask.getAlpha();
	}
	
	@Override
	public void scrollTo(int x, int y) {
		// 
		super.scrollTo(x, y);
		int curX = Math.abs(getScrollX());
		//scale的范围为0-1
		float scale = curX/ (float)leftMenu.getMeasuredWidth();
		middleMask.setAlpha(scale);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//设置各个布局菜单的位置
		super.onLayout(changed, l, t, r, b);
		//将中间界面的位置设置为l，t，r，b
		middleMenu.layout(l, t, r, b);
		middleMask.layout(l, t, r, b);
		//根据middleMenu的位置确定其他菜单的位置
		leftMenu.layout(l-leftMenu.getMeasuredWidth(), t, r-middleMenu.getMeasuredWidth(), b);
		rightMenu.layout(l+middleMenu.getMeasuredWidth(), t, l+rightMenu.getMeasuredWidth()+middleMenu.getMeasuredWidth(), b);
	}
	
	
	public boolean isTestCompete;
	public int finalX=0;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (!isTestCompete) {
			getTypeEvent(ev);
			return true;
		}
		if (isLeftRightFragment) {
			switch (ev.getActionMasked()) {
			case MotionEvent.ACTION_MOVE:
				int curScrollX = getScrollX();      //获取滑动的距离，向左为负，向右为正
				int dis_x = (int) (ev.getX() - point.x);
				int expectX = -dis_x + curScrollX;
				if (expectX<0) {
					//向左滑动
					finalX = Math.max(expectX, -leftMenu.getMeasuredWidth());
				}else {
					//向右滑动
					finalX = Math.min(expectX, rightMenu.getMeasuredWidth());
				}
				//跟随点的滑动，页面滑动
				scrollTo(finalX, 0);
				point.x= (int) ev.getX();
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				curScrollX = getScrollX();
				if (Math.abs(curScrollX) > leftMenu.getMeasuredWidth() >> 1) {
					//当滑动的距离大于外布局宽度的一半时
					if (curScrollX < 0) {
						//向左滑动
						mScroller.startScroll(curScrollX, 0, -leftMenu.getMeasuredWidth()-curScrollX, 0 ,200);
					}else {
						//向右滑动
						mScroller.startScroll(curScrollX, 0, leftMenu.getMeasuredWidth()-curScrollX, 0,200);
					}
				}else {
					//小于宽度的一半，自动返回原位
					mScroller.startScroll(curScrollX, 0, -curScrollX, 0,200);
				}
				invalidate();    //view的重绘
				isLeftRightFragment = false;
				isTestCompete = false;
				break;
			}
		}else {
		switch (ev.getActionMasked()) {
			case MotionEvent.ACTION_UP:
			isLeftRightFragment = false;
			isTestCompete = false;
			break;
			default:
				break;
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	//回调的方法
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (!mScroller.computeScrollOffset()) {
			return;
		}
		int tempX = mScroller.getCurrX();
		scrollTo(tempX, 0);
	}
	
	private Point point = new  Point();
	private boolean isLeftRightFragment;
	//设置比较值20；当移动小于20dp时，默认没有移动
	private static final int TEST_DIS = 20;
	private void getTypeEvent(MotionEvent ev) {
		switch (ev.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			point.x = (int) ev.getX();
			point.y = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int dX = Math.abs((int)ev.getX() - point.x);
			int dY = Math.abs((int)ev.getY() - point.y);
			if (dX>TEST_DIS&&dX>dY) {           //左右滑动
				isLeftRightFragment = true;
				isTestCompete = true;
				point.x = (int) ev.getX();
				point.y = (int) ev.getY();
			}else if (dY>TEST_DIS&&dY>dX) {     //上下滑动
				isLeftRightFragment = false;
				isTestCompete = true;
				point.x = (int) ev.getX();
				point.y = (int) ev.getY();
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		default:
			break;
		}	
	}
}
