package com.example.hzp.androiddemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by hzp on 2018/4/3.
 * Scroller 竖向滑动
 */

public class ScrollerVerticalView extends ViewGroup {
    private Scroller scroller;//滑动控制
    private Context mContext;
    private VelocityTracker mVelocityTracker;//用于判断甩动手势
    private static final int SNAP_VELOCITY = 5000;//x轴速度基值，大于改值进行切换
    private int mCusScreen;//当前页面为第几屏
    private float mLastMotionY;//记住上次触摸屏位置
    private int delteY;

    public ScrollerVerticalView(Context context) {
        super(context, null);
        init(context);
    }

    public ScrollerVerticalView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    public ScrollerVerticalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        //初始化scroller
        this.scroller = new Scroller(mContext);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置该viewGroup大小
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //在onMeasure(int int) 中，必须调用setMeasuredDismension(int width,int height)
        //来存储测试得到的宽度和高度，不然会导致触发IllegalstateException
        setMeasuredDimension(width, height);
        //得到子类的个数
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            //测量子类宽高,方法内部通过childView的 measure(newWidthMeasureSpec, heightMeasureSpec)
            // 函数将子view获取到到宽高，存储到childView中，以便childView的getMeasuredWidth()
            // 和getMeasuredHeight() 的值可以被后续工作得到。
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        scrollTo(0, mCusScreen * height);//移动到第一页位置
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            // 重绘
            postInvalidate();
        }
    }

    /**
     * 覆写computeScroll
     * 在computeScroll()方法中
     * 在View的源码中可以看到public void computeScroll(){}是一个空方法.
     * 具体的实现需要自己来写.在该方法中我们可调用scrollTo()或scrollBy()
     * 来实现移动(动画).该方法才是实现移动的核心.
     * 4.1 利用Scroller的mScroller.computeScrollOffset()判断移动过程是否完成
     * 注意:该方法是Scroller中的方法而不是View中的!!!!!!
     * public boolean computeScrollOffset(){ }
     * Call this when you want to know the new location.
     * If it returns true,the animation is not yet finished.
     * loc will be altered to provide the new location.
     * 返回true时表示还移动还没有完成.
     * 4.2 若动画没有结束,则调用:scrollTo(By)();
     * 使其滑动scrolling
     * <p>
     * 5 再次调用invalidate()或者postInvalidate();.
     * 调用invalidate()方法那么又会重绘View树.
     * 从而跳转到第3步,如此循环,便形成了动画移动的效果，直到computeScrollOffset返回false
     * <p>
     * <p>
     * invalidate()与postInvalidate() 区别：
     * Invalidate the whole view. If the view is visible,
     * {@link #onDraw(android.graphics.Canvas)} will be called at some point in
     * the future.
     * <p>
     * This must be called from a UI thread. To call from a non-UI thread, call
     * {@link #postInvalidate()}.
     * 这段话的意思是：
     * invalidate()只能在UI线程调用而不能在非UI线程调用
     * postInvalidate() 可以在非UI线程调用也可以在UI线程调用
     */

    /**
     * 布局子view
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int marginTop = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != View.GONE) {
                int childHeight = view.getMeasuredHeight();
                //竖排排列
                view.layout(0,marginTop , view.getMeasuredWidth(),  view.getMeasuredHeight()+marginTop);
                marginTop += childHeight;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y= event.getY();//得到初始值x
        Log.d("ScrollSmoothView", "y:" + y);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //收集速度追踪点数据
                obtaionVelocityTracker(event);
                //如果屏幕动画还没结束，按下，结束动画
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                //记录按下时的x轴坐标
                mLastMotionY = y;

                break;
            case MotionEvent.ACTION_MOVE:
                //计算两次手指间的距离
                delteY = (int) (mLastMotionY - y);
                obtaionVelocityTracker(event);
                //记录最后一次移动的坐标
                mLastMotionY = y;
                //正向获负向移动，屏幕跟随手指移动
                scrollBy(0, delteY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //当手指离开屏幕时，记录mVerlocityTracker的记录，并取得x轴滑动速度
                obtaionVelocityTracker(event);
                /**
                 * computeCurrentVelocity (int units)
                 * computeCurrentVelocity (int units, float maxVelocity)：
                 * 基于你所收集到的点计算当前的速率,当你确定要获得速率信息的时候，在调用该方法，
                 * 因为使用它需要消耗很大的性能。然后，你可以通过getXVelocity()
                 * 和getYVelocity()获得横向和竖向的速率。
                 *
                 * 参数：units  你想要指定的得到的速度单位，如果值为1，代表1毫秒运动了多少像素。
                 * 如果值为1000，代表1秒内运动了多少像素。
                 *
                 * 参数：maxVelocity  该方法所能得到的最大速度，这个速度必须和你指定的units使用同样的单位，而且
                 * 必须是整数。（也就是，你指定一个速度的最大值，如果计算超过这个最大值，
                 * 就使用这个最大值，否则，使用计算的的结果）
                 */
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                float yVelocity = mVelocityTracker.getYVelocity();
                // 当X轴滑动速度大于SNAP_VELOCITY
                // velocityX为正值说明手指向右滑动，为负值说明手指向左滑动
                if (yVelocity > SNAP_VELOCITY && mCusScreen > 0) {
                    // Fling enough to move left
                    snapToScreen(mCusScreen - 1);
                } else if (yVelocity < -SNAP_VELOCITY
                        && mCusScreen < getChildCount() - 1) {
                    // Fling enough to move right
                    snapToScreen(mCusScreen + 1);
                } else {
                    snapToDestination();//弹性滑动
                }
                //释放资源
                releaseVelocityTracker();
                break;

        }

