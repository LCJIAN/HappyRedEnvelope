package com.lcjian.happyredenvelope.ui.search;

import android.support.v7.widget.RecyclerView;

import com.lcjian.happyredenvelope.common.RecyclerFragment;
import com.lcjian.happyredenvelope.data.entity.PageResult;
import com.lcjian.lib.entity.Displayable;

import java.util.List;

import rx.Observable;

/**
 * Created by root on 17-7-3.
 */

public class SearchFragment extends RecyclerFragment<Displayable> {

    @Override
    public RecyclerView.Adapter onCreateAdapter(List<Displayable> data) {
        return null;
    }

    @Override
    public Observable<PageResult<Displayable>> onCreatePageObservable(int currentPage) {
        return null;
    }

    @Override
    public void notifyDataChanged(List<Displayable> data) {

    }
}
