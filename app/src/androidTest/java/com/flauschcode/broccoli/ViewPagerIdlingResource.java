package com.flauschcode.broccoli;

import androidx.test.espresso.IdlingResource;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

public class ViewPagerIdlingResource implements IdlingResource {

    private final String mName;

    private boolean mIdle = true; // Default to idle since we can't query the scroll state.

    private ResourceCallback mResourceCallback;

    public ViewPagerIdlingResource(ViewPager2 viewPager, String name) {
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageScrollStateChanged(int state) {
                mIdle = (state == ViewPager.SCROLL_STATE_IDLE
                        // Treat dragging as idle, or Espresso will block itself when swiping.
                        || state == ViewPager.SCROLL_STATE_DRAGGING);
                if (mIdle && mResourceCallback != null) {
                    mResourceCallback.onTransitionToIdle();
                }
            }
        });
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isIdleNow() {
        return mIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        mResourceCallback = resourceCallback;
    }

}
