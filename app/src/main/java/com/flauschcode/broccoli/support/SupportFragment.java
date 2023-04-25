package com.flauschcode.broccoli.support;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.flauschcode.broccoli.BuildConfig;
import com.flauschcode.broccoli.R;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class SupportFragment extends Fragment {

    @Inject
    BillingService billingService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_support, container, false);

        Button cookieButton = root.findViewById(R.id.buy_me_cookie_button);
        billingService.getCookiePrice().observe(getViewLifecycleOwner(), cookieButton::setText);
        cookieButton.setOnClickListener(v -> {
            try {
                billingService.purchaseCookie(getActivity());
            } catch (BillingService.BillingException e) {
                Toast.makeText(requireActivity(), getString(R.string.play_store_sign_in_message), Toast.LENGTH_LONG).show();
            }
        });

        Button coffeeButton = root.findViewById(R.id.buy_me_coffee_button);
        billingService.getCoffeePrice().observe(getViewLifecycleOwner(), coffeeButton::setText);
        coffeeButton.setOnClickListener(v -> {
            try {
                billingService.purchaseCoffee(getActivity());
            } catch (BillingService.BillingException e) {
                Toast.makeText(requireActivity(), getString(R.string.play_store_sign_in_message), Toast.LENGTH_LONG).show();
            }
        });

        Button burgerButton = root.findViewById(R.id.buy_me_burger_button);
        billingService.getBurgerPrice().observe(getViewLifecycleOwner(), burgerButton::setText);
        burgerButton.setOnClickListener(v -> {
            try {
                billingService.purchaseBurger(getActivity());
            } catch (BillingService.BillingException e) {
                Toast.makeText(requireActivity(), getString(R.string.play_store_sign_in_message), Toast.LENGTH_LONG).show();
            }
        });

        Button giveRatingButton = root.findViewById(R.id.give_rating_button);
        giveRatingButton.setOnClickListener(v -> giveRating());

        Button shareAppButton = root.findViewById(R.id.share_app_button);
        shareAppButton.setOnClickListener(v -> shareApp());

        return root;
    }

    public void giveRating() {
        try {
            Uri uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID);
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.playstore_url))));
        }
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.playstore_url));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, null));
    }
}