package com.arcblock.sdk.demo.corekit;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.apollographql.apollo.api.Response;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.config.CoreKitConfig;
import com.arcblock.corekit.viewmodel.CoreKitViewModel;
import com.arcblock.corekit.viewmodel.i.CoreKitBeanMapperInterface;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.btc.AccountByAddressQuery;
import com.arcblock.sdk.demo.btc.BlockByHashQuery;
import com.google.gson.Gson;
import com.yuyh.jsonviewer.library.JsonRecyclerView;

public class MultiQueryInOnePageActivity extends AppCompatActivity {

    private JsonRecyclerView json_view_account;
    private JsonRecyclerView json_view_block;

    private CoreKitBeanMapperInterface<Response<AccountByAddressQuery.Data>, AccountByAddressQuery.AccountByAddress> accountMapper;
    private CoreKitBeanMapperInterface<Response<BlockByHashQuery.Data>, BlockByHashQuery.BlockByHash> blockMapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_query_in_one_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.multi_query_in_one_page);

        initView();
        initData();
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

    private void initData() {
        // init data mapper
        accountMapper = new CoreKitBeanMapperInterface<Response<AccountByAddressQuery.Data>, AccountByAddressQuery.AccountByAddress>() {

            @Override
            public AccountByAddressQuery.AccountByAddress map(Response<AccountByAddressQuery.Data> dataResponse) {
                if (dataResponse != null) {
                    return dataResponse.data().getAccountByAddress();
                }
                return null;
            }
        };
        blockMapper = new CoreKitBeanMapperInterface<Response<BlockByHashQuery.Data>, BlockByHashQuery.BlockByHash>() {

            @Override
            public BlockByHashQuery.BlockByHash map(Response<BlockByHashQuery.Data> dataResponse) {
                if (dataResponse != null) {
                    return dataResponse.data().getBlockByHash();
                }
                return null;
            }
        };
    }

    private void queryAccountDetail() {
        AccountByAddressQuery query = AccountByAddressQuery.builder().address("1M5MZ9hM19MetHBtB7a7DzULbf9hDuiX9r").build();

        CoreKitViewModel.DefaultFactory factoryAccount = new CoreKitViewModel.DefaultFactory(query, accountMapper, DemoApplication.getInstance(), CoreKitConfig.API_TYPE_BTC);
        CoreKitViewModel<Response<AccountByAddressQuery.Data>, AccountByAddressQuery.AccountByAddress> mAccountByAddressViewModel = CoreKitViewModel.getInstance(this, factoryAccount);

        mAccountByAddressViewModel.getQueryData(query).observe(this, new Observer<CoreKitBean<AccountByAddressQuery.AccountByAddress>>() {
            @Override
            public void onChanged(@Nullable CoreKitBean<AccountByAddressQuery.AccountByAddress> coreKitBean) {
                json_view_account.bindJson(new Gson().toJson(coreKitBean));
            }
        });
    }

    private void queryBlockDetail() {
        BlockByHashQuery query = BlockByHashQuery.builder().hash("00000000000000000018438255d3b4c13eb1fe44024a9dfb0de83d598e5e42c6").build();

        CoreKitViewModel.CustomClientFactory factoryBlock = new CoreKitViewModel.CustomClientFactory(query, blockMapper, DemoApplication.getInstance().abCoreKitClientBtc());
        CoreKitViewModel<Response<BlockByHashQuery.Data>, BlockByHashQuery.BlockByHash> mBlockByHashQueryViewModel = CoreKitViewModel.getInstance(this, factoryBlock);

        mBlockByHashQueryViewModel.getQueryData(query).observe(this, new Observer<CoreKitBean<BlockByHashQuery.BlockByHash>>() {
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
}
