package com.arcblock.corekit.socket;

public interface CoreKitSocketStatusCallBack {
	void onOpen();
	void onClose();
	void onError();
}
