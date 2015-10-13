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
	//��ʼ��
	private void initView(Context context){
		this.context=context;
		mScroller = new Scroller(context, new DecelerateInterpolator());    //�ڶ�������Ϊ��Ⱦ��
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
		//����͸����
		middleMask.setAlpha(0);  
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		middleMenu.measure(widthMeasureSpec, heightMeasureSpec);
		middleMask.measure(widthMeasureSpec, heightMeasureSpec);
		//�õ��˵��Ŀ��Ĵ�С
		int realWidth = MeasureSpec.getSize(widthMeasureSpec);
		//��tempRealtempwidth���ڲ˵�0.8���Ŀ��������������Ҳ˵��Ŀ���
		int tempRealWidth = MeasureSpec.makeMeasureSpec((int)(realWidth*0.8f),
				MeasureSpec.EXACTLY);
		//�������Ҳ˵��Ĵ�С
		leftMenu.measure(tempRealWidth, heightMeasureSpec);
		rightMenu.measure(tempRealWidth, heightMeasureSpec);
	}
	
	public float onMiddleMask(){
		System.out.println("͸���ȣ�"+middleMask.getAlpha());
		return middleMask.getAlpha();
	}
	
	@Override
	public void scrollTo(int x, int y) {
		// 
		super.scrollTo(x, y);
		int curX = Math.abs(getScrollX());
		//scale�ķ�ΧΪ0-1
		float scale = curX/ (float)leftMenu.getMeasuredWidth();
		middleMask.setAlpha(scale);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//���ø������ֲ˵���λ��
		super.onLayout(changed, l, t, r, b);
		//���м�����λ������Ϊl��t��r��b
		middleMenu.layout(l, t, r, b);
		middleMask.layout(l, t, r, b);
		//����middleMenu��λ��ȷ�������˵���λ��
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
				int curScrollX = getScrollX();      //��ȡ�����ľ��룬����Ϊ��������Ϊ��
				int dis_x = (int) (ev.getX() - point.x);
				int expectX = -dis_x + curScrollX;
				if (expectX<0) {
					//���󻬶�
					finalX = Math.max(expectX, -leftMenu.getMeasuredWidth());
				}else {
					//���һ���
					finalX = Math.min(expectX, rightMenu.getMeasuredWidth());
				}
				//�����Ļ�����ҳ�滬��
				scrollTo(finalX, 0);
				point.x= (int) ev.getX();
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				curScrollX = getScrollX();
				if (Math.abs(curScrollX) > leftMenu.getMeasuredWidth() >> 1) {
					//�������ľ�������Ⲽ�ֿ��ȵ�һ��ʱ
					if (curScrollX < 0) {
						//���󻬶�
						mScroller.startScroll(curScrollX, 0, -leftMenu.getMeasuredWidth()-curScrollX, 0 ,200);
					}else {
						//���һ���
						mScroller.startScroll(curScrollX, 0, leftMenu.getMeasuredWidth()-curScrollX, 0,200);
					}
				}else {
					//С�ڿ��ȵ�һ�룬�Զ�����ԭλ
					mScroller.startScroll(curScrollX, 0, -curScrollX, 0,200);
				}
				invalidate();    //view���ػ�
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

	//�ص��ķ���
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
	//���ñȽ�ֵ20�����ƶ�С��20dpʱ��Ĭ��û���ƶ�
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
			if (dX>TEST_DIS&&dX>dY) {           //���һ���
				isLeftRightFragment = true;
				isTestCompete = true;
				point.x = (int) ev.getX();
				point.y = (int) ev.getY();
			}else if (dY>TEST_DIS&&dY>dX) {     //���»���
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