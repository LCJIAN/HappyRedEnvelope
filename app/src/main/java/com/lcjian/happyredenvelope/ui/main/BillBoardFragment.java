package com.lcjian.happyredenvelope.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lcjian.happyredenvelope.BaseFragment;
import com.lcjian.happyredenvelope.R;

public class BillBoardFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_billboard, container, false);
        return view;
    }
}
