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

        Button getSupporterEditionButton = root.findViewById(R.id.get_supporter_edition_button);
        getSupporterEditionButton.setOnClickListener(v -> {
            try {
                billingService.purchaseSupporterEdition(getActivity());
            } catch (BillingService.BillingException e) {
                Toast.makeText(requireActivity(), getString(R.string.support_supporter_edition_get_error_message), Toast.LENGTH_LONG).show();
            }
        });

        billingService.isPremium().observe(getViewLifecycleOwner(), isPremium -> {
            if (Boolean.TRUE.equals(isPremium)) {
                getSupporterEditionButton.setVisibility(View.GONE);
                root.findViewById(R.id.supporter_edition_thanks).setVisibility(View.VISIBLE);
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
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
        }
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, null));
    }
}