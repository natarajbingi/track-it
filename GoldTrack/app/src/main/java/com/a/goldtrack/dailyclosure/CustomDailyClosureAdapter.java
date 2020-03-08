package com.a.goldtrack.dailyclosure;


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
import com.a.goldtrack.Model.GetItemsRes;
import com.a.goldtrack.Model.GetUserDailyClosureRes;
import com.a.goldtrack.R;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomDailyClosureAdapter extends RecyclerView.Adapter<CustomDailyClosureAdapter.ViewHolder> {
    private static final String TAG = "CustomDailyCAdapter";

    private List<GetUserDailyClosureRes.DataList> mDataSet;
    RecycleItemClicked companyClicked;
    Context context;

    public CustomDailyClosureAdapter(Context context, List<GetUserDailyClosureRes.DataList> dataSet) {
        mDataSet = dataSet;
        this.context = context;
    }

    public void updateListNew(List<GetUserDailyClosureRes.DataList> mDataset) {
        this.mDataSet.clear();
        this.mDataSet.addAll(mDataset);
        this.notifyDataSetChanged();
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
        if (position % 2 == 1) {
            viewHolder.parent_tile_bg.setBackgroundColor(context.getResources().getColor(R.color.light_me));
        } else {
            viewHolder.parent_tile_bg.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        viewHolder.textView.setText(mDataSet.get(position).userName);
        viewHolder.text_sub.setText("Fund Recieved: " +mDataSet.get(position).fundRecieved +"\t\tCash In Hand: " +mDataSet.get(position).cashInHand + "\nExpenses: " + mDataSet.get(position).expenses);
        viewHolder.text_date.setText(mDataSet.get(position).date);
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
