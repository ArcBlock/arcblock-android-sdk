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

import android.content.Context;
import android.text.TextUtils;

import com.arcblock.sdk.demo.R;
import com.arcblock.sdk.demo.adapter.base.BaseViewHolder;
import com.arcblock.sdk.demo.adapter.base.CustomBaseAdapter;
import com.arcblock.sdk.demo.btc.AccountByAddressQuery;

import java.util.List;

public class TsSentAdapter extends CustomBaseAdapter<AccountByAddressQuery.Datum> {
    public TsSentAdapter(Context context, int resource, List<AccountByAddressQuery.Datum> list) {
        super(context, resource, list);
    }

    @Override
    public void setConvert(BaseViewHolder viewHolder, AccountByAddressQuery.Datum datum1) {
        viewHolder.setTextView(R.id.item_tv, TextUtils.isEmpty(datum1.getHash()) ? "txsHash is empty!" : datum1.getHash());
    }
}
