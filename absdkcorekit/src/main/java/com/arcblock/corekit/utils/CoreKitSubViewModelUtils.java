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
package com.arcblock.corekit.utils;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.apollographql.apollo.api.Subscription;
import com.arcblock.corekit.viewmodel.CoreKitSubViewModel;

public class CoreKitSubViewModelUtils {

	public static <T extends Subscription> CoreKitSubViewModel getCoreKitSubViewModel(T graphQlSub, FragmentActivity activity, ViewModelProvider.Factory factory){
		return ViewModelProviders.of(activity, factory).get(graphQlSub.operationId() + "$" + graphQlSub.variables().valueMap().hashCode(), CoreKitSubViewModel.class);
	}

	public static <T extends Subscription> CoreKitSubViewModel getCoreKitSubViewModel(T graphQlSub, Fragment fragment, ViewModelProvider.Factory factory){
		return ViewModelProviders.of(fragment, factory).get(graphQlSub.operationId() + "$" + graphQlSub.variables().valueMap().hashCode(), CoreKitSubViewModel.class);
	}
}
