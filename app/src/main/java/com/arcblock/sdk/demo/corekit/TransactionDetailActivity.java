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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.apollographql.apollo.api.Response;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.utils.CoreKitBeanMapper;
import com.arcblock.corekit.viewmodel.CoreKitViewModel;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.adapter.TsInputsAdapter;
import com.arcblock.sdk.demo.adapter.TsOutputsAdapter;
import com.arcblock.sdk.demo.btc.TransactionByHashQuery;

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

	private CoreKitViewModel<Response<TransactionByHashQuery.Data>, TransactionByHashQuery.TransactionByHash> mTransactionByHashQueryViewModel;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction_detail);

		transactionHash = getIntent().getExtras().getString(TRANSACTION_HASH_KEY);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Tx-" + transactionHash);

		initView();

		// init data mapper
		CoreKitBeanMapper<Response<TransactionByHashQuery.Data>, TransactionByHashQuery.TransactionByHash> transactionMapper = new CoreKitBeanMapper<Response<TransactionByHashQuery.Data>, TransactionByHashQuery.TransactionByHash>() {

			@Override
			public TransactionByHashQuery.TransactionByHash map(Response<TransactionByHashQuery.Data> dataResponse) {
				if (dataResponse != null) {
					return dataResponse.data().getTransactionByHash();
				}
				return null;
			}
		};
		// init a query
		TransactionByHashQuery query = TransactionByHashQuery.builder().hash(transactionHash).build();
		// init the ViewModel with CustomClientFactory
		CoreKitViewModel.CustomClientFactory factory = new CoreKitViewModel.CustomClientFactory(query, transactionMapper, DemoApplication.getInstance().abCoreKitClientBtc());
		mTransactionByHashQueryViewModel = CoreKitViewModel.getInstance(this, factory);
		mTransactionByHashQueryViewModel.getQueryData(query).observe(this, new Observer<CoreKitBean<TransactionByHashQuery.TransactionByHash>>() {
			@Override
			public void onChanged(@Nullable CoreKitBean<TransactionByHashQuery.TransactionByHash> coreKitBean) {
				if (coreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
					TransactionByHashQuery.TransactionByHash transactionByHash = coreKitBean.getData();
					if (transactionByHash != null) {
						block_hash_tv.setText(transactionByHash.getBlockHash());
						block_height_tv.setText(transactionByHash.getBlockHeight() + "");
						size_tv.setText(transactionByHash.getSize() + " Bytes");
						virtual_size_tv.setText(transactionByHash.getVirtualSize() + " Bytes");
						weight_tv.setText(transactionByHash.getWeight() + "");
						input_total_tv.setText(transactionByHash.getTotal() + " BTC");
						output_total_tv.setText(transactionByHash.getTotal() + " BTC");
						fees_tv.setText(transactionByHash.getFees() + " BTC");

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
				} else {
					// show error msg
				}
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
					Intent intent = new Intent(TransactionDetailActivity.this, AccountDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString(AccountDetailActivity.ADDRESS_KEY, inputs.get(position).getAccount());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

		mTsOutputsAdapter = new TsOutputsAdapter(this, R.layout.item_transaction_detail_accounts, outputs);
		output_lv.setAdapter(mTsOutputsAdapter);
		output_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position < outputs.size()) {
					Intent intent = new Intent(TransactionDetailActivity.this, AccountDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString(AccountDetailActivity.ADDRESS_KEY, outputs.get(position).getAccount());
					intent.putExtras(bundle);
					startActivity(intent);
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
