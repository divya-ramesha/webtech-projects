package com.example.cscihw9app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wssholmes.stark.circular_score.CircularScoreView;

import org.json.JSONException;
import org.json.JSONObject;


public class ShippingFragment extends Fragment {

    String productDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shipping, container, false);
        String myTag = getTag();
        ((ProductDetailsActivity)getActivity()).setShippingFragment(myTag);
        productDetails = ((ProductDetailsActivity) getActivity()).getProductDetails();
        return rootView;
    }

    private void setFeedbackStar(JSONObject feedbackStar) {
        View shippingPage = getView();
        try {
            ImageView feedbackView = ((ImageView)shippingPage.findViewById(R.id.feedbackStarValue));
            if (feedbackStar.getString("icon").equals("stars"))
                feedbackView.setImageResource(R.drawable.stars);
            else
                feedbackView.setImageResource(R.drawable.star_border);
            switch (feedbackStar.getString("color")) {
                case "yellow":
                    feedbackView.setColorFilter(ContextCompat.getColor(getContext(), R.color.yellow), android.graphics.PorterDuff.Mode.MULTIPLY);
                    break;
                case "blue":
                    feedbackView.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.MULTIPLY);
                    break;
                case "purple":
                    feedbackView.setColorFilter(ContextCompat.getColor(getContext(), R.color.purple), android.graphics.PorterDuff.Mode.MULTIPLY);
                    break;
                case "red":
                    feedbackView.setColorFilter(ContextCompat.getColor(getContext(), R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
                    break;
                case "green":
                    feedbackView.setColorFilter(ContextCompat.getColor(getContext(), R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
                    break;
                case "silver":
                    feedbackView.setColorFilter(ContextCompat.getColor(getContext(), R.color.silver), android.graphics.PorterDuff.Mode.MULTIPLY);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void upateShippingView(String itemJson) {
        try {
            JSONObject itemFromResults = new JSONObject(productDetails);
            JSONObject itemFromProduct = new JSONObject(itemJson);
            if (itemFromProduct.getString("status").equals("success")) {
                itemFromProduct = itemFromProduct.getJSONObject("item");
                View shippingPage = getView();
                ((TextView)shippingPage.findViewById(R.id.storeNameValue)).setText(Html.fromHtml("<a href='"+itemFromProduct.getJSONObject("Storefront").getString("StoreURL")+"'>" + itemFromProduct.getJSONObject("Storefront").getString("StoreName") + "</a>"));
                ((TextView)shippingPage.findViewById(R.id.storeNameValue)).setMovementMethod(LinkMovementMethod.getInstance());
                ((TextView)shippingPage.findViewById(R.id.feedbackScoreValue)).setText(itemFromResults.getJSONObject("sellerInfo").getJSONArray("feedbackScore").getString(0));
                ((CircularScoreView)shippingPage.findViewById(R.id.popularityValue)).setScore(new Double(itemFromResults.getJSONObject("sellerInfo").getJSONArray("positiveFeedbackPercent").getString(0)).intValue());
                setFeedbackStar(itemFromResults.getJSONObject("sellerInfo").getJSONObject("feedbackRatingStar"));
                ((TextView)shippingPage.findViewById(R.id.costValue)).setText(itemFromResults.getString("shipping"));
                ((TextView)shippingPage.findViewById(R.id.globalValue)).setText(itemFromProduct.getString("GlobalShipping"));
                ((TextView)shippingPage.findViewById(R.id.handlingValue)).setText(itemFromProduct.get("HandlingTime").toString());
                ((TextView)shippingPage.findViewById(R.id.conditionValue)).setText(itemFromResults.getString("condition"));

                ((TextView)shippingPage.findViewById(R.id.policyValue)).setText(itemFromProduct.getJSONObject("ReturnPolicy").getString("ReturnsAccepted"));
                ((TextView)shippingPage.findViewById(R.id.withinValue)).setText(itemFromProduct.getJSONObject("ReturnPolicy").getString("ReturnsWithin"));
                ((TextView)shippingPage.findViewById(R.id.modeValue)).setText(itemFromProduct.getJSONObject("ReturnPolicy").getString("Refund"));
                ((TextView)shippingPage.findViewById(R.id.shippedByValue)).setText(itemFromProduct.getJSONObject("ReturnPolicy").getString("ShippingCostPaidBy"));
                shippingPage.findViewById(R.id.shippingFragment).setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
