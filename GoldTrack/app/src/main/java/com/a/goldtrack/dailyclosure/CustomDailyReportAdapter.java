package com.a.goldtrack.dailyclosure;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.a.goldtrack.Interfaces.RecycleItemClicked;
import com.a.goldtrack.Model.GetUserDailyClosureRes;
import com.a.goldtrack.R;
import com.a.goldtrack.utils.Constants;

import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomDailyReportAdapter extends RecyclerView.Adapter<CustomDailyReportAdapter.ViewHolder> {
    private static final String TAG = "CustomDailyRAdapter";

    private List<GetUserDailyClosureRes.DataList> mDataSet;
    RecycleItemClicked companyClicked;
    Context context;

    public CustomDailyReportAdapter(Context context, List<GetUserDailyClosureRes.DataList> dataSet) {
        mDataSet = dataSet;
        this.context = context;
    }

    public void updateListNew(List<GetUserDailyClosureRes.DataList> mDataset) {
        this.mDataSet = (mDataset);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView slNo, name, cashPaid, noOfBills,expenses, grossWt, netWt, TotalBill, ClBal;
        private final RelativeLayout parent_tile_bg;

        public ViewHolder(View v) {
            super(v);
            parent_tile_bg = (RelativeLayout) v.findViewById(R.id.parent_tile_bg);
            slNo = (TextView) v.findViewById(R.id.slNo);
            name = (TextView) v.findViewById(R.id.name);
            cashPaid = (TextView) v.findViewById(R.id.cashPaid);
            noOfBills = (TextView) v.findViewById(R.id.noOfBills);
            expenses = (TextView) v.findViewById(R.id.expenses);
            grossWt = (TextView) v.findViewById(R.id.grossWt);
            netWt = (TextView) v.findViewById(R.id.netWt);
            TotalBill = (TextView) v.findViewById(R.id.TotalBill);
            ClBal = (TextView) v.findViewById(R.id.ClBal);

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
                .inflate(R.layout.report_list_text, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Log.d(TAG, "Element " + position + " set.");
        if (position % 2 == 1) {
            viewHolder.parent_tile_bg.setBackgroundColor(context.getResources().getColor(R.color.light_me));
        } else {
            viewHolder.parent_tile_bg.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        viewHolder.slNo.setText((position + 1) + "");
        viewHolder.name.setText(mDataSet.get(position).userName);
        viewHolder.cashPaid.setText(mDataSet.get(position).fundRecieved);
        viewHolder.expenses.setText(mDataSet.get(position).expenses);

        if (mDataSet.get(position).transactionsForday != null && mDataSet.get(position).transactionsForday.size() > 0) {
            viewHolder.noOfBills.setText(mDataSet.get(position).transactionsForday.size() + "");
            String[] strAmts = Constants.getTotalGross(mDataSet.get(position).transactionsForday).split("_");
            viewHolder.grossWt.setText(strAmts[0]);   // totalNettWeight
            viewHolder.netWt.setText(strAmts[1]);     // totalStoneOtherWastage
            viewHolder.TotalBill.setText(strAmts[2]); // grossAmount
            viewHolder.ClBal.setText(strAmts[3]);     // nettAmount
        } else {
            viewHolder.noOfBills.setText("0");
            viewHolder.grossWt.setText("0");   // totalNettWeight
            viewHolder.netWt.setText("0");     // totalStoneOtherWastage
            viewHolder.TotalBill.setText("0"); // grossAmount
            viewHolder.ClBal.setText("0");     // nettAmount
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
