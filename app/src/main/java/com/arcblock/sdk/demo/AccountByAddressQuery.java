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
public final class AccountByAddressQuery implements Query<AccountByAddressQuery.Data, AccountByAddressQuery.Data, AccountByAddressQuery.Variables> {
  public static final String OPERATION_DEFINITION = "query accountByAddress($address: String!) {\n"
      + "  accountByAddress(address: $address) {\n"
      + "    __typename\n"
      + "    address\n"
      + "    balance\n"
      + "    pubKey\n"
      + "    scriptType\n"
      + "    txsReceived {\n"
      + "      __typename\n"
      + "      data {\n"
      + "        __typename\n"
      + "        hash\n"
      + "      }\n"
      + "    }\n"
      + "    txsSent {\n"
      + "      __typename\n"
      + "      data {\n"
      + "        __typename\n"
      + "        hash\n"
      + "      }\n"
      + "    }\n"
      + "  }\n"
      + "}";

  public static final String OPERATION_ID = "14de06854733bcabcc90598016f65a28f977c0e21d437ada119fba90b4d8d9fb";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  public static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "accountByAddress";
    }
  };

  private final Variables variables;

  public AccountByAddressQuery(@NotNull String address) {
    Utils.checkNotNull(address, "address == null");
    variables = new Variables(address);
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
    private @NotNull String address;

    Builder() {
    }

    public Builder address(@NotNull String address) {
      this.address = address;
      return this;
    }

    public AccountByAddressQuery build() {
      Utils.checkNotNull(address, "address == null");
      return new AccountByAddressQuery(address);
    }
  }

  public static final class Variables extends Operation.Variables {
    private final @NotNull String address;

    private final transient Map<String, Object> valueMap = new LinkedHashMap<>();

    Variables(@NotNull String address) {
      this.address = address;
      this.valueMap.put("address", address);
    }

    public @NotNull String address() {
      return address;
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
          writer.writeString("address", address);
        }
      };
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forObject("accountByAddress", "accountByAddress", new UnmodifiableMapBuilder<String, Object>(1)
      .put("address", new UnmodifiableMapBuilder<String, Object>(2)
        .put("kind", "Variable")
        .put("variableName", "address")
        .build())
      .build(), true, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nullable AccountByAddress accountByAddress;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(@Nullable AccountByAddress accountByAddress) {
      this.accountByAddress = accountByAddress;
    }

    /**
     * Returns information of an account.
     */
    public @Nullable AccountByAddress getAccountByAddress() {
      return this.accountByAddress;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeObject($responseFields[0], accountByAddress != null ? accountByAddress.marshaller() : null);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "accountByAddress=" + accountByAddress
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
        return ((this.accountByAddress == null) ? (that.accountByAddress == null) : this.accountByAddress.equals(that.accountByAddress));
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= (accountByAddress == null) ? 0 : accountByAddress.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final AccountByAddress.Mapper accountByAddressFieldMapper = new AccountByAddress.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final AccountByAddress accountByAddress = reader.readObject($responseFields[0], new ResponseReader.ObjectReader<AccountByAddress>() {
          @Override
          public AccountByAddress read(ResponseReader reader) {
            return accountByAddressFieldMapper.map(reader);
          }
        });
        return new Data(accountByAddress);
      }
    }
  }

  public static class AccountByAddress {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("address", "address", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("balance", "balance", null, true, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("pubKey", "pubKey", null, true, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("scriptType", "scriptType", null, true, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forObject("txsReceived", "txsReceived", null, true, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forObject("txsSent", "txsSent", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @NotNull String address;

    final @Nullable Integer balance;

    final @Nullable String pubKey;

    final @Nullable String scriptType;

    final @Nullable TxsReceived txsReceived;

    final @Nullable TxsSent txsSent;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public AccountByAddress(@NotNull String __typename, @NotNull String address,
        @Nullable Integer balance, @Nullable String pubKey, @Nullable String scriptType,
        @Nullable TxsReceived txsReceived, @Nullable TxsSent txsSent) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.address = Utils.checkNotNull(address, "address == null");
      this.balance = balance;
      this.pubKey = pubKey;
      this.scriptType = scriptType;
      this.txsReceived = txsReceived;
      this.txsSent = txsSent;
    }

    public @NotNull String get__typename() {
      return this.__typename;
    }

    public @NotNull String getAddress() {
      return this.address;
    }

    public @Nullable Integer getBalance() {
      return this.balance;
    }

    public @Nullable String getPubKey() {
      return this.pubKey;
    }

    public @Nullable String getScriptType() {
      return this.scriptType;
    }

    public @Nullable TxsReceived getTxsReceived() {
      return this.txsReceived;
    }

    public @Nullable TxsSent getTxsSent() {
      return this.txsSent;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeString($responseFields[1], address);
          writer.writeInt($responseFields[2], balance);
          writer.writeString($responseFields[3], pubKey);
          writer.writeString($responseFields[4], scriptType);
          writer.writeObject($responseFields[5], txsReceived != null ? txsReceived.marshaller() : null);
          writer.writeObject($responseFields[6], txsSent != null ? txsSent.marshaller() : null);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "AccountByAddress{"
          + "__typename=" + __typename + ", "
          + "address=" + address + ", "
          + "balance=" + balance + ", "
          + "pubKey=" + pubKey + ", "
          + "scriptType=" + scriptType + ", "
          + "txsReceived=" + txsReceived + ", "
          + "txsSent=" + txsSent
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof AccountByAddress) {
        AccountByAddress that = (AccountByAddress) o;
        return this.__typename.equals(that.__typename)
         && this.address.equals(that.address)
         && ((this.balance == null) ? (that.balance == null) : this.balance.equals(that.balance))
         && ((this.pubKey == null) ? (that.pubKey == null) : this.pubKey.equals(that.pubKey))
         && ((this.scriptType == null) ? (that.scriptType == null) : this.scriptType.equals(that.scriptType))
         && ((this.txsReceived == null) ? (that.txsReceived == null) : this.txsReceived.equals(that.txsReceived))
         && ((this.txsSent == null) ? (that.txsSent == null) : this.txsSent.equals(that.txsSent));
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
        h ^= address.hashCode();
        h *= 1000003;
        h ^= (balance == null) ? 0 : balance.hashCode();
        h *= 1000003;
        h ^= (pubKey == null) ? 0 : pubKey.hashCode();
        h *= 1000003;
        h ^= (scriptType == null) ? 0 : scriptType.hashCode();
        h *= 1000003;
        h ^= (txsReceived == null) ? 0 : txsReceived.hashCode();
        h *= 1000003;
        h ^= (txsSent == null) ? 0 : txsSent.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<AccountByAddress> {
      final TxsReceived.Mapper txsReceivedFieldMapper = new TxsReceived.Mapper();

      final TxsSent.Mapper txsSentFieldMapper = new TxsSent.Mapper();

      @Override
      public AccountByAddress map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String address = reader.readString($responseFields[1]);
        final Integer balance = reader.readInt($responseFields[2]);
        final String pubKey = reader.readString($responseFields[3]);
        final String scriptType = reader.readString($responseFields[4]);
        final TxsReceived txsReceived = reader.readObject($responseFields[5], new ResponseReader.ObjectReader<TxsReceived>() {
          @Override
          public TxsReceived read(ResponseReader reader) {
            return txsReceivedFieldMapper.map(reader);
          }
        });
        final TxsSent txsSent = reader.readObject($responseFields[6], new ResponseReader.ObjectReader<TxsSent>() {
          @Override
          public TxsSent read(ResponseReader reader) {
            return txsSentFieldMapper.map(reader);
          }
        });
        return new AccountByAddress(__typename, address, balance, pubKey, scriptType, txsReceived, txsSent);
      }
    }
  }

  public static class TxsReceived {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forList("data", "data", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @Nullable List<Datum> data;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public TxsReceived(@NotNull String __typename, @Nullable List<Datum> data) {
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
        $toString = "TxsReceived{"
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
      if (o instanceof TxsReceived) {
        TxsReceived that = (TxsReceived) o;
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

    public static final class Mapper implements ResponseFieldMapper<TxsReceived> {
      final Datum.Mapper datumFieldMapper = new Datum.Mapper();

      @Override
      public TxsReceived map(ResponseReader reader) {
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
        return new TxsReceived(__typename, data);
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

  public static class TxsSent {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forList("data", "data", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @Nullable List<Datum1> data;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public TxsSent(@NotNull String __typename, @Nullable List<Datum1> data) {
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
        $toString = "TxsSent{"
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
      if (o instanceof TxsSent) {
        TxsSent that = (TxsSent) o;
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

    public static final class Mapper implements ResponseFieldMapper<TxsSent> {
      final Datum1.Mapper datum1FieldMapper = new Datum1.Mapper();

      @Override
      public TxsSent map(ResponseReader reader) {
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
        return new TxsSent(__typename, data);
      }
    }
  }

  public static class Datum1 {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("hash", "hash", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @NotNull String hash;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Datum1(@NotNull String __typename, @NotNull String hash) {
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
        $toString = "Datum1{"
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
      if (o instanceof Datum1) {
        Datum1 that = (Datum1) o;
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

    public static final class Mapper implements ResponseFieldMapper<Datum1> {
      @Override
      public Datum1 map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String hash = reader.readString($responseFields[1]);
        return new Datum1(__typename, hash);
      }
    }
  }
}
