package com.example.cscihw9app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;
    private String currentZipCode = "90007";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View searchView = inflater.inflate(R.layout.fragment_search, container, false);
        getCurrentZipcode();
        CheckBox checkbox = (CheckBox)searchView.findViewById(R.id.enableSearch);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                View searchView = getView();
                if(isChecked)
                {
                    ViewGroup.MarginLayoutParams searchButtonParams = (ViewGroup.MarginLayoutParams)searchView.findViewById(R.id.searchButton).getLayoutParams();
                    ViewGroup.MarginLayoutParams clearButtonParams = (ViewGroup.MarginLayoutParams)searchView.findViewById(R.id.clearButton).getLayoutParams();

                    searchView.findViewById(R.id.milesFrom).setVisibility(View.VISIBLE);
                    searchView.findViewById(R.id.fromtitle).setVisibility(View.VISIBLE);
                    searchView.findViewById(R.id.location).setVisibility(View.VISIBLE);
                    searchView.findViewById(R.id.currentlocationlabel).setVisibility(View.VISIBLE);
                    searchView.findViewById(R.id.zipcodeText).setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams currentLocaltionParams = (ViewGroup.MarginLayoutParams) ((TextView)searchView.findViewById(R.id.currentlocationlabel)).getLayoutParams();
                    searchButtonParams.topMargin = currentLocaltionParams.topMargin + 270;
                    clearButtonParams.topMargin = currentLocaltionParams.topMargin + 270;
                    searchView.findViewById(R.id.searchButton).setLayoutParams(searchButtonParams);
                    searchView.findViewById(R.id.clearButton).setLayoutParams(clearButtonParams);
                    ((RadioGroup)searchView.findViewById(R.id.location)).check(R.id.current_location);
                    searchView.findViewById(R.id.zipcodeText).setEnabled(false);
                }
                else
                {
                    removeZipcodeView(searchView);
                }
            }
        });

        RadioGroup locationRb = (RadioGroup) searchView.findViewById(R.id.location);
        locationRb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.current_location:
                        searchView.findViewById(R.id.zipcodeText).setEnabled(false);
                        break;
                    case R.id.zipcode:
                        searchView.findViewById(R.id.zipcodeText).setEnabled(true);
                        break;
                }
            }

        });

        final AppCompatAutoCompleteTextView autoCompleteTextView =
                searchView.findViewById(R.id.zipcodeText);
        final TextView selectedText = searchView.findViewById(R.id.selected_item);

        //Setting up the adapter for AutoSuggest
        autoSuggestAdapter = new AutoSuggestAdapter(getActivity(),
                android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        selectedText.setText(autoSuggestAdapter.getObject(position));
                    }
                });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        makeApiCall(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });

        Button searchButton = (Button) searchView.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String keyword = ((EditText) searchView.findViewById(R.id.keyword)).getText().toString().trim();
                boolean enableSearchChecked = ((CheckBox) searchView.findViewById(R.id.enableSearch)).isChecked();
                int radioGroupButton = ((RadioGroup) searchView.findViewById(R.id.location)).getCheckedRadioButtonId();
                String zipcode = ((AutoCompleteTextView) searchView.findViewById(R.id.zipcodeText)).getText().toString().trim();
                if (keyword.equals("") || (enableSearchChecked && radioGroupButton == R.id.zipcode && (zipcode == "" || zipcode == null || !zipcode.matches("^[0-9]{5}$")))) {
                    if (keyword.equals("")) {
                        searchView.findViewById(R.id.keywordError).setVisibility(View.VISIBLE);
                    } else {
                        searchView.findViewById(R.id.keywordError).setVisibility(View.GONE);
                    }
                    if (enableSearchChecked && radioGroupButton == R.id.zipcode && (zipcode == "" || zipcode == null || !zipcode.matches("^[0-9]{5}$"))) {
                        searchView.findViewById(R.id.zipcodeError).setVisibility(View.VISIBLE);
                    } else {
                        searchView.findViewById(R.id.zipcodeError).setVisibility(View.GONE);
                    }
                    Toast.makeText(getContext(), "Please fix all fields with error", Toast.LENGTH_SHORT).show();
                } else {
                    searchView.findViewById(R.id.keywordError).setVisibility(View.GONE);
                    searchView.findViewById(R.id.zipcodeError).setVisibility(View.GONE);
                    Intent searchForm = new Intent(getActivity(), SearchResultsActivity.class);
                    searchForm.putExtra("keyword", ((EditText)searchView.findViewById(R.id.keyword)).getText().toString().trim());
                    searchForm.putExtra("category", getCategoryIdByString(((Spinner) searchView.findViewById(R.id.category)).getSelectedItem().toString().trim()));
                    searchForm.putExtra("New", Boolean.toString(((CheckBox) searchView.findViewById(R.id.New)).isChecked()));
                    searchForm.putExtra("Used", Boolean.toString(((CheckBox) searchView.findViewById(R.id.Used)).isChecked()));
                    searchForm.putExtra("Unspecified", Boolean.toString(((CheckBox) searchView.findViewById(R.id.Unspecified)).isChecked()));
                    searchForm.putExtra("LocalPickupOnly", Boolean.toString(((CheckBox) searchView.findViewById(R.id.LocalPickupOnly)).isChecked()));
                    searchForm.putExtra("FreeShipping", Boolean.toString(((CheckBox) searchView.findViewById(R.id.FreeShipping)).isChecked()));
                    searchForm.putExtra("zipcode", currentZipCode);
                    if (enableSearchChecked) {
                        if (!((EditText)searchView.findViewById(R.id.milesFrom)).getText().toString().trim().equals("")) {
                            String miles = ((EditText)searchView.findViewById(R.id.milesFrom)).getText().toString();
                            if (miles != null && !miles.trim().equals(""))
                                searchForm.putExtra("milesFrom", miles.trim());
                            else
                                searchForm.putExtra("milesFrom", "10");
                        } else
                            searchForm.putExtra("milesFrom", "10");
                        if (radioGroupButton == R.id.zipcode)
                            searchForm.putExtra("zipcode", zipcode);
                    } else {
                        searchForm.putExtra("milesFrom", "0");
                    }
                    startActivity(searchForm);
                }
            }

            private String getCategoryIdByString(String categoryStr) {
                if (categoryStr.equals("All"))
                    return "";
                else if (categoryStr.startsWith("Art"))
                    return "550";
                else if (categoryStr.startsWith("Baby"))
                    return "2984";
                else if (categoryStr.startsWith("Books"))
                    return "267";
                else if (categoryStr.startsWith("Clothing"))
                    return "11450";
                else if (categoryStr.startsWith("Computers"))
                    return "58058";
                else if (categoryStr.startsWith("Health"))
                    return "26395";
                else if (categoryStr.startsWith("Music"))
                    return "11233";
                else if (categoryStr.startsWith("Video"))
                    return "1249";
                else
                    return "";
            }
        });

        Button clearButton = (Button) searchView.findViewById(R.id.clearButton);

        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((EditText)searchView.findViewById(R.id.keyword)).setText("");
                ((Spinner) searchView.findViewById(R.id.category)).setSelection(0);
                ((CheckBox) searchView.findViewById(R.id.New)).setChecked(false);
                ((CheckBox) searchView.findViewById(R.id.Used)).setChecked(false);
                ((CheckBox) searchView.findViewById(R.id.Unspecified)).setChecked(false);
                ((CheckBox) searchView.findViewById(R.id.LocalPickupOnly)).setChecked(false);
                ((CheckBox) searchView.findViewById(R.id.FreeShipping)).setChecked(false);
                ((CheckBox) searchView.findViewById(R.id.enableSearch)).setChecked(false);
                searchView.findViewById(R.id.keywordError).setVisibility(View.GONE);
                removeZipcodeView(searchView);
            }
        });

        return searchView;
    }

    private void getCurrentZipcode() {
        ApiCall.makeJsonObjectRequest(getContext(), "http://ip-api.com/json", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    currentZipCode = response.getString("zip");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void removeZipcodeView(View searchView) {
        ViewGroup.MarginLayoutParams searchButtonParams = (ViewGroup.MarginLayoutParams)searchView.findViewById(R.id.searchButton).getLayoutParams();
        ViewGroup.MarginLayoutParams clearButtonParams = (ViewGroup.MarginLayoutParams)searchView.findViewById(R.id.clearButton).getLayoutParams();

        searchView.findViewById(R.id.milesFrom).setVisibility(View.GONE);
        searchView.findViewById(R.id.fromtitle).setVisibility(View.GONE);
        searchView.findViewById(R.id.location).setVisibility(View.GONE);
        searchView.findViewById(R.id.currentlocationlabel).setVisibility(View.GONE);
        searchView.findViewById(R.id.zipcodeText).setVisibility(View.GONE);
        ViewGroup.MarginLayoutParams enableSearchParams = (ViewGroup.MarginLayoutParams) ((TextView)searchView.findViewById(R.id.enableSearch)).getLayoutParams();
        searchButtonParams.topMargin = enableSearchParams.topMargin + 210;
        clearButtonParams.topMargin = enableSearchParams.topMargin + 210;
        searchView.findViewById(R.id.searchButton).setLayoutParams(searchButtonParams);
        searchView.findViewById(R.id.clearButton).setLayoutParams(clearButtonParams);
        ((AutoCompleteTextView) searchView.findViewById(R.id.zipcodeText)).setText("");
        ((EditText) searchView.findViewById(R.id.milesFrom)).setText("");
        ((TextView) searchView.findViewById(R.id.selected_item)).setText("");
        searchView.findViewById(R.id.zipcodeError).setVisibility(View.GONE);
    }

    private void makeApiCall(String text) {
        ApiCall.makeJsonArrayRequest(getActivity(), "http://csci571hw9app.us-west-2.elasticbeanstalk.com/getZipcodeAutoComplete?searchText=" + text, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<String> stringList = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        stringList.add(response.getString(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

}
