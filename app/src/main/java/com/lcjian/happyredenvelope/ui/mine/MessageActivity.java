package com.lcjian.happyredenvelope.ui.mine;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.db.table.PushMessageTable;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.happyredenvelope.data.entity.PushMessage;
import com.lcjian.lib.util.common.DateUtils;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MessageActivity extends BaseActivity {

    @BindView(R.id.btn_top_bar_left)
    ImageButton btn_top_bar_left;
    @BindView(R.id.tv_top_bar_title)
    TextView tv_top_bar_title;
    @BindView(R.id.tv_top_bar_right)
    TextView tv_top_bar_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_message);
        ButterKnife.bind(this);

        btn_top_bar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_top_bar_title.setText(R.string.message);
        tv_top_bar_right.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_fragment_container,
                new PushMessagesFragment()).commit();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public static class PushMessagesFragment extends RecyclerFragment<PushMessage> {

        private PushMessageAdapter mAdapter;

        @Override
        public RecyclerView.Adapter onCreateAdapter(List<PushMessage> data) {
            mAdapter = new PushMessageAdapter(data);
            return mAdapter;
        }

        @Override
        public Observable<PageResult<PushMessage>> onCreatePageObservable(int currentPage) {
            return mStorIOSQLite.get()
                    .listOfObjects(PushMessage.class)
                    .withQuery(Query.builder().table(PushMessageTable.TABLE_NAME).build())
                    .prepare()
                    .asRxObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<List<PushMessage>, PageResult<PushMessage>>() {
                        @Override
                        public PageResult<PushMessage> call(List<PushMessage> pushMessages) {
                            PageResult<PushMessage> result = new PageResult<>();
                            result.elements = pushMessages;
                            result.total_pages = 1;
                            return result;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        @Override
        public void notifyDataChanged(List<PushMessage> data) {
            mAdapter.replaceAll(data);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            swipe_refresh_layout.setColorSchemeResources(R.color.colorLightRed);

            recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
            recycler_view.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                    .color(ContextCompat.getColor(getContext(), R.color.colorDivider))
                    .size(1)
                    .build());

            super.onViewCreated(view, savedInstanceState);
        }
    }

    static class PushMessageAdapter extends RecyclerView.Adapter<PushMessageAdapter.PushMessageViewHolder> {

        private List<PushMessage> mData;

        PushMessageAdapter(List<PushMessage> data) {
            this.mData = data;
        }

        void replaceAll(final List<PushMessage> data) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {

                @Override
                public int getOldListSize() {
                    return mData == null ? 0 : mData.size();
                }

                @Override
                public int getNewListSize() {
                    return data == null ? 0 : data.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mData.get(oldItemPosition) == data.get(newItemPosition);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return mData.get(oldItemPosition) == data.get(newItemPosition);
                }
            }, true);
            this.mData = data;
            diffResult.dispatchUpdatesTo(this);
        }

        @Override
        public PushMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PushMessageViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(PushMessageViewHolder holder, int position) {
            holder.bindTo(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        static class PushMessageViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_push_message_title)
            TextView tv_push_message_title;
            @BindView(R.id.tv_push_message_time)
            TextView tv_push_message_time;
            @BindView(R.id.tv_push_message_text)
            TextView tv_push_message_text;

            PushMessageViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.push_message_item, parent, false));
                ButterKnife.bind(this, this.itemView);
            }

            void bindTo(PushMessage pushMessage) {
                tv_push_message_title.setText(pushMessage.title);
                tv_push_message_time.setText(DateUtils.convertDateToStr(new Date(pushMessage.createTime), "yyyy-MM-dd HH:mm"));
                tv_push_message_text.setText(pushMessage.text);
            }
        }
    }
}
