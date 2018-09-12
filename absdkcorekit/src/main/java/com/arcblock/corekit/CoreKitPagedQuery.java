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
package com.arcblock.corekit;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.arcblock.corekit.bean.CoreKitPagedBean;
import com.arcblock.corekit.viewmodel.i.CoreKitBeanMapperInterface;
import com.arcblock.corekit.viewmodel.i.CoreKitPagedHelperInterface;
import com.arcblock.corekit.viewmodel.CoreKitPagedViewModel;

import java.util.List;

/**
*  The CoreKitPagedQuery is used to make developer use CoreKitPagedViewModel more easily.
*  Created by Nate on 2018/9/12
**/
public abstract class CoreKitPagedQuery<T extends Query.Data, D> implements CoreKitPagedHelperInterface, CoreKitBeanMapperInterface<Response<T>, List<D>> {

    private boolean hasMore = true;
    private String cursor;
    private CoreKitPagedViewModel<T, D> coreKitPagedViewModel;
    private LifecycleOwner mLifecycleOwner;

    public CoreKitPagedQuery(FragmentActivity activity, LifecycleOwner lifecycleOwner, ABCoreKitClient client) {
        CoreKitPagedViewModel.CustomClientFactory factory = new CoreKitPagedViewModel.CustomClientFactory(this, this, client);
        this.coreKitPagedViewModel = CoreKitPagedViewModel.getInstance(activity, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }

    public CoreKitPagedQuery(FragmentActivity activity, LifecycleOwner lifecycleOwner, Context context, int apiType) {
        CoreKitPagedViewModel.DefaultFactory factory = new CoreKitPagedViewModel.DefaultFactory(this, this, context, apiType);
        this.coreKitPagedViewModel = CoreKitPagedViewModel.getInstance(activity, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }

    public CoreKitPagedQuery(Fragment fragment, LifecycleOwner lifecycleOwner, ABCoreKitClient client) {
        CoreKitPagedViewModel.CustomClientFactory factory = new CoreKitPagedViewModel.CustomClientFactory(this, this, client);
        this.coreKitPagedViewModel = CoreKitPagedViewModel.getInstance(fragment, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }

    public CoreKitPagedQuery(Fragment fragment, LifecycleOwner lifecycleOwner, Context context, int apiType) {
        CoreKitPagedViewModel.DefaultFactory factory = new CoreKitPagedViewModel.DefaultFactory(this, this, context, apiType);
        this.coreKitPagedViewModel = CoreKitPagedViewModel.getInstance(fragment, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }


    /**
     * @return is page have next
     */
    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        // if already hasMore = false, just return
        if (!this.hasMore) {
            return;
        }
        this.hasMore = hasMore;
    }

    public void setHasMoreForRefresh() {
        this.hasMore = true;
        this.cursor = "";
    }

    /**
     * @return current cursor
     */
    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public void refresh() {
        this.coreKitPagedViewModel.refresh();
    }

    public void loadMore() {
        this.coreKitPagedViewModel.loadMore();
    }

    public void setObserve(Observer<CoreKitPagedBean<List<D>>> observe) {
        this.coreKitPagedViewModel.getCleanQueryData().observe(mLifecycleOwner, observe);
    }

}
