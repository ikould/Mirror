package com.peiyu.frame.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.peiyu.frame.fragment.FrameBaseFragment;
import com.peiyu.frame.utils.KeyBoardUtils;
import com.peiyu.frame.view.residemenu.ResideMenu;
import com.peiyu.frame.view.residemenu.ResideMenuItem;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import joe.framelibrary.R;

/**
 * Description  框架基础Activity类
 * Created by chenqiao on 2015/7/15.
 */
public abstract class FrameBaseActivity extends AppCompatActivity {

    protected AppCompatActivity context;
    /**
     * 替代Actionbar的Toolbar
     */
    private Toolbar mToolbar;

    private TextView mTitleTv;

    private FragmentManager fragmentManager;
    /**
     * Toolbar之下的layout
     */
    private FrameLayout mContentLayout;

    /**
     * 侧滑菜单
     */
    private ResideMenu mResideMenu;

    /**
     * 是否注册了EventBus，true时会在onDestroy()中自动注销
     */
    private boolean isRegisterEventBus = false;

    private boolean isSupportActionbar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_activity_layout);
        mContentLayout = (FrameLayout) findViewById(R.id.rootlayout_baseactivity);
        context = this;
        ActivityTaskStack.add(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mTitleTv = (TextView) findViewById(R.id.toolbarTitle);

        if (setToolbarAsActionbar()) {
            isSupportActionbar = true;
            setSupportActionBar(mToolbar);
        } else {
            isSupportActionbar = false;
        }
        setTitle("");
        fragmentManager = getSupportFragmentManager();
        onBaseActivityCreated(savedInstanceState);
    }

    /**
     * 设定是否将Toolbar作为Actionbar使用，将会
     * 影响Toolbar的菜单的使用方法。
     *
     * @return true则使用{@link #onCreateMyToolbarMenu()}进行菜单创建
     */
    protected abstract boolean setToolbarAsActionbar();

    /**
     * 设置Toolbar标题
     *
     * @param title    标题
     * @param isCenter 是否居中
     */
    public void setToolbarTitle(String title, boolean isCenter) {
        if (isSupportActionbar) {
            if (!isCenter) {
                getSupportActionBar().setTitle(title);
            } else {
                mTitleTv.setText(title);
            }
        } else {
            if (!isCenter) {
                mToolbar.setTitle(title);
            } else {
                mTitleTv.setText(title);
            }
        }
    }

    /**
     * 设置沉浸式
     */
    public void setImmersive() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 设置Toolbar标题
     *
     * @param resid    标题资源
     * @param isCenter 是否居中
     */
    public void setToolbarTitle(int resid, boolean isCenter) {
        if (isSupportActionbar) {
            if (!isCenter) {
                getSupportActionBar().setTitle(resid);
            } else {
                mTitleTv.setText(resid);
            }
        } else {
            if (!isCenter) {
                mToolbar.setTitle(resid);
            } else {
                mTitleTv.setText(resid);
            }
        }
    }

    /**
     * 设置居中标题的颜色
     *
     * @param color 颜色
     */
    public final void setCenterTitleColor(int color) {
        if (mTitleTv != null) {
            mTitleTv.setTextColor(color);
        }
    }

    /**
     * 设置居中标题的字体大小
     *
     * @param size 大小
     */
    public final void setCenterTitleSize(float size) {
        if (mTitleTv != null) {
            mTitleTv.setTextSize(size);
        }
    }

    /**
     * 代替onCreate的入口类
     *
     * @param savedInstanceState
     */
    protected abstract void onBaseActivityCreated(Bundle savedInstanceState);


    /**
     * 注册EventBus
     */
    protected final void registerEventBus() {
        EventBus.getDefault().register(this);
        isRegisterEventBus = true;
    }

    protected final void registerEventBusForSticky() {
        EventBus.getDefault().registerSticky(this);
        isRegisterEventBus = true;
    }

    /**
     * 重写onDestroy，如果注册了EventBus，则需要注销
     */
    @Override
    protected void onDestroy() {
        if (isRegisterEventBus) {
            EventBus.getDefault().unregister(this);
        }
        ActivityTaskStack.remove(this);
        super.onDestroy();
    }

    /**
     * 获取内容布局id
     *
     * @return 根布局Id
     */
    public final int getRootFrameLayoutId() {
        return R.id.rootlayout_baseactivity;
    }

    /**
     * 获取Toolbar下的根布局
     *
     * @return 根布局
     */
    public final FrameLayout getRootFrameLayout() {
        return mContentLayout;
    }

    /**
     * 设置Activity的中心内容
     *
     * @param layoutResID 资源Id
     */
    protected final void setMyContentView(int layoutResID) {
        if (mContentLayout != null) {
            LayoutInflater.from(this).inflate(layoutResID, mContentLayout, true);
        }
    }

    protected final void setMyContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
    }

    protected final View findViewByTag(Object tag) {
        return mContentLayout.findViewWithTag(tag);
    }

    /**
     * 替换Activity的内容
     *
     * @param fragment
     * @param isBackStack
     */
    protected void replaceFragment(FrameBaseFragment fragment, String isBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (TextUtils.isEmpty(isBackStack)) {
            fragmentTransaction.replace(R.id.rootlayout_baseactivity, fragment);
        } else {
            fragmentTransaction.replace(R.id.rootlayout_baseactivity, fragment, isBackStack);
            fragmentTransaction.addToBackStack(isBackStack);
        }
        KeyBoardUtils.closeKeyboard(context);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private Map<String, Fragment> fragments;

    /**
     * 显示Fragment
     *
     * @param id
     * @param fragment
     * @param tag
     */
    protected void showFragment(int id, FrameBaseFragment fragment, String tag) {
        if (fragments == null)
            fragments = new HashMap<>();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();  //Activity中
        for (String fragmentTag : fragments.keySet()) {
            if (fragmentTag.equals(tag))
                transaction.show(fragments.get(fragmentTag));
            else
                transaction.hide(fragments.get(fragmentTag));
        }
        transaction.commitAllowingStateLoss();
        if (!fragments.keySet().contains(tag)) {
            addFragment(id, fragment, tag);
            fragments.put(tag, fragment);
        }
    }

    /**
     * 替换Fragment，同时添加动画
     *
     * @param id       要替换的View id
     * @param fragment 要添加的Fragment
     * @param tag      标记
     */
    protected void addFragment(int id, FrameBaseFragment fragment, String tag) {
        if (fragment == null) {
            return;
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (TextUtils.isEmpty(tag)) {
            fragmentTransaction.add(id, fragment);
        } else {
            fragmentTransaction.add(id, fragment, tag);
        }
        KeyBoardUtils.closeKeyboard(context);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Toolbar相关
     */
    public final Toolbar getToolbar() {
        if (mToolbar != null) {
            return mToolbar;
        } else {
            return null;
        }
    }

    public final void hideToolbar() {
        if (mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
    }

    public final void showToolbar() {
        if (mToolbar != null) {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    public void setFullScreen(boolean isFullScreen) {
        if (isFullScreen) {
            hideToolbar();
            hideStatusBar(this);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        } else {
            showStatusBar(this);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    public static void hideStatusBar(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.hideNaviBar");
        intent.putExtra("hide", true);
        context.sendBroadcast(intent);
    }

    public static void showStatusBar(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.hideNaviBar");
        intent.putExtra("hide", false);
        context.sendBroadcast(intent);
    }

    /**
     * 侧滑菜单相关
     */
    //侧滑时页面缩小系数
    private static float DEFAULT_SCALE = 0.6f;
    //默认侧滑方向
    private int direction = ResideMenu.DIRECTION_LEFT;
    //侧滑方向常量
    protected static final int DIRECTION_LEFT = ResideMenu.DIRECTION_LEFT;
    protected static final int DIRECTION_RIGHT = ResideMenu.DIRECTION_RIGHT;

    /**
     * 初始化侧滑菜单，不设定侧滑方向
     *
     * @return mResideMenu
     */
    protected final ResideMenu initResideMenu(int backgroundResId) {
        mResideMenu = new ResideMenu(this);
        mResideMenu.attachToActivity(this);
        mResideMenu.setScaleValue(DEFAULT_SCALE);
        mResideMenu.setBackground(backgroundResId);
        return mResideMenu;
    }

    /**
     * 获取侧滑菜单对象
     *
     * @return 侧滑菜单实例
     */
    public final ResideMenu getResideMenu() {
        if (mResideMenu != null) {
            return mResideMenu;
        } else {
            return null;
        }
    }

    /**
     * 初始化侧滑菜单
     *
     * @param swipeDirection 侧滑方向
     * @return mResideMenu
     */
    protected final ResideMenu initResideMenu(int swipeDirection, int backgroundResId) {
        this.direction = swipeDirection;
        mResideMenu = new ResideMenu(this);
        mResideMenu.attachToActivity(this);
        mResideMenu.setBackground(backgroundResId);
        mResideMenu.setScaleValue(DEFAULT_SCALE);
        switch (swipeDirection) {
            case ResideMenu.DIRECTION_LEFT:
                mResideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
                break;
            case ResideMenu.DIRECTION_RIGHT:
                mResideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        }
        return mResideMenu;
    }

    /**
     * 添加单个item到侧滑菜单
     *
     * @param item
     */
    protected final void addMenuItemToMenu(ResideMenuItem item) {
        if (mResideMenu != null) {
            mResideMenu.addMenuItem(item, this.direction);
        }
    }

    /**
     * 添加多个item到侧滑菜单
     *
     * @param items
     */
    protected final void addMenuItemsToMenu(ArrayList<ResideMenuItem> items) {
        if (mResideMenu != null) {
            for (ResideMenuItem item : items) {
                mResideMenu.addMenuItem(item, this.direction);
            }
        }
    }

    /**
     * 添加滑动事件忽略View
     *
     * @param view 需要自己处理滑动事件的View
     */
    protected final void addIgnoredView(View view) {
        if (mResideMenu != null) {
            mResideMenu.addIgnoredView(view);
        }
    }

    /**
     * 将整个Fragment的内容添加到忽略View中
     *
     * @param fragment
     */
    protected final void addIgnoredFragment(FrameBaseFragment fragment) {
        if (mResideMenu != null) {
            mResideMenu.addIgnoredView(fragment.getContentView());
        }
    }

    /**
     * 重写触碰事件，如果添加了ResideMenu，则由ResideMenu处理滑动事件；
     * 如果不希望ResideMenu处理滑动事件的区域使用方法{#ResideMenu.addIgnoredView(view)}
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mResideMenu != null) {
            return mResideMenu.dispatchTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }
}