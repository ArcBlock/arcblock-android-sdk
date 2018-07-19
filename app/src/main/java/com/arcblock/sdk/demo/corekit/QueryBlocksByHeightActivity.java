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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.bean.CoreKitBeanMapper;
import com.arcblock.corekit.bean.CoreKitPagedBean;
import com.arcblock.corekit.viewmodel.CoreKitPagedViewModel;
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

	SwipeRefreshLayout content;
	ProgressBar progressBar;

	private List<BlocksByHeightQuery.Datum> mBlocks = new ArrayList<>();
	private CoreKitPagedViewModel<BlocksByHeightQuery.Data, BlocksByHeightQuery.BlocksByHeight, BlocksByHeightQuery.Datum> mBlocksByHeightQueryViewModel;
	private boolean haveMore = true;
	private String cursor = "";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_list_blocks);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.query_list_blocks_data);

		content = (SwipeRefreshLayout) findViewById(R.id.content_holder);
		progressBar = (ProgressBar) findViewById(R.id.loading_bar);

		content.setProgressBackgroundColorSchemeResource(android.R.color.white);
		content.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

		content.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mBlocks.clear();
				cursor = "";
				haveMore = true;
				mListBlocksAdapter.setEnableLoadMore(false);
				mListBlocksAdapter.notifyDataSetChanged();
				mBlocksByHeightQueryViewModel.refresh(getQuery());
			}
		});

		RecyclerView feedRecyclerView = (RecyclerView) findViewById(R.id.rv_feed_list);
		feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		mListBlocksAdapter = new ListBlocksAdapter(R.layout.item_list_blocks, mBlocks);
		mListBlocksAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
			@Override
			public void onLoadMoreRequested() {
				mBlocksByHeightQueryViewModel.loadMore(getQuery());
			}
		}, feedRecyclerView);
		mListBlocksAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
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

		// init data mapper
		CoreKitBeanMapper<Response<BlocksByHeightQuery.Data>, List<BlocksByHeightQuery.Datum>> blocksMapper = new CoreKitBeanMapper<Response<BlocksByHeightQuery.Data>, List<BlocksByHeightQuery.Datum>>() {

			@Override
			public List<BlocksByHeightQuery.Datum> map(Response<BlocksByHeightQuery.Data> dataResponse) {
				if (dataResponse != null && dataResponse.data().getBlocksByHeight() != null) {
					if (dataResponse.data().getBlocksByHeight().getPage() != null) {
						haveMore = dataResponse.data().getBlocksByHeight().getPage().isNext();
						if (haveMore) {
							cursor = dataResponse.data().getBlocksByHeight().getPage().getCursor();
						}
					}
					return dataResponse.data().getBlocksByHeight().getData();
				}
				return null;
			}
		};
		// init the ViewModel with CustomClientFactory
		CoreKitPagedViewModel.CustomClientFactory factory = new CoreKitPagedViewModel.CustomClientFactory(blocksMapper, DemoApplication.getInstance().abCoreKitClient());
		mBlocksByHeightQueryViewModel = ViewModelProviders.of(this, factory).get(CoreKitPagedViewModel.class);
		mBlocksByHeightQueryViewModel.getQueryData(getQuery()).observe(this, new Observer<CoreKitPagedBean<List<BlocksByHeightQuery.Datum>>>() {
			@Override
			public void onChanged(@Nullable CoreKitPagedBean<List<BlocksByHeightQuery.Datum>> coreKitPagedBean) {
				content.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				content.setRefreshing(false);

				if (coreKitPagedBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
					if (coreKitPagedBean.getData() != null) {
						mListBlocksAdapter.addData(coreKitPagedBean.getData());
//						List<BlocksByHeightQuery.Datum> oldList = mBlocks;
//						List<BlocksByHeightQuery.Datum> newList = coreKitPagedBean.getData();
//						DiffUtil.DiffResult result = DiffUtil.calculateDiff(new CoreKitDiffUtil<>(oldList, newList), true);
//						mListBlocksAdapter.setNewListData(newList);
//						result.dispatchUpdatesTo(mListBlocksAdapter);
					}
				} else {
					// todo show error msg.
				}

				if (haveMore) {
					mListBlocksAdapter.setEnableLoadMore(true);
					mListBlocksAdapter.loadMoreComplete();
				} else {
					mListBlocksAdapter.loadMoreEnd();
				}
			}
		});
	}

	private Query getQuery() {
		PageInput pageInput = null;
		if (!TextUtils.isEmpty(cursor)) {
			pageInput = PageInput.builder().cursor(cursor).build();
		}
		return BlocksByHeightQuery.builder().fromHeight(448244).toHeight(448254).paging(pageInput).build();
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
