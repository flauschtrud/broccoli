package com.flauschcode.broccoli;

import android.app.Activity;
import android.view.View;

import androidx.preference.PreferenceManager;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

public class FeatureDiscoveryTargetBuilder {

    private final Activity activity;
    private String title;
    private String description;

    private FeatureDiscoveryTargetBuilder(Activity activity) {
        this.activity = activity;
    }

    public static FeatureDiscoveryTargetBuilder buildInContextOf(Activity activity) {
        return new FeatureDiscoveryTargetBuilder(activity);
    }

    public FeatureDiscoveryTargetBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public FeatureDiscoveryTargetBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public void discoverIfNew(View view) {
        if (hasNotBeenDiscoveredYet(view)) {
            TapTargetView.showFor(activity, TapTarget.forView(view, title != null? title : "", description != null? description : "")
                    .tintTarget(false), new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    view.performClick();
                    view.dismiss(true);
                    markAsDiscovered(view);
                }
            });
        }
    }

    private void markAsDiscovered(View view) {
        PreferenceManager.getDefaultSharedPreferences(view.getContext())
                .edit()
                .putBoolean(String.valueOf(view.getId()), true)
                .apply();
    }

    private boolean hasNotBeenDiscoveredYet(View view) {
        return PreferenceManager.getDefaultSharedPreferences(view.getContext())
                .getBoolean(String.valueOf(view.getId()), false);
    }
}
