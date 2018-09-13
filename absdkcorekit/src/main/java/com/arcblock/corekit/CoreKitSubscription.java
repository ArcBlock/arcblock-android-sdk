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

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.apollographql.apollo.api.Subscription;
import com.arcblock.corekit.config.CoreKitConfig;
import com.arcblock.corekit.socket.CoreKitSocketStatusCallBack;
import com.arcblock.corekit.viewmodel.CoreKitSubscriptionViewModel;
import com.arcblock.corekit.viewmodel.i.CoreKitSubHelperInterface;

/**
 * The CoreKitSubscription is used to make developer use CoreKitSubscriptionViewModel more easily.
 * Created by Nate on 2018/9/12
 **/
public abstract class CoreKitSubscription<T extends Subscription.Data, D extends Subscription> implements CoreKitSubHelperInterface<T, D> {

    private CoreKitSubscriptionViewModel<T, D> mCoreKitSubscriptionViewModel;

    /**
     * The construct for activity and custom client
     * @param activity
     * @param client
     */
    public CoreKitSubscription(FragmentActivity activity, ABCoreKitClient client) {
        CoreKitSubscriptionViewModel.CustomClientFactory factory =
                new CoreKitSubscriptionViewModel.CustomClientFactory(client, getSubscription(), getResultDataClass());
        this.mCoreKitSubscriptionViewModel = CoreKitSubscriptionViewModel.getInstance(activity, factory);
        this.mCoreKitSubscriptionViewModel.subscription();
    }

    /**
     * The construct for activity and default client
     * @param activity
     * @param context
     * @param apiType
     */
    public CoreKitSubscription(FragmentActivity activity, Context context, CoreKitConfig.ApiType apiType) {
        CoreKitSubscriptionViewModel.DefaultFactory factory =
                new CoreKitSubscriptionViewModel.DefaultFactory(context, apiType, getSubscription(), getResultDataClass());
        this.mCoreKitSubscriptionViewModel = CoreKitSubscriptionViewModel.getInstance(activity, factory);
        this.mCoreKitSubscriptionViewModel.subscription();
    }

    /**
     * The construct for fragment and custom client
     * @param fragment
     * @param client
     */
    public CoreKitSubscription(Fragment fragment, ABCoreKitClient client) {
        CoreKitSubscriptionViewModel.CustomClientFactory factory =
                new CoreKitSubscriptionViewModel.CustomClientFactory(client, getSubscription(), getResultDataClass());
        this.mCoreKitSubscriptionViewModel = CoreKitSubscriptionViewModel.getInstance(fragment, factory);
        this.mCoreKitSubscriptionViewModel.subscription();
    }

    /**
     * The construct for fragment and default client
     * @param fragment
     * @param context
     * @param apiType
     */
    public CoreKitSubscription(Fragment fragment, Context context, CoreKitConfig.ApiType apiType) {
        CoreKitSubscriptionViewModel.DefaultFactory factory =
                new CoreKitSubscriptionViewModel.DefaultFactory(context, apiType, getSubscription(), getResultDataClass());
        this.mCoreKitSubscriptionViewModel = CoreKitSubscriptionViewModel.getInstance(fragment, factory);
        this.mCoreKitSubscriptionViewModel.subscription();
    }

    public void setCoreKitSubCallBack(CoreKitSubscriptionViewModel.CoreKitSubCallBack callBack) {
        this.mCoreKitSubscriptionViewModel.setCoreKitSubCallBack(callBack);
    }


    public void setCoreKitSocketStatusCallBack(CoreKitSocketStatusCallBack callBack) {
        this.mCoreKitSubscriptionViewModel.setCoreKitSocketStatusCallBack(callBack);
    }

    public void doManualReconnect() {
        this.mCoreKitSubscriptionViewModel.doManualReconnect();
    }

}
