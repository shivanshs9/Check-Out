package com.checkin.app.checkin.Misc;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFragmentAdapterBottomNav extends FragmentStatePagerAdapter {
    private ViewPager mPager;
    private int mSelectedPos = -1;
    private List<TabSelectionHandler> selectionHandlers = new ArrayList<>();

    public BaseFragmentAdapterBottomNav(FragmentManager fm) {
        super(fm);
    }

    @DrawableRes
    public abstract int getTabDrawable(int position);

    @LayoutRes
    public int getCustomView(int position) {
        return R.layout.view_tab_bottom_nav;
    }

    protected void bindTabText(TextView tvTitle, int position) {
        if (getPageTitle(position) != null) {
            tvTitle.setText(getPageTitle(position));
            tvTitle.setVisibility(View.VISIBLE);
        }
        else
            tvTitle.setVisibility(View.GONE);
    }

    protected void bindTabIcon(ImageView imIcon, int position) {
        imIcon.setImageResource(getTabDrawable(position));
    }

    protected void bindCustomView(View view, int position) {
        TextView tvTitle = view.findViewById(R.id.tv_bnav_title);
        ImageView imIcon = view.findViewById(R.id.iv_bnav_icon);

        bindTabText(tvTitle, position);
        bindTabIcon(imIcon, position);
    }

    public void setupWithTab(TabLayout tabLayout, ViewPager viewPager) {
        mPager = viewPager;

        int count = getCount();
        if (count > 0)
            mSelectedPos = 0;
        for (int pos = 0; pos < count; pos++) {
            View itemView = LayoutInflater.from(tabLayout.getContext()).inflate(getCustomView(pos), null, false);
            this.bindCustomView(itemView, pos);

            selectionHandlers.add(new TabSelectionHandler(pos, itemView));

            TabLayout.Tab tab = tabLayout.getTabAt(pos);
            if (tab != null) {
                tab.setCustomView(itemView);
            }
        }
    }

    protected void onTabClick(int position) {
        mPager.setCurrentItem(position, true);
    }

    private class TabSelectionHandler implements View.OnClickListener {
        private int mPos;
        private View mView;

        TabSelectionHandler(int pos, View  view) {
            mPos = pos;
            mView = view;
            mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mSelectedPos != -1) {
                selectionHandlers.get(mSelectedPos).deselect();
            }
            mSelectedPos = mPos;
            mView.setSelected(true);
            onTabClick(mSelectedPos);
        }

        void deselect() {
            mView.setSelected(false);
        }
    }
}