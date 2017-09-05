package com.bian.base.baseclass;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.bian.base.util.utilbase.AppActivityManager;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.bian.base.util.utilevent.EventUtil;


/**
 * 所有项目通用的基类Activity
 * Created by BianLing on 2016/8/23.
 */
@SuppressWarnings({"UnusedParameters", "unused"})
public abstract class
AbsBaseActivity extends AppCompatActivity {
    public final static String INTENT_EXTRAS ="data";

    private boolean first = true;

    protected boolean getShouldOnCreateSwipeBack() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏白底深色文字
        beforeSetContentView();
        setContent(bindLayoutId());
        afterSetContentView(savedInstanceState);
    }

    @CallSuper
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (getShouldOnCreateSwipeBack()) {
             /*左滑退出*/
            SwipeBackHelper.onPostCreate(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firstOnResume(first);
        first = false;
    }

    /*---------------------------------------------------------------------*/
    /*应被子类重写和可被子类重写的方法*/
    protected void firstOnResume(boolean firstOnResume) {

    }

    /**
     * 该方法用于对dataBinding进行支持，重写该方法可将{@link #setContentView(int)}方法替换为
     * android.databinding.DataBindingUtil#setContentView(Activity, int)方法，从而拿到binding引用
     *
     * @param layoutResID 布局id,{@link #bindLayoutId()}
     */
    protected void setContent(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    /**
     * 需要返回子类ContentView的Id,以用于为子类加载视图
     *
     * @return 子类layout的Id
     */
    protected abstract
    @LayoutRes
    int bindLayoutId();

    /**
     * 在{@link #setContentView(int)}之前被调用的方法
     * 可重写该方法做一些需要在{@link #setContentView(int)}之前进行的操作
     */
    protected void beforeSetContentView() {
        settingBeforeSetContentView();
    }

    /**
     * 在{@link #setContentView(int)}之后被调用的方法
     * {@link #initView(Bundle)}
     * {@link #initData()}
     * {@link #initListener()}
     * 这三个抽象方法均在该方法中被调用，
     * 所以如果需要重写该方法，应在重写中确保这三个方法中被用到的方法被调用了
     *
     * @param savedInstanceState Bundle 可用于恢复数据
     */
    protected void afterSetContentView(Bundle savedInstanceState) {
        settingAfterSetContentView();
        initView(savedInstanceState);
        initData();
        initListener();
    }

    /**
     * 初始化视图
     *
     * @param savedInstanceState 保存的信息
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 初始化数据，可选择的重写方法
     * 在initView之后被调用
     */
    protected void initData() {

    }

    /**
     * 初始化监听器，可选择的重写方法
     * 在initData之后被调用
     */
    protected void initListener() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @CallSuper
    public void handleMessage(Object msg) {
    }
    /*---------------------------------------------------------------------*/


    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getShouldOnCreateSwipeBack()) {
            SwipeBackHelper.onDestroy(this);
        }
        AppActivityManager.getInstance().removeActivity(this);
    }

    @CallSuper
    @Override
    protected void onStop() {
        super.onStop();
        unRegisterRxBus();
    }

    @CallSuper
    @Override
    protected void onStart() {
        super.onStart();
        registerRxBus();
    }

    @CallSuper
    protected void settingBeforeSetContentView() {
        /*窗口没有标题栏*/
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*强制竖屏*/
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /*软键盘模式为不自动弹出*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (getShouldOnCreateSwipeBack()) {
        /*左滑退出*/
            SwipeBackHelper.onCreate(this);
            SwipeBackHelper.getCurrentPage(this)
                    .setSwipeEdge(200);
        }
    }

    protected final void setSwipeBackEnable(boolean enable) {
        if (getShouldOnCreateSwipeBack()) {
            SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(enable);
        }
    }

    @CallSuper
    protected void settingAfterSetContentView() {
        AppActivityManager.getInstance().addActivity(this);
    }

    /**
     * 订阅RxBus
     */
    private void registerRxBus() {
        EventUtil.get().register(this);
    }


    /**
     * 取消订阅RxBus
     */
    private void unRegisterRxBus() {
        EventUtil.get().unregister(this);
    }


    public void startActivity(Class className) {
        Intent starter = new Intent(this, className);
        startActivity(starter);
    }

    public void startActivity(Class className, Bundle bundle) {
        Intent starter = new Intent(this, className);
        starter.putExtras(bundle);
        startActivity(starter);
    }
}
