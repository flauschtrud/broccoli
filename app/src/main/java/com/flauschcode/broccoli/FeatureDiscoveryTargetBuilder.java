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
    private String tag;

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

    public FeatureDiscoveryTargetBuilder withTag(String tag) {
        this.tag = tag;
        return this;
    }

    public void discoverIfNew(View clickableView) {
        if (tag == null) {
            throw new IllegalArgumentException("The unique tag must not be null!");
        }

        if (hasNotBeenDiscoveredYet()) {
            TapTargetView.showFor(activity, TapTarget.forView(clickableView, title != null? title : "", description != null? description : "")
                    .tintTarget(false), new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    clickableView.performClick();
                    markAsDiscovered();
                }
            });
        }
    }

    private void markAsDiscovered() {
        PreferenceManager.getDefaultSharedPreferences(activity)
                .edit()
                .putBoolean(tag, true)
                .apply();
    }

    private boolean hasNotBeenDiscoveredYet() {
        return !PreferenceManager.getDefaultSharedPreferences(activity)
                .getBoolean(tag, false);
    }
}
