package com.arcblock.corekit.bean;

import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Query;

public interface PageQuery<D extends Operation.Data, T, V extends Operation.Variables> extends Query<D, T, V>{

	void setPageInput(Input<PageInput> input);

}
