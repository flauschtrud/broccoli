package com.flauschcode.broccoli.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
        getSupporterEditionButton.setOnClickListener(v -> billingService.purchaseSupporterEdition(getActivity()));

        billingService.getPremiumPrice().observe(getViewLifecycleOwner(), premiumPrice -> getSupporterEditionButton.setText("Get for " + premiumPrice));

        billingService.isEnabled().observe(getViewLifecycleOwner(), isEnabled -> {
            TextView errorMessageTextView = root.findViewById(R.id.supporter_edition_error_message);
            if (Boolean.FALSE.equals(isEnabled)) {
                getSupporterEditionButton.setVisibility(View.GONE);
                errorMessageTextView.setVisibility(View.VISIBLE);
            } else {
                getSupporterEditionButton.setVisibility(View.VISIBLE);
                errorMessageTextView.setVisibility(View.GONE);
            }
        });

        billingService.isPremium().observe(getViewLifecycleOwner(), isPremium -> {
            if (Boolean.TRUE.equals(isPremium)) {
                root.findViewById(R.id.support_layout_thanks).setVisibility(View.VISIBLE);
                root.findViewById(R.id.support_layout_community_edition).setVisibility(View.GONE);
                root.findViewById(R.id.support_layout_supporter_edition).setVisibility(View.GONE);
            }
        });

        return root;
    }
}