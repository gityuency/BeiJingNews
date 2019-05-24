package com.example.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.beijingnews.R;

/**
 * 自定义下拉刷新 ListView
 */
public class RefreshListView extends ListView {


    /**
     * 下拉刷新和顶部轮播图
     */
    private LinearLayout headerView;

    /**
     * 下拉刷新控件
     */
    private View ll_pull_down_refresh;

    /**
     * 箭头
     */
    private ImageView iv_arrow;

    /**
     * 进度条
     */
    private ProgressBar pb_status;

    /**
     * 刷新文字
     */
    private TextView tv_status;

    /**
     * 时间
     */
    private TextView tv_time;

    /**
     * 刷新控件的高度
     */
    private int pullDownRefreshHeight;


    /**
     * 下拉刷新
     * 大写  command + shift + u
     */
    public static final int PULL_DOWN_REFRESH = 0;

    /**
     * 手松刷新
     */
    public static final int RELEASE_REFRESH = 1;

    /**
     * 正在刷新
     */
    public static final int REFRESHING = 2;


    /**
     * 当前的刷新状态
     */
    private int CURRENTSTATUS = PULL_DOWN_REFRESH;


    /// 生成了三个构造方法  然后就是第一个构造方法调用第二个构造方法,第二个构造方法调用第三个构造方法
    public RefreshListView(Context context) {
        //super(context);
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        //super(context, attrs);
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initHeadView(context);
        initAnimation();
    }


    private Animation upAnimation;

    private Animation downAnimation;

    private void initAnimation() {

        upAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180, -360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);

    }

    private void initHeadView(Context context) {

        headerView = (LinearLayout) View.inflate(context, R.layout.refresh_header, null);

        ll_pull_down_refresh = headerView.findViewById(R.id.ll_pull_down_refresh);

        iv_arrow = headerView.findViewById(R.id.iv_arrow);

        pb_status = headerView.findViewById(R.id.pb_status);

        tv_status = headerView.findViewById(R.id.tv_status);

        tv_time = headerView.findViewById(R.id.tv_time);


        ll_pull_down_refresh.measure(0, 0); //这里面两个参数传递0, 这两个参数可能对测量时没有影响的
        pullDownRefreshHeight = ll_pull_down_refresh.getMeasuredHeight(); //拿到测量的高度


        //需要得到线性布局的高,把这个高度设置top padding
        ll_pull_down_refresh.setPadding(0, -pullDownRefreshHeight, 0, 0);


        //添加头
        addHeaderView(headerView);  // 这么写也可以 RefreshListView.this.addHeaderView(headerView)


    }


    private float startY = -1;

    /**
     * 重写这个方法.
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:

                //记录其实坐标
                startY = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) {
                    startY = ev.getY();
                }

                if (CURRENTSTATUS == REFRESHING) {
                    break;

                }


                //2.来到新的坐标
                float endY = ev.getY();

                //3.计算滑动的距离
                float distanceY = endY - startY;

                if (distanceY > 0) {  //下拉
                    int paddingTop = (int) (-pullDownRefreshHeight + distanceY);


                    if (paddingTop < 0 && CURRENTSTATUS != PULL_DOWN_REFRESH) {
                        //下拉刷新状态
                        CURRENTSTATUS = PULL_DOWN_REFRESH;
                        //更新状态
                        refreshViewState();

                    } else if (paddingTop > 0 && CURRENTSTATUS != RELEASE_REFRESH) {
                        //手松刷新状态
                        CURRENTSTATUS = RELEASE_REFRESH;
                        //更新状态
                        refreshViewState();

                    }


                    ll_pull_down_refresh.setPadding(0, paddingTop, 0, 0);
                }
                break;
            case MotionEvent.ACTION_UP:

                startY = -1;

                if (CURRENTSTATUS == PULL_DOWN_REFRESH) {

                    ll_pull_down_refresh.setPadding(0, -pullDownRefreshHeight, 0, 0);

                } else if (CURRENTSTATUS == RELEASE_REFRESH) {

                    //正在刷新
                    CURRENTSTATUS = REFRESHING;


                    ll_pull_down_refresh.setPadding(0, 00, 0, 0);

                    refreshViewState();
                }

                break;
        }

        return super.onTouchEvent(ev);
    }

    private void refreshViewState() {

        switch (CURRENTSTATUS) {

            case PULL_DOWN_REFRESH:

                iv_arrow.startAnimation(downAnimation);
                tv_status.setText("下拉刷新.....");

                break;
            case RELEASE_REFRESH:

                iv_arrow.startAnimation(upAnimation);
                tv_status.setText("手松刷新.....");

                break;
            case REFRESHING:

                tv_status.setText("正在刷新.....");
                pb_status.setVisibility(VISIBLE);
                iv_arrow.clearAnimation();
                iv_arrow.setVisibility(GONE);
                break;
        }
    }
}
