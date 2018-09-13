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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public abstract class CustomBaseAdapter<T> extends ArrayAdapter<T> {
    protected static String TAG_LOG = null;

    private int resourceId;

    protected List<T> list;

    public CustomBaseAdapter(Context context, int resource, List<T> list) {
        super(context, resource, list);
        this.list = list;
        this.resourceId = resource;
        TAG_LOG = this.getClass().getSimpleName();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    public void remove(int position) {
        this.list.remove(position);
        notifyDataSetChanged();
    }

    public void remove(T t) {
        this.list.remove(t);
        notifyDataSetChanged();
    }

    public void updateListView(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder viewHolder = BaseViewHolder.get(getContext(), parent,
                resourceId, position, convertView);
        // 设置每个item控件
        setConvert(viewHolder, getItem(position));
        return viewHolder.getConvertView();
    }

    public abstract void setConvert(BaseViewHolder viewHolder, T t);

    public void setList(List<T> list) {
        this.list = list;
    }

}
