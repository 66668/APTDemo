package com.sjy.aptdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sjy.annotation.MyBindView;
import com.sjy.butterknife.MyButterKnife;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    //自定义注解
    @MyBindView(R.id.btn_01)
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //添加自定义注解
        MyButterKnife.bind(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "自定义起效果了", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyButterKnife.unBind(this);
    }
}
