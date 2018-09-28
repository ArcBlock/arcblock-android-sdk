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

import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Response;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.config.CoreKitConfig;
import com.arcblock.corekit.viewmodel.CoreKitMutationViewModel;
import com.arcblock.corekit.viewmodel.i.CoreKitBeanMapperInterface;
import com.arcblock.corekit.viewmodel.i.CoreKitMutationInterface;

/**
 * The CoreKitMutation is used to make developer use CoreKitMutationViewModel more easily.
 * Created by Paper on 2018/9/27
 **/
public abstract class CoreKitMutation<T extends Operation.Data, D> implements CoreKitMutationInterface, CoreKitBeanMapperInterface<Response<T>, D> {

    private CoreKitMutationViewModel mCoreKitMutationViewModel;
    private LifecycleOwner mLifecycleOwner;

    /**
     * The construct for activity and custom client
     *
     * @param activity
     * @param lifecycleOwner
     * @param client
     */
    public CoreKitMutation(FragmentActivity activity, LifecycleOwner lifecycleOwner, ABCoreKitClient client) {
        CoreKitMutationViewModel.CustomClientFactory factory = new CoreKitMutationViewModel.CustomClientFactory(this, client);
        this.mCoreKitMutationViewModel = mCoreKitMutationViewModel.getInstance(activity, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }

    /**
     * The construct for activity and default client
     *
     * @param activity
     * @param lifecycleOwner
     * @param context
     * @param apiType
     */
    public CoreKitMutation(FragmentActivity activity, LifecycleOwner lifecycleOwner, Context context, CoreKitConfig.ApiType apiType) {
        CoreKitMutationViewModel.DefaultFactory factory = new CoreKitMutationViewModel.DefaultFactory(this, context, apiType);
        this.mCoreKitMutationViewModel = mCoreKitMutationViewModel.getInstance(activity, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }

    /**
     * he construct for fragment and custom client
     *
     * @param fragment
     * @param fragment
     * @param lifecycleOwner
     * @param client
     */
    public CoreKitMutation(Fragment fragment, LifecycleOwner lifecycleOwner, ABCoreKitClient client) {
        CoreKitMutationViewModel.CustomClientFactory factory = new CoreKitMutationViewModel.CustomClientFactory(this, client);
        this.mCoreKitMutationViewModel = mCoreKitMutationViewModel.getInstance(fragment, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }

    /**
     * The construct for fragment and default client
     *
     * @param fragment
     * @param lifecycleOwner
     * @param context
     * @param apiType
     */
    public CoreKitMutation(Fragment fragment, LifecycleOwner lifecycleOwner, Context context, CoreKitConfig.ApiType apiType) {
        CoreKitMutationViewModel.DefaultFactory factory = new CoreKitMutationViewModel.DefaultFactory(this, context, apiType);
        this.mCoreKitMutationViewModel = mCoreKitMutationViewModel.getInstance(fragment, factory);
        this.mLifecycleOwner = lifecycleOwner;
    }

    public void mutation(Mutation mutation) {
        this.mCoreKitMutationViewModel.mutationData(mutation);
    }

    public void setObserve(Observer<CoreKitBean<D>> observe) {
        this.mCoreKitMutationViewModel.observeData().observe(mLifecycleOwner, observe);
    }


}
