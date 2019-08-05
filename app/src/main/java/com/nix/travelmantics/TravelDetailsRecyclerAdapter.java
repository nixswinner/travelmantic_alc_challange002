package com.nix.travelmantics;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nix.travelmantics.model.TravelDetails;

import java.util.List;

public class TravelDetailsRecyclerAdapter
        extends RecyclerView.Adapter<TravelDetailsRecyclerAdapter.ViewHolder> {
    private List<TravelDetails> mValues;
    Context context;
    public TravelDetailsRecyclerAdapter(List<TravelDetails> items,
                                        Context ctx) {
        mValues = items;
        context = ctx;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.travel_details_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TravelDetails result = mValues.get(position);
        holder.txtname.setText(result.getName());
        holder.txtdesc.setText(result.getDesc());
        holder.txtcost.setText(result.getCost());
        Glide.with(context)
                .load(result.getImage_url())
                .into(holder.imgViewPlace);
        Log.e("Image Url", "onBindViewHolder: "+result.getImage_url() );

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView txtname,txtcost,txtdesc;
        ImageView imgViewPlace;
        public TravelDetails mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtname = view.findViewById(R.id.txt_name);
            txtcost = view.findViewById(R.id.txt_cost);
            txtdesc = view.findViewById(R.id.txt_desc);
            imgViewPlace = view.findViewById(R.id.img_place);
        }

    }
}
