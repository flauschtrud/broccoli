package com.flauschcode.broccoli.support;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BillingService implements BillingClientStateListener, PurchasesUpdatedListener {

    public static final String PREMIUM_SKU_NAME = "premium";
    private final BillingClient billingClient;

    private final MutableLiveData<Boolean> isPremium = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isEnabled = new MutableLiveData<>(false);

    @Inject
    public BillingService(Application application) {
        // TODO crashes without Google Play Services https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability#isGooglePlayServicesAvailable(android.content.Context)
        billingClient = BillingClient.newBuilder(application).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(this);
    }

    @Override
    public void onBillingServiceDisconnected() {
        Log.d("BILLING SERVICE", "Billing service DISCONNECTED!");
        // TODO handle the retrying of the connection yourself
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            isEnabled.postValue(false); // TODO retry
            Log.e(getClass().getName(), "onBillingSetupFinished: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
            return;
        }
        isEnabled.postValue(true);

        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult1, list) -> list.forEach(BillingService.this::process));
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.e(getClass().getName(), "onPurchasesUpdated: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
            return;
        }

        if (list == null) {
            Log.e(getClass().getName(), "onPurchasesUpdated: Purchase list is null.");
            return;
        }

        list.forEach(this::process);
    }

    public void purchaseSupporterEdition(Activity activity) {
        // it is a little slower to query the SKUs just before purchase, but they are only ever needed if a purchase has to made anyway
        billingClient.querySkuDetailsAsync(SkuDetailsParams.newBuilder()
                .setType(BillingClient.SkuType.INAPP)
                .setSkusList(Collections.singletonList(PREMIUM_SKU_NAME))
                .build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (BillingClient.BillingResponseCode.OK != billingResult.getResponseCode()) {
                    Log.e(getClass().getName(), "onSkuDetailsResponse: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
                    return;
                }

                if (list == null || list.isEmpty()) {
                    Log.e(getClass().getName(), "onSkuDetailsResponse: Found null or empty SkuDetails.");
                    return;
                }

                Optional<SkuDetails> skuDetailsPremium = list.stream().filter(skuDetails -> PREMIUM_SKU_NAME.equals(skuDetails.getSku())).findFirst();

                if (!skuDetailsPremium.isPresent()) {
                    Log.e(getClass().getName(), "onSkuDetailsResponse: Could not find " + PREMIUM_SKU_NAME + " SKU.");
                    return;
                }

                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsPremium.get())
                        .build();

                BillingResult billingResultBilling = billingClient.launchBillingFlow(activity, billingFlowParams);
                if (billingResultBilling.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    Log.e(getClass().getName(), "Billing flow failed: " + billingResultBilling.getResponseCode() + " " + billingResultBilling.getDebugMessage());
                }
            }
        });

    }

    public LiveData<Boolean> isPremium() {
        return isPremium;
    }

    public MutableLiveData<Boolean> isEnabled() {
        return isEnabled;
    }

    private void process(Purchase purchase) {
        if (!purchase.getSkus().contains(PREMIUM_SKU_NAME)) {
            Log.e(getClass().getName(), "Unknown purchase: " + purchase.getOrderId());
            return;
        }

        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
            Log.e(getClass().getName(), "Purchase is not yet in state PURCHASED: " + purchase.getOrderId());
            return;
        }

        if (!isSignatureValid(purchase)) {
            Log.e(getClass().getName(), "Invalid signature for purchase: " + purchase.getOrderId());
            return;
        }

        isPremium.postValue(true);

        if (!purchase.isAcknowledged()) {
            acknowledge(purchase);
        }
    }

    private boolean isSignatureValid(@NonNull Purchase purchase) {
        return SupportUtil.verifyPurchase(purchase.getOriginalJson(), purchase.getSignature());
    }

    private void acknowledge(Purchase purchase) {
        AcknowledgePurchaseParams acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                if (BillingClient.BillingResponseCode.OK != billingResult.getResponseCode()) {
                    Log.e(getClass().getName(), "onAcknowledgePurchaseResponse: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
                }
            }
        });
    }

}
