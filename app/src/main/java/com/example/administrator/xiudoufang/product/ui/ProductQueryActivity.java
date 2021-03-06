package com.example.administrator.xiudoufang.product.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.xiudoufang.R;
import com.example.administrator.xiudoufang.base.IActivityBase;
import com.example.administrator.xiudoufang.bean.ProductFilter;
import com.example.administrator.xiudoufang.bean.Supplier;
import com.example.administrator.xiudoufang.bean.TypeListBean;
import com.example.administrator.xiudoufang.common.widget.SearchInfoView;
import com.example.administrator.xiudoufang.purchase.ui.SingleLineTextDialog;
import com.example.administrator.xiudoufang.purchase.ui.SupplierDetailsActivity;
import com.example.administrator.xiudoufang.purchase.ui.SupplierListActivity;
import com.example.administrator.xiudoufang.stock.ui.TypeListActivity;

import java.util.ArrayList;
import java.util.Collections;

public class ProductQueryActivity extends AppCompatActivity implements IActivityBase, View.OnClickListener {

    public static final String TAG = ProductQueryActivity.class.getSimpleName();
    private static final int RESULT_FOR_TYPE_LIST = 115;
    public static final String PRODUCT_FILTER = "product_filter";

    private SearchInfoView mSivName;
    private SearchInfoView mSivSupplier;
    private SearchInfoView mSivType;
    private SearchInfoView mSivPic;
    private SingleLineTextDialog mPicDialog;

    private ArrayList<String> mPicList;
    private int mPicIndex;
    private String mIdType = "";
    private String mIsIncludeSubclass = "";
    private Supplier mSupplier;

    @Override
    public int getLayoutId() {
        return R.layout.activity_product_query;
    }

    @Override
    public void initView() {
        setTitle("产品查询");
        mSivName = findViewById(R.id.siv_name);
        mSivSupplier = findViewById(R.id.siv_supplier);
        mSivType = findViewById(R.id.siv_type);
        mSivPic = findViewById(R.id.siv_pic);
        TextView tvQuery = findViewById(R.id.tv_query);
        TextView tvReset = findViewById(R.id.tv_reset);

        tvQuery.setOnClickListener(this);
        tvReset.setOnClickListener(this);
        mSivSupplier.setOnClickListener(this);
        mSivType.setOnClickListener(this);
        mSivPic.setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //******** 返回供应商 ********
        Supplier supplier = getIntent().getParcelableExtra(SupplierDetailsActivity.SELECTED_SUPPLIER);
        if (supplier != null) {
            mSivSupplier.setValue(supplier.getCustomername());
            mSupplier = supplier;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FOR_TYPE_LIST && data != null) { //******** 返回类别选择 ********
            ArrayList<TypeListBean.TypeBean> list = data.getParcelableArrayListExtra(TypeListActivity.TYPE_LIST);
            StringBuilder idBuilder = new StringBuilder();
            StringBuilder nameBuilder = new StringBuilder();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    idBuilder.append(list.get(i).getId()).append(",");
                    nameBuilder.append(list.get(i).getName()).append(",");
                }
            }
            idBuilder.setLength(idBuilder.length() - 1);
            nameBuilder.setLength(nameBuilder.length() - 1);
            mIdType = idBuilder.toString();
            mSivType.setValue(nameBuilder);
            mIsIncludeSubclass = data.getBooleanExtra(TypeListActivity.INCLUDE_SUBCLASS, false) ? "1" : "0";
        }
    }

    @Override
    public void initData() {
        String[] picArr = {"全部", "有", "无"};
        mPicList = new ArrayList<>();
        Collections.addAll(mPicList, picArr);
        mSivPic.setValue(mPicList.get(0));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.siv_supplier:
                startActivity(new Intent(ProductQueryActivity.this, SupplierListActivity.class)
                        .putExtra(SupplierListActivity.FROM_CLASS, TAG));
                break;
            case R.id.siv_type:
                startActivityForResult(new Intent(ProductQueryActivity.this, TypeListActivity.class), RESULT_FOR_TYPE_LIST);
                break;
            case R.id.siv_pic:
                if (mPicDialog == null) {
                    mPicDialog = SingleLineTextDialog.newInstance(mPicList);
                    mPicDialog.setOnItemChangedListener(new SingleLineTextDialog.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            mPicIndex = position;
                            mSivPic.setValue(mPicList.get(position));
                        }
                    });
                }
                mPicDialog.show(getSupportFragmentManager(), "SingleLineTextDialog");
                break;
            case R.id.tv_query:
                Intent intent_2 = new Intent();
                ProductFilter filter = new ProductFilter();
                filter.setName(mSivName.getValue());
                filter.setSupplierId(mSupplier == null ? "" : mSupplier.getC_id());
                filter.setTypeId(mIdType);
                filter.setAction(mIsIncludeSubclass);
                filter.setIsIncludePic(mPicIndex == 0 ? "" : mPicIndex == 1 ? "1" : "0");
                intent_2.putExtra(PRODUCT_FILTER, filter);
                setResult(Activity.RESULT_OK, intent_2);
                finish();
                break;
            case R.id.tv_reset:
                mSivName.setValue("");
                mSivSupplier.setValue("'");
                mSivType.setValue("");
                mSivPic.setValue(mPicList.get(mPicIndex = 0));
                mSupplier = null;
                mIdType = null;
                break;
        }
    }
}
