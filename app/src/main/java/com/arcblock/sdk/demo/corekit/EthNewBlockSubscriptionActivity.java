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
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcblock.corekit.CoreKitSubscription;
import com.arcblock.corekit.CoreKitSubscriptionResultListener;
import com.arcblock.corekit.socket.CoreKitSocketStatusCallBack;
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
    private CoreKitSubscription<NewBlockMinedSubscription.Data, NewBlockMinedSubscription> mCoreKitSubscription;

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
                mCoreKitSubscription.doManualReconnect();
            }
        });
    }

    private void initData() {
        // init corekit subscription
        mCoreKitSubscription = new CoreKitSubscription<>(this, DemoApplication.getInstance().abCoreKitClientEth(), new NewBlockMinedSubscription(), NewBlockMinedSubscription.Data.class);
        mCoreKitSubscription.setResultListener(new CoreKitSubscriptionResultListener<NewBlockMinedSubscription.Data>() {
            @Override
            public void onSuccess(NewBlockMinedSubscription.Data data) {
                block_height_tv.setText(data.getNewBlockMined().getHeight() + "");
                if (data.getNewBlockMined().getTransactions().getData() != null) {
                    mDatumList.clear();
                    mDatumList.addAll(data.getNewBlockMined().getTransactions().getData());
                    mNewEthBlockTxsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(EthNewBlockSubscriptionActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        // add status callback
        mCoreKitSubscription.setCoreKitSocketStatusCallBack(new CoreKitSocketStatusCallBack() {
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
}
