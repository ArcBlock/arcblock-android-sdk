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
package com.arcblock.corekit.data.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.arcblock.corekit.bean.ArcBlockBean;

import java.util.List;

public class DatabaseManager {
    private static final String DATABASE_NAME = "arcblock-db";
    private static DatabaseManager INSTANCE = null;
    private ArcBlockDatabase mDatabase;

    private DatabaseManager() {

    }

    public static DatabaseManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DatabaseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DatabaseManager();
                }
            }
        }
        return INSTANCE;
    }

    public void createDB(Context context) {
        new AsyncTask<Context, Void, Void>() {

            @Override
            protected Void doInBackground(Context... params) {
                Context context = params[0].getApplicationContext();
                mDatabase = Room.databaseBuilder(context, ArcBlockDatabase.class, DATABASE_NAME).build();
                return null;
            }
        }.execute(context.getApplicationContext());
    }


    public void insertItems(final List<ArcBlockBean> arcBlockBeans) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mDatabase.beginTransaction();
                try {
                    mDatabase.arcBlockDao().insertItems(arcBlockBeans);
                    mDatabase.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mDatabase.endTransaction();
                }
                return null;
            }
        }.execute();
    }

    public LiveData<List<ArcBlockBean>> getAllItems() {
        return mDatabase.arcBlockDao().loadAllItems();
    }
}
