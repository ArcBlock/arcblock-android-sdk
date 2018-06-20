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

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.arcblock.corekit.bean.ArcBlockBean;
import com.arcblock.corekit.data.DataRepositoryFactory;
import com.arcblock.corekit.data.db.DatabaseManager;
import com.arcblock.corekit.data.net.NetDataUtil;
import com.arcblock.corekit.viewmodel.ArcBlockViewModel;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryDataActivity extends AppCompatActivity {

    private ArcBlockViewModel mArcBlockViewModel;
    private TextView resultTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_data);

        resultTv = findViewById(R.id.query_data_result_tv);

        findViewById(R.id.add_new_data_btn).setOnClickListener(v -> {
            List<ArcBlockBean> temp = new ArrayList<>();
            temp.add(new ArcBlockBean("4","test add",new Date()));
            DatabaseManager.getInstance().insertItems(temp);
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.query_data);
        ArcBlockViewModel.Factory factory = new ArcBlockViewModel.Factory(DemoApplication.getInstance(), DataRepositoryFactory.getDataRepository(DemoApplication.getInstance()));
        mArcBlockViewModel = ViewModelProviders.of(this, factory).get(ArcBlockViewModel.class);

        // do subscribe
        mArcBlockViewModel.getArcBlockBeans().observe(this, arcBlockBeans -> {
            if (arcBlockBeans != null && !arcBlockBeans.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (ArcBlockBean bean : arcBlockBeans) {
                    stringBuilder.append(bean.toString()).append("\n");
                }
                resultTv.setText(stringBuilder.toString());
            }
        });

        NetDataUtil.getInstance().getAllArcBlockBeans();
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
