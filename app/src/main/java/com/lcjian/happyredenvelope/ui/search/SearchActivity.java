package com.lcjian.happyredenvelope.ui.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.db.table.SearchHistoryTable;
import com.lcjian.happyredenvelope.data.entity.SearchHistory;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_back)
    ImageButton btn_back;
    @BindView(R.id.et_room_id)
    EditText et_room_id;
    @BindView(R.id.tv_search)
    TextView tv_search;

    private CompositeSubscription mSubscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mSubscriptions = new CompositeSubscription();

        btn_back.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fl_search_histories_container, new SearchFragment()).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.tv_search:
                final String keyword = et_room_id.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    mRxBus.send(new SearchAction(keyword));
                    mSubscriptions.add(mStorIOSQLite.get().listOfObjects(SearchHistory.class)
                            .withQuery(Query.builder()
                                    .table(SearchHistoryTable.TABLE_NAME)
                                    .where(SearchHistoryTable.COLUMN_TEXT + " = ? ")
                                    .whereArgs(keyword)
                                    .build())
                            .prepare()
                            .asRxObservable()
                            .take(1)
                            .flatMap(new Func1<List<SearchHistory>, Observable<?>>() {
                                @Override
                                public Observable<?> call(List<SearchHistory> searchHistories) {
                                    SearchHistory searchHistory;
                                    if (searchHistories.isEmpty()) {
                                        searchHistory = new SearchHistory();
                                        searchHistory.text = keyword;
                                    } else {
                                        searchHistory = searchHistories.get(0);
                                    }
                                    searchHistory.updateTime = System.currentTimeMillis();
                                    return mStorIOSQLite.put().object(searchHistory).prepare().asRxObservable();
                                }
                            }).subscribe());
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mSubscriptions != null) {
            mSubscriptions.unsubscribe();
        }
        super.onDestroy();
    }

    static class SearchAction {

        String keyword;

        SearchAction(String keyword) {
            this.keyword = keyword;
        }
    }
}
