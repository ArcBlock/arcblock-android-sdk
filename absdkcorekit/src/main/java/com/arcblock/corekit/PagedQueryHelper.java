package com.arcblock.corekit;

import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Query;

import java.util.List;

/**
*  Created by Nate on 2018/10/21
**/
public abstract class PagedQueryHelper<T extends Operation.Data, K> {
    private boolean hasMore = true;
    private String cursor;

    /**
     * @return initial query object for page initial query
     */
    public abstract Query getInitialQuery();

    /**
     * @return loadMore query object for page loadMore query
     */
    public abstract Query getLoadMoreQuery();

    /**
     * this method is for ...
     */
    public void setHasMoreForRefresh(){
        this.hasMore = true;
        this.cursor = "";
    }

    public abstract List<K> map(T data);

    /**
     * @return is page have next
     */
    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        // if already hasMore = false, just return
        if (!this.hasMore) {
            return;
        }
        this.hasMore = hasMore;
    }

    /**
     * @return current cursor
     */
    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
}
