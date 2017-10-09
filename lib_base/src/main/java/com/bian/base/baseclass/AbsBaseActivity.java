package com.bian.base.baseclass;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.bian.base.util.utilbase.AppActivityManager;
import com.bian.base.util.utilevent.EventUtil;
import com.jude.swipbackhelper.SwipeBackHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashSet;
import java.util.Set;


/**
 * 所有项目通用的基类Activity
 * Created by BianLing on 2016/8/23.
 */
@SuppressWarnings({"UnusedParameters", "unused"})
public abstract class
AbsBaseActivity extends AppCompatActivity {
    public final static String INTENT_EXTRAS = "data";
    private boolean first = true;
    private Set<Fragment> fragments = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏白底深色文字
        beforeSetContentView();
        setContent(bindLayoutId());
        afterSetContentView(savedInstanceState);
    }

    /*---------------------------------------------------------------------*/
    /*应被子类重写和可被子类重写的方法*/
    private void addFragment(Fragment fragment, @IdRes int containerId) {
        fragments.add(fragment);
        getSupportFragmentManager().beginTransaction().add(containerId, fragment,
                fragment.getClass().getSimpleName()).commit();
    }

    public final void showFragment(Fragment fragment, @IdRes int containerId) {
        if (!fragments.contains(fragment)) {
            addFragment(fragment, containerId);
        }
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragmentTemp : fragments) {
            if (fragmentTemp.getClass().getSimpleName().equals(
                    fragment.getClass().getSimpleName())) {
                mFragmentTransaction.show(fragmentTemp);
            } else {
                mFragmentTransaction.hide(fragmentTemp);
            }
        }
        mFragmentTransaction.commit();
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

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        firstOnResume(first);
        first = false;
    }


    /**
     * 与{@link #onResume()}同时被调用的方法，但参数包含列是否是第一次onResume
     *
     * @param firstOnResume 是否是第一次onResume
     */
    protected void firstOnResume(boolean firstOnResume) {

    }

    /**
     * 是否支持左滑推出 默认返回true,可重写
     */
    protected boolean getShouldOnCreateSwipeBack() {
        return true;
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

    /**
     * 重写该方法以接收事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    @CallSuper
    public void handleMessage(Object msg) {
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getShouldOnCreateSwipeBack()) {
            SwipeBackHelper.onDestroy(this);
        }
        AppActivityManager.getInstance().removeActivity(this);
        fragments.clear();
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


    /**
     * 重写该方法以在{@link #setContentView(int)}被调用之前更改某些设置
     */
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

    /**
     * 重写该方法以在{@link #setContentView(int)}被调用之后更改某些设置
     */
    @CallSuper
    protected void settingAfterSetContentView() {
        AppActivityManager.getInstance().addActivity(this);
    }

    /*---------------------------------------------------------------------*/

    protected final void setSwipeBackEnable(boolean enable) {
        if (getShouldOnCreateSwipeBack()) {
            SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(enable);
        }
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


    public final void startActivity(Class cls) {
        Intent starter = new Intent(this, cls);
        startActivity(starter);
    }

    public final void startActivity(Class cls, Bundle bundle) {
        Intent starter = new Intent(this, cls);
        starter.putExtras(bundle);
        startActivity(starter);
    }
}

