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
package com.arcblock.corekit.config;

public class CoreKitConfig {

    public static final int API_TYPE_BTC = 0;
    public static final int API_TYPE_ETH = 1;

    public static final String BASE_URL_BTC = "https://ocap.arcblock.io/api/btc";
    public static final String BASE_URL_ETH = "https://ocap.arcblock.io/api/eth";

    public static final String SUBSCRIPTION_BASE_URL_ETH = "wss://ocap.arcblock.io/api/eth/socket/websocket";
    public static final String SUBSCRIPTION_BASE_URL_BTC = "wss://ocap.arcblock.io/api/btc/socket/websocket";

    /**
     * @param type
     * @return api url by type
     */
    public static String getApiUrl(int type) {
        if (type == API_TYPE_BTC) {
            return BASE_URL_BTC;
        } else {
            return BASE_URL_ETH;
        }
    }
}
