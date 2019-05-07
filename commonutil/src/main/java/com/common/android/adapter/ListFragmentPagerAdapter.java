package com.common.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ListFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;
    private FragmentManager fm;
    private String[] titles;

    public ListFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    public ListFragmentPagerAdapter(FragmentManager fm,
                                    ArrayList<Fragment> fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setFragments(ArrayList<Fragment> fragments) {
        if (this.fragments != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.fragments) {
                ft.remove(f);
            }
            ft.commit();
            fm.executePendingTransactions();
        }
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Object obj = super.instantiateItem(container, position);
        return obj;
    }

    public void setPageTitle(String[] titles) {
        this.titles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null) {
            return titles[position % titles.length];
        } else {
            return super.getPageTitle(position);
        }
    }

}
