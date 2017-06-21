package com.lcjian.happyredenvelope.ui.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lcjian.happyredenvelope.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.fl_main_fragment_container, new ExploreFragment()).commit();
    }
}
