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
