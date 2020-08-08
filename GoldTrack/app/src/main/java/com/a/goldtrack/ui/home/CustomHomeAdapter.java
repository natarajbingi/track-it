package com.a.goldtrack.ui.home;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.R;
import com.a.goldtrack.utils.Constants;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomHomeAdapter extends RecyclerView.Adapter<CustomHomeAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<GetTransactionRes.DataList> mDataSet;
    public RecycleItemClicked itemClicked;
    Context context;

    public CustomHomeAdapter(Context context, List<GetTransactionRes.DataList> dataSet) {
        mDataSet = dataSet;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView, text_sub, text_date;
        private final ImageView logo_id;
        private final RelativeLayout parent_tile_bg;

        public ViewHolder(View v) {
            super(v);
            parent_tile_bg = (RelativeLayout) v.findViewById(R.id.parent_tile_bg);
            logo_id = (ImageView) v.findViewById(R.id.logo_id);
            textView = (TextView) v.findViewById(R.id.text_header);
            text_date = (TextView) v.findViewById(R.id.text_date);
            text_sub = (TextView) v.findViewById(R.id.text_sub);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("test", "" + getAdapterPosition());
            if (itemClicked != null)
                itemClicked.oncItemClicked(v, getAdapterPosition());
        }
    }

    public void setItemClicked(RecycleItemClicked itemClicked) {
        this.itemClicked = itemClicked;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (position % 2 == 1) {
            viewHolder.parent_tile_bg.setBackgroundColor(context.getResources().getColor(R.color.light_me));
        } else {
            viewHolder.parent_tile_bg.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        // Log.d(TAG, "Element " + position + " set.");
        viewHolder.textView.setText(mDataSet.get(position).customerName);
        viewHolder.text_date.setText("Date: " + Constants.getMiliToDateyyyymmmdd(mDataSet.get(position).createdDt)
                + "\nBill No: " + mDataSet.get(position).billNumber);
        String str = "";
        if (mDataSet.get(position).amountPayable.contains("-")) {
            str += "\nPayable Amt: " + Constants.priceToString(mDataSet.get(position).amountPayable);
            viewHolder.text_sub.setTextColor(context.getResources().getColor(R.color.button_red));
           // Log.d("bingiRed", mDataSet.get(position).amountPayable + " : " + position);
        } else {
            viewHolder.text_sub.setTextColor(context.getResources().getColor(R.color.oil));
        }

        viewHolder.text_sub.setText("Commodity: " + mDataSet.get(position).commodity
                + "\t\t\tTotal Amt: " + Constants.priceToString(mDataSet.get(position).totalAmount) + str);

    }

    public void updateListNew(List<GetTransactionRes.DataList> mDataset) {
        this.mDataSet = (mDataset);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
