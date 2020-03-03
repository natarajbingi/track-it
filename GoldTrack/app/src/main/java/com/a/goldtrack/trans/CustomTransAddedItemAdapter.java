package com.a.goldtrack.trans;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.Model.ItemsTrans;
import com.a.goldtrack.R;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomTransAddedItemAdapter extends RecyclerView.Adapter<CustomTransAddedItemAdapter.ViewHolder> {
    private static final String TAG = "CustomCustomersAdapter";

    private List<ItemsTrans> mDataSet;
    RecycleItemClicked companyClicked;

    public CustomTransAddedItemAdapter(List<ItemsTrans> dataSet) {
        mDataSet = dataSet;
    }

    public void updateListNew(List<ItemsTrans> mDataset) {
        this.mDataSet = (mDataset);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView commodity_name, commodity_amount, commodity_weight, stone_wastage, nett_weight, other_wastage
                , purity, margin;
        private final LinearLayout hidden_layout;

        public ViewHolder(View v) {
            super(v);
            final boolean[] show = {true};
            ImageView edit_click = (ImageView) v.findViewById(R.id.edit_click);
            ImageView down_click = (ImageView) v.findViewById(R.id.down_click);
            commodity_name = (TextView) v.findViewById(R.id.commodity_name);
            commodity_amount = (TextView) v.findViewById(R.id.commodity_amount);
            commodity_weight = (TextView) v.findViewById(R.id.commodity_weight);
            stone_wastage = (TextView) v.findViewById(R.id.stone_wastage);
            nett_weight = (TextView) v.findViewById(R.id.nett_weight);
            other_wastage = (TextView) v.findViewById(R.id.other_wastage);
            margin = (TextView) v.findViewById(R.id.margin);
            purity = (TextView) v.findViewById(R.id.purity);
            hidden_layout = (LinearLayout) v.findViewById(R.id.hidden_layout);

            edit_click.setOnClickListener(this);
            down_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (show[0]) {
                        hidden_layout.setVisibility(View.VISIBLE);
                    } else {
                        hidden_layout.setVisibility(View.GONE);
                    }
                    show[0] = !show[0];
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (companyClicked != null) {
                companyClicked.oncItemClicked(view, getAdapterPosition());
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_trans_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        viewHolder.commodity_name.setText(mDataSet.get(position).commodity);
        viewHolder.commodity_amount.setText("Rs. " + mDataSet.get(position).amount);
        viewHolder.commodity_weight.setText("Commodity Wt: " + mDataSet.get(position).commodityWeight + " Grms");
        viewHolder.nett_weight.setText("Net Wt: " + mDataSet.get(position).nettWeight);
        viewHolder.stone_wastage.setText("Stone Wt: " + mDataSet.get(position).stoneWastage);
        viewHolder.other_wastage.setText("Other Wt: " + mDataSet.get(position).otherWastage);
        viewHolder.purity.setText("Purity: " + mDataSet.get(position).purity);
        viewHolder.margin.setText("Margin: " + mDataSet.get(position).margin);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    // allows clicks events to be caught
    public void setClickListener(RecycleItemClicked companyClicked) {
        this.companyClicked = companyClicked;
    }


}
