package com.a.goldtrack.company;


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
import com.a.goldtrack.Model.GetCompanyRes;
import com.a.goldtrack.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomCompanyAdapter extends RecyclerView.Adapter<CustomCompanyAdapter.ViewHolder> {
    private static final String TAG = "CustomCompanyAdapter";

    private List<GetCompanyRes.ResList> mDataSet;
    RecycleItemClicked companyClicked;
    Context context;

    public CustomCompanyAdapter(Context context, List<GetCompanyRes.ResList> dataSet) {
        mDataSet = dataSet;
        this.context = context;
    }

    public void updateListNew(List<GetCompanyRes.ResList> mDataset) {
        this.mDataSet = (mDataset);
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
        if (position % 2 == 1) {
            viewHolder.parent_tile_bg.setBackgroundColor(context.getResources().getColor(R.color.light_me));
        } else {
            viewHolder.parent_tile_bg.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        //Log.d(TAG, "Element " + position + " set.");
        viewHolder.textView.setText(mDataSet.get(position).name);
        viewHolder.text_sub.setText(mDataSet.get(position).desc);
        viewHolder.text_date.setText("");
        viewHolder.logo_id.setVisibility(View.VISIBLE);

        if (mDataSet.get(position).logoImagePath != null && !mDataSet.get(position).logoImagePath.isEmpty()) {
            Picasso.get()
                    .load(mDataSet.get(position).logoImagePath)
                    .fit()
                    .centerCrop()
                    .into(viewHolder.logo_id);
        }
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
