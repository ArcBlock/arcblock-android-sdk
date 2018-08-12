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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.utils.CoreKitSubViewModelUtils;
import com.arcblock.corekit.viewmodel.CoreKitSubViewModel;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.adapter.NewEthBlockTxsAdapter;
import com.arcblock.sdk.demo.eth.NewBlockMinedSubscription;
import com.arcblock.sdk.demo.eth.NewContractCreatedSubscription;

import java.util.ArrayList;
import java.util.List;

public class EthNewBlockSubscriptionActivity extends AppCompatActivity {

	private TextView block_height_tv;
	private ListView transactions_lv;
	private CoreKitSubViewModel<NewBlockMinedSubscription.Data, NewBlockMinedSubscription> mDataCoreKitSubViewModel;
	private CoreKitSubViewModel<NewBlockMinedSubscription.Data, NewBlockMinedSubscription> mDataCoreKitSubViewModel2;
	private CoreKitSubViewModel<NewContractCreatedSubscription.Data, NewBlockMinedSubscription> mDataCoreKitSubViewModelForNewContract;
	private NewEthBlockTxsAdapter mNewEthBlockTxsAdapter;
	private List<NewBlockMinedSubscription.Datum> mDatumList = new ArrayList<>();

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
		transactions_lv = findViewById(R.id.transactions_lv);
		mNewEthBlockTxsAdapter = new NewEthBlockTxsAdapter(this, R.layout.item_eth_new_block_sub, mDatumList);
		transactions_lv.setAdapter(mNewEthBlockTxsAdapter);
		block_height_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initData3();
			}
		});
	}

	private void initData() {

		NewBlockMinedSubscription newBlockMinedSubscription = new NewBlockMinedSubscription();

		CoreKitSubViewModel.CustomClientFactory<NewBlockMinedSubscription.Data, NewBlockMinedSubscription> factory =
				new CoreKitSubViewModel.CustomClientFactory<>(DemoApplication.getInstance().abCoreKitClientEth(), newBlockMinedSubscription, NewBlockMinedSubscription.Data.class);

		mDataCoreKitSubViewModel = CoreKitSubViewModelUtils.getCoreKitSubViewModel(newBlockMinedSubscription, this, factory);
		mDataCoreKitSubViewModel.subscription(NewBlockMinedSubscription.QUERY_DOCUMENT).observe(this, new Observer<CoreKitBean<NewBlockMinedSubscription.Data>>() {
			@Override
			public void onChanged(@Nullable CoreKitBean<NewBlockMinedSubscription.Data> dataCoreKitBean) {
				if (dataCoreKitBean != null && dataCoreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
					block_height_tv.setText(dataCoreKitBean.getData().getNewBlockMined().getHeight() + "");
					if (dataCoreKitBean.getData().getNewBlockMined().getTransactions().getData() != null) {
						mDatumList.clear();
						mDatumList.addAll(dataCoreKitBean.getData().getNewBlockMined().getTransactions().getData());
						mNewEthBlockTxsAdapter.notifyDataSetChanged();
					}
				}
			}
		});
	}

	private void initData3() {

		NewBlockMinedSubscription newBlockMinedSubscription = new NewBlockMinedSubscription();

		CoreKitSubViewModel.CustomClientFactory<NewBlockMinedSubscription.Data, NewBlockMinedSubscription> factory =
				new CoreKitSubViewModel.CustomClientFactory<>(DemoApplication.getInstance().abCoreKitClientEth(), newBlockMinedSubscription, NewBlockMinedSubscription.Data.class);

		mDataCoreKitSubViewModel2 = CoreKitSubViewModelUtils.getCoreKitSubViewModel(newBlockMinedSubscription, this, factory);
		mDataCoreKitSubViewModel2.subscription(NewBlockMinedSubscription.QUERY_DOCUMENT).observe(this, new Observer<CoreKitBean<NewBlockMinedSubscription.Data>>() {
			@Override
			public void onChanged(@Nullable CoreKitBean<NewBlockMinedSubscription.Data> dataCoreKitBean) {
				if (dataCoreKitBean != null && dataCoreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {

				}
			}
		});


	}

	private void initData2() {

		NewContractCreatedSubscription newContractCreatedSubscription = new NewContractCreatedSubscription();

		CoreKitSubViewModel.CustomClientFactory<NewContractCreatedSubscription.Data, NewContractCreatedSubscription> factory2 =
				new CoreKitSubViewModel.CustomClientFactory<>(DemoApplication.getInstance().abCoreKitClientEth(), newContractCreatedSubscription, NewContractCreatedSubscription.Data.class);

		mDataCoreKitSubViewModelForNewContract = CoreKitSubViewModelUtils.getCoreKitSubViewModel(newContractCreatedSubscription, this, factory2);
		mDataCoreKitSubViewModelForNewContract.subscription(NewContractCreatedSubscription.QUERY_DOCUMENT).observe(this, new Observer<CoreKitBean<NewContractCreatedSubscription.Data>>() {
			@Override
			public void onChanged(@Nullable CoreKitBean<NewContractCreatedSubscription.Data> dataCoreKitBean) {
				if (dataCoreKitBean != null && dataCoreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {

				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mDataCoreKitSubViewModel.leaveChannel();
		mDataCoreKitSubViewModel2.leaveChannel();
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
