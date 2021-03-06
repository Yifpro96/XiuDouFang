package com.example.administrator.xiudoufang.purchase.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.administrator.xiudoufang.R;
import com.example.administrator.xiudoufang.base.IActivityBase;
import com.example.administrator.xiudoufang.bean.ProductItem;
import com.example.administrator.xiudoufang.bean.SalesProductListBean;
import com.example.administrator.xiudoufang.common.callback.JsonCallback;
import com.example.administrator.xiudoufang.common.utils.LogUtils;
import com.example.administrator.xiudoufang.common.utils.PreferencesUtils;
import com.example.administrator.xiudoufang.common.utils.StringUtils;
import com.example.administrator.xiudoufang.common.utils.TintUtils;
import com.example.administrator.xiudoufang.common.utils.ToastUtils;
import com.example.administrator.xiudoufang.common.widget.LoadingViewDialog;
import com.example.administrator.xiudoufang.open.logic.SalesOrderLogic;
import com.example.administrator.xiudoufang.open.ui.SalesOrderActivity;
import com.example.administrator.xiudoufang.open.ui.SameBarcodeSalesProductDialog;
import com.example.administrator.xiudoufang.purchase.logic.NewPurchaseOrderLogic;
import com.lzy.okgo.model.Response;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Created by Administrator on 2018/8/20
 */

public class ScanActivity extends AppCompatActivity implements IActivityBase, QRCodeView.Delegate, View.OnClickListener {

    private final boolean IS_OPEN_SEQUENTIAL_SCAN = PreferencesUtils.getPreferences().getBoolean(PreferencesUtils.SEQUENTIAL_SCAN, false);
    public static final String FROM_CLASS = "from_class";
    public static final String BARCODE_PRODUCT = "barcode_product";
    public static final int SALES_ORDER = 0;
    public static final int PRODUCT_LIST = 1;
    public static final int STOCK_LIST = 2;
    public static final int CREATE_PURCHASE_ORDER = 3;
    public static final int PURCHASE_ORDER_DETAILS = 4;
    private static final int PAGENUM = 1;
    private static final int COUNT = 10;

    private ZXingView mZXingView;
    private TextView mTvFlash;

    private SensorManager mSensorManager;
    private boolean mIsOpenFlash; //******** 闪光灯状态 ********
    private ArrayList<SalesProductListBean.SalesProductBean> mSalesProductBeans;
    private ArrayList<ProductItem> mProductItems;
    private int mFromClass = -1;

