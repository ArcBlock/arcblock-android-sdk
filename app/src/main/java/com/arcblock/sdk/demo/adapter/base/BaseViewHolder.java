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
package com.arcblock.sdk.demo.adapter.base;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BaseViewHolder {

    private SparseArray<View> mViews;

    private int mPosition;

    private View mConvertView;

    private BaseViewHolder(Context context, ViewGroup parent, int layoutId,
                           int position) {
        this.mViews = new SparseArray<View>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId,
                parent, false);
        this.mPosition = position;
        mConvertView.setTag(this);
    }

    public static BaseViewHolder get(Context context, ViewGroup parent,
                                     int layoutId, int position, View convertView) {
        if (convertView == null) {
            return new BaseViewHolder(context, parent, layoutId, position);
        } else {
            BaseViewHolder holder = (BaseViewHolder) convertView.getTag();
            // 复用视图，但是position要更新
            holder.mPosition = position;
            return holder;
        }
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public int getPosition() {
        return mPosition;
    }

    public BaseViewHolder setTextView(int viewId, String content) {
        TextView tv = getView(viewId);
        tv.setText(content);
        return this;
    }

    public BaseViewHolder setTextView(int viewId, Spanned content) {
        TextView tv = getView(viewId);
        tv.setText(content);
        return this;
    }

    public BaseViewHolder setTextView(int viewId, SpannableString content) {
        TextView tv = getView(viewId);
        tv.setText(content);
        return this;
    }

    public BaseViewHolder setTextView(int viewId, int content) {
        TextView tv = getView(viewId);
        tv.setText(content);
        return this;
    }

}
