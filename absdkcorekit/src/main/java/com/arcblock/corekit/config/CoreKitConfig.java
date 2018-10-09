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

    public static final String BASE_URL_BTC = "https://ocap.arcblock.io/api/btc";
    public static final String BASE_URL_ETH = "https://ocap.arcblock.io/api/eth";
    public static final String BASE_URL_AUTH = "http://10.0.2.2:8080/playground/";

    public static final String SUBSCRIPTION_BASE_URL_ETH = "wss://ocap.arcblock.io/api/eth/socket/websocket";
    public static final String SUBSCRIPTION_BASE_URL_BTC = "wss://ocap.arcblock.io/api/btc/socket/websocket";
    public static final String SUBSCRIPTION_BASE_URL_AUTH = "wss://ocap.arcblock.io/api/btc/socket/websocket";

    /**
     * @param type
     * @return api url by type
     */
    public static String getApiUrl(ApiType type) {
        if (type == ApiType.API_TYPE_BTC) {
            return BASE_URL_BTC;
        } else if (type == ApiType.API_TYPE_ETH) {
            return BASE_URL_ETH;
        } else if (type == ApiType.API_TYPE_AUTH) {
            return BASE_URL_AUTH;
        } else {
            return "";
        }
    }

    /**
     * @param type
     * @return subscription url by type
     */
    public static String getSubUrl(ApiType type){
        if (type == ApiType.API_TYPE_BTC) {
            return SUBSCRIPTION_BASE_URL_BTC;
        } else if (type == ApiType.API_TYPE_ETH) {
            return SUBSCRIPTION_BASE_URL_ETH;
        } else if (type == ApiType.API_TYPE_AUTH) {
            return SUBSCRIPTION_BASE_URL_AUTH;
        } else {
            return "";
        }
    }

    /**
     *  Api type for different endpoint
     */
    public enum ApiType {
        API_TYPE_BTC,
        API_TYPE_ETH,
        API_TYPE_AUTH,
        API_TYPE_CUSTOM
    }
}
