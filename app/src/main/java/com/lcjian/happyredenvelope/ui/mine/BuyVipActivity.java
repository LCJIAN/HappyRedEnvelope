package com.lcjian.happyredenvelope.ui.mine;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lcjian.happyredenvelope.App;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.happyredenvelope.data.entity.VipCombo;
import com.lcjian.happyredenvelope.data.entity.VipPrivilege;
import com.lcjian.happyredenvelope.data.entity.WeChatPayOrder;
import com.lcjian.lib.util.common.DimenUtils;
import com.lqpinxuan.lqpx.wxapi.WeChatPay;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class BuyVipActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.btn_back)
    ImageButton btn_back;
    @BindView(R.id.rv_vip_privileges)
    RecyclerView rv_vip_privileges;
    @BindView(R.id.btn_buy_vip)
    Button btn_buy_vip;
    @BindView(R.id.tv_vip_price)
    TextView tv_vip_price;

    private Subscription mSubscription;
    private Subscription mSubscription2;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_vip);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        btn_back.setOnClickListener(this);
        btn_buy_vip.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rv_vip_privileges.setLayoutManager(gridLayoutManager);
        rv_vip_privileges.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int offsets = (int) DimenUtils.dipToPixels(4, parent.getContext());
                outRect.set(offsets, offsets, offsets, offsets);
            }
        });
        rv_vip_privileges.setHasFixedSize(true);

        mRestAPI.redEnvelopeService().getVipCombo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<List<VipCombo>>>() {
                    @Override
                    public void call(ResponseData<List<VipCombo>> listResponseData) {
                        if (listResponseData.code == 0) {
                            tv_vip_price.setText(getString(R.string.vip_price,
                                    listResponseData.data.get(0).vipName,
                                    new DecimalFormat("0.00").format(listResponseData.data.get(0).vipAmount)));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });

        mSubscription = mRestAPI.redEnvelopeService().getVipPrivileges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseData<List<VipPrivilege>>>() {
                    @Override
                    public void call(ResponseData<List<VipPrivilege>> listResponseData) {
                        if (listResponseData.code == 0) {
                            rv_vip_privileges.setAdapter(new VipPrivilegeAdapter(listResponseData.data));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        if (mSubscription2 != null) {
            mSubscription2.unsubscribe();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
                onBackPressed();
            }
            break;
            case R.id.btn_buy_vip: {
                mProgressDialog.show();
                if (mSubscription2 != null) {
                    mSubscription2.unsubscribe();
                }
                mSubscription2 = mRestAPI.redEnvelopeService()
                        .createBuyingVipOrder(mUserInfoSp.getLong("user_id", 0), 1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseData<WeChatPayOrder>>() {
                            @Override
                            public void call(ResponseData<WeChatPayOrder> orderResponseData) {
                                mProgressDialog.dismiss();
                                if (orderResponseData.code == 0) {
                                    WeChatPay.pay(BuyVipActivity.this, orderResponseData.data.appid,
                                            orderResponseData.data.partnerid,
                                            orderResponseData.data.prepayid,
                                            orderResponseData.data.noncestr,
                                            orderResponseData.data.timestamp,
                                            orderResponseData.data.packages,
                                            orderResponseData.data.sign);
                                } else {
                                    Toast.makeText(App.getInstance(), orderResponseData.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                mProgressDialog.dismiss();
                                Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            break;
            default:
                break;
        }
    }

    static class VipPrivilegeAdapter extends RecyclerView.Adapter<VipPrivilegeAdapter.VipPrivilegeViewHolder> {

        private List<VipPrivilege> mVipPrivileges;

        private VipPrivilegeAdapter(List<VipPrivilege> vipPrivileges) {
            this.mVipPrivileges = vipPrivileges;
        }

        @Override
        public VipPrivilegeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VipPrivilegeViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(VipPrivilegeViewHolder holder, int position) {
            holder.bindTo(mVipPrivileges.get(position));
        }

        @Override
        public int getItemCount() {
            return mVipPrivileges == null ? 0 : mVipPrivileges.size();
        }

        static class VipPrivilegeViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_vip_privilege)
            TextView tv_vip_privilege;

            VipPrivilegeViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.vip_privilege_item, parent, false));
                ButterKnife.bind(this, this.itemView);
            }

            void bindTo(VipPrivilege vipPrivilege) {
                Context context = itemView.getContext();
                Glide.with(context)
                        .load(vipPrivilege.icon)
                        .into(new ViewTarget<TextView, Drawable>(tv_vip_privilege) {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                view.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null);
                            }
                        });
                tv_vip_privilege.setText(vipPrivilege.name);
            }
        }
    }
}
