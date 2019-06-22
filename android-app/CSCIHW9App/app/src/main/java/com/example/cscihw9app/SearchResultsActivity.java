package com.example.cscihw9app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarResults);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        frameLayout = findViewById(R.id.frame);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.frame, new HomeFragment()).commit();
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");
    }

    public String getSearchKeyword() {
        return keyword;
    }

    public String getProductListsUrl() throws UnsupportedEncodingException {
        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");
        String category = intent.getStringExtra("category");
        Boolean New = Boolean.valueOf(intent.getStringExtra("New"));
        Boolean Used = Boolean.valueOf(intent.getStringExtra("Used"));
        Boolean Unspecified = Boolean.valueOf(intent.getStringExtra("Unspecified"));
        Boolean localPickupOnly = Boolean.valueOf(intent.getStringExtra("LocalPickupOnly"));
        Boolean freeShipping = Boolean.valueOf(intent.getStringExtra("FreeShipping"));
        String milesFrom = intent.getStringExtra("milesFrom");
        String zipcode = intent.getStringExtra("zipcode");
        String ebayUrl = "http://csci571hw9app.us-west-2.elasticbeanstalk.com/getProductsList?keyword=" + URLEncoder.encode(keyword, "UTF-8");
        if (!category.equals("")) {
            ebayUrl += "&category=" + category;
        }
        if (New || Used || Unspecified) {
            ebayUrl += "&condition=";
            List<String> condition = new ArrayList<String>();
            if (New)
                condition.add("New");
            if (Used)
                condition.add("Used");
            if (Unspecified)
                condition.add("Unspecified");
            ebayUrl += android.text.TextUtils.join(",", condition);
        }
        if (localPickupOnly || freeShipping) {
            ebayUrl += "&shipping=";
            List<String> shipping = new ArrayList<String>();
            if (localPickupOnly)
                shipping.add("LocalPickupOnly");
            if (freeShipping)
                shipping.add("FreeShippingOnly");
            ebayUrl += android.text.TextUtils.join(",", shipping);
        }
        ebayUrl += "&distance=" + milesFrom + "&zipcode=" + zipcode;
        System.out.println(ebayUrl);
        return ebayUrl;
    }
}
