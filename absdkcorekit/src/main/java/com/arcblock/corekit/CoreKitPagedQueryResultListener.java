package com.arcblock.corekit;

import java.util.List;

/**
*  Created by Nate on 2018/10/19
**/
public interface CoreKitPagedQueryResultListener<K> {
    void onSuccess(List<K> datas);

    void onError(String errMsg);

    void onComplete();
}
