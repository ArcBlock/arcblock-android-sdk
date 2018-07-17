package com.arcblock.corekit.bean;

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

public class PageData<T>{

	public Page page;

	public List<T> data;

	public @Nullable Page getPage() {
		return this.page;
	}

	public @Nullable List<T> getData() {
		return this.data;
	}

	public static class Page {
		static final ResponseField[] $responseFields = {
				ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
				ResponseField.forString("cursor", "cursor", null, false, Collections.<ResponseField.Condition>emptyList()),
				ResponseField.forBoolean("next", "next", null, false, Collections.<ResponseField.Condition>emptyList()),
				ResponseField.forInt("total", "total", null, true, Collections.<ResponseField.Condition>emptyList())
		};

		final @NotNull
		String __typename;

		final @NotNull String cursor;

		final boolean next;

		final @Nullable
		Integer total;

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
