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
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.arcblock.sdk.demo.R;

public class QueryTransactionsActivity extends AppCompatActivity {

	private static final String TAG = QueryTransactionsActivity.class.getSimpleName();
	//private TransactionsAdapter mTransactionsAdapter;

	ViewGroup content;
	ProgressBar progressBar;
	Handler uiHandler = new Handler(Looper.getMainLooper());
	//ApolloCall<TransactionsQuery.Data> transactionsCall;
	//List<TransactionsQuery.Transaction> mTransactions = new ArrayList<>();

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
