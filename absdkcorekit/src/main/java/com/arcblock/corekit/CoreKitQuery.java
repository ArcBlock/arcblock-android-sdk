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

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nate on 2018/10/19
 **/
public class CoreKitQuery implements LifecycleObserver {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private ABCoreKitClient mABCoreKitClient;

    public CoreKitQuery(LifecycleOwner lifecycleOwner, ABCoreKitClient aBCoreKitClient) {
        this.mABCoreKitClient = aBCoreKitClient;
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    public <T extends  Operation.Data> void query(Query query, final CoreKitResultListener<T> listener) {
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
                                    listener.onError(((Error) ((Response) t).errors().get(0)).message());
                                } catch (Exception e) {
                                    listener.onError(e.toString());
                                }
                            } else {
                                listener.onSuccess(t.data());
                            }
                        } else {
                            listener.onError("The result is empty.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onError("The result is empty.");
                    }

                    @Override
                    public void onComplete() {
                        listener.onComplete();
                    }
                });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        mCompositeDisposable.dispose();
        mCompositeDisposable.clear();
    }
}
