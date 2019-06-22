package com.example.cscihw9app;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class HomeFragment extends Fragment implements Response.Listener<JSONObject> , Response.ErrorListener {

    View view;
    RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context context;
    private List<Model> contactList  = new ArrayList<>();
    private String url = "";
    Button btn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment,container,false);
        context = getContext();
        mLayoutManager = new GridLayoutManager(context,2);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getEbayProductsList();
        return view;
    }

    private void getEbayProductsList(){
        try {
            url = ((SearchResultsActivity) getActivity()).getProductListsUrl();
        } catch (Exception e) {
            url = "http://csci571hw9app.us-west-2.elasticbeanstalk.com/getProductsList?keyword=iphone&distance=10&zipcode=90007";
        }
        System.out.println("********************************************************************");
        System.out.println(url);
        ApiCall.makeJsonObjectRequest(getContext(), url, this, this);
        //Controller.getInstance(context).makeNetworkCalls(Request.Method.GET,url,this,this);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        System.out.println(error.getMessage());
        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonResponse) {
        if (jsonResponse == null) {
            Toast.makeText(context, "Couldn't fetch the response! Pleas try again.", Toast.LENGTH_LONG).show();
            return;
        }
        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
        getActivity().findViewById(R.id.progressBarMessage).setVisibility(View.GONE);
        try {
            if (jsonResponse.getString("status").equals("failure")) {
                ((TextView) getActivity().findViewById(R.id.progressBarMessage)).setText("No Records");
                getActivity().findViewById(R.id.progressBarMessage).setVisibility(View.VISIBLE);
            } else {
                List<Model> items = new Gson().fromJson(jsonResponse.getJSONArray("products").toString(), new TypeToken<List<Model>>(){}.getType());
                contactList.addAll(items);
                String keyword = ((SearchResultsActivity) getActivity()).getSearchKeyword();
                if (keyword == null || keyword.equals(""))
                    keyword = "iphone";
                String first = "Showing <font color='#d11111'>" + items.size() + "</font> results for ";
                first += "<font color='#d11111'>" + keyword + "</font>";
                ((TextView) getActivity().findViewById(R.id.resultsPageTitle)).setText(Html.fromHtml(first));
                getActivity().findViewById(R.id.resultsPageTitle).setVisibility(View.VISIBLE);
                MyAdapter rcAdapter = new MyAdapter(contactList, context);
                mRecyclerView.setAdapter(rcAdapter);
            }
        } catch (JSONException e) {
        }
    }
}
