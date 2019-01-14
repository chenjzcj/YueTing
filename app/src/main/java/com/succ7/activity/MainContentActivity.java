package com.succ7.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

import com.slidingmenu.lib.SlidingMenu;
import com.succ7.fragment.FrameLocalMusicFragment;
import com.succ7.fragment.MenuFragment;
import com.succ7.yueting.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 * 
 * @author zhongcj(QQ:527633405) 2015-8-11
 * 
 */
public class MainContentActivity extends FragmentActivity implements
		OnBackStackChangedListener {

	private int mBackStackEntryCount = 0;
	private SlidingMenu mSlidingMenu;
	private FrameLocalMusicFragment mCurrentFragment;
	private FragmentManager fragmentManager;
	// 存放Fragment的集合
	private List<Fragment> mFragmentList = new ArrayList<>();
	private GestureDetector mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_content);

		fragmentManager = getSupportFragmentManager();
		fragmentManager.addOnBackStackChangedListener(this);

		mBackStackEntryCount = fragmentManager.getBackStackEntryCount();

		// 初始化SlidingMenu,并为其填充Fragment
		initSlidingMenu();

		initPopulateFragment();
		// 设置滑动手势
		mDetector = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// 从左向右滑动
				if (e1 != null && e2 != null) {
					if (e1.getX() - e2.getX() > 120) {
						switchToPlayer();
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * 切换到播放页面
	 */
	public void switchToPlayer() {
		startActivity(new Intent(MainContentActivity.this, PlayerActivity.class));
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	/**
	 * 为SlidingMenu和Content填充Fragment
	 */
	private void initPopulateFragment() {
		MenuFragment menuFragment = new MenuFragment();
		mCurrentFragment = new FrameLocalMusicFragment();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.frame_menu, menuFragment, menuFragment
				.getClass().getName());

		fragmentTransaction.replace(R.id.frame_main, mCurrentFragment,
				mCurrentFragment.getClass().getName());
		fragmentTransaction.commit();

		mFragmentList.add(mCurrentFragment);
	}

	public SlidingMenu getSlidingMenu() {
		return mSlidingMenu;
	}

	/**
	 * 设置SlindingMenu
	 */
	private void initSlidingMenu() {
		mSlidingMenu = new SlidingMenu(this);
		// 1.为SlidingMenu宿主一个Activity
		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		// 2.为SlidingMenu指定布局
		mSlidingMenu.setMenu(R.layout.layout_menu);
		// 3.设置SlidingMenu从何处可以滑出
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// 4.设置SlidingMenu的滑出方向
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		// 5.设置SlidingMenu的其他参数
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		// 6.滑动时侧滑菜单的内容静止不动
		mSlidingMenu.setBehindScrollScale(0.0f);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 将事件交给手势监听器
		return this.mDetector.onTouchEvent(event);
	}

	@Override
	public void onBackStackChanged() {
		Toast.makeText(this, "onBackStackChanged", 0).show();

	}
}
