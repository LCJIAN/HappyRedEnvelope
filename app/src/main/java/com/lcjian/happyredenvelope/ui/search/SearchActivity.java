package com.lcjian.happyredenvelope.ui.search;

import android.os.Bundle;

import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_search_histories_container, new SearchFragment()).commit();
    }
}
