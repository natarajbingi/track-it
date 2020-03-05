package com.a.goldtrack.ui.home;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.GetTransactionRes;
import com.a.goldtrack.R;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomHomeAdapter extends RecyclerView.Adapter<CustomHomeAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<GetTransactionRes.DataList> mDataSet;
    public RecycleItemClicked itemClicked;

    public CustomHomeAdapter(List<GetTransactionRes.DataList> dataSet) {
        mDataSet = dataSet;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView, text_sub, text_date;
        private final ImageView logo_id;

        public ViewHolder(View v) {
            super(v);
            logo_id = (ImageView) v.findViewById(R.id.logo_id);
            textView = (TextView) v.findViewById(R.id.text_header);
            text_date = (TextView) v.findViewById(R.id.text_date);
            text_sub = (TextView) v.findViewById(R.id.text_sub);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
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
        Log.d(TAG, "Element " + position + " set.");
        viewHolder.textView.setText(mDataSet.get(position).customerName);
        viewHolder.text_date.setText("Bill No: " + mDataSet.get(position).billNumber);
        viewHolder.text_sub.setText("Commodity: " + mDataSet.get(position).commodity + "\t\tTotal Amt:" + mDataSet.get(position).totalAmount);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
