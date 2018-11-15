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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.arcblock.corekit.CoreKitQuery;
import com.arcblock.corekit.CoreKitResultListener;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.adapter.TestFunctionInputAdapter;
import com.arcblock.sdk.demo.eth.BlockByHeightQuery;

import java.util.ArrayList;
import java.util.List;

/**
*  Created by Nate on 2018/11/15
**/
public class TestFunctionInputActivity  extends AppCompatActivity {

    private TestFunctionInputAdapter mTestFunctionInputAdapter;
    private List<BlockByHeightQuery.Datum> mDatumList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_function_input);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Test FunctionInput");

        final RecyclerView tracesRcv = (RecyclerView) findViewById(R.id.traces_rcv);
        tracesRcv.setLayoutManager(new LinearLayoutManager(this));

        mTestFunctionInputAdapter = new TestFunctionInputAdapter(R.layout.item_list_test_function_input, mDatumList);
        tracesRcv.setAdapter(mTestFunctionInputAdapter);


        CoreKitQuery coreKitQuery = new CoreKitQuery(this, DemoApplication.getInstance().abCoreKitClientEth());
        coreKitQuery.query(BlockByHeightQuery.builder().height(6000000).build(), new CoreKitResultListener<BlockByHeightQuery.Data>() {
            @Override
            public void onSuccess(BlockByHeightQuery.Data data) {
                if (data.getBlockByHeight()!=null&&data.getBlockByHeight().getTransactions()!=null&&
                        data.getBlockByHeight().getTransactions().getData()!=null) {
                    mDatumList.clear();
                    mDatumList.addAll(data.getBlockByHeight().getTransactions().getData());
                    mTestFunctionInputAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
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
