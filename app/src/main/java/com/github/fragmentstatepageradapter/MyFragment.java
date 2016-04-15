package com.github.fragmentstatepageradapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MyFragment extends Fragment implements IGetItemId {
    private static final String TAG = "MyFragment";
    private Item mItem;

    public static MyFragment newInstance(Item item) {
        Bundle args = new Bundle();
        args.putParcelable("item", item);
        MyFragment fragment = new MyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("item", mItem);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        if (null != savedInstanceState) {
            bundle = savedInstanceState;
            mItem = bundle.getParcelable("item");
            Log.d(TAG, "restore Fragment item:" + mItem);
        } else {
            bundle = getArguments();
            mItem = bundle.getParcelable("item");
            Log.d(TAG, "new Fragment item:" + mItem);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my, null, false);
        ((TextView) rootView.findViewById(R.id.text)).setText(mItem.text);
        return rootView;
    }

    @Override
    public long getItemId() {
        return mItem.id;
    }
}
