package com.lcjian.happyredenvelope.ui.withdrawal;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CopyTokenDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    @BindView(R.id.btn_close)
    ImageButton btn_close;
    @BindView(R.id.tv_withdrawal_token)
    TextView tv_withdrawal_token;
    @BindView(R.id.btn_copy_token)
    Button btn_copy_token;
    Unbinder unbinder;

    private String mWithdrawalToken;

    public static CopyTokenDialogFragment newInstance(String withdrawalToken) {
        CopyTokenDialogFragment fragment = new CopyTokenDialogFragment();
        Bundle args = new Bundle();
        args.putString("withdrawal_token", withdrawalToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWithdrawalToken = getArguments().getString("withdrawal_token");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_copy_token, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btn_close.setOnClickListener(this);
        btn_copy_token.setOnClickListener(this);

        tv_withdrawal_token.setText(mWithdrawalToken);
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
            case R.id.btn_copy_token:
                ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE))
                        .setPrimaryClip(ClipData.newPlainText("token", mWithdrawalToken));
                Toast.makeText(App.getInstance(), R.string.copied, Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            default:
                break;
        }
    }
}
