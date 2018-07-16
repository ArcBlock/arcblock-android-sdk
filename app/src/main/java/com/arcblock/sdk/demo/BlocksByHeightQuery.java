package com.arcblock.sdk.demo;

import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.InputFieldMarshaller;
import com.apollographql.apollo.api.InputFieldWriter;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ResponseFieldMapper;
import com.apollographql.apollo.api.ResponseFieldMarshaller;
import com.apollographql.apollo.api.ResponseReader;
import com.apollographql.apollo.api.ResponseWriter;
import com.apollographql.apollo.api.internal.UnmodifiableMapBuilder;
import com.apollographql.apollo.api.internal.Utils;
import com.arcblock.sdk.demo.type.PageInput;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

@Generated("Apollo GraphQL")
public final class BlocksByHeightQuery implements Query<BlocksByHeightQuery.Data, BlocksByHeightQuery.Data, BlocksByHeightQuery.Variables> {
  public static final String OPERATION_DEFINITION = "query blocksByHeight($fromHeight: Int!, $toHeight: Int!, $paging: PageInput) {\n"
      + "  blocksByHeight(fromHeight: $fromHeight, toHeight: $toHeight, paging: $paging) {\n"
      + "    __typename\n"
      + "    data {\n"
      + "      __typename\n"
      + "      height\n"
      + "      hash\n"
      + "      numberTxs\n"
      + "    }\n"
      + "    page {\n"
      + "      __typename\n"
      + "      cursor\n"
      + "      next\n"
      + "      total\n"
      + "    }\n"
      + "  }\n"
      + "}";

