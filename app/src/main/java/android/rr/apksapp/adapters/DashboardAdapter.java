package android.rr.apksapp.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import android.rr.apksapp.Dashboard;
import android.rr.apksapp.R;
import android.rr.apksapp.models.AppsApkDetails;
import android.rr.apksapp.utils.GlideCircleTransform;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private Context context;
    private ArrayList<AppsApkDetails> appsApkDetails;
    private boolean isLongClicked = false;
    private final String TAG = DashboardAdapter.class.getSimpleName();

    public DashboardAdapter(Context context, ArrayList<AppsApkDetails> appsApkDetails) {
        this.context = context;
        this.appsApkDetails = appsApkDetails;
    }

    public void updateAdapter(Context context, ArrayList<AppsApkDetails> appsApkDetails) {
        this.context = context;
        this.appsApkDetails = appsApkDetails;

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.for_dashboard_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return appsApkDetails.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {
        CardView cardView;
        TextView appNameTV;
        ImageView appIconIV;
        int clickedPosition;

        private ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            appNameTV = itemView.findViewById(R.id.appNameTV);
            appIconIV = itemView.findViewById(R.id.appIconIV);
        }

        public void bindData(int position) {
            this.clickedPosition = position;
            Glide.with(context).load("").placeholder(appsApkDetails.get(position)
                    .getDrawable()).transform(new GlideCircleTransform(context)).
                    into(appIconIV);
            appNameTV.setText(appsApkDetails.get(position).getAppName());

            if (appsApkDetails.get(position).getIsSelected()) {
                itemView.setBackgroundColor(context.getResources().
                        getColor(R.color.cardSelected));
            } else {
                itemView.setBackgroundColor(context.getResources().
                        getColor(R.color.cardUnSelected));
            }

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick(), clickedPosition: " + clickedPosition +
                    ", isLongClicked: " + isLongClicked);

            if (isLongClicked) {
                if (appsApkDetails.get(clickedPosition).getIsSelected()) {
                    appsApkDetails.get(clickedPosition).setIsSelected(false);
                    itemView.setBackgroundColor(context.getResources().
                            getColor(R.color.cardUnSelected));
                } else {
                    appsApkDetails.get(clickedPosition).setIsSelected(true);
                    itemView.setBackgroundColor(context.getResources().
                            getColor(R.color.cardSelected));
                }
            }

            if (0 == getSelectedItemsCount()) {
                isLongClicked = false;
                ((Dashboard) context).showOrHideBottomBar(false);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Log.d(TAG, "onLongClick(), clickedPosition: " + clickedPosition +
                    ", isLongClicked: " + isLongClicked);

            if (!isLongClicked) {
                isLongClicked = true;
                ((Dashboard) context).showOrHideBottomBar(true);

                appsApkDetails.get(clickedPosition).setIsSelected(true);
                itemView.setBackgroundColor(context.getResources().
                        getColor(R.color.cardSelected));
            }

            return true;
        }
    }

    private int getSelectedItemsCount() {
        int selectedItemsCount = 0;
        for (int i = 0; i < appsApkDetails.size(); i++) {
            if (appsApkDetails.get(i).getIsSelected()) {
                selectedItemsCount++;
            }
        }

        Log.d(TAG, "getSelectedItemsCount(), selectedItemsCount: " + selectedItemsCount);
        return selectedItemsCount;
    }

    public ArrayList<AppsApkDetails> getTheSelectedAppsList() {
        ArrayList<AppsApkDetails> arrayList = new ArrayList<>();
        arrayList.addAll(appsApkDetails);

        for (int i = 0; i < arrayList.size(); i++) {
            if (!arrayList.get(i).getIsSelected()) {
                arrayList.remove(i);
                i--;
            }
        }

        return arrayList;
    }

}