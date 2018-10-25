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

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nate on 2018/10/21
 **/
public class CoreKitPagedQuery<T extends Operation.Data, K> implements LifecycleObserver {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private ABCoreKitClient mABCoreKitClient;
    private PagedQueryHelper mPagedQueryHelper;
    private CoreKitPagedQueryResultListener<K> mPagedQueryResultListener;
    private boolean isLoading;
    private List<K> resultDatas = new ArrayList<>();

    public CoreKitPagedQuery(LifecycleOwner lifecycleOwner, ABCoreKitClient aBCoreKitClient, PagedQueryHelper pagedQueryHelper) {
        this.mABCoreKitClient = aBCoreKitClient;
        this.mPagedQueryHelper = pagedQueryHelper;
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    public void setPagedQueryResultListener(CoreKitPagedQueryResultListener<K> pagedQueryResultListener) {
        mPagedQueryResultListener = pagedQueryResultListener;
    }

    public void startInitQuery() {
        if (isLoading) {
            if (mPagedQueryResultListener != null) {
                mPagedQueryResultListener.onError(new Throwable("Cannot do refresh or initial query when loading."));
            }
            return;
        }
        if (mPagedQueryHelper != null && mPagedQueryHelper.getInitialQuery() != null) {
            resultDatas.clear();
            isLoading = true;
            mPagedQueryHelper.setHasMoreForRefresh();
            query(mPagedQueryHelper.getInitialQuery());
        } else {
            if (mPagedQueryResultListener != null) {
                mPagedQueryResultListener.onError(new Throwable("Initial query is empty."));
            }
        }
    }

    public void startLoadMoreQuery() {
        if (isLoading) {
            if (mPagedQueryResultListener != null) {
                mPagedQueryResultListener.onError(new Throwable("Cannot do loadMore when loading."));
            }
            return;
        }
        if (mPagedQueryHelper != null && mPagedQueryHelper.getLoadMoreQuery() != null) {
            isLoading = true;
            query(mPagedQueryHelper.getLoadMoreQuery());
        } else {
            if (mPagedQueryResultListener != null) {
                mPagedQueryResultListener.onError(new Throwable("Load more query is empty."));
            }
        }
    }

    private void query(Query query) {
        Rx2Apollo.from(mABCoreKitClient.query(query))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<T>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Response<T> t) {
                        if (t != null && t.data() != null) {
                            if (t.hasErrors()) {
                                try {
                                    if (mPagedQueryResultListener != null) {
                                        mPagedQueryResultListener.onError(new Throwable(((Error) ((Response) t).errors().get(0)).message()));
                                    }
                                } catch (Exception e) {
                                    if (mPagedQueryResultListener != null) {
                                        mPagedQueryResultListener.onError(e);
                                    }
                                }
                            } else {
                                handleData(t.data());
                                isLoading = false;
                            }
                        } else {
                            if (mPagedQueryResultListener != null) {
                                mPagedQueryResultListener.onError(new Throwable("The result is empty."));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mPagedQueryResultListener != null) {
                            mPagedQueryResultListener.onError(new Throwable("The result is empty."));
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mPagedQueryResultListener != null) {
                            mPagedQueryResultListener.onComplete();
                        }
                    }
                });
    }

    /**
     * handle response t to the data which are we want.
     *
     * @param data
     */
    private synchronized void handleData(Operation.Data data) {
        if (mPagedQueryHelper == null) {
            if (mPagedQueryResultListener != null) {
                mPagedQueryResultListener.onError(new Throwable("The PagedQueryHelper is empty when handleData."));
            }
            return;
        }
        List<K> temp = mPagedQueryHelper.map(data);
        if (temp == null) {
            if (mPagedQueryResultListener != null) {
                mPagedQueryResultListener.onError(new Throwable("The paged result is empty when handleData."));
            }
            return;
        }
        // handle list for repeated data
        for (int i = 0; i < temp.size(); i++) {
            if (isNotInBlocks(temp.get(i))) {
                resultDatas.add(temp.get(i));
            }
        }
        if (mPagedQueryResultListener != null) {
            mPagedQueryResultListener.onSuccess(resultDatas);
        }
    }

    /**
     * @param k
     * @return if the item k is not in the listDatas
     */
    private boolean isNotInBlocks(K k) {
        for (int i = 0; i < resultDatas.size(); i++) {
            if (k.equals(resultDatas.get(i))) {
                return false;
            }
        }
        return true;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
    }
}