  public static final String OPERATION_ID = "294673cb4f7408452600a91f0056a5d27e7b60832dcee35bcc5aab762606a897";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  public static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "blocksByHeight";
    }
  };

  private final Variables variables;

  public BlocksByHeightQuery(int fromHeight, int toHeight, @NotNull Input<PageInput> paging) {
    Utils.checkNotNull(paging, "paging == null");
    variables = new Variables(fromHeight, toHeight, paging);
  }

  @Override
  public String operationId() {
    return OPERATION_ID;
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public Data wrapData(Data data) {
    return data;
  }

  @Override
  public Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<Data> responseFieldMapper() {
    return new Data.Mapper();
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public OperationName name() {
    return OPERATION_NAME;
  }

  public static final class Builder {
    private int fromHeight;

    private int toHeight;

    private Input<PageInput> paging = Input.absent();

    Builder() {
    }

    public Builder fromHeight(int fromHeight) {
      this.fromHeight = fromHeight;
      return this;
    }

    public Builder toHeight(int toHeight) {
      this.toHeight = toHeight;
      return this;
    }

    public Builder paging(@Nullable PageInput paging) {
      this.paging = Input.fromNullable(paging);
      return this;
    }

    public Builder pagingInput(@NotNull Input<PageInput> paging) {
      this.paging = Utils.checkNotNull(paging, "paging == null");
      return this;
    }

    public BlocksByHeightQuery build() {
      return new BlocksByHeightQuery(fromHeight, toHeight, paging);
    }
  }

  public static final class Variables extends Operation.Variables {
    private final int fromHeight;

    private final int toHeight;

    private final Input<PageInput> paging;

    private final transient Map<String, Object> valueMap = new LinkedHashMap<>();

    Variables(int fromHeight, int toHeight, Input<PageInput> paging) {
      this.fromHeight = fromHeight;
      this.toHeight = toHeight;
      this.paging = paging;
      this.valueMap.put("fromHeight", fromHeight);
      this.valueMap.put("toHeight", toHeight);
      if (paging.defined) {
        this.valueMap.put("paging", paging.value);
      }
    }

    public int fromHeight() {
      return fromHeight;
    }

    public int toHeight() {
      return toHeight;
    }

    public Input<PageInput> paging() {
      return paging;
    }

    @Override
    public Map<String, Object> valueMap() {
      return Collections.unmodifiableMap(valueMap);
    }

    @Override
    public InputFieldMarshaller marshaller() {
      return new InputFieldMarshaller() {
        @Override
        public void marshal(InputFieldWriter writer) throws IOException {
          writer.writeInt("fromHeight", fromHeight);
          writer.writeInt("toHeight", toHeight);
          if (paging.defined) {
            writer.writeObject("paging", paging.value != null ? paging.value.marshaller() : null);
          }
        }
      };
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forObject("blocksByHeight", "blocksByHeight", new UnmodifiableMapBuilder<String, Object>(3)
      .put("fromHeight", new UnmodifiableMapBuilder<String, Object>(2)
        .put("kind", "Variable")
        .put("variableName", "fromHeight")
        .build())
      .put("toHeight", new UnmodifiableMapBuilder<String, Object>(2)
        .put("kind", "Variable")
        .put("variableName", "toHeight")
        .build())
      .put("paging", new UnmodifiableMapBuilder<String, Object>(2)
        .put("kind", "Variable")
        .put("variableName", "paging")
        .build())
      .build(), true, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nullable BlocksByHeight blocksByHeight;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(@Nullable BlocksByHeight blocksByHeight) {
      this.blocksByHeight = blocksByHeight;
    }

    /**
     * Returns blockks with paginations based on their height.
     */
    public @Nullable BlocksByHeight getBlocksByHeight() {
      return this.blocksByHeight;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeObject($responseFields[0], blocksByHeight != null ? blocksByHeight.marshaller() : null);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "blocksByHeight=" + blocksByHeight
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Data) {
        Data that = (Data) o;
        return ((this.blocksByHeight == null) ? (that.blocksByHeight == null) : this.blocksByHeight.equals(that.blocksByHeight));
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= (blocksByHeight == null) ? 0 : blocksByHeight.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final BlocksByHeight.Mapper blocksByHeightFieldMapper = new BlocksByHeight.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final BlocksByHeight blocksByHeight = reader.readObject($responseFields[0], new ResponseReader.ObjectReader<BlocksByHeight>() {
          @Override
          public BlocksByHeight read(ResponseReader reader) {
            return blocksByHeightFieldMapper.map(reader);
          }
        });
        return new Data(blocksByHeight);
      }
    }
  }

  public static class BlocksByHeight {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forList("data", "data", null, true, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forObject("page", "page", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @Nullable List<Datum> data;

    final @Nullable Page page;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public BlocksByHeight(@NotNull String __typename, @Nullable List<Datum> data,
        @Nullable Page page) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.data = data;
      this.page = page;
    }

    public @NotNull String get__typename() {
      return this.__typename;
    }

    public @Nullable List<Datum> getData() {
      return this.data;
    }

    public @Nullable Page getPage() {
      return this.page;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeList($responseFields[1], data, new ResponseWriter.ListWriter() {
            @Override
            public void write(Object value, ResponseWriter.ListItemWriter listItemWriter) {
              listItemWriter.writeObject(((Datum) value).marshaller());
            }
          });
          writer.writeObject($responseFields[2], page != null ? page.marshaller() : null);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "BlocksByHeight{"
          + "__typename=" + __typename + ", "
          + "data=" + data + ", "
          + "page=" + page
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof BlocksByHeight) {
        BlocksByHeight that = (BlocksByHeight) o;
        return this.__typename.equals(that.__typename)
         && ((this.data == null) ? (that.data == null) : this.data.equals(that.data))
         && ((this.page == null) ? (that.page == null) : this.page.equals(that.page));
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= __typename.hashCode();
        h *= 1000003;
        h ^= (data == null) ? 0 : data.hashCode();
        h *= 1000003;
        h ^= (page == null) ? 0 : page.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<BlocksByHeight> {
      final Datum.Mapper datumFieldMapper = new Datum.Mapper();

      final Page.Mapper pageFieldMapper = new Page.Mapper();

      @Override
      public BlocksByHeight map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final List<Datum> data = reader.readList($responseFields[1], new ResponseReader.ListReader<Datum>() {
          @Override
          public Datum read(ResponseReader.ListItemReader listItemReader) {
            return listItemReader.readObject(new ResponseReader.ObjectReader<Datum>() {
              @Override
              public Datum read(ResponseReader reader) {
                return datumFieldMapper.map(reader);
              }
            });
          }
        });
        final Page page = reader.readObject($responseFields[2], new ResponseReader.ObjectReader<Page>() {
          @Override
          public Page read(ResponseReader reader) {
            return pageFieldMapper.map(reader);
          }
        });
        return new BlocksByHeight(__typename, data, page);
      }
    }
  }

  public static class Datum {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("height", "height", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("hash", "hash", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("numberTxs", "numberTxs", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final int height;

    final @NotNull String hash;

    final int numberTxs;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Datum(@NotNull String __typename, int height, @NotNull String hash, int numberTxs) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.height = height;
      this.hash = Utils.checkNotNull(hash, "hash == null");
      this.numberTxs = numberTxs;
    }

    public @NotNull String get__typename() {
      return this.__typename;
    }

    public int getHeight() {
      return this.height;
    }

    public @NotNull String getHash() {
      return this.hash;
    }

    public int getNumberTxs() {
      return this.numberTxs;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeInt($responseFields[1], height);
          writer.writeString($responseFields[2], hash);
          writer.writeInt($responseFields[3], numberTxs);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Datum{"
          + "__typename=" + __typename + ", "
          + "height=" + height + ", "
          + "hash=" + hash + ", "
          + "numberTxs=" + numberTxs
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Datum) {
        Datum that = (Datum) o;
        return this.__typename.equals(that.__typename)
         && this.height == that.height
         && this.hash.equals(that.hash)
         && this.numberTxs == that.numberTxs;
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= __typename.hashCode();
        h *= 1000003;
        h ^= height;
        h *= 1000003;
        h ^= hash.hashCode();
        h *= 1000003;
        h ^= numberTxs;
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Datum> {
      @Override
      public Datum map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final int height = reader.readInt($responseFields[1]);
        final String hash = reader.readString($responseFields[2]);
        final int numberTxs = reader.readInt($responseFields[3]);
        return new Datum(__typename, height, hash, numberTxs);
      }
    }
  }

  public static class Page {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("cursor", "cursor", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forBoolean("next", "next", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("total", "total", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @NotNull String cursor;

    final boolean next;

    final @Nullable Integer total;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Page(@NotNull String __typename, @NotNull String cursor, boolean next,
        @Nullable Integer total) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.cursor = Utils.checkNotNull(cursor, "cursor == null");
      this.next = next;
      this.total = total;
    }

    public @NotNull String get__typename() {
      return this.__typename;
    }

    public @NotNull String getCursor() {
      return this.cursor;
    }

    public boolean isNext() {
      return this.next;
    }

    public @Nullable Integer getTotal() {
      return this.total;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeString($responseFields[1], cursor);
          writer.writeBoolean($responseFields[2], next);
          writer.writeInt($responseFields[3], total);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Page{"
          + "__typename=" + __typename + ", "
          + "cursor=" + cursor + ", "
          + "next=" + next + ", "
          + "total=" + total
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Page) {
        Page that = (Page) o;
        return this.__typename.equals(that.__typename)
         && this.cursor.equals(that.cursor)
         && this.next == that.next
         && ((this.total == null) ? (that.total == null) : this.total.equals(that.total));
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= __typename.hashCode();
        h *= 1000003;
        h ^= cursor.hashCode();
        h *= 1000003;
        h ^= Boolean.valueOf(next).hashCode();
        h *= 1000003;
        h ^= (total == null) ? 0 : total.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Page> {
      @Override
      public Page map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String cursor = reader.readString($responseFields[1]);
        final boolean next = reader.readBoolean($responseFields[2]);
        final Integer total = reader.readInt($responseFields[3]);
        return new Page(__typename, cursor, next, total);
      }
    }
  }
}
