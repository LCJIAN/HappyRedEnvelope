package com.lcjian.happyredenvelope.ui.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.RedEnvelope;
import com.lcjian.happyredenvelope.data.entity.ResponseData;
import com.lcjian.lib.util.common.DateUtils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RedEnvelopeHistoriesActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_back)
    ImageButton btn_back;
    @BindView(R.id.tv_filter)
    TextView tv_filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_envelope_histories);
        ButterKnife.bind(this);

        btn_back.setOnClickListener(this);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_red_env_histories_container,new RedEnvelopesFragment()).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back: {
                onBackPressed();
            }
            break;
            default:
                break;
        }
    }

    public static class RedEnvelopesFragment extends RecyclerFragment<RedEnvelope> {
        
        private RedEnvelopeAdapter mAdapter;

        @Override
        public RecyclerView.Adapter onCreateAdapter(List<RedEnvelope> data) {
            mAdapter = new RedEnvelopeAdapter(data);
            return mAdapter;
        }

        @Override
        public Observable<PageResult<RedEnvelope>> onCreatePageObservable(int currentPage) {
            return mRestAPI.redEnvelopeService()
                    .getRedEnvHistories(mUserInfoSp.getLong("user_id", 0), currentPage, 10)
                    .map(new Func1<ResponseData<PageResult<RedEnvelope>>, PageResult<RedEnvelope>>() {
                        @Override
                        public PageResult<RedEnvelope> call(ResponseData<PageResult<RedEnvelope>> pageResultResponseData) {
                            return pageResultResponseData.data;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public void notifyDataChanged(List<RedEnvelope> data) {
        }
    }

    static class RedEnvelopeAdapter extends RecyclerView.Adapter<RedEnvelopeAdapter.RedEnvelopeViewHolder> {

        private List<RedEnvelope> mRedEnvelopeHistories;

        RedEnvelopeAdapter(List<RedEnvelope> redEnvelopeHistories) {
            this.mRedEnvelopeHistories = redEnvelopeHistories;
        }

        @Override
        public RedEnvelopeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RedEnvelopeViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RedEnvelopeViewHolder holder, int position) {
            holder.bindTo(mRedEnvelopeHistories.get(position));
        }

        @Override
        public int getItemCount() {
            return mRedEnvelopeHistories == null ? 0 : mRedEnvelopeHistories.size();
        }

        static class RedEnvelopeViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.iv_red_envelop_avatar) ImageView iv_red_envelop_avatar;
            @BindView(R.id.tv_red_envelop_title) TextView tv_red_envelop_title;
            @BindView(R.id.tv_red_envelop_time) TextView tv_red_envelop_time;
            @BindView(R.id.iv_red_envelop_amount) TextView iv_red_envelop_amount;

            private DecimalFormat mDecimalFormat;

            RedEnvelopeViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.red_envelope_history_item, parent, false));
                ButterKnife.bind(this, this.itemView);
                this.mDecimalFormat = new DecimalFormat("0.00");
            }

            void bindTo(RedEnvelope redEnvelope) {
                Context context = itemView.getContext();
                Glide.with(context)
                        .load(redEnvelope.hongBaoRoom.pic)
                        .apply(RequestOptions.placeholderOf(R.drawable.shape_user_no_avatar_bg).centerCrop())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(iv_red_envelop_avatar);

                tv_red_envelop_title.setText(redEnvelope.hongBaoRoom.name);
                tv_red_envelop_time.setText(DateUtils.convertDateToStr(new Date(redEnvelope.time), "yyyy-MM-dd HH:mm"));
                iv_red_envelop_amount.setText(String.format(Locale.getDefault(), "%s%s", "+" , mDecimalFormat.format(redEnvelope.money)));
            }
        }
    }
}
