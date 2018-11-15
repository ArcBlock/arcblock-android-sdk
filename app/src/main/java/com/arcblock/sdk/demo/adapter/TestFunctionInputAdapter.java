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
package com.arcblock.sdk.demo.adapter;

import android.support.annotation.Nullable;

import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.eth.BlockByHeightQuery;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
*  Created by Nate on 2018/11/15
**/
public class TestFunctionInputAdapter extends BaseQuickAdapter<BlockByHeightQuery.Datum, BaseViewHolder> {
    public TestFunctionInputAdapter(int layoutResId, @Nullable List<BlockByHeightQuery.Datum> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BlockByHeightQuery.Datum item) {
        if (item.getTraces()!=null&&item.getTraces().getData()!=null&&item.getTraces().getData().get(0)!=null) {
            if (item.getTraces().getData().get(0).getActionFunctionInput()!=null) {
                helper.setText(R.id.item_tv,item.getTraces().getData().get(0).getActionFunctionInput().get(0));
            } else {
                helper.setText(R.id.item_tv,"No input.");
            }
        }else{
            helper.setText(R.id.item_tv,"No input.");
        }
    }
}
