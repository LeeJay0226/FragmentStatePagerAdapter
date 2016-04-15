package com.github.fragmentstatepageradapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SlidingTabLayout extends HorizontalScrollView implements View.OnClickListener {
    //    private Paint mLinePaint;
    private final int mScrollOffset = 80;
    private int mTabViewTextViewId;
    private int mCurrentPosition = 0;
    private int mTabCount;
    private LinearLayout mTabsContainer;
    private LinearLayout.LayoutParams mDefaultTabLayoutParams;
    private int mTabTextColorDefault;
    private int mTabTextColorFocus;
    private int mIndicatorHeight = 3;
    private int mIndicatorThickness = 3;
    //    private int mTabPadding = 14;
    private Paint rectPaint;
    private int lastScrollX = 0;
    private float mCurrentPositionOffset = 0f;
    private OnTabFocusListener mOnTabFocusListener;

    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);
        setWillNotDraw(false);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        mIndicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mIndicatorHeight, dm);
        mIndicatorThickness = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mIndicatorThickness, dm);
//        mTabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTabPadding, dm);

        mTabTextColorDefault = 0xff051b28;
        mTabTextColorFocus = 0xffee0a3b;

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(mTabTextColorFocus);

//        mLinePaint = new Paint();
//        mLinePaint.setAntiAlias(true);
//        mLinePaint.setStyle(Paint.Style.FILL);
//        mLinePaint.setColor(0xffdddddd);

        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mDefaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    }

    public void setOnTabFocusListener(OnTabFocusListener onTabFocusListener) {
        mOnTabFocusListener = onTabFocusListener;
    }

    public void setTextViewId(int textViewId) {
        mTabViewTextViewId = textViewId;
    }

    public void addTab(final int position, View tab) {
        tab.setTag(position);
        tab.setOnClickListener(this);

        mTabsContainer.addView(tab, position, mDefaultTabLayoutParams);
        mTabCount++;
    }

    @Override
    public void onClick(View v) {
        int pos = (Integer) v.getTag();
        setFocusedPosition(pos);

        if (null != mOnTabFocusListener) {
            mOnTabFocusListener.onTabFocused(pos, v);
        }
    }

    public void clearTabs() {
        mTabsContainer.removeAllViews();
        mTabCount = 0;
        mCurrentPosition = 0;
        mCurrentPositionOffset = 0;
    }

    public int getTabCount() {
        return mTabCount;
    }

    public ArrayList<View> getTabViews() {
        if (mTabCount > 0) {
            ArrayList<View> tabViews = new ArrayList<View>(mTabCount);
            for (int i = 0; i < mTabCount; i++) {
                tabViews.add(mTabsContainer.getChildAt(i));
            }
            return tabViews;
        }
        return null;
    }

    public View getTabItem(int pos) {
        if (pos >= 0 && pos < mTabCount) {
            return mTabsContainer.getChildAt(pos);
        }
        return null;
    }

    public void setCurrentPosition(int position) {
        if (position >= 0 && position < mTabCount) {
            this.mCurrentPosition = position;
            ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getViewTreeObserver().removeOnPreDrawListener(this);
                    scrollToChild(mCurrentPosition, 0);
                    return true;
                }
            });
        }
    }

    public void setFocusedPosition(int position) {
        if (0 <= position && position < mTabCount) {
            for (int i = 0; i < mTabCount; i++) {
                View view = mTabsContainer.getChildAt(i);
                if (view instanceof TextView) {
                    if (position == i) {
                        ((TextView) view).setTextColor(mTabTextColorFocus);
                    } else {
                        ((TextView) view).setTextColor(mTabTextColorDefault);
                    }
                } else {
                    View text = view.findViewById(mTabViewTextViewId);
                    if (text instanceof TextView) {
                        if (position == i) {
                            ((TextView) text).setTextColor(mTabTextColorFocus);
                        } else {
                            ((TextView) text).setTextColor(mTabTextColorDefault);
                        }
                    }
                }
            }
            invalidate();
        }
    }

    private void scrollToChild(int position, float offset) {
        if (mTabCount == 0) {
            return;
        }

        View currentTab = mTabsContainer.getChildAt(position);
        int newScrollX = (int) (currentTab.getLeft() + offset * currentTab.getWidth());

        if (position > 0 || offset > 0) {
            newScrollX -= mScrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (isInEditMode() || mTabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line
        View currentTab = mTabsContainer.getChildAt(mCurrentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (mCurrentPositionOffset > 0f && mCurrentPosition < mTabCount - 1) {
            View nextTab = mTabsContainer.getChildAt(mCurrentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft += mCurrentPositionOffset * (nextTabLeft - lineLeft);
            lineRight += mCurrentPositionOffset * (nextTabRight - lineRight);
        }

//        canvas.drawRect(0, height - 1, mTabsContainer.getWidth(), height, mLinePaint);
        canvas.drawRect(lineLeft, height - mIndicatorHeight, lineRight, height - mIndicatorHeight + mIndicatorThickness, rectPaint);
    }

    public void onPageScrolled(int position, float positionOffset) {
        if (mTabCount <= 0 || position >= mTabCount || position < 0) {
            return;
        }
        mCurrentPosition = position;
        mCurrentPositionOffset = positionOffset;
        scrollToChild(position, positionOffset);
        invalidate();
    }

//    @Override
//    public void onRestoreInstanceState(Parcelable state) {
//        if (state instanceof Bundle) {
//            Bundle bundle = (Bundle) state;
//            mCurrentPosition = bundle.getInt("CURRENT_POS");
//            super.onRestoreInstanceState(bundle.getParcelable("SUPER_STATE"));
//            requestLayout();
//        }
//    }
//
//    @Override
//    public Parcelable onSaveInstanceState() {
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("SUPER_STATE", super.onSaveInstanceState());
//        bundle.putInt("CURRENT_POS", mCurrentPosition);
//        return bundle;
//    }

    public interface OnTabFocusListener {
        void onTabFocused(int pos, View v);
    }
}
