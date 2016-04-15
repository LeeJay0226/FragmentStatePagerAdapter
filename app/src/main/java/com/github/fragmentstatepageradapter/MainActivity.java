package com.github.fragmentstatepageradapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        SlidingTabLayout.OnTabFocusListener,
        ViewPager.OnPageChangeListener,
        View.OnClickListener {

    private ViewPager mViewPager;
    private SlidingTabLayout mTabLayout;
    private EditText mEditText;
    private EditText mSelectEditText;
    private ViewPagerAdapter mPagerAdapter;
    private ArrayList<Item> mItems;
    private int mCurrSelectedPos;
    private long mCurrTabId;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mItems", mItems);
        outState.putInt("mCurrSelectedPos", mCurrSelectedPos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null != savedInstanceState) {
            mItems = savedInstanceState.getParcelableArrayList("mItems");
            mCurrSelectedPos = savedInstanceState.getInt("mCurrSelectedPos", 0);
        } else {
            mItems = getDatas();
            mCurrSelectedPos = 0;
        }

        mTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabLayout.setOnTabFocusListener(this);

        setMenus();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_menu) {
            showEditWindow();
        }
        return super.onOptionsItemSelected(item);
    }

    private PopupWindow mPopupWindow;

    private void showEditWindow() {
        Toast.makeText(this, "请输入数字，以空格分隔", Toast.LENGTH_LONG).show();
        if (null == mPopupWindow) {
            mPopupWindow = new PopupWindow(this);
            View view = LayoutInflater.from(this).inflate(R.layout.edit_layout, null, false);
            mPopupWindow.setContentView(view);
            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            view.findViewById(R.id.cancel).setOnClickListener(this);
            view.findViewById(R.id.ok).setOnClickListener(this);
            mEditText = (EditText) view.findViewById(R.id.editText);
            mEditText.setText(generateNumbers());
            mSelectEditText = (EditText) view.findViewById(R.id.select_editText);
        }
        mPopupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
        mPopupWindow.update();
    }

    private String generateNumbers() {
        StringBuilder sb = new StringBuilder();
        for (Item item : mItems) {
            sb.append(item.id).append(" ");
        }
        if (sb.length() > 1) {
            return sb.substring(0, sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.ok == id) {
            if (null != mPopupWindow && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
                if (mEditText != null && mSelectEditText != null) {
                    String text = mEditText.getText().toString().trim();
                    String selectText = mSelectEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)) {
                        String[] numbers = text.split(" ");
                        ArrayList<Item> items = new ArrayList<>();
                        for (String number : numbers) {
                            long i = Long.parseLong(number);
                            items.add(new Item(i, "Text " + i));
                        }

                        if (!TextUtils.isEmpty(selectText)) {
                            try {
                                mCurrTabId = Long.parseLong(selectText);
                            } catch (NumberFormatException e) {
                                mCurrTabId = items.get(0).id;
                            }
                        } else {
                            mCurrTabId = items.get(0).id;
                        }
                        mItems = items;
                        mCurrSelectedPos = 0;
                        setMenus();
                    }
                }
            }

        } else if (R.id.cancel == id) {
            if (null != mPopupWindow && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
        }
    }

    private ArrayList<Item> getDatas() {
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            items.add(new Item(i, "Text " + i));
        }
        return items;
    }

    private void setMenus() {
        ArrayList<View> recycleTabViews = null;
        if (mTabLayout.getTabCount() > 0) {
            recycleTabViews = mTabLayout.getTabViews();
            mTabLayout.clearTabs();
        }

        for (int i = 0, len = mItems.size(); i < len; i++) {
            final Item item = mItems.get(i);
            TextView tabView;
            if (null != recycleTabViews && recycleTabViews.size() > 0) {
                tabView = (TextView) recycleTabViews.remove(0);
            } else {
                tabView = new TextView(this);
                tabView.setPadding((int) dip2px(this, 30), 0, (int) dip2px(this, 30), 0);
                tabView.setGravity(Gravity.CENTER);
            }
            tabView.setText(item.id + "");
            mTabLayout.addTab(i, tabView);

            if (0 == mCurrSelectedPos && item.id == mCurrTabId) {
                mCurrSelectedPos = i;
            }
        }

        mTabLayout.setFocusedPosition(mCurrSelectedPos);
        mTabLayout.setCurrentPosition(mCurrSelectedPos);

        if (null == mPagerAdapter) {
            mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            mPagerAdapter.setData(mItems);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setOnPageChangeListener(this);
        } else {
            mPagerAdapter.setData(mItems);
            //当调用notifyDataSetChanged后，由于设置了setOnPageChangeListener,mCurrSelectedPos有可能发生改变
            mViewPager.setOnPageChangeListener(null);
            mPagerAdapter.notifyDataSetChanged();
            mViewPager.setOnPageChangeListener(this);
        }

        if (mCurrSelectedPos != mViewPager.getCurrentItem()) {
            mViewPager.setCurrentItem(mCurrSelectedPos, false);
        }
    }

    public void onTabFocused(int position, View v) {
        if (position != mCurrSelectedPos) {
            mCurrSelectedPos = position;
            mCurrTabId = mItems.get(position).id;
            mViewPager.setCurrentItem(position, false);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mTabLayout.onPageScrolled(position, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        if (mCurrSelectedPos != position) {
            mCurrSelectedPos = position;
            mCurrTabId = mItems.get(position).id;
            mTabLayout.setFocusedPosition(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private static float dip2px(Context context, float dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
