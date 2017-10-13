/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zzh.myscan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zzh.myscan.callback.ToCallBack;
import com.zzh.myscan.mode.Data;
import com.zzh.myscan.utils.CheckPermissionUtils;
import com.zzh.myscan.utils.Constant;
import com.zzh.myscan.utils.SharedPreferencesUtil;
import com.zzh.myscan.zxing.ScanListener;
import com.zzh.myscan.zxing.ScanManager;
import com.zzh.myscan.zxing.decode.DecodeThread;
import com.zzh.myscan.zxing.decode.Utils;

import butterknife.Bind;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 二维码扫描使用
 *
 * @author 刘红亮  2015年4月29日  下午5:49:45
 */
public final class CommonScanActivity extends BaseActivity implements ScanListener, View.OnClickListener {
    static final String TAG = CommonScanActivity.class.getSimpleName();
    SurfaceView scanPreview = null;
    View scanContainer;
    View scanCropView;
    ImageView scanLine;
    ScanManager scanManager;
    TextView iv_light;
    TextView qrcode_g_gallery;
    TextView qrcode_ic_back;
    final int PHOTOREQUESTCODE = 1111;
    private android.support.v7.app.AlertDialog dialog;
    @Bind(R.id.service_register_rescan)
    Button rescan;
    @Bind(R.id.setting)
    TextView mSetting;
    @Bind(R.id.scan_image)
    ImageView scan_image;
    @Bind(R.id.authorize_return)
    ImageView authorize_return;
    private int scanMode;//扫描模型（条形，二维码，全部）

    @Bind(R.id.common_title_TV_center)
    TextView title;
    @Bind(R.id.scan_hint)
    TextView scan_hint;
    @Bind(R.id.tv_scan_result)
    TextView tv_scan_result;

