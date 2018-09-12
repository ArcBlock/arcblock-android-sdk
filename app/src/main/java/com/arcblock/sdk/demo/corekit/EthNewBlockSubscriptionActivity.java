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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.CoreKitSubscription;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.socket.CoreKitSocketStatusCallBack;
import com.arcblock.corekit.viewmodel.CoreKitSubscriptionViewModel;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.adapter.NewEthBlockTxsAdapter;
import com.arcblock.sdk.demo.eth.NewBlockMinedSubscription;

import java.util.ArrayList;
import java.util.List;

public class EthNewBlockSubscriptionActivity extends AppCompatActivity {

    private TextView block_height_tv;
    private TextView connect_status_tv;
    private ListView transactions_lv;
    private NewEthBlockTxsAdapter mNewEthBlockTxsAdapter;
    private List<NewBlockMinedSubscription.Datum> mDatumList = new ArrayList<>();
    private EthNewBlockSubscription ethNewBlockSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eth_new_block_subscription);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Eth Block Subscription");

        initView();
        initData();
    }

    private void initView() {
        block_height_tv = findViewById(R.id.block_height_tv);
        connect_status_tv = findViewById(R.id.connect_status_tv);
        transactions_lv = findViewById(R.id.transactions_lv);
        mNewEthBlockTxsAdapter = new NewEthBlockTxsAdapter(this, R.layout.item_eth_new_block_sub, mDatumList);
        transactions_lv.setAdapter(mNewEthBlockTxsAdapter);
        block_height_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initData2();
            }
        });
        connect_status_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ethNewBlockSubscription.doManualReconnect();
            }
        });
    }

    private void initData() {
        // init subscription
        ethNewBlockSubscription = new EthNewBlockSubscription(this, DemoApplication.getInstance().abCoreKitClientEth());
        // add data callback
        ethNewBlockSubscription.setCoreKitSubCallBack(new CoreKitSubscriptionViewModel.CoreKitSubCallBack<NewBlockMinedSubscription.Data>() {
            @Override
            public void onNewData(CoreKitBean<NewBlockMinedSubscription.Data> coreKitBean) {
                if (coreKitBean != null && coreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
                    block_height_tv.setText(coreKitBean.getData().getNewBlockMined().getHeight() + "");
                    if (coreKitBean.getData().getNewBlockMined().getTransactions().getData() != null) {
                        mDatumList.clear();
                        mDatumList.addAll(coreKitBean.getData().getNewBlockMined().getTransactions().getData());
                        mNewEthBlockTxsAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        // add status callback
        ethNewBlockSubscription.setCoreKitSocketStatusCallBack(new CoreKitSocketStatusCallBack() {
            @Override
            public void onOpen() {
                connect_status_tv.setVisibility(View.GONE);
            }

            @Override
            public void onClose() {
                connect_status_tv.setVisibility(View.VISIBLE);
                connect_status_tv.setText("connect close");
            }

            @Override
            public void onError() {
                connect_status_tv.setVisibility(View.VISIBLE);
                connect_status_tv.setText("connect error");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://返回键的id
                this.onBackPressed();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class EthNewBlockSubscription extends CoreKitSubscription<NewBlockMinedSubscription.Data, NewBlockMinedSubscription> {

        public EthNewBlockSubscription(FragmentActivity activity, ABCoreKitClient client) {
            super(activity, client);
        }

        @Override
        public NewBlockMinedSubscription getSubscription() {
            return new NewBlockMinedSubscription();
        }

        @Override
        public Class<NewBlockMinedSubscription.Data> getSubscriptionClass() {
            return NewBlockMinedSubscription.Data.class;
        }
    }
}
