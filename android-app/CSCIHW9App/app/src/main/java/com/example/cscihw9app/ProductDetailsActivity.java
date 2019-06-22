package com.example.cscihw9app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    String productDetails;
    String shippingFragment;

    public void setShippingFragment(String t){
        shippingFragment = t;
    }

    public String getShippingFragment(){
        return shippingFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.getTabAt(0).setIcon(R.drawable.information_variant);
        tabLayout.getTabAt(1).setIcon(R.drawable.truck_delivery);
        tabLayout.getTabAt(2).setIcon(R.drawable.google);
        tabLayout.getTabAt(3).setIcon(R.drawable.equal);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        Intent i = getIntent();
        productDetails = i.getStringExtra("product");
        try {
            JSONObject json = new JSONObject(productDetails);
            ((Toolbar) findViewById(R.id.toolbar)).setTitle(json.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        View facebookIcon = findViewById(R.id.facebook_icon);
        facebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject jsonObj = new JSONObject(productDetails);
                    String facebookStr = "Buy " + jsonObj.getString("fullTitle") + " for " + jsonObj.getString("price") + " from Ebay!";
                    String facebookApiUrl = "https://www.facebook.com/dialog/share?app_id=277375383184972&display=popup&href="+ URLEncoder.encode(jsonObj.getString("itemURL"), "UTF-8")+"&quote="+URLEncoder.encode(facebookStr, "UTF-8")+"&hashtag="+URLEncoder.encode("#CSCI571Spring2019Ebay", "UTF-8");

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookApiUrl));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.android.chrome");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        intent.setPackage(null);
                        startActivity(intent);
                    }
                } catch (Exception e) {

                }
            }
        });
        Model m = new Gson().fromJson(productDetails, new TypeToken<Model>(){}.getType());
        FloatingActionButton fab = ((FloatingActionButton) findViewById(R.id.fab));
        if (Wishlist.checkIfFavoriteExist(m)) {
            fab.setImageResource(R.drawable.cart_remove_white);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model model = new Gson().fromJson(productDetails, new TypeToken<Model>(){}.getType());
                if (!Wishlist.checkIfFavoriteExist(model)) {
                    Toast.makeText(v.getContext(), model.getTitle() + " was added to wishlist", Toast.LENGTH_SHORT).show();
                    ((FloatingActionButton) v).setImageResource(R.drawable.cart_remove_white);
                    Wishlist.setFavorite(model);

                } else {
                    Toast.makeText(v.getContext(), model.getTitle() + " was removed from wishlist", Toast.LENGTH_SHORT).show();
                    ((FloatingActionButton) v).setImageResource(R.drawable.cart_plus_white);
                    Wishlist.removeFavorite(model);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_product_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.facebook_icon) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position) {
                case 0:
                    return new ProductFragment();
                case 1:
                    return new ShippingFragment();
                case 2:
                    return new PhotosFragment();
                case 3:
                    return new SimilarFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }

    public String getProductDetails() {
        return productDetails;
    }

}
