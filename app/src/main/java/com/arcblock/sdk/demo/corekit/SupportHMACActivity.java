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

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.arcblock.corekit.CoreKitQuery;
import com.arcblock.corekit.CoreKitResultListener;
import com.arcblock.corekit.utils.CoreKitLogUtils;
import com.arcblock.sdk.demo.DemoApplication;
import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.btc.BlocksByHeightQuery;
import com.arcblock.sdk.demo.btc.type.PageInput;
import com.blankj.utilcode.util.MetaDataUtils;

public class SupportHMACActivity extends AppCompatActivity {

    private TextView result_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_hmac);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Support HMAC");

        TextView access_key_tv = findViewById(R.id.access_key_tv);
        TextView access_secret_tv = findViewById(R.id.access_secret_tv);
        result_tv = findViewById(R.id.result_tv);

        access_key_tv.setText(MetaDataUtils.getMetaDataInApp("ArcBlock_Access_Key"));
        access_secret_tv.setText(MetaDataUtils.getMetaDataInApp("ArcBlock_Access_Secret"));


        findViewById(R.id.try_hmac_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testQueryWithHMAC();
            }
        });
    }

    private void testQueryWithHMAC() {
        CoreKitQuery coreKitQuery = new CoreKitQuery(this, DemoApplication.getInstance().abCoreKitClientBtcWithHMAC());
        coreKitQuery.query(BlocksByHeightQuery.builder().paging(PageInput.builder().size(100).cursor(null).build()).fromHeight(9889).toHeight(20000).build(), new CoreKitResultListener<BlocksByHeightQuery.Data>() {
            @Override
            public void onSuccess(BlocksByHeightQuery.Data data) {
                CoreKitLogUtils.e("data size=>"+data.getBlocksByHeight().getData().size());
                // if the size == 100 , mean the hmac worked.
                if (data.getBlocksByHeight().getData().size() == 100) {
                    result_tv.setText("Success!!! HMAC Worked~");
                    result_tv.setTextColor(Color.parseColor("#00F926"));
                } else {
                    result_tv.setText("Oh NO!!! HMAC didn't work~");
                    result_tv.setTextColor(Color.parseColor("#FF0000"));
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
