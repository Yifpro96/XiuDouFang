package com.example.administrator.xiudoufang.receipt.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.administrator.xiudoufang.R;
import com.example.administrator.xiudoufang.base.BaseTextWatcher;
import com.example.administrator.xiudoufang.base.IActivityBase;
import com.example.administrator.xiudoufang.bean.CustomerListBean;
import com.example.administrator.xiudoufang.common.utils.LogUtils;
import com.example.administrator.xiudoufang.common.utils.SoftInputHelper;
import com.example.administrator.xiudoufang.common.utils.StringUtils;
import com.example.administrator.xiudoufang.common.widget.LoadingViewDialog;
import com.example.administrator.xiudoufang.common.utils.SoftKeyBoardListener;
import com.example.administrator.xiudoufang.open.ui.AddCustomerActivity;
import com.example.administrator.xiudoufang.open.ui.SalesOrderActivity;
import com.example.administrator.xiudoufang.receipt.adapter.CustomerListAdapter;
import com.example.administrator.xiudoufang.receipt.logic.CustomerListLogic;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerListActivity extends AppCompatActivity implements IActivityBase, View.OnClickListener {

    private final int COUNT = 20;
    public static final String SELECTED_ITEM = "selected_item";
    public static final String FROM_CLASS = "from_class";

    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private EditText mEtFilter;
    private ImageView mIvClose;
    private TextView mTvCancel;

    private CustomerListLogic mLogic;
    private CustomerListAdapter mAdapter;
    private List<CustomerListBean.CustomerBean> mList;
    private String mFilterText = "";
    private boolean mIsShowSoftInput;
    private int mCurrentPage = 1;
    private HashMap<String, String> mParams;

    public static void start(Context context) {
        Intent intent = new Intent(context, CustomerListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_customer_list;
    }

    @Override
    public void initView() {
        setTitle("客户列表");
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRecyclerView = findViewById(R.id.recycler_view);
        mEtFilter = findViewById(R.id.et_filter);
        mIvClose = findViewById(R.id.iv_close);
        mTvCancel = findViewById(R.id.tv_cancel);

        mEtFilter.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        mEtFilter.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mEtFilter.addTextChangedListener(new InnerTextWatcher());
        mEtFilter.setOnEditorActionListener(new InnerEditActionListener());
        SoftKeyBoardListener.setListener(CustomerListActivity.this, new InnerSoftKeyBoardChangeListener());
    }

    @Override
    public void initData() {
        mLogic = new CustomerListLogic();
        mAdapter = new CustomerListAdapter(R.layout.item_customer_list, mList);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new InnerItemClickListener());
        mRefreshLayout.setOnRefreshListener(new InnerRefreshListener());
        mRefreshLayout.setOnLoadMoreListener(new InnerLoadMoreListener());
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new InnerScrollListener());
        mList = new ArrayList<>();
        LoadingViewDialog.getInstance().show(this);
        loadCustomerList(true);
    }

    //******** 加载客户列表 ********
    private void loadCustomerList(final boolean isRefresh) {
        if (isRefresh) mCurrentPage = 1;
        if (mParams == null) {
            JSONObject jsonObject = JSONObject.parseObject(StringUtils.readInfoForFile(StringUtils.LOGIN_INFO));
            mParams = new HashMap<>();
            mParams.put("dianid", jsonObject.getString("dianid"));
            mParams.put("userdengji", jsonObject.getString("dengji_value")); //******** 用户服务等级 ********
            mParams.put("userid", jsonObject.getString("userid"));
            mParams.put("dqc_id", "0"); //******** 客户id ********
            mParams.put("count", String.valueOf(COUNT));
        }
        mParams.put("search", mFilterText);
        mParams.put("pagenum", String.valueOf(mCurrentPage++));
        mLogic.requestCustomerList(this, mParams, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                LogUtils.e("客户列表 -> " + response.body());
                LoadingViewDialog.getInstance().dismiss();
                CustomerListBean customerListBean = JSONObject.parseObject(response.body(), CustomerListBean.class);
                List<CustomerListBean.CustomerBean> temp = customerListBean.getCustomerlist();
                if (isRefresh) {
                    mList.clear();
                    mList.addAll(temp);
                    mAdapter.setNewData(mList);
                    mRefreshLayout.finishRefresh();
                    mRefreshLayout.setNoMoreData(mList.size() < COUNT);
                } else {
                    mList.addAll(temp);
                    mAdapter.addData(temp);
                    if (temp.size() < COUNT) {
                        mRefreshLayout.finishLoadMoreWithNoMoreData();
                    } else {
                        mRefreshLayout.finishLoadMore();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (SalesOrderActivity.TAG.equals(getIntent().getStringExtra(FROM_CLASS)))
            getMenuInflater().inflate(R.menu.menu_customer_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_customer:
                AddCustomerActivity.start(CustomerListActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_filter:
                mEtFilter.setCursorVisible(true);
                break;
            case R.id.iv_close:
                mEtFilter.setText("");
                if (!mIsShowSoftInput)
                    SoftInputHelper.showSoftInput(this, mEtFilter);
                break;
            case R.id.tv_cancel:
                mFilterText = "";
                mEtFilter.setText("");
                mEtFilter.setCursorVisible(false);
                mIvClose.setVisibility(View.GONE);
                mTvCancel.setVisibility(View.GONE);
                SoftInputHelper.hideSoftInput(this);
                LoadingViewDialog.getInstance().show(this);
                loadCustomerList(true);
                break;
        }
    }

    private class InnerSoftKeyBoardChangeListener implements SoftKeyBoardListener.OnSoftKeyBoardChangeListener {

        @Override
        public void keyBoardShow(int height) {
            mTvCancel.setVisibility(View.VISIBLE);
            mIsShowSoftInput = true;
        }

        @Override
        public void keyBoardHide(int height) {
            mIsShowSoftInput = false;
        }
    }

    private class InnerEditActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                if (mFilterText.length() != 0) {
                    LoadingViewDialog.getInstance().show(CustomerListActivity.this);
                    loadCustomerList(true);
                    return true;
                }
            }
            return false;
        }
    }

    private class InnerTextWatcher extends BaseTextWatcher {

        @Override
        public void afterTextChanged(Editable editable) {
            mFilterText = editable.toString().trim();
            int length = editable.toString().trim().length();
            mIvClose.setVisibility(length > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private class InnerItemClickListener implements BaseQuickAdapter.OnItemClickListener {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            Class clazz = PaymentActivity.class;
            if (SalesOrderActivity.TAG.equals(getIntent().getStringExtra(FROM_CLASS))) {
                clazz = CustomerDetailsActivity.class;
            }
            Intent intent = new Intent(CustomerListActivity.this, clazz);
            intent.putExtra(SELECTED_ITEM, mList.get(position));
            CustomerListActivity.this.startActivity(intent);
        }
    }

    private class InnerRefreshListener implements OnRefreshListener {

        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            mRefreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadCustomerList(true);
                }
            }, 2000);
        }
    }

    private class InnerLoadMoreListener implements OnLoadMoreListener {

        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            mRefreshLayout.getLayout().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadCustomerList(false);
                }
            }, 2000);
        }
    }

    private class InnerScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                Glide.with(CustomerListActivity.this).resumeRequests();
            }else {
                Glide.with(CustomerListActivity.this).pauseRequests();
            }
        }
    }
}
