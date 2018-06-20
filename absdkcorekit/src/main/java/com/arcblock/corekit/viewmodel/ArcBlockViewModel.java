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

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.arcblock.corekit.bean.ArcBlockBean;
import com.arcblock.corekit.data.DataRepository;

import java.util.List;

public class ArcBlockViewModel extends AndroidViewModel {

    private DataRepository mDataRepository = null;

    private ArcBlockViewModel(@NonNull Application application, DataRepository dataRepository) {
        super(application);
        this.mDataRepository = dataRepository;
    }

    public LiveData<List<ArcBlockBean>> getArcBlockBeans() {
        return mDataRepository.getAllArcBlockBeans();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private Application mApplication;
        private DataRepository mDataRepository;

        public Factory(Application application, DataRepository dataRepository) {
            this.mApplication = application;
            this.mDataRepository = dataRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ArcBlockViewModel(mApplication, mDataRepository);
        }
    }
}
