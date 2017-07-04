package com.lcjian.happyredenvelope.common;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.GroupHeader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupHeaderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_group_title)
    TextView tv_group_title;

    public GroupHeaderViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_header_item, parent, false));
        ButterKnife.bind(this, this.itemView);
    }

    public void bindTo(GroupHeader groupHeader) {
        tv_group_title.setText(groupHeader.title);
    }
}
