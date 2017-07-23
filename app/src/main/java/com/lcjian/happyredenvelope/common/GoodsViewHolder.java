package com.lcjian.happyredenvelope.common;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lcjian.happyredenvelope.Global;
import com.lcjian.happyredenvelope.R;
import com.lcjian.happyredenvelope.data.entity.Goods;
import com.lcjian.happyredenvelope.ui.web.WebViewActivity;

import java.text.DecimalFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoodsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_goods_avatar)
    ImageView iv_goods_avatar;
    @BindView(R.id.tv_goods_name)
    TextView tv_goods_name;
    @BindView(R.id.tv_goods_price)
    TextView tv_goods_price;
    @BindView(R.id.tv_goods_sale_count)
    TextView tv_goods_sale_count;

    private Goods goods;

    private DecimalFormat mDecimalFormat;

    public GoodsViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_item, parent, false));
        ButterKnife.bind(this, this.itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), WebViewActivity.class)
                        .putExtra("url", goods.quanLink));
            }
        });
        this.mDecimalFormat = new DecimalFormat("0.00");
    }

    public void bindTo(Goods goods) {
        this.goods = goods;
        Context context = itemView.getContext();
        Glide.with(context)
                .load(goods.pic)
                .apply(Global.centerCrop)
                .transition(Global.crossFade)
                .into(iv_goods_avatar);
        tv_goods_name.setText(goods.title);
        tv_goods_price.setText(String.format(Locale.getDefault(), "%s%s", "￥", mDecimalFormat.format(goods.price)));
        tv_goods_sale_count.setText(context.getString(R.string.sale_count, goods.saleCount));
    }
}
