package com.example.cscihw9app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;


public class ProductFragment extends Fragment {

    String productDetails;
    private ImageLoader mImageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);
        mImageLoader = MySingleton.getInstance(getContext()).getImageLoader();
        getProductDetailsFromEbay();
        return rootView;
    }

    private void getProductDetailsFromEbay() {
        try {
            productDetails = ((ProductDetailsActivity) getActivity()).getProductDetails();
            final JSONObject jsonObj = new JSONObject(productDetails);
            System.out.println("http://csci571hw9app.us-west-2.elasticbeanstalk.com/getProductDetails?itemId="+jsonObj.getString("id"));
            ApiCall.makeJsonObjectRequest(getContext(), "http://csci571hw9app.us-west-2.elasticbeanstalk.com/getProductDetails?itemId="+jsonObj.getString("id"), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.print(response);
                    String TabOfFragmentB = ((ProductDetailsActivity)getActivity()).getShippingFragment();
                    ShippingFragment fragmentB = (ShippingFragment)getActivity().getSupportFragmentManager().findFragmentByTag(TabOfFragmentB);
                    fragmentB.upateShippingView(response.toString());
                    //Toast.makeText(getContext(), "Sent data to shipping", Toast.LENGTH_SHORT).show();

                    View productPage = getView();
                    LinearLayout mGallery = (LinearLayout) getView().findViewById(R.id.id_gallery);
                    LayoutInflater mInflater = LayoutInflater.from(getContext());
                    JSONObject item;
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            item = response.getJSONObject("item");
                            for (int i = 0; i < item.getJSONArray("Photos").length(); i++)
                            {
                                String photo = item.getJSONArray("Photos").getString(i);
                                View view = mInflater.inflate(R.layout.product_image_view,
                                        mGallery, false);
                                NetworkImageView img = (NetworkImageView) view
                                        .findViewById(R.id.productPicture);
                                img.setImageUrl(photo, mImageLoader);
                                mGallery.addView(view);
                            }

                            String shippingCost = jsonObj.getString("shipping");
                            if (!shippingCost.contains("Free"))
                                shippingCost = shippingCost + " Shipping";
                            String htmlList = "";
                            if (item.has("ItemSpecifics")) {
                                String innerList = "";
                                JSONObject itemSpecifics = item.getJSONObject("ItemSpecifics");
                                Iterator<String> keys = itemSpecifics.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    if (key.equals("Brand"))
                                        htmlList += "&#8226; " + itemSpecifics.get(key) + "<br/>";
                                    else
                                        innerList += "&#8226; " + itemSpecifics.get(key) + "<br/>";
                                }
                                htmlList += innerList;
                            }

                            ((TextView)productPage.findViewById(R.id.productName)).setText(Html.fromHtml("<font color='#000000'>"+item.getString("Title")+"</font>"));
                            ((TextView)productPage.findViewById(R.id.priceWithShipping)).setText(Html.fromHtml("<font color='#6313ae'><b>"+item.getString("Price")+"</b></font>" + " With " + shippingCost));
                            ((TextView)productPage.findViewById(R.id.subtitleValue)).setText(jsonObj.getString("subTitle"));
                            ((TextView)productPage.findViewById(R.id.priceValue)).setText(item.getString("Price"));
                            ((TextView)productPage.findViewById(R.id.brandValue)).setText(item.getString("Brand"));
                            ((TextView)productPage.findViewById(R.id.specificationValue)).setText(Html.fromHtml(htmlList));

                            if (jsonObj.getString("subTitle").equals("N/A")) {
                                productPage.findViewById(R.id.subtitleRow).setLayoutParams(new LinearLayout.LayoutParams(0,0));
                                productPage.findViewById(R.id.subtitleRow).setVisibility(View.GONE);
                            }

                            if (item.getString("Brand").equals("N/A")) {
                                productPage.findViewById(R.id.brandRow).setLayoutParams(new LinearLayout.LayoutParams(0,0));
                                productPage.findViewById(R.id.brandRow).setVisibility(View.GONE);
                            }

                            if (htmlList.equals("")) {
                                productPage.findViewById(R.id.specifications).setLayoutParams(new RelativeLayout.LayoutParams(0,0));
                                productPage.findViewById(R.id.specifications).setVisibility(View.GONE);
                            }

                            productPage.findViewById(R.id.productProgressBar).setVisibility(View.GONE);
                            productPage.findViewById(R.id.productProgressBarMessage).setVisibility(View.GONE);
                            productPage.findViewById(R.id.product_contents).setVisibility(View.VISIBLE);
                        } else {
                            productPage.findViewById(R.id.productProgressBar).setVisibility(View.GONE);
                            ((TextView)productPage.findViewById(R.id.productProgressBarMessage)).setText("No records");
                            productPage.findViewById(R.id.productProgressBarMessage).setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        System.out.println(e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error.getMessage());
                    getView().findViewById(R.id.productProgressBar).setVisibility(View.GONE);
                    ((TextView)getView().findViewById(R.id.productProgressBarMessage)).setText("No records");
                    getView().findViewById(R.id.productProgressBarMessage).setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            getView().findViewById(R.id.productProgressBar).setVisibility(View.GONE);
            ((TextView)getView().findViewById(R.id.productProgressBarMessage)).setText("No records");
            getView().findViewById(R.id.productProgressBarMessage).setVisibility(View.VISIBLE);
        }
    }
}
