package com.example.cscihw9app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SimilarFragment extends Fragment {

    String productDetails;
    private RecyclerView recyclerView;
    private SimilarItemAdapter adapter;
    private ArrayList<SimilarItem> similarArrayList = new ArrayList<SimilarItem>();
    String similarOrder = "Default";
    String similarDir = "Ascending";
    String[] orderNames;
    String[] orderDirections;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_similar, container, false);
        productDetails = ((ProductDetailsActivity) getActivity()).getProductDetails();
        getDataAndCreateList(rootView);
        orderNames = getResources().getStringArray(R.array.orderNames);
        orderDirections = getResources().getStringArray(R.array.orderDirections);
        ((Spinner)rootView.findViewById(R.id.orderName)).setEnabled(false);
        ((Spinner)rootView.findViewById(R.id.orderDirection)).setEnabled(false);
        Spinner orderNameSpinner = (Spinner)rootView.findViewById(R.id.orderName);

        orderNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int index = adapterView.getSelectedItemPosition();
                similarOrder = orderNames[index];
                sortSimilarItems(rootView);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        Spinner orderDirSpinner = (Spinner)rootView.findViewById(R.id.orderDirection);

        orderDirSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int index = adapterView.getSelectedItemPosition();
                similarDir = orderDirections[index];
                sortSimilarItems(rootView);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        return rootView;
    }

    private void sortSimilarItems(View view) {
        if (adapter != null)
            adapter.sortSimilarItems(similarOrder, similarDir, view);
    }

    private void hideProgressBarWithNoItems() {
        ((Spinner)getView().findViewById(R.id.orderName)).setEnabled(false);
        ((Spinner)getView().findViewById(R.id.orderDirection)).setEnabled(false);
        getView().findViewById(R.id.similarprogressBar).setVisibility(View.GONE);
        ((TextView)getView().findViewById(R.id.similarprogressBarMessage)).setText("No records");
    }


    private void getDataAndCreateList(final View similarView) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(productDetails);
            ApiCall.makeJsonObjectRequest(getContext(), "http://csci571hw9app.us-west-2.elasticbeanstalk.com/getSimilarProducts?itemId=" + jsonObject.getString("id"), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    //System.out.println(response);
                    try {
                        if (response.getString("status").equals("success")) {
                            List<SimilarItem> items = new Gson().fromJson(response.getJSONArray("output").toString(), new TypeToken<List<SimilarItem>>(){}.getType());
                            similarArrayList.addAll(items);
                            recyclerView = (RecyclerView) similarView.findViewById(R.id.recycler_view);
                            ((Spinner)similarView.findViewById(R.id.orderName)).setEnabled(true);
                            Collections.sort(similarArrayList, new Comparator<SimilarItem>() {
                                @Override
                                public int compare(SimilarItem lhs, SimilarItem rhs) {
                                    return lhs.getShipping().compareTo(rhs.getShipping());
                                }
                            });
                            adapter = new SimilarItemAdapter(similarArrayList, getContext());
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);
                            getView().findViewById(R.id.similarprogressBar).setVisibility(View.GONE);
                            getView().findViewById(R.id.similarprogressBarMessage).setVisibility(View.GONE);
                        } else {
                            hideProgressBarWithNoItems();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        hideProgressBarWithNoItems();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgressBarWithNoItems();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            hideProgressBarWithNoItems();
        }
    }
}
