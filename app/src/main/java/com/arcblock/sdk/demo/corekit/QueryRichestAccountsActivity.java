/*
 * Copyright (c) 2017-present ArcBlock Foundation Ltd <https://www.arcblock.io/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.arcblock.sdk.demo.corekit;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.apollographql.apollo.api.Response;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.viewmodel.CoreKitViewModel;
import com.arcblock.corekit.viewmodel.i.CoreKitBeanMapperInterface;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.adapter.RichestAccountsAdapter;
import com.arcblock.sdk.demo.btc.RichestAccountsQuery;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

public class QueryRichestAccountsActivity extends AppCompatActivity {

    private RichestAccountsAdapter mRichestAccountsAdapter;

    ViewGroup content;
    ProgressBar progressBar;
    List<RichestAccountsQuery.Datum> mAccounts = new ArrayList<>();

    private CoreKitViewModel<Response<RichestAccountsQuery.Data>, RichestAccountsQuery.RichestAccounts> mRichestAccountsQueryViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_richest_accounts);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.query_richest_account_data);

        content = (ViewGroup) findViewById(R.id.content_holder);
        progressBar = (ProgressBar) findViewById(R.id.loading_bar);

        RecyclerView feedRecyclerView = (RecyclerView) findViewById(R.id.rv_feed_list);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRichestAccountsAdapter = new RichestAccountsAdapter(R.layout.item_richest_account, mAccounts);
        feedRecyclerView.setAdapter(mRichestAccountsAdapter);
        mRichestAccountsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(QueryRichestAccountsActivity.this, AccountDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(AccountDetailActivity.ADDRESS_KEY, mAccounts.get(position).getAddress());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // init data mapper
        CoreKitBeanMapperInterface<Response<RichestAccountsQuery.Data>, RichestAccountsQuery.RichestAccounts> richestAccountsMapper = new CoreKitBeanMapperInterface<Response<RichestAccountsQuery.Data>, RichestAccountsQuery.RichestAccounts>() {

            @Override
            public RichestAccountsQuery.RichestAccounts map(Response<RichestAccountsQuery.Data> dataResponse) {
                if (dataResponse != null) {
                    return dataResponse.data().getRichestAccounts();
                }
                return null;
            }
        };
        // init a query
        RichestAccountsQuery query = RichestAccountsQuery.builder().build();
        // init the ViewModel with CustomClientFactory
        CoreKitViewModel.CustomClientFactory factory = new CoreKitViewModel.CustomClientFactory(query, richestAccountsMapper, DemoApplication.getInstance().abCoreKitClientBtc());
        mRichestAccountsQueryViewModel = CoreKitViewModel.getInstance(this, factory);
        mRichestAccountsQueryViewModel.getQueryData(query).observe(this, new Observer<CoreKitBean<RichestAccountsQuery.RichestAccounts>>() {
            @Override
            public void onChanged(@Nullable CoreKitBean<RichestAccountsQuery.RichestAccounts> coreKitBean) {
                progressBar.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                if (coreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
                    if (coreKitBean.getData() != null) {
                        mAccounts.clear();
                        mAccounts.addAll(coreKitBean.getData().getData());
                        mRichestAccountsAdapter.notifyDataSetChanged();
                    }
                } else {
                    // show error msg
                }
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
