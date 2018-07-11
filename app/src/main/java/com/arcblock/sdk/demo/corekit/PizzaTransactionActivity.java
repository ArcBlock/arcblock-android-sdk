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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloCallback;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.PizzaTransactionQuery;
import com.arcblock.sdk.demo.R;

import org.jetbrains.annotations.NotNull;

public class PizzaTransactionActivity extends AppCompatActivity {

	private static final String TAG = PizzaTransactionActivity.class.getSimpleName();

	Handler uiHandler = new Handler(Looper.getMainLooper());
	ApolloCall<PizzaTransactionQuery.Data> pizzaTransactionCall;

	TextView loading_tv;
	TextView txhash_tv;
	TextView from_tv;
	TextView to_tv;
	TextView value_tv;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_pizza_transaction);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.query_pizza_transaction);

		loading_tv = findViewById(R.id.loading_tv);
		txhash_tv = findViewById(R.id.txhash_tv);
		from_tv = findViewById(R.id.from_tv);
		to_tv = findViewById(R.id.to_tv);
		value_tv = findViewById(R.id.value_tv);

		loading_tv.setVisibility(View.VISIBLE);
		fetchPizzaTransaction();
	}

	private ApolloCall.Callback<PizzaTransactionQuery.Data> dataCallback
			= new ApolloCallback<>(new ApolloCall.Callback<PizzaTransactionQuery.Data>() {
		@Override
		public void onResponse(@NotNull Response<PizzaTransactionQuery.Data> response) {
			loading_tv.setVisibility(View.GONE);
			if (response.data() != null && response.data().getTransactionsByAddress() != null
					&& response.data().getTransactionsByAddress().getData() != null) {
				PizzaTransactionQuery.Datum datum = response.data().getTransactionsByAddress().getData().get(0);
				txhash_tv.setText(datum.getHash());
				// gen senders string
				StringBuilder senders = new StringBuilder();
				if (datum.getInputs() != null && datum.getInputs().getData() != null) {
					for (int i = 0; i < datum.getInputs().getData().size(); i++) {
						if (i == 0) {
							senders.append(datum.getInputs().getData().get(i).getAccount());
						} else {
							senders.append("\n").append(datum.getInputs().getData().get(i).getAccount());
						}
					}
				}
				from_tv.setText(senders.toString());
				// gen receivers string
				StringBuilder receivers = new StringBuilder();
				if (datum.getOutputs() != null && datum.getOutputs().getData() != null) {
					for (int i = 0; i < datum.getOutputs().getData().size(); i++) {
						if (i == 0) {
							receivers.append(datum.getOutputs().getData().get(i).getAccount());
						} else {
							receivers.append("\n").append(datum.getOutputs().getData().get(i).getAccount());
						}
					}
				}
				to_tv.setText(receivers.toString());

				value_tv.setText(datum.getTotal() + "");
			}
		}

		@Override
		public void onFailure(@NotNull ApolloException e) {
			Log.e(TAG, e.getMessage(), e);
			loading_tv.setVisibility(View.GONE);
		}
	}, uiHandler);

	private void fetchPizzaTransaction() {
		PizzaTransactionQuery pizzaTransactionQuery = PizzaTransactionQuery.builder()
				.sender("17SkEw2md5avVNyYgj6RiXuQKNwkXaxFyQ")
				.receiver("13TETb2WMr58mexBaNq1jmXV1J7Abk2tE2")
				.build();
		pizzaTransactionCall = DemoApplication.getInstance().abCoreKitClient()
				.query(pizzaTransactionQuery)
				.responseFetcher(ApolloResponseFetchers.NETWORK_FIRST);
		pizzaTransactionCall.enqueue(dataCallback);
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
		if (pizzaTransactionCall != null) {
			pizzaTransactionCall.cancel();
		}
	}
}
