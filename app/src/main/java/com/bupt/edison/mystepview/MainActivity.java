package com.bupt.edison.mystepview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button next;
    Button init;
    Button fail;
    Button success;
    Button addtime;
    Button start;
    ExpendStepView expendStepView;
    int step =0;
    int total = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    void initView(){
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(this);
        init = (Button)findViewById(R.id.init);
        init.setOnClickListener(this);
        fail = (Button)findViewById(R.id.fail);
        fail.setOnClickListener(this);
        success = (Button)findViewById(R.id.success);
        success.setOnClickListener(this);
        addtime = (Button)findViewById(R.id.addtime);
        addtime.setOnClickListener(this);
        start = (Button)findViewById(R.id.start);
        start.setOnClickListener(this);
        expendStepView = (ExpendStepView)findViewById(R.id.stepview);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.init:
                step = 0;
                expendStepView.restore(); //具有初始化的作用
                break;
            case R.id.next:
                step++;
                expendStepView.nextStep();
                break;
            case R.id.addtime:
                expendStepView.nextStep("02-02 21:54");
                break;
            case R.id.fail:
                expendStepView.setProgressFailture();
                break;
            case R.id.success:
                expendStepView.setProgressCompeleted();
                break;
            case R.id.start:
                expendStepView.startProgress("02-02 21:54");
                Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}
