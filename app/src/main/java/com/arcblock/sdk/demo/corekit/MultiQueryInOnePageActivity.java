package com.arcblock.sdk.demo.corekit;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.CoreKitQuery;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.config.CoreKitConfig;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.btc.AccountByAddressQuery;
import com.arcblock.sdk.demo.btc.BlockByHashQuery;
import com.google.gson.Gson;
import com.yuyh.jsonviewer.library.JsonRecyclerView;

public class MultiQueryInOnePageActivity extends AppCompatActivity {

    private JsonRecyclerView json_view_account;
    private JsonRecyclerView json_view_block;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_query_in_one_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.multi_query_in_one_page);

        initView();
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
        // init AccountByAddressQueryHelper and get data
        AccountByAddressQueryHelper accountByAddressQueryHelper = new AccountByAddressQueryHelper(this, this, DemoApplication.getInstance(), CoreKitConfig.ApiType.API_TYPE_BTC);
        accountByAddressQueryHelper.setObserve(new Observer<CoreKitBean<AccountByAddressQuery.AccountByAddress>>() {
            @Override
            public void onChanged(@Nullable CoreKitBean<AccountByAddressQuery.AccountByAddress> coreKitBean) {
                json_view_account.bindJson(new Gson().toJson(coreKitBean));
            }
        });
    }

    private void queryBlockDetail() {
        // init BlockByHashQueryHelper and get data
        BlockByHashQueryHelper blockByHashQueryHelper = new BlockByHashQueryHelper(this, this, DemoApplication.getInstance().abCoreKitClientBtc());
        blockByHashQueryHelper.setObserve(new Observer<CoreKitBean<BlockByHashQuery.BlockByHash>>() {
            @Override
            public void onChanged(@Nullable CoreKitBean<BlockByHashQuery.BlockByHash> coreKitBean) {
                json_view_block.bindJson(new Gson().toJson(coreKitBean));
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

    /**
     * AccountByAddressQueryHelper for AccountByAddressQuery
     */
    private class AccountByAddressQueryHelper extends CoreKitQuery<AccountByAddressQuery.Data, AccountByAddressQuery.AccountByAddress> {

        public AccountByAddressQueryHelper(FragmentActivity activity, LifecycleOwner lifecycleOwner, Context context, CoreKitConfig.ApiType apiType) {
            super(activity, lifecycleOwner, context, apiType);
        }

        @Override
        public AccountByAddressQuery.AccountByAddress map(Response<AccountByAddressQuery.Data> dataResponse) {
            if (dataResponse != null) {
                return dataResponse.data().getAccountByAddress();
            }
            return null;
        }

        @Override
        public Query getQuery() {
            return AccountByAddressQuery.builder().address("1M5MZ9hM19MetHBtB7a7DzULbf9hDuiX9r").build();
        }
    }

    /**
     * BlockByHashQueryHelper for BlockByHashQuery
     */
    private class BlockByHashQueryHelper extends CoreKitQuery<BlockByHashQuery.Data, BlockByHashQuery.BlockByHash> {

        public BlockByHashQueryHelper(FragmentActivity activity, LifecycleOwner lifecycleOwner, ABCoreKitClient client) {
            super(activity, lifecycleOwner, client);
        }

        @Override
        public BlockByHashQuery.BlockByHash map(Response<BlockByHashQuery.Data> dataResponse) {
            if (dataResponse != null) {
                return dataResponse.data().getBlockByHash();
            }
            return null;
        }

        @Override
        public Query getQuery() {
            return BlockByHashQuery.builder().hash("00000000000000000018438255d3b4c13eb1fe44024a9dfb0de83d598e5e42c6").build();
        }
    }

}
