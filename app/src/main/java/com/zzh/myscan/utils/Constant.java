package com.zzh.myscan.utils;

/**
 * Created by 刘红亮 on 2015/9/24 14:08.
 */
public class Constant {

    /**
     * 二维码请求的type
     */
    public static final String REQUEST_SCAN_TYPE="type";
    /**
     * 普通类型，扫完即关闭
     */
    public static final int REQUEST_SCAN_TYPE_COMMON=0;
    /**
     * 服务商登记类型，扫描
     */
    public static final int REQUEST_SCAN_TYPE_REGIST=1;


    /**
     * 扫描类型
     * 条形码或者二维码：REQUEST_SCAN_MODE_ALL_MODE
     * 条形码： REQUEST_SCAN_MODE_BARCODE_MODE
     * 二维码：REQUEST_SCAN_MODE_QRCODE_MODE
     *
     */
    public static final String REQUEST_SCAN_MODE="ScanMode";
    /**
     * 条形码： REQUEST_SCAN_MODE_BARCODE_MODE
     */
    public static final int REQUEST_SCAN_MODE_BARCODE_MODE = 0X100;
    /**
     * 二维码：REQUEST_SCAN_MODE_ALL_MODE
     */
    public static final int REQUEST_SCAN_MODE_QRCODE_MODE = 0X200;
    /**
     * 条形码或者二维码：REQUEST_SCAN_MODE_ALL_MODE
     */
    public static final int REQUEST_SCAN_MODE_ALL_MODE = 0X300;

    /**
     * 瓶子是否为置换
     * */
    public static Boolean isZH = false;
//    public static String ipUrl = "192.168.6.66:8080";//内网 自己用的
    public static String ipUrl = "192.168.1.200:8080";
}
