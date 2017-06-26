package com.lcjian.happyredenvelope.ui.mine;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lcjian.happyredenvelope.BaseActivity;
import com.lcjian.happyredenvelope.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyLuckCardActivity extends BaseActivity {

    @BindView(R.id.chb_quarter_luck_card)
    CheckBox chb_quarter_luck_card;
    @BindView(R.id.chb_half_an_hour_luck_card)
    CheckBox chb_half_an_hour_luck_card;
    @BindView(R.id.chb_one_hour_luck_card)
    CheckBox chb_one_hour_luck_card;
    @BindView(R.id.chb_two_hours_luck_card)
    CheckBox chb_two_hours_luck_card;
    @BindView(R.id.chb_day_luck_card)
    CheckBox chb_day_luck_card;
    @BindView(R.id.btn_decrease)
    Button btn_decrease;
    @BindView(R.id.tv_buy_luck_card_count)
    TextView tv_buy_luck_card_count;
    @BindView(R.id.btn_increase)
    Button btn_increase;
    @BindView(R.id.tv_total_price)
    TextView tv_total_price;
    @BindView(R.id.btn_buy_now)
    Button btn_buy_now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_luck_card);
        ButterKnife.bind(this);
    }
}
