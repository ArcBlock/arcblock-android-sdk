package com.arcblock.sdk.demo;

import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ResponseFieldMapper;
import com.apollographql.apollo.api.ResponseFieldMarshaller;
import com.apollographql.apollo.api.ResponseReader;
import com.apollographql.apollo.api.ResponseWriter;
import com.apollographql.apollo.api.internal.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import javax.annotation.Generated;

@Generated("Apollo GraphQL")
public final class RichestAccountsQuery implements Query<RichestAccountsQuery.Data, RichestAccountsQuery.Data, Operation.Variables> {
  public static final String OPERATION_DEFINITION = "query richestAccounts {\n"
      + "  richestAccounts {\n"
      + "    __typename\n"
      + "    data {\n"
      + "      __typename\n"
      + "      address\n"
      + "      balance\n"
      + "    }\n"
      + "  }\n"
      + "}";

  public static final String OPERATION_ID = "45417dec1036384f9cec82a9630ff637db6d8173a82169a15d7933ee1b8c46ad";

  public static final String QUERY_DOCUMENT = OPERATION_DEFINITION;

  public static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "richestAccounts";
    }
  };

  private final Variables variables;

  public RichestAccountsQuery() {
    this.variables = Operation.EMPTY_VARIABLES;
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
    Builder() {
    }

    public RichestAccountsQuery build() {
      return new RichestAccountsQuery();
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forObject("richestAccounts", "richestAccounts", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @Nullable RichestAccounts richestAccounts;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Data(@Nullable RichestAccounts richestAccounts) {
      this.richestAccounts = richestAccounts;
    }

    /**
     * Returns richest accounts, order by balance.
     */
    public @Nullable RichestAccounts getRichestAccounts() {
      return this.richestAccounts;
    }

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeObject($responseFields[0], richestAccounts != null ? richestAccounts.marshaller() : null);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "richestAccounts=" + richestAccounts
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
        return ((this.richestAccounts == null) ? (that.richestAccounts == null) : this.richestAccounts.equals(that.richestAccounts));
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= (richestAccounts == null) ? 0 : richestAccounts.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final RichestAccounts.Mapper richestAccountsFieldMapper = new RichestAccounts.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final RichestAccounts richestAccounts = reader.readObject($responseFields[0], new ResponseReader.ObjectReader<RichestAccounts>() {
          @Override
          public RichestAccounts read(ResponseReader reader) {
            return richestAccountsFieldMapper.map(reader);
          }
        });
        return new Data(richestAccounts);
      }
    }
  }

  public static class RichestAccounts {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forList("data", "data", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @Nullable List<Datum> data;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public RichestAccounts(@NotNull String __typename, @Nullable List<Datum> data) {
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
        $toString = "RichestAccounts{"
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
      if (o instanceof RichestAccounts) {
        RichestAccounts that = (RichestAccounts) o;
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

    public static final class Mapper implements ResponseFieldMapper<RichestAccounts> {
      final Datum.Mapper datumFieldMapper = new Datum.Mapper();

      @Override
      public RichestAccounts map(ResponseReader reader) {
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
        return new RichestAccounts(__typename, data);
      }
    }
  }

  public static class Datum {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("address", "address", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forInt("balance", "balance", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @NotNull String address;

    final @Nullable Integer balance;

    private volatile String $toString;

    private volatile int $hashCode;

    private volatile boolean $hashCodeMemoized;

    public Datum(@NotNull String __typename, @NotNull String address, @Nullable Integer balance) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.address = Utils.checkNotNull(address, "address == null");
      this.balance = balance;
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

    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeString($responseFields[1], address);
          writer.writeInt($responseFields[2], balance);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Datum{"
          + "__typename=" + __typename + ", "
          + "address=" + address + ", "
          + "balance=" + balance
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
         && this.address.equals(that.address)
         && ((this.balance == null) ? (that.balance == null) : this.balance.equals(that.balance));
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
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Datum> {
      @Override
      public Datum map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String address = reader.readString($responseFields[1]);
        final Integer balance = reader.readInt($responseFields[2]);
        return new Datum(__typename, address, balance);
      }
    }
  }
}
