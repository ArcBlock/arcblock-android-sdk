package com.arcblock.corekit;

import com.apollographql.apollo.api.Operation;

/**
*  Created by Nate on 2018/10/19
**/
public interface CoreKitResultListener<T extends Operation.Data> {
    void onSuccess(T t);

    void onError(String errMsg);

    void onComplete();
}
