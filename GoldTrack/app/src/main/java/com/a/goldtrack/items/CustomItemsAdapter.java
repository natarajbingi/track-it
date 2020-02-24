package com.a.goldtrack.items;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.Model.GetItemsRes;
import com.a.goldtrack.R;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomItemsAdapter extends RecyclerView.Adapter<CustomItemsAdapter.ViewHolder> {
    private static final String TAG = "CustomItemsAdapter";

    private List<GetItemsRes.ResList> mDataSet;
    RecycleItemClicked companyClicked;

    public CustomItemsAdapter(List<GetItemsRes.ResList> dataSet) {
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
        public void onClick(View view) {
            if (companyClicked != null) {
                companyClicked.oncItemClicked(view, getAdapterPosition());
            }
        }
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
        viewHolder.textView.setText(mDataSet.get(position).itemName);
        viewHolder.text_sub.setText(mDataSet.get(position).itemDesc + "\nCommodity: " + mDataSet.get(position).commodity);
        viewHolder.text_date.setText("");
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
