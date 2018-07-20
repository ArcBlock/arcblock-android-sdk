package com.arcblock.corekit.bean;

public class CoreKitPagedBean<T> extends CoreKitBean<T> {
	public static int DATA_TYPE_LOAD_MORE = 0;
	public static int DATA_TYPE_REFRESH = 1;
	public static int DATA_TYPE_NONE = -1;

	private int dataType;

	public CoreKitPagedBean() {
	}

	public CoreKitPagedBean(T data, int status, String errorMessage, int dataType) {
		super(data, status, errorMessage);
		this.dataType = dataType;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
}
