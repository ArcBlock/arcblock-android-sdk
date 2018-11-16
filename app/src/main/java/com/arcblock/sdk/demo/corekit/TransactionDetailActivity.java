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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcblock.corekit.CoreKitQuery;
import com.arcblock.corekit.CoreKitResultListener;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.adapter.TsInputsAdapter;
import com.arcblock.sdk.demo.adapter.TsOutputsAdapter;
import com.arcblock.sdk.demo.btc.TransactionByHashQuery;
import com.arcblock.sdk.demo.utils.BtcValueUtils;

import java.util.ArrayList;
import java.util.List;

public class TransactionDetailActivity extends AppCompatActivity {

    public static final String TRANSACTION_HASH_KEY = "transaction_hash_key";
    private String transactionHash = "";

    private TextView block_hash_tv;
    private TextView block_height_tv;
    private TextView size_tv;
    private TextView virtual_size_tv;
    private TextView weight_tv;
    private TextView input_total_tv;
    private TextView output_total_tv;
    private TextView fees_tv;
    private TextView input_title_tv;
    private ListView input_lv;
    private TextView output_title_tv;
    private ListView output_lv;

    private boolean isInputLvShow = true;
    private boolean isOutputLvShow = true;

    private TsInputsAdapter mTsInputsAdapter;
    private TsOutputsAdapter mTsOutputsAdapter;
    private List<TransactionByHashQuery.Datum1> inputs = new ArrayList<>();
    private List<TransactionByHashQuery.Datum> outputs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        transactionHash = getIntent().getExtras().getString(TRANSACTION_HASH_KEY);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tx-" + transactionHash);

        initView();

        // init CorekitQuery and do query
        CoreKitQuery coreKitQuery = new CoreKitQuery(this, DemoApplication.getInstance().abCoreKitClientBtc());
        coreKitQuery.query(TransactionByHashQuery.builder().hash(transactionHash).build(), new CoreKitResultListener<TransactionByHashQuery.Data>() {
            @Override
            public void onSuccess(TransactionByHashQuery.Data data) {
                TransactionByHashQuery.TransactionByHash transactionByHash = data.getTransactionByHash();
                if (transactionByHash != null) {
                    block_hash_tv.setText(transactionByHash.getBlockHash());
                    block_height_tv.setText(transactionByHash.getBlockHeight() + "");
                    size_tv.setText(transactionByHash.getSize() + " Bytes");
                    virtual_size_tv.setText(transactionByHash.getVirtualSize() + " Bytes");
                    weight_tv.setText(transactionByHash.getWeight() + "");
                    input_total_tv.setText(BtcValueUtils.formatBtcValue(transactionByHash.getTotal()));
                    output_total_tv.setText(BtcValueUtils.formatBtcValue(transactionByHash.getTotal()));
                    fees_tv.setText(BtcValueUtils.formatBtcValue(transactionByHash.getFees()));

                    input_title_tv.setText(String.format("Input(%s)", transactionByHash.getNumberInputs() + ""));
                    output_title_tv.setText(String.format("Output(%s)", transactionByHash.getNumberOutputs() + ""));

                    if (transactionByHash.getInputs() != null && transactionByHash.getInputs().getData() != null) {
                        inputs.clear();
                        inputs.addAll(transactionByHash.getInputs().getData());
                        mTsInputsAdapter.notifyDataSetChanged();
                    }

                    if (transactionByHash.getOutputs() != null && transactionByHash.getOutputs().getData() != null) {
                        outputs.clear();
                        outputs.addAll(transactionByHash.getOutputs().getData());
                        mTsOutputsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(TransactionDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void initView() {
        block_hash_tv = findViewById(R.id.block_hash_tv);
        block_height_tv = findViewById(R.id.block_height_tv);
        size_tv = findViewById(R.id.size_tv);
        virtual_size_tv = findViewById(R.id.virtual_size_tv);
        weight_tv = findViewById(R.id.weight_tv);
        input_total_tv = findViewById(R.id.input_total_tv);
        output_total_tv = findViewById(R.id.output_total_tv);
        fees_tv = findViewById(R.id.fees_tv);
        input_title_tv = findViewById(R.id.input_title_tv);
        input_lv = findViewById(R.id.input_lv);
        output_title_tv = findViewById(R.id.output_title_tv);
        output_lv = findViewById(R.id.output_lv);

        mTsInputsAdapter = new TsInputsAdapter(this, R.layout.item_transaction_detail_accounts, inputs);
        input_lv.setAdapter(mTsInputsAdapter);
        input_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < inputs.size()) {
                    if (TextUtils.isEmpty(inputs.get(position).getAccount())) {
                        Toast.makeText(TransactionDetailActivity.this, "CoinBase", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(TransactionDetailActivity.this, AccountDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(AccountDetailActivity.ADDRESS_KEY, inputs.get(position).getAccount());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }
        });

        mTsOutputsAdapter = new TsOutputsAdapter(this, R.layout.item_transaction_detail_accounts, outputs);
        output_lv.setAdapter(mTsOutputsAdapter);
        output_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < outputs.size()) {
                    if (TextUtils.isEmpty(outputs.get(position).getAccount())) {
                        Toast.makeText(TransactionDetailActivity.this, "Account is empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(TransactionDetailActivity.this, AccountDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(AccountDetailActivity.ADDRESS_KEY, outputs.get(position).getAccount());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }
        });

        refreshLvState();

        input_title_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInputLvShow = !isInputLvShow;
                refreshLvState();
            }
        });

        output_title_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOutputLvShow = !isOutputLvShow;
                refreshLvState();
            }
        });

    }

    private void refreshLvState() {
        if (isInputLvShow) {
            input_lv.setVisibility(View.VISIBLE);
        } else {
            input_lv.setVisibility(View.GONE);
        }
        if (isOutputLvShow) {
            output_lv.setVisibility(View.VISIBLE);
        } else {
            output_lv.setVisibility(View.GONE);
        }
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
