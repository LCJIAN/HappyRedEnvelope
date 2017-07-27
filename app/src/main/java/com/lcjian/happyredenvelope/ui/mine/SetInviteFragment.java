package com.lcjian.happyredenvelope.ui.mine;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseDialogFragment;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.ResponseData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SetInviteFragment extends BaseDialogFragment implements View.OnClickListener {

    @BindView(R.id.et_invite_id)
    EditText et_invite_id;
    @BindView(R.id.btn_confirm)
    Button btn_confirm;
    @BindView(R.id.btn_skip)
    Button btn_skip;
    Unbinder unbinder;

    private Subscription mSubscription;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_invite, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btn_confirm.setOnClickListener(this);
        btn_skip.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                if (!TextUtils.isEmpty(et_invite_id.getEditableText().toString())) {
                    if (mSubscription != null) {
                        mSubscription.unsubscribe();
                    }
                    mSubscription = mRestAPI.redEnvelopeService().setInvite(mUserInfoSp.getLong("user_id", 0), et_invite_id.getEditableText().toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<ResponseData<String>>() {
                                @Override
                                public void call(ResponseData<String> stringResponseData) {
                                    if (stringResponseData.code == 0) {
                                        mSettingSp.edit().putBoolean("invited", true).apply();
                                        Toast.makeText(App.getInstance(), R.string.invited_success, Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    } else {
                                        Toast.makeText(App.getInstance(), stringResponseData.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                break;
            case R.id.btn_skip:
                dismiss();
                break;
            default:
                break;
        }
    }
}