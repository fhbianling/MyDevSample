package com.bian.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.bian.util.core.AppActivityManager;
import com.bian.util.eventbus.BusUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashSet;
import java.util.Set;


/**
 * 所有项目通用的基类Activity
 * Created by BianLing on 2016/8/23.
 * <p>
 * <p>
 * 提供的功能：
 * 1.事件总线
 * {@link #shouldRegisterBus()}返回true，则该Activity会注册事件总线，否则不会。
 * 当注册后，重写{@link #handleMessage(Object)}即可接受事件
 * <p>
 * 2.Activity管理
 * {@link #onDestroy()}和{@link #settingAfterSetContentView()}两个方法中，
 * 通过{@link AppActivityManager}对该Activity实体管理
 * <p>
 * 3.Fragment的支持
 * {@link #showFragment(Fragment, int)}方法，隐藏了Fragment显示的hide,show,add三个细节，换句话说
 * 可以直接new出Fragment的实例，并调用该方法,相应的实例会被显示
 * {@link #getFragments()}返回已经被添加的Fragment实例的set
 * 但是这里没有考虑对不同的containerId添加相同的Fragment的情况，需注意
 * <p>
 * 4.DataBinding支持
 * 在重写了{@link #bindLayoutId()}方法后
 * 可以在{@link #setContent(int)}方法中调用DataBindingUtil.setContentView(..)拿到Activity的binding对象
 * 不重写setContent则默认调用的是Activity的setContentView方法
 * <p>
 * 5.Activity启动
 *
 * @see #startActivity(Class)
 * @see #startActivity(Class, Bundle)
 */
public abstract class
AbsBaseActivity extends AppCompatActivity {
    //在intent只需要传递单一值时，下面这个常量可以用作intent的key
    public final static String INTENT_EXTRAS = "data";
    private boolean first = true;
    private Set<Fragment> fragments = new HashSet<>();

    /*---------------------------------------------------------------------*/
    /*应被子类重写和可被子类重写的方法*/

    /**
     * 重写该方法
     * 该值默认返回false，true注册EventBus,false则不注册
     */
    protected boolean shouldRegisterBus() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        beforeOnCreate();
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        setContent(bindLayoutId());
        afterSetContentView(savedInstanceState);
    }

    protected void beforeOnCreate() {

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
        /*窗口没有标题栏*/
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*强制竖屏*/
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /*软键盘模式为不自动弹出*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
        AppActivityManager.getInstance().removeActivity(this);
        fragments.clear();
    }

    @CallSuper
    @Override
    protected void onStop() {
        super.onStop();
        unRegisterBus();
    }

    @CallSuper
    @Override
    protected void onStart() {
        super.onStart();
        registerBus();
    }

    /**
     * 重写该方法以在{@link #setContentView(int)}被调用之后更改某些设置
     */
    @CallSuper
    protected void settingAfterSetContentView() {
        AppActivityManager.getInstance().addActivity(this);
    }
    /*---------------------------------------------------------------------*/

    /*---------------------------------------------------------------------*/
    //可供调用的方法
    public final void startActivity(Class cls) {
        Intent starter = new Intent(this, cls);
        startActivity(starter);
    }

    public final void startActivity(Class cls, Bundle bundle) {
        Intent starter = new Intent(this, cls);
        starter.putExtras(bundle);
        startActivity(starter);
    }

    public final void showFragment(Fragment fragment, @IdRes int containerId) {
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragments.contains(fragment)) {
            //未添加则先添加
            fragments.add(fragment);
            mFragmentTransaction.add(containerId, fragment, fragment.getClass().getSimpleName());
        }
        //相应的fragment显示，其他隐藏
        for (Fragment fragmentTemp : fragments) {
            if (fragmentTemp.equals(fragment)) {
                mFragmentTransaction.show(fragmentTemp);
            } else {
                mFragmentTransaction.hide(fragmentTemp);
            }
        }
        mFragmentTransaction.commit();
    }

    public Set<Fragment> getFragments() {
        return fragments;
    }
    /*---------------------------------------------------------------------*/

    /**
     * 订阅事件Bus
     */
    private void registerBus() {
        if (!shouldRegisterBus()) return;
        BusUtil.get().register(this);
    }

    /**
     * 取消订阅事件Bus
     */
    private void unRegisterBus() {
        if (!shouldRegisterBus()) return;
        BusUtil.get().unregister(this);
    }

}

