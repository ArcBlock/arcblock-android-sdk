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

import com.apollographql.apollo.api.Query;

public abstract class CoreKitPagedHelper {

	private boolean hasMore = true;
	private String cursor;

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

	public void setHasMoreForRefresh() {
		this.hasMore = true;
		this.cursor = "";
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

	/**
	 * @return initial query object for page initial query
	 */
	public abstract Query getInitialQuery();

	/**
	 * @return loadMore query object for page loadMore query
	 */
	public abstract Query getLoadMoreQuery();

	/**
	 * @return refresh query object for page refresh query
	 */
	public abstract Query getRefreshQuery();


}
