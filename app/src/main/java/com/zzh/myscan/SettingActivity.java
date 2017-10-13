package com.zzh.myscan;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zzh.myscan.utils.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 腾翔信息 on 2017/10/12.
 */

public class SettingActivity extends BaseActivity {

    @Bind(R.id.setting_btn)
    Button mBtn;
    @Bind(R.id.url)//输入 url
    EditText mEdit;
    @Bind(R.id.tool_set)
    Toolbar mTool;
    @OnClick(R.id.setting_btn) void set(){
        if (!TextUtils.isEmpty(mEdit.getText().toString())){
            Constant.ipUrl = mEdit.getText().toString();
            finish();
        }else {
            showToast("服务器地址不能为空！");
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_main_layout);
        ButterKnife.bind(this);
        mTool.setTitle("");
        setSupportActionBar(mTool);
        mTool.setNavigationIcon(R.drawable.ic_my_returns_arrow);
        mTool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
