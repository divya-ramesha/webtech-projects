package com.example.cscihw9app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Wishlist extends Fragment {

    static List<Model> favorites = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);
        return rootView;
    }

    public static void setFavorite(Model m) {
        favorites.add(m);
    }

    public static void removeFavorite(Model m) {
        favorites.remove(m);
    }

    public static boolean checkIfFavoriteExist(Model m) {
        return favorites.contains(m);
    }
}
