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
import com.arcblock.sdk.demo.eth.BlocksByHeightQuery;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class EthListBlocksAdapter extends BaseQuickAdapter<BlocksByHeightQuery.Datum, BaseViewHolder> {
	public EthListBlocksAdapter(int layoutResId, @Nullable List<BlocksByHeightQuery.Datum> data) {
		super(layoutResId, data);
	}

	public void setNewListData(List<BlocksByHeightQuery.Datum> newList){
		this.mData = newList;
	}

	@Override
	protected void convert(BaseViewHolder helper, BlocksByHeightQuery.Datum item) {
		helper.setText(R.id.hash_tv, item.getHash());
		helper.setText(R.id.height_tv, "" + item.getHeight());
	}
}
