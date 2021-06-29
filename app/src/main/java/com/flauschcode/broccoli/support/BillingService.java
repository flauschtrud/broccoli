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

import javax.inject.Inject;

public class BillingService {

    private Application application; // TODO still needed?

    private final BillingClient billingClient;
    private SkuDetails skuDetailsPremium;

    private MutableLiveData<String> premiumPrice = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPremium = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isEnabled = new MutableLiveData<>(false);

    @Inject
    public BillingService(Application application) {
        this.application = application;

        // TODO crashes without Google Play Services https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability#isGooglePlayServicesAvailable(android.content.Context)

        billingClient = BillingClient.newBuilder(application).enablePendingPurchases().setListener(
                new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) { // TODO extract
                            list.forEach(purchase -> {
                                Log.d("PURCHASE order id", purchase.getOrderId());
                                Log.d("PURCHASE developer payload", purchase.getDeveloperPayload());
                                Log.d("PURCHASE purchase state (purchased = 1, pending = 2)", String.valueOf(purchase.getPurchaseState()));

                                // acknowledge
                                AcknowledgePurchaseParams acknowledgePurchaseParams =
                                        AcknowledgePurchaseParams.newBuilder()
                                                .setPurchaseToken(purchase.getPurchaseToken())
                                                .build();
                                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                                    @Override
                                    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                                        Log.d("PURCHASE acknowledged", billingResult.getDebugMessage());
                                    }
                                });

                                checkPremiumStateFor(purchase);
                            });
                        } else {
                            Log.d("PURCHASE NOT OK response code", String.valueOf(billingResult.getResponseCode())); // TODO handle cancel/already owned
                            Log.d("PURCHASE NOT OK message", billingResult.getDebugMessage());
                        }
                    }
                }).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Log.d("BILLING SERVICE", "Billing service DISCONNECTED!");
                // TODO handle the retrying of the connection yourself
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.d("BILLING SERVICE", "Billing setup finished.");
                Log.d("RESPONSE CODE", String.valueOf(billingResult.getResponseCode()));
                Log.d("DEBUG MESSAGE", billingResult.getDebugMessage());
                Log.d("BILLING CLIENT READY AFTER SETUP?", String.valueOf(billingClient.isReady()));

                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    isEnabled.postValue(false);
                    return;
                }
                isEnabled.postValue(true);

                billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult1, list) -> list.forEach(BillingService.this::checkPremiumStateFor));

                billingClient.querySkuDetailsAsync(SkuDetailsParams.newBuilder()
                        .setType(BillingClient.SkuType.INAPP)
                        .setSkusList(Collections.singletonList("premium")) // TODO get somewhere else
                        .build(), new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                        // TODO switch (responseCode)

                        Log.d("RESPONSE CODE", String.valueOf(billingResult.getResponseCode()));
                        Log.d("DEBUG MESSAGE", billingResult.getDebugMessage());

                        if (list == null || list.isEmpty()) {
                            Log.e("SKU DETAILS", "Found null or empty SkuDetails.");
                        } else {
                            list.forEach(skuDetails -> {
                                Log.d("SKU DETAILS", skuDetails.getTitle());
                                Log.d("SKU DETAILS", skuDetails.getDescription());

                                if ("premium".equals(skuDetails.getSku())) {
                                    premiumPrice.postValue(skuDetails.getPrice());
                                    skuDetailsPremium = skuDetails;
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void checkPremiumStateFor(Purchase purchase) {
        if (purchase.getSkus().contains("premium") && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && isSignatureValid(purchase)) { // TODO acknowledge again? just in case?
            isPremium.postValue(true);
        }
    }

    private boolean isSignatureValid(@NonNull Purchase purchase) {
        return SupportUtil.verifyPurchase(purchase.getOriginalJson(), purchase.getSignature());
    }

    public void purchaseSupporterEdition(Activity activity) { // TODO wtf

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetailsPremium) // TODO check not null
                .build();
        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            Log.e("PURCHASE", "Billing flow in progress.");
        } else {
            Log.e("PURCHASE", "Billing failed: + " + billingResult.getResponseCode() + ": " + billingResult.getDebugMessage());
        }
    }

    public LiveData<Boolean> isPremium() {
           return isPremium;
    }

    public LiveData<String> getPremiumPrice() {
        return premiumPrice;
    }

    public MutableLiveData<Boolean> isEnabled() {
        return isEnabled;
    }

}
