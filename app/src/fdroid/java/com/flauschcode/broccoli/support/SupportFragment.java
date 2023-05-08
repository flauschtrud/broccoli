package com.flauschcode.broccoli.support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.flauschcode.broccoli.R;

import dagger.android.support.AndroidSupportInjection;

public class SupportFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_support, container, false);

        Button buyMeACoffeeButton = root.findViewById(R.id.buy_me_coffee_button);
        buyMeACoffeeButton.setOnClickListener(v -> donate());

        Button shareAppButton = root.findViewById(R.id.share_app_button);
        shareAppButton.setOnClickListener(v -> shareApp());

        return root;
    }

    public void donate() {
        Uri uri = Uri.parse(getString(R.string.kofi_url));
        Intent goToKofi = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(goToKofi);
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.fdroid_url));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, null));
    }
}