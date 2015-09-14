package com.trinew.easytime.adapters.pagers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.trinew.easytime.R;
import com.trinew.easytime.fragments.EasyBoxFragment;

/**
 * Created by jonathanlu on 9/7/15.
 */
public class GraphPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    public final static int NUM_PAGES = 1;

    public final static int PAGE_EASY_BOX = 0;

    private Context mContext;

    public GraphPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);

        mContext = context;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position) {
            case PAGE_EASY_BOX:
                fragment = Fragment.instantiate(mContext, EasyBoxFragment.class.getName());
                break;
        }
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence result = null;
        switch(position) {
            case PAGE_EASY_BOX:
                result = "Box";
                break;
        }

        return result;
    }

    @Override
    public View getCustomTabView(ViewGroup parent, int position) {

        //View tabView = LayoutInflater.from(mContext).inflate(R.layout.psts_tab, parent, false);

        View tabView = LayoutInflater.from(mContext).inflate(R.layout.tab_item, parent, false);

        ImageView tabIconImage = (ImageView) tabView.findViewById(R.id.icon_image);

        switch(position) {
            case PAGE_EASY_BOX:
                tabIconImage.setImageResource(R.drawable.ic_action_box);
                break;
        }

        return tabView;
    }
}