//        return super.onTouchEvent(event);
        return true;//返回true,不然只接受down
    }
    /**
     * 使用完VelocityTracker，必须释放资源
     */
    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
    /**
     * 使屏幕移动到第whichScreen+1屏
     *
     * @param whichScreen
     */
    public void snapToScreen(int whichScreen) {
        int scrollY = getScrollY();
        /**
         * 调用snapToDestination()时，判断，避免出界。
         */
        if(whichScreen > getChildCount() - 1) {
            whichScreen = getChildCount() - 1;
        }else if(whichScreen <0){
            whichScreen=0;
        }
//
        if (scrollY != (whichScreen * getHeight())) {
            int delta = whichScreen * getHeight() - scrollY;
            scroller.startScroll(0, scrollY, 0, delta, 500);
            mCusScreen = whichScreen;
            //手动调用重绘
//            invalidate();
            postInvalidate();
        }
    }
    /**
     * 当不需要滑动时，会调用该方法，弹性滑动效果
     * 条件：
     * 滑动超过当前view的宽度一半，则滑动到下一个界面
     * 滑动未超过当前view的宽度一半，则停留在原来界面
     */
    private void snapToDestination() {
        //获取当前view宽度
        int screenHeight = getHeight();
        //计算是否滑动到一个界面或者停留在原来的界面
        int whichScreen = (getScrollY() + (screenHeight / 2)) / screenHeight;
        snapToScreen(whichScreen);
    }
    /**
     * VelocityTracker帮助你追踪一个touch事件（flinging事件和其他手势事件）的速率。
     * 当你要跟踪一个touch事件的时候，使用obtain()方法得到这个类的实例，
     * 然后用addMovement(MotionEvent)函数将你接受到的Motion event
     * 加入到VelocityTracker类实例中。当你使用到速率时，
     * 使用computeCurrentVelocity(int)
     * 初始化速率的单位，并获得当前的事件的速率，然后使用getXVelocity()
     * 或getXVelocity()获得横向和竖向的速率
     *
     * @param event
     */
    private void obtaionVelocityTracker(MotionEvent event) {
        /**
         * obtain()的方法介绍
         * 得到一个速率追踪者对象去检测一个事件的速率。确认在完成的时候调用recycle()方法。
         * 一般情况下，你只要维持一个活动的速率追踪者对象去追踪一个事件，那么，这个速率追踪者
         * 可以在别的地方重复使用。
         */
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

}
