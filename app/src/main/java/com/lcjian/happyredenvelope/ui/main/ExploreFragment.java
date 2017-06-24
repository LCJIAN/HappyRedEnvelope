package com.lcjian.happyredenvelope.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lcjian.happyredenvelope.BaseFragment;
import com.lcjian.happyredenvelope.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ExploreFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.tv_explore_one)
    TextView tv_explore_one;
    @BindView(R.id.tv_explore_two)
    TextView tv_explore_two;
    @BindView(R.id.tv_explore_three)
    TextView tv_explore_three;
    @BindView(R.id.tv_explore_four)
    TextView tv_explore_four;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tv_explore_one.setOnClickListener(this);
        tv_explore_two.setOnClickListener(this);
        tv_explore_three.setOnClickListener(this);
        tv_explore_four.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_explore_one: {

            }
            break;
            case R.id.tv_explore_two: {

            }
            break;
            case R.id.tv_explore_three: {

            }
            break;
            case R.id.tv_explore_four: {

            }
            break;
            default:
                break;
        }
    }
}
