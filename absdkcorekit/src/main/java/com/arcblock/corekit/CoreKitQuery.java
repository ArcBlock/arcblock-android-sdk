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
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.config.CoreKitConfig;
import com.arcblock.corekit.viewmodel.CoreKitQueryViewModel;
import com.arcblock.corekit.viewmodel.i.CoreKitBeanMapperInterface;
import com.arcblock.corekit.viewmodel.i.CoreKitQueryInterface;

/**
 * The CoreKitQuery is used to make developer use CoreKitQueryViewModel more easily.
 * Created by Nate on 2018/9/12
 **/
public abstract class CoreKitQuery<T extends Query.Data, D> implements CoreKitQueryInterface, CoreKitBeanMapperInterface<Response<T>, D> {

    private CoreKitQueryViewModel mCoreKitQueryViewModel;
    private LifecycleOwner mLifecycleOwner;

    /**
     * The construct for activity and custom client
     * @param activity
     * @param lifecycleOwner
     * @param client
     */
    public CoreKitQuery(FragmentActivity activity,LifecycleOwner lifecycleOwner, ABCoreKitClient client) {
        CoreKitQueryViewModel.CustomClientFactory factory = new CoreKitQueryViewModel.CustomClientFactory(getQuery(), this, client);
        this.mCoreKitQueryViewModel = CoreKitQueryViewModel.getInstance(activity, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }

    /**
     * The construct for activity and default client
     * @param activity
     * @param lifecycleOwner
     * @param context
     * @param apiType
     */
    public CoreKitQuery(FragmentActivity activity,LifecycleOwner lifecycleOwner, Context context, CoreKitConfig.ApiType apiType) {
        CoreKitQueryViewModel.DefaultFactory factory = new CoreKitQueryViewModel.DefaultFactory(getQuery(), this, context, apiType);
        this.mCoreKitQueryViewModel = CoreKitQueryViewModel.getInstance(activity, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }

    /**
     * he construct for fragment and custom client
     * @param fragment
     * @param fragment
     * @param lifecycleOwner
     * @param client
     */
    public CoreKitQuery(Fragment fragment, LifecycleOwner lifecycleOwner, ABCoreKitClient client) {
        CoreKitQueryViewModel.CustomClientFactory factory = new CoreKitQueryViewModel.CustomClientFactory(getQuery(), this, client);
        this.mCoreKitQueryViewModel = CoreKitQueryViewModel.getInstance(fragment, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }

    /**
     * The construct for fragment and default client
     * @param fragment
     * @param lifecycleOwner
     * @param context
     * @param apiType
     */
    public CoreKitQuery(Fragment fragment,LifecycleOwner lifecycleOwner, Context context, CoreKitConfig.ApiType apiType) {
        CoreKitQueryViewModel.DefaultFactory factory = new CoreKitQueryViewModel.DefaultFactory(getQuery(), this, context, apiType);
        this.mCoreKitQueryViewModel = CoreKitQueryViewModel.getInstance(fragment, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }

    public void setObserve(Observer<CoreKitBean<D>> observe){
        this.mCoreKitQueryViewModel.getQueryData(getQuery()).observe(mLifecycleOwner,observe);
    }

}
