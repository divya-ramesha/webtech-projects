package com.example.cscihw9app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SimilarItemAdapter extends RecyclerView.Adapter<SimilarItemAdapter.SimilarItemViewHolder> {

    private ArrayList<SimilarItem> dataList;
    private ImageLoader mImageLoader;
    public Context similarContext;

    public SimilarItemAdapter(ArrayList<SimilarItem> dataList, Context mCOntext) {
        this.dataList = dataList;
        mImageLoader = MySingleton.getInstance(mCOntext).getImageLoader();
        similarContext = mCOntext;
    }

    @Override
    public SimilarItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_similar_item, parent, false);
        return new SimilarItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimilarItemViewHolder holder, int position) {
        final SimilarItem similarItem = dataList.get(position);
        holder.title.setText(similarItem.getTitle());
        String shippingVal = similarItem.getShipping().toString();
        if (similarItem.getShipping() < 1) {
            shippingVal = "Free Shipping";
        }
        holder.shipping.setText(shippingVal);
        holder.daysLeft.setText(similarItem.getDaysLeft().toString() + " Days Left");
        holder.price.setText(Html.fromHtml("<font color='#6313ae'>$" + similarItem.getPrice().toString() + "</font>"));
        holder.photo.setImageUrl(similarItem.getImage(), mImageLoader);
        holder.item = similarItem;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Toast.makeText(similarContext, "Opening " + similarItem.getTitle(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(similarItem.getItemURL()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.android.chrome");
                    try {
                        similarContext.startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        intent.setPackage(null);
                        similarContext.startActivity(intent);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void sortSimilarItems(String similarOrder, String similarDir, View similarView) {
        ((Spinner)similarView.findViewById(R.id.orderDirection)).setEnabled(true);
        switch (similarOrder) {
            case "Default":
                ((Spinner)similarView.findViewById(R.id.orderDirection)).setEnabled(false);
                Collections.sort(dataList, new Comparator<SimilarItem>() {
                    @Override
                    public int compare(SimilarItem lhs, SimilarItem rhs) {
                        return lhs.getShipping().compareTo(rhs.getShipping());
                    }
                });
                this.notifyDataSetChanged();
                break;
            case "Name":
                Collections.sort(dataList, new Comparator<SimilarItem>() {
                    @Override
                    public int compare(SimilarItem lhs, SimilarItem rhs) {
                        return lhs.getTitle().compareTo(rhs.getTitle());
                    }
                });
                if (similarDir.equals("Descending"))
                    Collections.reverse(dataList);
                this.notifyDataSetChanged();
                break;
            case "Price":
                Collections.sort(dataList, new Comparator<SimilarItem>() {
                    @Override
                    public int compare(SimilarItem lhs, SimilarItem rhs) {
                        return lhs.getPrice().compareTo(rhs.getPrice());
                    }
                });
                if (similarDir.equals("Descending"))
                    Collections.reverse(dataList);
                this.notifyDataSetChanged();
                break;
            case "Days":
                Collections.sort(dataList, new Comparator<SimilarItem>() {
                    @Override
                    public int compare(SimilarItem lhs, SimilarItem rhs) {
                        return lhs.getDaysLeft().compareTo(rhs.getDaysLeft());
                    }
                });
                if (similarDir.equals("Descending"))
                    Collections.reverse(dataList);
                this.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    class SimilarItemViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView shipping;
        public TextView daysLeft;
        public TextView price;
        public NetworkImageView photo;
        public SimilarItem item;

        SimilarItemViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.similarTitle);
            shipping = (TextView) itemView.findViewById(R.id.similarShipping);
            daysLeft = (TextView) itemView.findViewById(R.id.similarDaysLeft);
            price = (TextView) itemView.findViewById(R.id.similarPrice);
            photo = (NetworkImageView) itemView.findViewById(R.id.similarPhoto);
        }
    }
}
