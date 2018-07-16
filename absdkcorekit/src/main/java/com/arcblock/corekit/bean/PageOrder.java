package com.arcblock.corekit.bean;

import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.InputFieldMarshaller;
import com.apollographql.apollo.api.InputFieldWriter;
import com.apollographql.apollo.api.internal.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import javax.annotation.Generated;

@Generated("Apollo GraphQL")
public final class PageOrder {
  private final Input<String> field;

  private final Input<String> type;

  private volatile int $hashCode;

  private volatile boolean $hashCodeMemoized;

  PageOrder(Input<String> field, Input<String> type) {
    this.field = field;
    this.type = type;
  }

  public @Nullable String field() {
    return this.field.value;
  }

  public @Nullable String type() {
    return this.type.value;
  }

  public static Builder builder() {
    return new Builder();
  }

  public InputFieldMarshaller marshaller() {
    return new InputFieldMarshaller() {
      @Override
      public void marshal(InputFieldWriter writer) throws IOException {
        if (field.defined) {
          writer.writeString("field", field.value);
        }
        if (type.defined) {
          writer.writeString("type", type.value);
        }
      }
    };
  }

  @Override
  public int hashCode() {
    if (!$hashCodeMemoized) {
      int h = 1;
      h *= 1000003;
      h ^= field.hashCode();
      h *= 1000003;
      h ^= type.hashCode();
      $hashCode = h;
      $hashCodeMemoized = true;
    }
    return $hashCode;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof PageOrder) {
      PageOrder that = (PageOrder) o;
      return this.field.equals(that.field)
       && this.type.equals(that.type);
    }
    return false;
  }

  public static final class Builder {
    private Input<String> field = Input.absent();

    private Input<String> type = Input.absent();

    Builder() {
    }

    public Builder field(@Nullable String field) {
      this.field = Input.fromNullable(field);
      return this;
    }

    public Builder type(@Nullable String type) {
      this.type = Input.fromNullable(type);
      return this;
    }

    public Builder fieldInput(@NotNull Input<String> field) {
      this.field = Utils.checkNotNull(field, "field == null");
      return this;
    }

    public Builder typeInput(@NotNull Input<String> type) {
      this.type = Utils.checkNotNull(type, "type == null");
      return this;
    }

    public PageOrder build() {
      return new PageOrder(field, type);
    }
  }
}
