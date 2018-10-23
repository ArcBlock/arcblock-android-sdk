package com.arcblock.sdk.demo.corekit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.arcblock.corekit.CoreKitQuery;
import com.arcblock.corekit.CoreKitResultListener;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.btc.AccountByAddressQuery;
import com.arcblock.sdk.demo.btc.BlockByHashQuery;
import com.google.gson.Gson;
import com.yuyh.jsonviewer.library.JsonRecyclerView;

public class MultiQueryInOnePageActivity extends AppCompatActivity {

    private JsonRecyclerView json_view_account;
    private JsonRecyclerView json_view_block;
    private CoreKitQuery mCoreKitQuery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_query_in_one_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.multi_query_in_one_page);

        initView();

        mCoreKitQuery = new CoreKitQuery(this, DemoApplication.getInstance().abCoreKitClientBtc());
    }

    private void initView() {
        json_view_account = (JsonRecyclerView) findViewById(R.id.json_view_account);
        json_view_block = (JsonRecyclerView) findViewById(R.id.json_view_block);

        findViewById(R.id.query_account_detail_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryAccountDetail();
            }
        });
        findViewById(R.id.query_block_detail_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryBlockDetail();
            }
        });
    }

    private void queryAccountDetail() {
        mCoreKitQuery.query(AccountByAddressQuery.builder().address("1M5MZ9hM19MetHBtB7a7DzULbf9hDuiX9r").build(), new CoreKitResultListener<AccountByAddressQuery.Data>() {
            @Override
            public void onSuccess(AccountByAddressQuery.Data data) {
                json_view_account.bindJson(new Gson().toJson(data));
            }

            @Override
            public void onError(String errMsg) {
                Toast.makeText(MultiQueryInOnePageActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void queryBlockDetail() {
        mCoreKitQuery.query(BlockByHashQuery.builder().hash("00000000000000000018438255d3b4c13eb1fe44024a9dfb0de83d598e5e42c6").build(), new CoreKitResultListener<BlockByHashQuery.Data>() {
            @Override
            public void onSuccess(BlockByHashQuery.Data data) {
                json_view_block.bindJson(new Gson().toJson(data));
            }

            @Override
            public void onError(String errMsg) {
                Toast.makeText(MultiQueryInOnePageActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://返回键的id
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
