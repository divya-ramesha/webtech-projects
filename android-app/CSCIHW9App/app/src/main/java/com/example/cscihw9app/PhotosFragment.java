package com.example.cscihw9app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PhotosFragment extends Fragment {

    String productDetails;
    private ImageLoader mImageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photos, container, false);
        productDetails = ((ProductDetailsActivity) getActivity()).getProductDetails();
        mImageLoader = MySingleton.getInstance(getContext()).getImageLoader();
        getProductPhotosFromGoogle();
        return rootView;
    }

    private void hideProgressBarWithNoPhotos() {
        getView().findViewById(R.id.photosProgressBar).setVisibility(View.GONE);
        ((TextView)getView().findViewById(R.id.photosProgressBarMessage)).setText("No records");
    }

    private void getProductPhotosFromGoogle() {
        try {
            JSONObject jsonObject = new JSONObject(productDetails);
            ApiCall.makeJsonObjectRequest(getActivity(), "http://csci571hw9app.us-west-2.elasticbeanstalk.com/getProductPhotos?product=" + jsonObject.getString("fullTitle"), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("status").equals("failure")) {
                            hideProgressBarWithNoPhotos();
                        } else {
                            JSONArray items = response.getJSONArray("items");
                            int[] viewIds={R.id.photo1,R.id.photo2,R.id.photo3,R.id.photo4,R.id.photo5,R.id.photo6,R.id.photo7,R.id.photo8};
                            for (int i=0; i<items.length(); i++) {
                                String photo = items.getJSONObject(i).getString("link");
                                NetworkImageView img = (NetworkImageView) getView().findViewById(viewIds[i]);
                                img.setImageUrl(photo, mImageLoader);
                            }
                            getView().findViewById(R.id.photosProgressBar).setVisibility(View.GONE);
                            getView().findViewById(R.id.photosProgressBarMessage).setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        hideProgressBarWithNoPhotos();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressBarWithNoPhotos();
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            hideProgressBarWithNoPhotos();
        }
    }
}
