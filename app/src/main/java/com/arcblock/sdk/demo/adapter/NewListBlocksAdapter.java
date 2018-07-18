package com.arcblock.sdk.demo.adapter;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arcblock.sdk.demo.BlocksByHeightQuery;
import com.arcblock.sdk.demo.R;

public class NewListBlocksAdapter extends PagedListAdapter<BlocksByHeightQuery.Datum, NewListBlocksAdapter.ListBlocksViewHolder> {


	private static DiffUtil.ItemCallback<BlocksByHeightQuery.Datum> DIFF_CALLBACK = new DiffUtil.ItemCallback<BlocksByHeightQuery.Datum>() {
		@Override
		public boolean areItemsTheSame(BlocksByHeightQuery.Datum oldItem, BlocksByHeightQuery.Datum newItem) {
			return TextUtils.equals(oldItem.getHash(), newItem.getHash());
		}

		@Override
		public boolean areContentsTheSame(BlocksByHeightQuery.Datum oldItem, BlocksByHeightQuery.Datum newItem) {
			return oldItem.equals(newItem);
		}
	};

	public NewListBlocksAdapter() {
		super(DIFF_CALLBACK);
	}

	@NonNull
	@Override
	public ListBlocksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_list_blocks, parent, false);
		return new ListBlocksViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ListBlocksViewHolder holder, int position) {
		// getItem() should be used with ListAdapter
		BlocksByHeightQuery.Datum item = getItem(position);

		// null placeholders if the PagedList is configured to use them
		// only works for data sets that have total count provided (i.e. PositionalDataSource)
		if (item == null) {
			return;
		}
		holder.hash_tv.setText(item.getHash());
		holder.txs_tv.setText("" + item.getNumberTxs());
		holder.height_tv.setText("" + item.getHeight());
	}

	public static class ListBlocksViewHolder extends RecyclerView.ViewHolder {

		public TextView hash_tv;
		public TextView txs_tv;
		public TextView height_tv;

		public ListBlocksViewHolder(View itemView) {
			super(itemView);
			hash_tv = itemView.findViewById(R.id.hash_tv);
			txs_tv = itemView.findViewById(R.id.txs_tv);
			height_tv = itemView.findViewById(R.id.height_tv);
		}
	}

}
