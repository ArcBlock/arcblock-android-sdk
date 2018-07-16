package com.arcblock.sdk.demo;

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
import com.arcblock.sdk.demo.type.CustomType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

@Generated("Apollo GraphQL")
public final class BlockByHashQuery implements Query<BlockByHashQuery.Data, BlockByHashQuery.Data, BlockByHashQuery.Variables> {
  public static final String OPERATION_DEFINITION = "query blockByHash($hash: String!) {\n"
      + "  blockByHash(hash: $hash) {\n"
      + "    __typename\n"
      + "    height\n"
      + "    size\n"
      + "    strippedSize\n"
      + "    weight\n"
      + "    numberTxs\n"
      + "    version\n"
      + "    bits\n"
      + "    nonce\n"
      + "    time\n"
      + "    preHash\n"
      + "    transactions {\n"
      + "      __typename\n"
      + "      data {\n"
      + "        __typename\n"
      + "        hash\n"
      + "      }\n"
      + "    }\n"
      + "  }\n"
      + "}";

  public static final String OPERATION_ID = "32cea5f8de345a0a7e9e77306401fd642112c09f7715397f2edddcdf31e49287";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  public static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "blockByHash";
    }
  };

  private final Variables variables;

  public BlockByHashQuery(@NotNull String hash) {
    Utils.checkNotNull(hash, "hash == null");
    variables = new Variables(hash);
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
    private @NotNull String hash;

    Builder() {
    }

    public Builder hash(@NotNull String hash) {
      this.hash = hash;
      return this;
    }

    public BlockByHashQuery build() {
      Utils.checkNotNull(hash, "hash == null");
      return new BlockByHashQuery(hash);
    }
  }

  public static final class Variables extends Operation.Variables {
    private final @NotNull String hash;

    private final transient Map<String, Object> valueMap = new LinkedHashMap<>();

    Variables(@NotNull String hash) {
      this.hash = hash;
      this.valueMap.put("hash", hash);
    }

    public @NotNull String hash() {
      return hash;
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
          writer.writeString("hash", hash);
        }
      };
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forObject("blockByHash", "blockByHash", new UnmodifiableMapBuilder<String, Object>(1)
      .put("hash", new UnmodifiableMapBuilder<String, Object>(2)
        .put("kind", "Variable")
        .put("variableName", "hash")
        .build())
      .build(), true, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nullable BlockByHash blockByHash;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(@Nullable BlockByHash blockByHash) {
      this.blockByHash = blockByHash;
    }

    /**
     * Returns a block by it's hash.
     */
    public @Nullable BlockByHash getBlockByHash() {
      return this.blockByHash;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeObject($responseFields[0], blockByHash != null ? blockByHash.marshaller() : null);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "blockByHash=" + blockByHash
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
        return ((this.blockByHash == null) ? (that.blockByHash == null) : this.blockByHash.equals(that.blockByHash));
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= (blockByHash == null) ? 0 : blockByHash.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final BlockByHash.Mapper blockByHashFieldMapper = new BlockByHash.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final BlockByHash blockByHash = reader.readObject($responseFields[0], new ResponseReader.ObjectReader<BlockByHash>() {
          @Override
          public BlockByHash read(ResponseReader reader) {
            return blockByHashFieldMapper.map(reader);
          }
        });
        return new Data(blockByHash);
      }
    }
  }

  public static class BlockByHash {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("height", "height", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("size", "size", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("strippedSize", "strippedSize", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("weight", "weight", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("numberTxs", "numberTxs", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("version", "version", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("bits", "bits", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("nonce", "nonce", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forCustomType("time", "time", null, false, CustomType.DATETIME, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("preHash", "preHash", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forObject("transactions", "transactions", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final int height;

    final int size;

    final int strippedSize;

    final int weight;

    final int numberTxs;

    final int version;

    final int bits;

    final int nonce;

    final @NotNull Object time;

    final @NotNull String preHash;

    final @Nullable Transactions transactions;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public BlockByHash(@NotNull String __typename, int height, int size, int strippedSize,
        int weight, int numberTxs, int version, int bits, int nonce, @NotNull Object time,
        @NotNull String preHash, @Nullable Transactions transactions) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.height = height;
      this.size = size;
      this.strippedSize = strippedSize;
      this.weight = weight;
      this.numberTxs = numberTxs;
      this.version = version;
      this.bits = bits;
      this.nonce = nonce;
      this.time = Utils.checkNotNull(time, "time == null");
      this.preHash = Utils.checkNotNull(preHash, "preHash == null");
      this.transactions = transactions;
    }

    public @NotNull String get__typename() {
      return this.__typename;
    }

    public int getHeight() {
      return this.height;
    }

    public int getSize() {
      return this.size;
    }

    public int getStrippedSize() {
      return this.strippedSize;
    }

    public int getWeight() {
      return this.weight;
    }

    public int getNumberTxs() {
      return this.numberTxs;
    }

    public int getVersion() {
      return this.version;
    }

    public int getBits() {
      return this.bits;
    }

    public int getNonce() {
      return this.nonce;
    }

    public @NotNull Object getTime() {
      return this.time;
    }

    public @NotNull String getPreHash() {
      return this.preHash;
    }

    public @Nullable Transactions getTransactions() {
      return this.transactions;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeInt($responseFields[1], height);
          writer.writeInt($responseFields[2], size);
          writer.writeInt($responseFields[3], strippedSize);
          writer.writeInt($responseFields[4], weight);
          writer.writeInt($responseFields[5], numberTxs);
          writer.writeInt($responseFields[6], version);
          writer.writeInt($responseFields[7], bits);
          writer.writeInt($responseFields[8], nonce);
          writer.writeCustom((ResponseField.CustomTypeField) $responseFields[9], time);
          writer.writeString($responseFields[10], preHash);
          writer.writeObject($responseFields[11], transactions != null ? transactions.marshaller() : null);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "BlockByHash{"
          + "__typename=" + __typename + ", "
          + "height=" + height + ", "
          + "size=" + size + ", "
          + "strippedSize=" + strippedSize + ", "
          + "weight=" + weight + ", "
          + "numberTxs=" + numberTxs + ", "
          + "version=" + version + ", "
          + "bits=" + bits + ", "
          + "nonce=" + nonce + ", "
          + "time=" + time + ", "
          + "preHash=" + preHash + ", "
          + "transactions=" + transactions
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof BlockByHash) {
        BlockByHash that = (BlockByHash) o;
        return this.__typename.equals(that.__typename)
         && this.height == that.height
         && this.size == that.size
         && this.strippedSize == that.strippedSize
         && this.weight == that.weight
         && this.numberTxs == that.numberTxs
         && this.version == that.version
         && this.bits == that.bits
         && this.nonce == that.nonce
         && this.time.equals(that.time)
         && this.preHash.equals(that.preHash)
         && ((this.transactions == null) ? (that.transactions == null) : this.transactions.equals(that.transactions));
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
        h ^= size;
        h *= 1000003;
        h ^= strippedSize;
        h *= 1000003;
        h ^= weight;
        h *= 1000003;
        h ^= numberTxs;
        h *= 1000003;
        h ^= version;
        h *= 1000003;
        h ^= bits;
        h *= 1000003;
        h ^= nonce;
        h *= 1000003;
        h ^= time.hashCode();
        h *= 1000003;
        h ^= preHash.hashCode();
        h *= 1000003;
        h ^= (transactions == null) ? 0 : transactions.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<BlockByHash> {
      final Transactions.Mapper transactionsFieldMapper = new Transactions.Mapper();

      @Override
      public BlockByHash map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final int height = reader.readInt($responseFields[1]);
        final int size = reader.readInt($responseFields[2]);
        final int strippedSize = reader.readInt($responseFields[3]);
        final int weight = reader.readInt($responseFields[4]);
        final int numberTxs = reader.readInt($responseFields[5]);
        final int version = reader.readInt($responseFields[6]);
        final int bits = reader.readInt($responseFields[7]);
        final int nonce = reader.readInt($responseFields[8]);
        final Object time = reader.readCustomType((ResponseField.CustomTypeField) $responseFields[9]);
        final String preHash = reader.readString($responseFields[10]);
        final Transactions transactions = reader.readObject($responseFields[11], new ResponseReader.ObjectReader<Transactions>() {
          @Override
          public Transactions read(ResponseReader reader) {
            return transactionsFieldMapper.map(reader);
          }
        });
        return new BlockByHash(__typename, height, size, strippedSize, weight, numberTxs, version, bits, nonce, time, preHash, transactions);
      }
    }
  }

  public static class Transactions {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forList("data", "data", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @Nullable List<Datum> data;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Transactions(@NotNull String __typename, @Nullable List<Datum> data) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.data = data;
    }

    public @NotNull String get__typename() {
      return this.__typename;
    }

    public @Nullable List<Datum> getData() {
      return this.data;
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
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Transactions{"
          + "__typename=" + __typename + ", "
          + "data=" + data
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Transactions) {
        Transactions that = (Transactions) o;
        return this.__typename.equals(that.__typename)
         && ((this.data == null) ? (that.data == null) : this.data.equals(that.data));
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
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Transactions> {
      final Datum.Mapper datumFieldMapper = new Datum.Mapper();

      @Override
      public Transactions map(ResponseReader reader) {
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
        return new Transactions(__typename, data);
      }
    }
  }

  public static class Datum {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("hash", "hash", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @NotNull String hash;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Datum(@NotNull String __typename, @NotNull String hash) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.hash = Utils.checkNotNull(hash, "hash == null");
    }

    public @NotNull String get__typename() {
      return this.__typename;
    }

    public @NotNull String getHash() {
      return this.hash;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeString($responseFields[1], hash);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Datum{"
          + "__typename=" + __typename + ", "
          + "hash=" + hash
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
         && this.hash.equals(that.hash);
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
        h ^= hash.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Datum> {
      @Override
      public Datum map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String hash = reader.readString($responseFields[1]);
        return new Datum(__typename, hash);
      }
    }
  }
}
