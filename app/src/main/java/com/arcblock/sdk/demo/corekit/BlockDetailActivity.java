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
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloCallback;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import com.arcblock.sdk.demo.BlockByHashQuery;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.adapter.BlockDetailTransactionsAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockDetailActivity extends AppCompatActivity {

	private static final String TAG = BlockDetailActivity.class.getSimpleName();
	public static final String BLOCK_HASH_KEY = "block_hash_key";
	private String blockHash = "";
	private ApolloCall<BlockByHashQuery.Data> blockByHashCall;
	private Handler uiHandler = new Handler(Looper.getMainLooper());

	private TextView block_height_tv;
	private TextView size_tv;
	private TextView striped_size_tv;
	private TextView weight_tv;
	private TextView version_tv;
	private TextView bits_tv;
	private TextView nonce_tv;
	private TextView time_tv;
	private TextView pre_hash_tv;
	private ListView transactions_lv;

	private BlockDetailTransactionsAdapter mBlockDetailTransactionsAdapter;
	private List<BlockByHashQuery.Datum> mDatumList = new ArrayList<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_block_detail);

		blockHash = getIntent().getExtras().getString(BLOCK_HASH_KEY);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Block-" + blockHash);

		initView();

		fetchBlockByHash();
	}

	private void initView() {
		block_height_tv = findViewById(R.id.block_height_tv);
		size_tv = findViewById(R.id.size_tv);
		striped_size_tv = findViewById(R.id.striped_size_tv);
		weight_tv = findViewById(R.id.weight_tv);
		version_tv = findViewById(R.id.version_tv);
		bits_tv = findViewById(R.id.bits_tv);
		nonce_tv = findViewById(R.id.nonce_tv);
		time_tv = findViewById(R.id.time_tv);
		pre_hash_tv = findViewById(R.id.pre_hash_tv);
		transactions_lv = findViewById(R.id.transactions_lv);

		mBlockDetailTransactionsAdapter = new BlockDetailTransactionsAdapter(this, R.layout.item_block_detail_transactions, mDatumList);
		transactions_lv.setAdapter(mBlockDetailTransactionsAdapter);

		pre_hash_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(pre_hash_tv.getText().toString())) {
					Intent intent = new Intent(BlockDetailActivity.this, BlockDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString(BLOCK_HASH_KEY, pre_hash_tv.getText().toString());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
	}

	private ApolloCall.Callback<BlockByHashQuery.Data> dataCallback
			= new ApolloCallback<>(new ApolloCall.Callback<BlockByHashQuery.Data>() {
		@Override
		public void onResponse(@NotNull Response<BlockByHashQuery.Data> response) {
			if (response != null && response.data() != null && response.data().getBlockByHash() != null) {
				BlockByHashQuery.BlockByHash blockByHash = response.data().getBlockByHash();
				block_height_tv.setText(blockByHash.getHeight() + "");
				size_tv.setText(blockByHash.getSize() + "Bytes");
				striped_size_tv.setText(blockByHash.getStrippedSize() + "Bytes");
				weight_tv.setText(blockByHash.getWeight() + "");
				version_tv.setText(blockByHash.getVersion() + "");
				bits_tv.setText(blockByHash.getBits() + "");
				nonce_tv.setText(blockByHash.getNonce() + "");
				time_tv.setText(blockByHash.getTime() + "");
				pre_hash_tv.setText(blockByHash.getPreHash() + "");
				if (blockByHash.getTransactions().getData() != null) {
					mDatumList.clear();
					mDatumList.addAll(blockByHash.getTransactions().getData());
					mBlockDetailTransactionsAdapter.notifyDataSetChanged();
				}
			}
		}

		@Override
		public void onFailure(@NotNull ApolloException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}, uiHandler);

	private void fetchBlockByHash() {
		BlockByHashQuery blockByHashQuery = BlockByHashQuery.builder()
				.hash(blockHash)
				.build();
		blockByHashCall = DemoApplication.getInstance().abCoreKitClient()
				.query(blockByHashQuery)
				.responseFetcher(ApolloResponseFetchers.NETWORK_FIRST);
		blockByHashCall.enqueue(dataCallback);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (blockByHashCall != null) {
			blockByHashCall.cancel();
		}
	}

}
