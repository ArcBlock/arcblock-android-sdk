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
package com.arcblock.corekit.data.net;

import android.os.AsyncTask;

import com.arcblock.corekit.bean.ArcBlockBean;
import com.arcblock.corekit.data.db.DatabaseManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NetDataUtil {

    private static NetDataUtil INSTANCE = null;

    // todo here need a Boolean livedata for loading status

    private NetDataUtil(){

    }

    public static NetDataUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (NetDataUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetDataUtil();
                }
            }
        }
        return INSTANCE;
    }

    public void getAllArcBlockBeans() {
        new AsyncTask<Void, Void, List<ArcBlockBean>>() {
            @Override
            protected List<ArcBlockBean> doInBackground(Void... voids) {
                List<ArcBlockBean> result = new ArrayList<>();
                result.add(new ArcBlockBean("1", "net data 1", new Date()));
                result.add(new ArcBlockBean("2", "net data 2", new Date()));
                result.add(new ArcBlockBean("3", "net data 3", new Date()));
                // 模拟网络延迟
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // get net data then insert them into db
                insertLocalArcBlockBeans(result);
                return result;
            }

            @Override
            protected void onPostExecute(List<ArcBlockBean> arcBlockBeans) {
                super.onPostExecute(arcBlockBeans);
            }
        }.execute();
    }

    private void insertLocalArcBlockBeans(List<ArcBlockBean> arcBlockBeans) {
        if (arcBlockBeans == null || arcBlockBeans.isEmpty()) {
            return;
        }
        DatabaseManager.getInstance().insertItems(arcBlockBeans);
    }
}
