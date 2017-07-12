package com.lcjian.happyredenvelope.ui.room;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.ui.mine.RedEnvelopeHistoriesActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RedEnvelopeSnatchFailedFragment extends AppCompatDialogFragment implements View.OnClickListener {

    @BindView(R.id.btn_close)
    ImageButton btn_close;
    @BindView(R.id.tv_go_red_envelope_history)
    TextView tv_go_red_envelope_history;
    @BindView(R.id.tv_msg_of_snatching)
    TextView tv_msg_of_snatching;
    @BindView(R.id.btn_receive_free_luck_card)
    Button btn_receive_free_luck_card;
    Unbinder unbinder;

    private String mMsg;

    public static RedEnvelopeSnatchFailedFragment newInstance(String msg) {
        RedEnvelopeSnatchFailedFragment fragment = new RedEnvelopeSnatchFailedFragment();
        Bundle args = new Bundle();
        args.putString("msg", msg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMsg = getArguments().getString("msg");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_red_envelope_snatch_failed, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tv_msg_of_snatching.setText(mMsg);
        btn_close.setOnClickListener(this);
        tv_go_red_envelope_history.setOnClickListener(this);
        btn_receive_free_luck_card.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.tv_go_red_envelope_history:
                startActivity(new Intent(v.getContext(), RedEnvelopeHistoriesActivity.class));
                break;
            case R.id.btn_receive_free_luck_card:
                dismiss();
                break;
            default:
                break;
        }
    }
}