    @OnClick(R.id.setting) void set(){
        startActivity(new Intent(CommonScanActivity.this,SettingActivity.class));
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scan_code);
        ButterKnife.bind(this);
        initPermission();
        scanMode = Constant.REQUEST_SCAN_MODE_ALL_MODE;//直接判定为所有的都扫描
//        scanMode=getIntent().getIntExtra(Constant.REQUEST_SCAN_MODE,Constant.REQUEST_SCAN_MODE_ALL_MODE);
        initView();
    }
    private void initPermission() {
        //检查权限
        String[] permissions = CheckPermissionUtils.checkPermission(this);
        if (permissions.length == 0) {
            //权限都申请了
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }
    void initView() {
        switch (scanMode){
            case DecodeThread.BARCODE_MODE://目前不需要
                title.setText(R.string.scan_barcode_title);
                scan_hint.setText(R.string.scan_barcode_hint);
                break;
            case DecodeThread.QRCODE_MODE://目前不需要
                title.setText(R.string.scan_qrcode_title);
                scan_hint.setText(R.string.scan_qrcode_hint);
                break;
            case DecodeThread.ALL_MODE:
                title.setText(R.string.scan_allcode_title);
                scan_hint.setText(R.string.scan_allcode_hint);
                break;
        }
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = findViewById(R.id.capture_container);
        scanCropView = findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        qrcode_g_gallery = (TextView) findViewById(R.id.qrcode_g_gallery);
        qrcode_g_gallery.setOnClickListener(this);
        qrcode_ic_back = (TextView) findViewById(R.id.qrcode_ic_back);
        qrcode_ic_back.setOnClickListener(this);
        iv_light = (TextView) findViewById(R.id.iv_light);
        iv_light.setOnClickListener(this);
        rescan.setOnClickListener(this);
        authorize_return.setOnClickListener(this);
        //构造出扫描管理器
        scanManager = new ScanManager(this, scanPreview, scanContainer, scanCropView, scanLine, scanMode,this);
    }

    @Override
    public void onResume() {
        super.onResume();
        scanManager.onResume();
        rescan.setVisibility(View.INVISIBLE);
        scan_image.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanManager.onPause();
    }
    /**
     *
     */
    public void scanResult(Result rawResult, Bundle bundle) {
        //扫描成功后，扫描器不会再连续扫描，如需连续扫描，调用reScan()方法。
        //scanManager.reScan();
//		Toast.makeText(that, "result="+rawResult.getText(), Toast.LENGTH_LONG).show();

        if (!scanManager.isScanning()) { //如果当前不是在扫描状态
            //设置再次扫描按钮出现
            rescan.setVisibility(View.VISIBLE);
            scan_image.setVisibility(View.VISIBLE);
            Bitmap barcode = null;
            byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
            if (compressedBitmap != null) {
                barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
            }
//            scan_image.setImageBitmap(barcode);//将扫描到的直接现在扫描框中
        }
        rescan.setVisibility(View.VISIBLE);
        scan_image.setVisibility(View.VISIBLE);
        tv_scan_result.setVisibility(View.VISIBLE);
        //获取数据地方
//        tv_scan_result.setText("结果："+"http://"+Constant.ipUrl+"/yhqg/app/getCustomerByID"+"/"+rawResult.getText());
        if (rawResult.getText().contains("http:")){
            SharedPreferencesUtil.getInstance(CommonScanActivity.this).putString("code",rawResult.getText().substring(rawResult.getText().lastIndexOf("/"),rawResult.getText().length()));//钢瓶编号
            getData("http://"+Constant.ipUrl+"/yhqg/app/getCustomerByID"+rawResult.getText().substring(rawResult.getText().lastIndexOf("/"),rawResult.getText().length()));
        }else {
            SharedPreferencesUtil.getInstance(CommonScanActivity.this).putString("code",rawResult.getText());//钢瓶编号
//            getData("http://192.168.6.66:8080/yhqg/app/getCustomerByID/312312123");
            getData("http://"+Constant.ipUrl+"/yhqg/app/getCustomerByID"+"/"+rawResult.getText());

        }
    }
    void getData(String url){
        OkHttpUtils.get().url(url).params(null).build().execute(new ToCallBack<Data>() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(Data data, int id) {
                final Intent intent = new Intent(CommonScanActivity.this,UpdataActivity.class);
                if (data == null && Constant.isZH == false){//新瓶子 扫描气罐条码、输入客户信息
                    intent.putExtra("TAG","new");
                    startActivity(intent);
                }else if (data != null && Constant.isZH == false){
                    //老瓶子信息  扫描旧罐条码、读取信息、
                    SharedPreferencesUtil.getInstance(CommonScanActivity.this).putString("oldNum",data.getCylinder_code());
                    SharedPreferencesUtil.getInstance(CommonScanActivity.this).putString("name",data.getUser_name());
                    SharedPreferencesUtil.getInstance(CommonScanActivity.this).putString("phone",data.getPhone_number());
                    SharedPreferencesUtil.getInstance(CommonScanActivity.this).putString("address",data.getAddress());
                    SharedPreferencesUtil.getInstance(CommonScanActivity.this).putString("ip",data.getId_number());
                    SharedPreferencesUtil.getInstance(CommonScanActivity.this).putString("id",data.getId());
                    intent.putExtra("TAG","old");
                    startActivity(intent);
                }else if (Constant.isZH == true){//
                    // 扫描新罐条码、确认（修改数据库）
                    dialog = new android.support.v7.app.AlertDialog.Builder(CommonScanActivity.this)
                            .setMessage("是否置换钢瓶").setTitle("提示")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
//                                    Constant.isZH = false;
                                    intent.putExtra("TAG","old");
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();

                }

            }
        });
    }
    void startScan() {
        if (rescan.getVisibility() == View.VISIBLE) {
            rescan.setVisibility(View.INVISIBLE);
            scan_image.setVisibility(View.GONE);
            scanManager.reScan();
        }
    }

    @Override
    public void scanError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        //相机扫描出错时
        if(e.getMessage()!=null&&e.getMessage().startsWith("相机")){
            scanPreview.setVisibility(View.INVISIBLE);
        }
    }

    public void showPictures(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String photo_path;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTOREQUESTCODE:
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(data.getData(), proj, null, null, null);
                    if (cursor.moveToFirst()) {
                        int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(colum_index);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(), data.getData());
                        }
                        scanManager.scanningImage(photo_path);
                    }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qrcode_g_gallery:
                showPictures(PHOTOREQUESTCODE);
                break;
            case R.id.iv_light:
                scanManager.switchLight();
                break;
            case R.id.qrcode_ic_back:
                finish();
                break;
            case R.id.service_register_rescan://再次开启扫描
                startScan();
                break;
            case R.id.authorize_return:
                finish();
                break;
            default:
                break;
        }
    }

}