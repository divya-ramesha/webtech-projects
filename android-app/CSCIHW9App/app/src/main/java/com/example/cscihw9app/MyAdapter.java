package com.example.cscihw9app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Model> mDataset;
    private ImageLoader mImageLoader;
    private Context parentContext;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView nameText;
        public TextView zipcodeText;
        public TextView shippingText;
        public TextView conditionText;
        public TextView priceText;
        public ImageView wishlistIcon;
        public NetworkImageView image;
        public Context parentContext;
        public Model productModel;

        public void setProductModel(Model m) {
            productModel = m;
        }

        @SuppressLint("WrongViewCast")
        public ViewHolder(View v, Context parentContxt) {
            super(v);
            nameText = v.findViewById(R.id.name_text);
            zipcodeText = v.findViewById(R.id.zipcode_text);
            shippingText = v.findViewById(R.id.shipping_text);
            conditionText = v.findViewById(R.id.condition_text);
            priceText = v.findViewById(R.id.price_text);
            wishlistIcon = v.findViewById(R.id.wishlistIcon);
            image = v.findViewById(R.id.imgAvatar);
            parentContext = parentContxt;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productPage = new Intent(parentContext, ProductDetailsActivity.class);
                    productPage.putExtra("product", new Gson().toJson(productModel));
                    parentContext.startActivity(productPage);
                }
            });

            wishlistIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = nameText.getText().toString();
                    if (!Wishlist.checkIfFavoriteExist(productModel)) {
                        Toast.makeText(v.getContext(), title + " was added to wishlist", Toast.LENGTH_SHORT).show();
                        wishlistIcon.setColorFilter(v.getContext().getResources().getColor(R.color.orange));
                        wishlistIcon.setImageResource(R.drawable.cart_remove);
                        Wishlist.setFavorite(productModel);
                    } else {
                        Toast.makeText(v.getContext(), title + " was removed from wishlist", Toast.LENGTH_SHORT).show();
                        wishlistIcon.setColorFilter(v.getContext().getResources().getColor(R.color.gray));
                        wishlistIcon.setImageResource(R.drawable.cart_plus);
                        Wishlist.removeFavorite(productModel);
                    }
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Model> myDataset, Context mCOntext) {
        mDataset = myDataset;
        mImageLoader = MySingleton.getInstance(mCOntext).getImageLoader();
        parentContext = mCOntext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v, parentContext);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset.get(position).getName());
        Model m = mDataset.get(position);
        holder.nameText.setText(Html.fromHtml("<font color='#000000'>"+m.getTitle().toUpperCase()+"</font>"));
        holder.zipcodeText.setText("Zip: " + m.getZipcode());
        holder.shippingText.setText(m.getShipping());
        String condition = m.getCondition();
        if (condition.length() > 13) {
            condition = condition.substring(0, 13) + "...";
        }
        holder.conditionText.setText(condition);
        holder.priceText.setText(Html.fromHtml("<font color='#6313ae'>" + m.getPrice() + "</font>"));
        holder.image.setImageUrl(m.getPhoto(),mImageLoader);
        if (Wishlist.checkIfFavoriteExist(m)) {
            holder.wishlistIcon.setColorFilter(parentContext.getResources().getColor(R.color.orange));
            holder.wishlistIcon.setImageResource(R.drawable.cart_remove);
        }
        holder.setProductModel(m);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
