package com.zzh.myscan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zzh.myscan.utils.Constant;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseApplication.getInstance().add(this);
        int mode = getIntent().getIntExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_ALL_MODE);
        ButterKnife.bind(this);
    }
    @OnClick({R.id.create_code,R.id.scan_2code,R.id.scan_bar_code,R.id.scan_code})
    public void click(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.create_code://生成吗
                intent = new Intent(this,CreateCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.scan_2code://扫描二维码
                intent = new Intent(this,CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_QRCODE_MODE);
                startActivity(intent);
                break;
            case R.id.scan_bar_code:
                intent = new Intent(this,CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_BARCODE_MODE);
                startActivity(intent);
                break;
            case R.id.scan_code:
                intent = new Intent(this,CommonScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_ALL_MODE);
                startActivity(intent);
                break;
        }
    }
}
