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
package com.arcblock.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.utils.CoreKitSubViewModelUtils;
import com.arcblock.corekit.viewmodel.CoreKitSubViewModel;
import com.arcblock.sdk.demo.corekit.EthNewBlockSubscriptionActivity;
import com.arcblock.sdk.demo.corekit.QueryBlocksByHeightActivity;
import com.arcblock.sdk.demo.corekit.QueryBlocksByHeightForEthActivity;
import com.arcblock.sdk.demo.corekit.QueryRichestAccountsActivity;
import com.arcblock.sdk.demo.corekit.TransactionDetailActivity;
import com.arcblock.sdk.demo.eth.NewBlockMinedSubscription;

public class CoreKitFragment extends Fragment {

	public static CoreKitFragment newInstance() {
		CoreKitFragment fragment = new CoreKitFragment();
		return fragment;
	}

	private CoreKitSubViewModel<NewBlockMinedSubscription.Data, NewBlockMinedSubscription> mDataCoreKitSubViewModel;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_corekit, null);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		view.findViewById(R.id.query_list_blocks_data_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), QueryBlocksByHeightActivity.class);
				startActivity(intent);
			}
		});

		view.findViewById(R.id.query_blocks_by_height_eth_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), QueryBlocksByHeightForEthActivity.class);
				startActivity(intent);
			}
		});

		view.findViewById(R.id.query_richest_accounts_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), QueryRichestAccountsActivity.class);
				startActivity(intent);
			}
		});

		view.findViewById(R.id.query_pizza_transaction_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), TransactionDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(TransactionDetailActivity.TRANSACTION_HASH_KEY, "cca7507897abc89628f450e8b1e0c6fca4ec3f7b34cccf55f3f531c659ff4d79");
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		view.findViewById(R.id.eth_new_block_subscription_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), EthNewBlockSubscriptionActivity.class);
				startActivity(intent);
			}
		});
		view.findViewById(R.id.eth_new_block_subscription_in_main_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initSub();
			}
		});
	}

	private void initSub() {
		NewBlockMinedSubscription newBlockMinedSubscription = new NewBlockMinedSubscription();

		CoreKitSubViewModel.CustomClientFactory<NewBlockMinedSubscription.Data, NewBlockMinedSubscription> factory =
				new CoreKitSubViewModel.CustomClientFactory<>(DemoApplication.getInstance().abCoreKitClientEth(), newBlockMinedSubscription, NewBlockMinedSubscription.Data.class);

		mDataCoreKitSubViewModel = CoreKitSubViewModelUtils.getCoreKitSubViewModel(newBlockMinedSubscription, this, factory);
		mDataCoreKitSubViewModel
				.subscription(NewBlockMinedSubscription.QUERY_DOCUMENT)
				.setCoreKitSubCallBack(new CoreKitSubViewModel.CoreKitSubCallBack<NewBlockMinedSubscription.Data>() {
					@Override
					public void onNewData(CoreKitBean<NewBlockMinedSubscription.Data> coreKitBean) {
						if (coreKitBean != null && coreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
							Toast.makeText(getActivity(), "New Block Height:" + coreKitBean.getData().getNewBlockMined().getHeight(), Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

}
