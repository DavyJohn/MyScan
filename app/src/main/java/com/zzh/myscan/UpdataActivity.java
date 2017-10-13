package com.zzh.myscan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zzh.myscan.callback.ToCallBack;
import com.zzh.myscan.mode.BaseData;
import com.zzh.myscan.utils.Constant;
import com.zzh.myscan.utils.SharedPreferencesUtil;

import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by 腾翔信息 on 2017/10/12.
 */

public class UpdataActivity extends BaseActivity {

    @Bind(R.id.commit_btn)
    Button mCommitBtn;
    @Bind(R.id.change_btn)
    Button mChangeBtn;
    @Bind(R.id.tool_updata)
    Toolbar mTool;
    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.address)
    EditText address;
    @Bind(R.id.phonenum)
    EditText phone;
    @Bind(R.id.peopleid)
    EditText id;
    //置换
    @OnClick(R.id.change_btn) void change(){
        Constant.isZH = true;
        finish();
    }
    @OnClick(R.id.commit_btn) void commit(){
        if (TextUtils.isEmpty(username.getText().toString())){
            showToast("用户名不能为空！");
        }else if (TextUtils.isEmpty(address.getText().toString())){
            showToast("用户地址不能为空！");
        }else if (TextUtils.isEmpty(phone.getText().toString())){
            showToast("用户手机号不能为空！");
        }else if (TextUtils.isEmpty(id.getText().toString())){
            showToast("用户身份信息不能为空！");
        }else {
            //保存
            commitData();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updata_main_layout);
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
        initEdit();

    }

    /**
     * 提交
     * **/
    private void commitData(){
        if (getIntent().getStringExtra("TAG").equals("old")){
            //置换
            updata(phone.getText().toString()
                    ,SharedPreferencesUtil.getInstance(UpdataActivity.this).getString("id")
                    ,username.getText().toString()
                    ,id.getText().toString()
                    ,address.getText().toString()
                    ,SharedPreferencesUtil.getInstance(UpdataActivity.this).getString("code"));
        }else {
            //保存
            save(phone.getText().toString()
                    ,username.getText().toString()
                    ,id.getText().toString()//身份证号
                    ,address.getText().toString()
                    ,SharedPreferencesUtil.getInstance(UpdataActivity.this).getString("code"));
        }
    }


    private void initEdit(){
        if (getIntent().getStringExtra("TAG").equals("old")){
            //老瓶子信息
            if (Constant.isZH == true){
                mCommitBtn.setVisibility(View.VISIBLE);
                mChangeBtn.setVisibility(View.GONE);
            }else {
                mCommitBtn.setVisibility(View.GONE);
                mChangeBtn.setVisibility(View.VISIBLE);
            }
            if (SharedPreferencesUtil.getInstance(UpdataActivity.this).getString("name") != null){
                username.setText(SharedPreferencesUtil.getInstance(UpdataActivity.this).getString("name"));
            }
            if (SharedPreferencesUtil.getInstance(UpdataActivity.this).getString("phone") != null){
                phone.setText(SharedPreferencesUtil.getInstance(UpdataActivity.this).getString("phone"));
            }
            if (SharedPreferencesUtil.getInstance(UpdataActivity.this).getString("address") != null){
                address.setText(SharedPreferencesUtil.getInstance(UpdataActivity.this).getString("address"));
            }
            if (SharedPreferencesUtil.getInstance(UpdataActivity.this).getString("ip") != null){
                id.setText(SharedPreferencesUtil.getInstance(UpdataActivity.this).getString("ip"));
            }
        }else {
            //新瓶子
            mChangeBtn.setVisibility(View.GONE);
            mCommitBtn.setVisibility(View.VISIBLE);
        }

    }

    private void save(String phone,String userName,String idNumber,String address,String code){
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("customer.user_name",userName);
        map.put("customer.id_number",idNumber);
        map.put("customer.phone_number",phone);
        map.put("customer.address",address);
        map.put("customer.cylinder_code",code);
        OkHttpUtils.post().params(map).url("http://"+Constant.ipUrl+"/yhqg/app/save").build().execute(new ToCallBack<BaseData>() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(BaseData data, int id) {
                showToast(data.getMsg());
                finish();
            }
        });
    }

    private void updata(String phone,String id,String userName,String idNumber,String address,String code){
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("customer.user_name",userName);
        map.put("customer.id_number",idNumber);
        map.put("customer.phone_number",phone);
        map.put("customer.address",address);
        map.put("customer.cylinder_code",code);
        map.put("customer.id",id);
        OkHttpUtils.post().params(map).url("http://"+Constant.ipUrl+"/yhqg/app/update").build().execute(new ToCallBack<BaseData>() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(BaseData data, int id) {
                showToast(data.getMsg());
                Constant.isZH = false;
                finish();
            }
        });

    }
}
