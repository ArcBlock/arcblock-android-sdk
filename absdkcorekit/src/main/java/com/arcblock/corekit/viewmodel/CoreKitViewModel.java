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
import android.support.annotation.NonNull;

import com.apollographql.apollo.api.Query;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.data.CoreKitRemote;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CoreKitViewModel<T> extends ViewModel {

	private CoreKitRemote mCoreKitRemote;
	private MutableLiveData<CoreKitBean<T>> mCoreKitBeanMutableLiveData = new MutableLiveData<>();

	public CoreKitViewModel(CoreKitRemote coreKitRemote) {
		this.mCoreKitRemote = coreKitRemote;
	}

	public MutableLiveData<CoreKitBean<T>> getQueryData(Query query) {
		doQuery(query);
		return mCoreKitBeanMutableLiveData;
	}

	public void setQuery(Query query) {
		doQuery(query);
	}

	public void doQuery(Query query) {
		mCoreKitRemote.query(query)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<T>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(T t) {
						mCoreKitBeanMutableLiveData.postValue(new CoreKitBean(t, CoreKitBean.SUCCESS_CODE, ""));
					}

					@Override
					public void onError(Throwable e) {
						mCoreKitBeanMutableLiveData.postValue(new CoreKitBean(null, CoreKitBean.FAIL_CODE, e.toString()));
					}

					@Override
					public void onComplete() {

					}
				});
		;
	}

	public static class Factory extends ViewModelProvider.NewInstanceFactory {

		private CoreKitRemote mCoreKitRemote;

		public Factory(CoreKitRemote coreKitRemote) {
			this.mCoreKitRemote = coreKitRemote;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			return (T) new CoreKitViewModel(mCoreKitRemote);
		}
	}

}
