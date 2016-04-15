package com.github.fragmentstatepageradapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Item> mItems;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(ArrayList<Item> items) {
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return null == mItems ? 0 : mItems.size();
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    @Override
    public Fragment getItem(int position) {
        return MyFragment.newInstance(mItems.get(position));
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).id;
    }

    /**
     * * @param object Object representing an item, previously returned by a call to
     * {@link #instantiateItem(View, int)}.
     *
     * @return object's new position index from [0, {@link #getCount()}),
     * {@link #POSITION_UNCHANGED} if the object's position has not changed,
     * or {@link #POSITION_NONE} if the item is no longer present.
     */
    @Override
    public int getItemPosition(Object object) {
        if (object instanceof IGetItemId) {
            long columnId = ((IGetItemId) object).getItemId();
            int size = mItems.size();
            for (int i = 0; i < size; i++) {
                if (columnId == mItems.get(i).id) {
                    return i;
                }
            }
        }
        return POSITION_NONE;
    }
}
