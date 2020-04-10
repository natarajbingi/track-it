package com.a.goldtrack.customer;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.DropdownDataForCompanyRes;
import com.a.goldtrack.Model.GetCustomerRes;
import com.a.goldtrack.Model.GetUserForCompanyRes;
import com.a.goldtrack.R;
import com.a.goldtrack.utils.Constants;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomCustomersAdapter extends RecyclerView.Adapter<CustomCustomersAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "CustomCustomersAdapter";

    private List<DropdownDataForCompanyRes.CustomerList> mDataSet;
    RecycleItemClicked companyClicked;
    Context context;

    public CustomCustomersAdapter(Context context, List<DropdownDataForCompanyRes.CustomerList> dataSet) {
        mDataSet = dataSet;
        this.context = context;
    }

    public void updateListNew(List<DropdownDataForCompanyRes.CustomerList> mDataset) {
        this.mDataSet = (mDataset);
        this.notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return null;
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
      //  Log.d(TAG, "Element " + position + " set.");
        if (position % 2 == 1) {
            viewHolder.parent_tile_bg.setBackgroundColor(context.getResources().getColor(R.color.light_me));
        } else {
            viewHolder.parent_tile_bg.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        String str = "";
        if (mDataSet.get(position).uploadedImages == null || mDataSet.get(position).uploadedImages.size() == 0) {
            viewHolder.textView.setTextColor(context.getResources().getColor(R.color.button_red));
            str += "\nIncomplete KYC";
        } else {
            viewHolder.textView.setTextColor(context.getResources().getColor(R.color.oil));
        }
        viewHolder.textView.setText(mDataSet.get(position).firstName + " " + mDataSet.get(position).lastName);
        viewHolder.text_sub.setText("Mobile No: " + mDataSet.get(position).mobileNum
                + "\nEmail: " + mDataSet.get(position).emailId + str);
        viewHolder.text_date.setText("Unique ID: " + mDataSet.get(position).uniqueId);
        viewHolder.logo_id.setVisibility(View.VISIBLE);
        Constants.setGilde(mDataSet.get(position).profile_pic_url, viewHolder.logo_id);

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
