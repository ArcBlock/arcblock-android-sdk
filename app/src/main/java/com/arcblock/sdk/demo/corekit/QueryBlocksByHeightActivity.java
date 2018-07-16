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
import android.arch.lifecycle.ViewModelProviders;
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

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.viewmodel.CoreKitViewModel;
import com.arcblock.sdk.demo.BlocksByHeightQuery;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.adapter.ListBlocksAdapter;
import com.arcblock.sdk.demo.type.PageInput;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

public class QueryBlocksByHeightActivity extends AppCompatActivity {

	private ListBlocksAdapter mListBlocksAdapter;

	ViewGroup content;
	ProgressBar progressBar;
	ApolloCall<BlocksByHeightQuery.Data> listBlocksCall;
	List<BlocksByHeightQuery.Datum> mBlocks = new ArrayList<>();

	private PageInput mPageInput;
	private BlocksByHeightQuery.Page mPage;

	private CoreKitViewModel<Response<BlocksByHeightQuery.Data>> mBlocksByHeightQueryViewModel;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_list_blocks);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.query_list_blocks_data);

		content = (ViewGroup) findViewById(R.id.content_holder);
		progressBar = (ProgressBar) findViewById(R.id.loading_bar);

		RecyclerView feedRecyclerView = (RecyclerView) findViewById(R.id.rv_feed_list);
		feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mListBlocksAdapter = new ListBlocksAdapter(R.layout.item_list_blocks, mBlocks);
		mListBlocksAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
			@Override
			public void onLoadMoreRequested() {
				if (mPage != null) {
					if (!mPage.isNext()) {
						mListBlocksAdapter.loadMoreEnd();
						return;
					}
				}
				mBlocksByHeightQueryViewModel.setQuery(getQuery());
			}
		}, feedRecyclerView);

		mListBlocksAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
				Intent intent = new Intent(QueryBlocksByHeightActivity.this, BlockDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(BlockDetailActivity.BLOCK_HASH_KEY, mBlocks.get(position).getHash());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		feedRecyclerView.setAdapter(mListBlocksAdapter);
		// init the ViewModel with CustomClientFactory
		CoreKitViewModel.CustomClientFactory factory = new CoreKitViewModel.CustomClientFactory(DemoApplication.getInstance().abCoreKitClient());
		mBlocksByHeightQueryViewModel = ViewModelProviders.of(this, factory).get(CoreKitViewModel.class);
		mBlocksByHeightQueryViewModel.getQueryData(getQuery()).observe(this, new Observer<CoreKitBean<Response<BlocksByHeightQuery.Data>>>() {
			@Override
			public void onChanged(@Nullable CoreKitBean<Response<BlocksByHeightQuery.Data>> coreKitBean) {
				progressBar.setVisibility(View.GONE);
				content.setVisibility(View.VISIBLE);
				mListBlocksAdapter.loadMoreComplete();
				if (coreKitBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
					Response<BlocksByHeightQuery.Data> response = coreKitBean.getData();
					if (response.data() != null && response.data().getBlocksByHeight() != null
							&& response.data().getBlocksByHeight().getData() != null) {
						mListBlocksAdapter.addData(response.data().getBlocksByHeight().getData());
						mPage = response.data().getBlocksByHeight().getPage();
					}
				} else {
					// todo show error msg
				}
			}
		});
	}

	private BlocksByHeightQuery getQuery(){
		if(mPage!=null){
			mPageInput = PageInput.builder().cursor(mPage.getCursor()).build();
		}
		return BlocksByHeightQuery.builder()
				.fromHeight(482244)
				.toHeight(482264)
				.paging(mPageInput)
				.build();
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
		if (listBlocksCall != null) {
			listBlocksCall.cancel();
		}
	}
}
