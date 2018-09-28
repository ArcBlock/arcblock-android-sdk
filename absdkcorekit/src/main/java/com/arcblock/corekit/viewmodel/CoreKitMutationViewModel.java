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
package com.arcblock.corekit.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.config.CoreKitConfig;
import com.arcblock.corekit.utils.CoreKitLogUtils;
import com.arcblock.corekit.viewmodel.i.CoreKitBeanMapperInterface;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CoreKitMutationViewModel<T, D> extends ViewModel {


    public static CoreKitMutationViewModel getInstance(FragmentActivity activity, CoreKitMutationViewModel.CustomClientFactory factory) {
        return ViewModelProviders.of(activity, factory).get(factory.getTag(), CoreKitMutationViewModel.class);
    }

    public static CoreKitMutationViewModel getInstance(FragmentActivity activity, CoreKitMutationViewModel.DefaultFactory factory) {
        return ViewModelProviders.of(activity, factory).get(factory.getTag(), CoreKitMutationViewModel.class);
    }

    public static CoreKitMutationViewModel getInstance(Fragment fragment, CoreKitMutationViewModel.CustomClientFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(factory.getTag(), CoreKitMutationViewModel.class);
    }

    public static CoreKitMutationViewModel getInstance(Fragment fragment, CoreKitMutationViewModel.DefaultFactory factory) {
        return ViewModelProviders.of(fragment, factory).get(factory.getTag(), CoreKitMutationViewModel.class);
    }


    private ABCoreKitClient mABCoreKitClient;
    private MutableLiveData<CoreKitBean<D>> mCoreKitBeanMutableLiveData = new MutableLiveData<>();
    private CoreKitBeanMapperInterface<T, D> mCoreKitBeanMapper;
//    private CompositeDisposable mCompositeDisposable = new CompositeDisposable(); Rx2Apollo maybe leak

    public CoreKitMutationViewModel(CoreKitBeanMapperInterface<T, D> mapper, Context context, CoreKitConfig.ApiType apiType) {
        this.mCoreKitBeanMapper = mapper;
        this.mABCoreKitClient = ABCoreKitClient.defaultInstance(context, apiType);
    }

    public CoreKitMutationViewModel(CoreKitBeanMapperInterface<T, D> mapper, ABCoreKitClient aBCoreKitClient) {
        this.mCoreKitBeanMapper = mapper;
        this.mABCoreKitClient = aBCoreKitClient;
    }


    /**
     * @param mutation
     */
    public void mutationData(Mutation mutation) {
        if (mCoreKitBeanMutableLiveData.hasObservers()) {
            doFinalMutation(mutation);
        } else {
            throw new IllegalStateException("You must have at least one Observable!");
        }
    }

    /**
     * @return a livedata object with CoreKitBean
     */
    public MutableLiveData<CoreKitBean<D>> observeData() {
        return mCoreKitBeanMutableLiveData;
    }


    private void doFinalMutation(Mutation mutation) {
        if (mutation == null) {
            mCoreKitBeanMutableLiveData.postValue(new CoreKitBean(null, CoreKitBean.FAIL_CODE, "The query is empty."));
            return;
        }
        Rx2Apollo.from(mABCoreKitClient.mutate(mutation))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(T t) {
                        if (t != null) {
                            mCoreKitBeanMutableLiveData.postValue(new CoreKitBean(mCoreKitBeanMapper.map(t), CoreKitBean.SUCCESS_CODE, ""));
                        } else {
                            mCoreKitBeanMutableLiveData.postValue(new CoreKitBean(null, CoreKitBean.FAIL_CODE, "The result is empty."));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCoreKitBeanMutableLiveData.postValue(new CoreKitBean(null, CoreKitBean.FAIL_CODE, e.toString()));
                    }

                    @Override
                    public void onComplete() {
                        CoreKitLogUtils.d("onComplete");
                    }
                });
    }

    public static class CustomClientFactory extends ViewModelProvider.NewInstanceFactory {

        private CoreKitBeanMapperInterface mCoreKitBeanMapper;
        private ABCoreKitClient mABCoreKitClient;
        private String mTag = "";

        public String getTag() {
            return mTag;
        }

        public CustomClientFactory(CoreKitBeanMapperInterface coreKitBeanMapper, ABCoreKitClient aBCoreKitClient) {
            this.mABCoreKitClient = aBCoreKitClient;
            this.mCoreKitBeanMapper = coreKitBeanMapper;
        }

        public CustomClientFactory(CoreKitBeanMapperInterface coreKitBeanMapper, ABCoreKitClient aBCoreKitClient, String tag) {
            this.mABCoreKitClient = aBCoreKitClient;
            this.mCoreKitBeanMapper = coreKitBeanMapper;
            this.mTag = tag;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CoreKitMutationViewModel(mCoreKitBeanMapper, mABCoreKitClient);
        }
    }

    public static class DefaultFactory extends ViewModelProvider.NewInstanceFactory {
        private CoreKitBeanMapperInterface mCoreKitBeanMapper;
        private Context mContext;
        private CoreKitConfig.ApiType apiType;
        private String mTag = "";

        public String getTag() {
            return mTag;
        }

        public DefaultFactory(CoreKitBeanMapperInterface coreKitBeanMapper, Context context, CoreKitConfig.ApiType apiType) {
            this.mCoreKitBeanMapper = coreKitBeanMapper;
            this.mContext = context;
            this.apiType = apiType;
        }

        public DefaultFactory(CoreKitBeanMapperInterface coreKitBeanMapper, Context context, CoreKitConfig.ApiType apiType, String tag) {
            this.mCoreKitBeanMapper = coreKitBeanMapper;
            this.mContext = context;
            this.apiType = apiType;
            this.mTag = tag;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CoreKitMutationViewModel(mCoreKitBeanMapper, mContext, apiType);
        }
    }


}
