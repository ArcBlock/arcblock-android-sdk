package com.arcblock.sdk.demo.type;

import com.apollographql.apollo.api.ScalarType;

import javax.annotation.Generated;

@Generated("Apollo GraphQL")
public enum CustomType implements ScalarType {
  DATETIME {
    @Override
    public String typeName() {
      return "DateTime";
    }

    @Override
    public Class javaType() {
      return Object.class;
    }
  },

  ID {
    @Override
    public String typeName() {
      return "ID";
    }

    @Override
    public Class javaType() {
      return String.class;
    }
  }
}