    public static void start(Context context) {
        Intent intent = new Intent(context, ScanActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_scan;
    }

    @Override
    public void onBackPressed() {
        if (IS_OPEN_SEQUENTIAL_SCAN && mFromClass != -1) {
            switch (mFromClass) {
                case SALES_ORDER:
                    setResult(Activity.RESULT_OK, new Intent().putParcelableArrayListExtra(BARCODE_PRODUCT, mSalesProductBeans));
                    break;
                case CREATE_PURCHASE_ORDER:
                case PURCHASE_ORDER_DETAILS:
                    setResult(Activity.RESULT_OK, new Intent().putParcelableArrayListExtra(NewPurchaseOrderActivity.SELECTED_PRODUCT_LIST, mProductItems));
                    break;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void initView() {
        setTitle("扫一扫");
        mZXingView = findViewById(R.id.zxingview);
        mTvFlash = findViewById(R.id.tv_flash);
        mZXingView.setDelegate(this);
        mTvFlash.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mZXingView.startSpot();
        mZXingView.setType(BarcodeType.ONE_DIMENSION, null);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);

        mTvFlash.setText("轻点打开");
        mTvFlash.setTextColor(Color.WHITE);
        mTvFlash.setCompoundDrawablesWithIntrinsicBounds(null, TintUtils.tintDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_flash),
                ColorStateList.valueOf(Color.WHITE)), null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_flash:
                if (mIsOpenFlash) {
                    mZXingView.closeFlashlight();
                    mTvFlash.setText("轻点打开");
                    mTvFlash.setTextColor(Color.WHITE);
                    mTvFlash.setCompoundDrawablesWithIntrinsicBounds(null, TintUtils.tintDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_flash),
                            ColorStateList.valueOf(Color.WHITE)), null, null);
                } else {
                    mZXingView.openFlashlight();
                    mTvFlash.setText("轻点关闭");
                    mTvFlash.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    mTvFlash.setCompoundDrawablesWithIntrinsicBounds(null, TintUtils.tintDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_flash),
                            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))), null, null);
                }
                mIsOpenFlash = !mIsOpenFlash;
                break;
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        ToastUtils.show(this, result);
        mFromClass = getIntent().getIntExtra(FROM_CLASS, 0);
        switch (mFromClass) {
            case SALES_ORDER: //******** 开单首页 ********
                SalesOrderLogic salesOrderLogic = new SalesOrderLogic();
                HashMap<String, String> params_1 = new HashMap<>();
                params_1.put("dianid", PreferencesUtils.getPreferences().getString(PreferencesUtils.DIAN_ID, ""));
                JSONObject jsonObject = JSONObject.parseObject(StringUtils.readInfoForFile(StringUtils.LOGIN_INFO));
                params_1.put("userdengji", jsonObject.getString("dengji_value")); //******** 用户服务等级 ********
                params_1.put("dqcpid", "0"); //******** 产品id 默认0 ********
                params_1.put("count", String.valueOf(COUNT));
                params_1.put("saomiao", "1");
                params_1.put("c_id", getIntent().getStringExtra(SalesOrderActivity.C_ID)); //******** 客户id 新客户0 ********
                params_1.put("searchitem", result);
                params_1.put("pagenum", String.valueOf(PAGENUM));
                salesOrderLogic.requestProductList(this, params_1, new JsonCallback<SalesProductListBean>() {
                    @Override
                    public void onSuccess(Response<SalesProductListBean> response) {
                        LoadingViewDialog.getInstance().dismiss();
                        List<SalesProductListBean.SalesProductBean> temp = response.body().getChanpinlist();
                        for (SalesProductListBean.SalesProductBean bean : temp) {
                            for (SalesProductListBean.SalesProductBean.PacklistBean b : bean.getPacklist()) {
                                if ("1".equals(b.getCheck())) {
                                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                    String price = decimalFormat.format(Double.parseDouble(b.getDengji_price()));
                                    bean.setOrder_prc(price);
                                    bean.setS_jiage2(price);
                                    bean.setFactor(b.getUnit_bilv());
                                    bean.setUnitname(b.getUnitname());
                                }
                            }

                        }
                        if (temp.size() > 0) {
                            if (temp.size() > 1) {
                                //******** 返回多个同条形码的产品 ********
                                ArrayList<SalesProductListBean.SalesProductBean> list = new ArrayList<>(temp.size());
                                for (SalesProductListBean.SalesProductBean bean : temp) {
                                    list.add(bean);
                                }
                                final SameBarcodeSalesProductDialog dialog = SameBarcodeSalesProductDialog.newInstance(mFromClass, list);
                                dialog.setOnItemClickListener(new SameBarcodeSalesProductDialog.OnItemClickListener() {
                                    @Override
                                    public void onSubmit(ArrayList<SalesProductListBean.SalesProductBean> list) {
                                        if (IS_OPEN_SEQUENTIAL_SCAN) {
                                            if (mSalesProductBeans == null)
                                                mSalesProductBeans = new ArrayList<>();
                                            mSalesProductBeans.addAll(list);
                                            dialog.dismiss();
                                            mZXingView.startSpot();
                                        } else {
                                            setResult(Activity.RESULT_OK, new Intent().putParcelableArrayListExtra(BARCODE_PRODUCT, list));
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onDismiss() {
                                        mZXingView.startSpot();
                                    }
                                });
                                dialog.show(getSupportFragmentManager(), "");
                            } else {
                                //******** 返回单个产品 ********
                                Intent intent = new Intent();
                                ArrayList<SalesProductListBean.SalesProductBean> list = new ArrayList<>();
                                list.add(temp.get(0));
                                intent.putExtra(NewPurchaseOrderActivity.SELECTED_PRODUCT, list);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                        }
                    }
                });
                break;
            case PRODUCT_LIST: //******** 产品模块的产品列表 ********
            case STOCK_LIST: //******** 库存列表 ********
                setResult(Activity.RESULT_OK, new Intent().putExtra(BARCODE_PRODUCT, result));
                finish();
                break;
            case CREATE_PURCHASE_ORDER: //******** 新开采购单 ********
            case PURCHASE_ORDER_DETAILS: //******** 采购单详情 ********
                final NewPurchaseOrderLogic newPurchaseOrderLogic = new NewPurchaseOrderLogic();
                HashMap<String, String> params_4 = new HashMap<>();
                params_4.put("dianid", PreferencesUtils.getPreferences().getString(PreferencesUtils.DIAN_ID, ""));
                params_4.put("userid", PreferencesUtils.getPreferences().getString(PreferencesUtils.USER_ID, ""));
                params_4.put("c_id", "0"); //******** 供应商id ********
                params_4.put("count", String.valueOf(COUNT));
                params_4.put("saomiao", "1"); //******** 是否扫描 ********
                params_4.put("dqcpid", "0"); //******** 产品id ********
                params_4.put("serchitem", result);
                params_4.put("pagenum", String.valueOf(PAGENUM));
                newPurchaseOrderLogic.requestProductList(this, params_4, new JsonCallback<String>() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LoadingViewDialog.getInstance().dismiss();
                        LogUtils.e("info->"+response.body());
                        ArrayList<ProductItem> temp = newPurchaseOrderLogic.parseProductListJson(JSONObject.parseObject(response.body()));
                        if (temp.size() > 0) {
                            if (temp.size() > 1) {
                                //******** 返回多个同条形码的产品 ********
                                final SameBarcodeSupplierProductDialog dialog = SameBarcodeSupplierProductDialog.newInstance(mFromClass, temp);
                                dialog.setOnItemClickListener(new SameBarcodeSupplierProductDialog.OnItemClickListener() {
                                    @Override
                                    public void onSubmit(ArrayList<ProductItem> list) {
                                        if (IS_OPEN_SEQUENTIAL_SCAN) {
                                            if (mProductItems == null)
                                                mProductItems = new ArrayList<>();
                                            mProductItems.addAll(list);
                                            dialog.dismiss();
                                            mZXingView.startSpot();
                                        } else {
                                            setResult(Activity.RESULT_OK, new Intent().putParcelableArrayListExtra(NewPurchaseOrderActivity.SELECTED_PRODUCT_LIST, list));
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onDismiss() {
                                        mZXingView.startSpot();
                                    }
                                });
                                dialog.show(getSupportFragmentManager(), "");
                            } else {
                                //******** 返回单个产品 ********
                                if (IS_OPEN_SEQUENTIAL_SCAN) {
                                    mProductItems.addAll(temp);
                                    mZXingView.startSpot();
                                } else {
                                    Intent intent = new Intent();
                                    intent.putExtra(NewPurchaseOrderActivity.SELECTED_PRODUCT, temp);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            }
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera();
        mZXingView.showScanRect();
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(listener);
        }
        super.onDestroy();
    }

    private SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float value = event.values[0];
            if (value < 7) {
                mTvFlash.setVisibility(View.VISIBLE);
            }
        }
    };

}
