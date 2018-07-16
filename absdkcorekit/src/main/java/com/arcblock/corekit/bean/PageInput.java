package com.arcblock.corekit.bean;

import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.InputFieldMarshaller;
import com.apollographql.apollo.api.InputFieldWriter;
import com.apollographql.apollo.api.internal.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

import javax.annotation.Generated;

@Generated("Apollo GraphQL")
public final class PageInput {
  private final Input<String> cursor;

  private final Input<List<PageOrder>> order;

  private final Input<Integer> size;

  private volatile int $hashCode;

  private volatile boolean $hashCodeMemoized;

  PageInput(Input<String> cursor, Input<List<PageOrder>> order, Input<Integer> size) {
    this.cursor = cursor;
    this.order = order;
    this.size = size;
  }

  public @Nullable String cursor() {
    return this.cursor.value;
  }

  public @Nullable List<PageOrder> order() {
    return this.order.value;
  }

  public @Nullable Integer size() {
    return this.size.value;
  }

  public static Builder builder() {
    return new Builder();
  }

  public InputFieldMarshaller marshaller() {
    return new InputFieldMarshaller() {
      @Override
      public void marshal(InputFieldWriter writer) throws IOException {
        if (cursor.defined) {
          writer.writeString("cursor", cursor.value);
        }
        if (order.defined) {
          writer.writeList("order", order.value != null ? new InputFieldWriter.ListWriter() {
            @Override
            public void write(InputFieldWriter.ListItemWriter listItemWriter) throws IOException {
              for (final PageOrder $item : order.value) {
                listItemWriter.writeObject($item != null ? $item.marshaller() : null);
              }
            }
          } : null);
        }
        if (size.defined) {
          writer.writeInt("size", size.value);
        }
      }
    };
  }

  @Override
  public int hashCode() {
    if (!$hashCodeMemoized) {
      int h = 1;
      h *= 1000003;
      h ^= cursor.hashCode();
      h *= 1000003;
      h ^= order.hashCode();
      h *= 1000003;
      h ^= size.hashCode();
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
    if (o instanceof PageInput) {
      PageInput that = (PageInput) o;
      return this.cursor.equals(that.cursor)
       && this.order.equals(that.order)
       && this.size.equals(that.size);
    }
    return false;
  }

  public static final class Builder {
    private Input<String> cursor = Input.absent();

    private Input<List<PageOrder>> order = Input.absent();

    private Input<Integer> size = Input.absent();

    Builder() {
    }

    public Builder cursor(@Nullable String cursor) {
      this.cursor = Input.fromNullable(cursor);
      return this;
    }

    public Builder order(@Nullable List<PageOrder> order) {
      this.order = Input.fromNullable(order);
      return this;
    }

    public Builder size(@Nullable Integer size) {
      this.size = Input.fromNullable(size);
      return this;
    }

    public Builder cursorInput(@NotNull Input<String> cursor) {
      this.cursor = Utils.checkNotNull(cursor, "cursor == null");
      return this;
    }

    public Builder orderInput(@NotNull Input<List<PageOrder>> order) {
      this.order = Utils.checkNotNull(order, "order == null");
      return this;
    }

    public Builder sizeInput(@NotNull Input<Integer> size) {
      this.size = Utils.checkNotNull(size, "size == null");
      return this;
    }

    public PageInput build() {
      return new PageInput(cursor, order, size);
    }
  }
}
