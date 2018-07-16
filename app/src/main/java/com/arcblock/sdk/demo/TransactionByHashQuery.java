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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

@Generated("Apollo GraphQL")
public final class TransactionByHashQuery implements Query<TransactionByHashQuery.Data, TransactionByHashQuery.Data, TransactionByHashQuery.Variables> {
  public static final String OPERATION_DEFINITION = "query transactionByHash($hash: String!) {\n"
      + "  transactionByHash(hash: $hash) {\n"
      + "    __typename\n"
      + "    blockHash\n"
      + "    blockHeight\n"
      + "    size\n"
      + "    virtualSize\n"
      + "    weight\n"
      + "    total\n"
      + "    fees\n"
      + "    numberInputs\n"
      + "    numberOutputs\n"
      + "    outputs {\n"
      + "      __typename\n"
      + "      data {\n"
      + "        __typename\n"
      + "        account\n"
      + "      }\n"
      + "    }\n"
      + "    inputs {\n"
      + "      __typename\n"
      + "      data {\n"
      + "        __typename\n"
      + "        account\n"
      + "      }\n"
      + "    }\n"
      + "  }\n"
      + "}";

  public static final String OPERATION_ID = "535456b200880699763a40aaf1edbb20d3d9fa2ccb223a407091f5153d67cbb6";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  public static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "transactionByHash";
    }
  };

  private final Variables variables;

  public TransactionByHashQuery(@NotNull String hash) {
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

    public TransactionByHashQuery build() {
      Utils.checkNotNull(hash, "hash == null");
      return new TransactionByHashQuery(hash);
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
      ResponseField.forObject("transactionByHash", "transactionByHash", new UnmodifiableMapBuilder<String, Object>(1)
      .put("hash", new UnmodifiableMapBuilder<String, Object>(2)
        .put("kind", "Variable")
        .put("variableName", "hash")
        .build())
      .build(), true, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nullable TransactionByHash transactionByHash;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(@Nullable TransactionByHash transactionByHash) {
      this.transactionByHash = transactionByHash;
    }

    /**
     * Returns a transaction by it's hash.
     */
    public @Nullable TransactionByHash getTransactionByHash() {
      return this.transactionByHash;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeObject($responseFields[0], transactionByHash != null ? transactionByHash.marshaller() : null);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "transactionByHash=" + transactionByHash
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
        return ((this.transactionByHash == null) ? (that.transactionByHash == null) : this.transactionByHash.equals(that.transactionByHash));
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= (transactionByHash == null) ? 0 : transactionByHash.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final TransactionByHash.Mapper transactionByHashFieldMapper = new TransactionByHash.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final TransactionByHash transactionByHash = reader.readObject($responseFields[0], new ResponseReader.ObjectReader<TransactionByHash>() {
          @Override
          public TransactionByHash read(ResponseReader reader) {
            return transactionByHashFieldMapper.map(reader);
          }
        });
        return new Data(transactionByHash);
      }
    }
  }

  public static class TransactionByHash {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("blockHash", "blockHash", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("blockHeight", "blockHeight", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("size", "size", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("virtualSize", "virtualSize", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("weight", "weight", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("total", "total", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("fees", "fees", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("numberInputs", "numberInputs", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("numberOutputs", "numberOutputs", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forObject("outputs", "outputs", null, true, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forObject("inputs", "inputs", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @NotNull String blockHash;

    final int blockHeight;

    final int size;

    final int virtualSize;

    final int weight;

    final int total;

    final int fees;

    final int numberInputs;

    final int numberOutputs;

    final @Nullable Outputs outputs;

    final @Nullable Inputs inputs;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public TransactionByHash(@NotNull String __typename, @NotNull String blockHash, int blockHeight,
        int size, int virtualSize, int weight, int total, int fees, int numberInputs,
        int numberOutputs, @Nullable Outputs outputs, @Nullable Inputs inputs) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.blockHash = Utils.checkNotNull(blockHash, "blockHash == null");
      this.blockHeight = blockHeight;
      this.size = size;
      this.virtualSize = virtualSize;
      this.weight = weight;
      this.total = total;
      this.fees = fees;
      this.numberInputs = numberInputs;
      this.numberOutputs = numberOutputs;
      this.outputs = outputs;
      this.inputs = inputs;
    }

    public @NotNull String get__typename() {
      return this.__typename;
    }

    public @NotNull String getBlockHash() {
      return this.blockHash;
    }

    public int getBlockHeight() {
      return this.blockHeight;
    }

    public int getSize() {
      return this.size;
    }

    public int getVirtualSize() {
      return this.virtualSize;
    }

    public int getWeight() {
      return this.weight;
    }

    public int getTotal() {
      return this.total;
    }

    public int getFees() {
      return this.fees;
    }

    public int getNumberInputs() {
      return this.numberInputs;
    }

    public int getNumberOutputs() {
      return this.numberOutputs;
    }

    public @Nullable Outputs getOutputs() {
      return this.outputs;
    }

    public @Nullable Inputs getInputs() {
      return this.inputs;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeString($responseFields[1], blockHash);
          writer.writeInt($responseFields[2], blockHeight);
          writer.writeInt($responseFields[3], size);
          writer.writeInt($responseFields[4], virtualSize);
          writer.writeInt($responseFields[5], weight);
          writer.writeInt($responseFields[6], total);
          writer.writeInt($responseFields[7], fees);
          writer.writeInt($responseFields[8], numberInputs);
          writer.writeInt($responseFields[9], numberOutputs);
          writer.writeObject($responseFields[10], outputs != null ? outputs.marshaller() : null);
          writer.writeObject($responseFields[11], inputs != null ? inputs.marshaller() : null);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "TransactionByHash{"
          + "__typename=" + __typename + ", "
          + "blockHash=" + blockHash + ", "
          + "blockHeight=" + blockHeight + ", "
          + "size=" + size + ", "
          + "virtualSize=" + virtualSize + ", "
          + "weight=" + weight + ", "
          + "total=" + total + ", "
          + "fees=" + fees + ", "
          + "numberInputs=" + numberInputs + ", "
          + "numberOutputs=" + numberOutputs + ", "
          + "outputs=" + outputs + ", "
          + "inputs=" + inputs
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof TransactionByHash) {
        TransactionByHash that = (TransactionByHash) o;
        return this.__typename.equals(that.__typename)
         && this.blockHash.equals(that.blockHash)
         && this.blockHeight == that.blockHeight
         && this.size == that.size
         && this.virtualSize == that.virtualSize
         && this.weight == that.weight
         && this.total == that.total
         && this.fees == that.fees
         && this.numberInputs == that.numberInputs
         && this.numberOutputs == that.numberOutputs
         && ((this.outputs == null) ? (that.outputs == null) : this.outputs.equals(that.outputs))
         && ((this.inputs == null) ? (that.inputs == null) : this.inputs.equals(that.inputs));
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
        h ^= blockHash.hashCode();
        h *= 1000003;
        h ^= blockHeight;
        h *= 1000003;
        h ^= size;
        h *= 1000003;
        h ^= virtualSize;
        h *= 1000003;
        h ^= weight;
        h *= 1000003;
        h ^= total;
        h *= 1000003;
        h ^= fees;
        h *= 1000003;
        h ^= numberInputs;
        h *= 1000003;
        h ^= numberOutputs;
        h *= 1000003;
        h ^= (outputs == null) ? 0 : outputs.hashCode();
        h *= 1000003;
        h ^= (inputs == null) ? 0 : inputs.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<TransactionByHash> {
      final Outputs.Mapper outputsFieldMapper = new Outputs.Mapper();

      final Inputs.Mapper inputsFieldMapper = new Inputs.Mapper();

      @Override
      public TransactionByHash map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String blockHash = reader.readString($responseFields[1]);
        final int blockHeight = reader.readInt($responseFields[2]);
        final int size = reader.readInt($responseFields[3]);
        final int virtualSize = reader.readInt($responseFields[4]);
        final int weight = reader.readInt($responseFields[5]);
        final int total = reader.readInt($responseFields[6]);
        final int fees = reader.readInt($responseFields[7]);
        final int numberInputs = reader.readInt($responseFields[8]);
        final int numberOutputs = reader.readInt($responseFields[9]);
        final Outputs outputs = reader.readObject($responseFields[10], new ResponseReader.ObjectReader<Outputs>() {
          @Override
          public Outputs read(ResponseReader reader) {
            return outputsFieldMapper.map(reader);
          }
        });
        final Inputs inputs = reader.readObject($responseFields[11], new ResponseReader.ObjectReader<Inputs>() {
          @Override
          public Inputs read(ResponseReader reader) {
            return inputsFieldMapper.map(reader);
          }
        });
        return new TransactionByHash(__typename, blockHash, blockHeight, size, virtualSize, weight, total, fees, numberInputs, numberOutputs, outputs, inputs);
      }
    }
  }

  public static class Outputs {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forList("data", "data", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @Nullable List<Datum> data;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Outputs(@NotNull String __typename, @Nullable List<Datum> data) {
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
        $toString = "Outputs{"
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
      if (o instanceof Outputs) {
        Outputs that = (Outputs) o;
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

    public static final class Mapper implements ResponseFieldMapper<Outputs> {
      final Datum.Mapper datumFieldMapper = new Datum.Mapper();

      @Override
      public Outputs map(ResponseReader reader) {
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
        return new Outputs(__typename, data);
      }
    }
  }

  public static class Datum {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("account", "account", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @Nullable String account;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Datum(@NotNull String __typename, @Nullable String account) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.account = account;
    }

    public @NotNull String get__typename() {
      return this.__typename;
    }

    public @Nullable String getAccount() {
      return this.account;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeString($responseFields[1], account);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Datum{"
          + "__typename=" + __typename + ", "
          + "account=" + account
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
         && ((this.account == null) ? (that.account == null) : this.account.equals(that.account));
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
        h ^= (account == null) ? 0 : account.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Datum> {
      @Override
      public Datum map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String account = reader.readString($responseFields[1]);
        return new Datum(__typename, account);
      }
    }
  }

  public static class Inputs {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forList("data", "data", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @Nullable List<Datum1> data;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Inputs(@NotNull String __typename, @Nullable List<Datum1> data) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.data = data;
    }

    public @NotNull String get__typename() {
      return this.__typename;
    }

    public @Nullable List<Datum1> getData() {
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
              listItemWriter.writeObject(((Datum1) value).marshaller());
            }
          });
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Inputs{"
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
      if (o instanceof Inputs) {
        Inputs that = (Inputs) o;
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

    public static final class Mapper implements ResponseFieldMapper<Inputs> {
      final Datum1.Mapper datum1FieldMapper = new Datum1.Mapper();

      @Override
      public Inputs map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final List<Datum1> data = reader.readList($responseFields[1], new ResponseReader.ListReader<Datum1>() {
          @Override
          public Datum1 read(ResponseReader.ListItemReader listItemReader) {
            return listItemReader.readObject(new ResponseReader.ObjectReader<Datum1>() {
              @Override
              public Datum1 read(ResponseReader reader) {
                return datum1FieldMapper.map(reader);
              }
            });
          }
        });
        return new Inputs(__typename, data);
      }
    }
  }

  public static class Datum1 {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("account", "account", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @Nullable String account;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Datum1(@NotNull String __typename, @Nullable String account) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.account = account;
    }

    public @NotNull String get__typename() {
      return this.__typename;
    }

    public @Nullable String getAccount() {
      return this.account;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeString($responseFields[1], account);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Datum1{"
          + "__typename=" + __typename + ", "
          + "account=" + account
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Datum1) {
        Datum1 that = (Datum1) o;
        return this.__typename.equals(that.__typename)
         && ((this.account == null) ? (that.account == null) : this.account.equals(that.account));
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
        h ^= (account == null) ? 0 : account.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Datum1> {
      @Override
      public Datum1 map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String account = reader.readString($responseFields[1]);
        return new Datum1(__typename, account);
      }
    }
  }
}
