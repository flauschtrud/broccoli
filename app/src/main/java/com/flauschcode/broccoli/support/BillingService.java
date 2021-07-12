package com.flauschcode.broccoli.support;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
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

    private final BillingClient billingClient;

    private final Handler handler;
    private static final long RECONNECT_TIMER_START_MILLISECONDS = 1000L;
    private static final long RECONNECT_TIMER_MAX_TIME_MILLISECONDS = 1000L * 60L * 15L; // 15 mins
    private long reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS;

    private final MutableLiveData<Boolean> isPremium = new MutableLiveData<>(false);
    private static final String PREMIUM_SKU_NAME = "premium";

    @Inject
    public BillingService(Application application) {
        handler = new Handler(Looper.getMainLooper());
        billingClient = BillingClient.newBuilder(application).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(this);
    }

    @Override
    public void onBillingServiceDisconnected() {
        Log.e(getClass().getSimpleName(), "Billing service disconnected.");
        retryBillingServiceConnectionWithExponentialBackoff();
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.e(getClass().getSimpleName(), "onBillingSetupFinished: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
            retryBillingServiceConnectionWithExponentialBackoff();
            return;
        }

        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult1, list) -> list.forEach(BillingService.this::process));
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.e(getClass().getSimpleName(), "onPurchasesUpdated: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
            return;
        }

        if (list == null) {
            Log.e(getClass().getSimpleName(), "onPurchasesUpdated: Purchase list is null.");
            return;
        }

        list.forEach(this::process);
    }

    public void purchaseSupporterEdition(Activity activity) throws BillingException {
        if (!billingClient.isReady()) {
            Log.e(getClass().getSimpleName(), "purchaseSupporterEdition: A purchase has been requested but the billing service is not ready yet.");
            throw new BillingException("The Billing service is not ready yet.");
        }

        // it is a little slower to query the SKUs just before purchase, but they are only ever needed if a purchase has to made anyway
        billingClient.querySkuDetailsAsync(SkuDetailsParams.newBuilder()
                .setType(BillingClient.SkuType.INAPP)
                .setSkusList(Collections.singletonList(PREMIUM_SKU_NAME))
                .build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (BillingClient.BillingResponseCode.OK != billingResult.getResponseCode()) {
                    Log.e(getClass().getSimpleName(), "onSkuDetailsResponse: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
                    return;
                }

                if (list == null || list.isEmpty()) {
                    Log.e(getClass().getSimpleName(), "onSkuDetailsResponse: Found null or empty SkuDetails.");
                    return;
                }

                Optional<SkuDetails> skuDetailsPremium = list.stream().filter(skuDetails -> PREMIUM_SKU_NAME.equals(skuDetails.getSku())).findFirst();

                if (!skuDetailsPremium.isPresent()) {
                    Log.e(getClass().getSimpleName(), "onSkuDetailsResponse: Could not find " + PREMIUM_SKU_NAME + " SKU.");
                    return;
                }

                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsPremium.get())
                        .build();

                BillingResult billingResultBilling = billingClient.launchBillingFlow(activity, billingFlowParams);
                if (billingResultBilling.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    Log.e(getClass().getSimpleName(), "Billing flow failed: " + billingResultBilling.getResponseCode() + " " + billingResultBilling.getDebugMessage());
                }
            }
        });

    }

    public LiveData<Boolean> isPremium() {
        return isPremium;
    }

    private void process(Purchase purchase) {
        if (!purchase.getSkus().contains(PREMIUM_SKU_NAME)) {
            Log.e(getClass().getSimpleName(), "Unknown purchase: " + purchase.getOrderId());
            return;
        }

        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
            Log.e(getClass().getSimpleName(), "Purchase is not yet in state PURCHASED: " + purchase.getOrderId());
            return;
        }

        if (!isSignatureValid(purchase)) {
            Log.e(getClass().getSimpleName(), "Invalid signature for purchase: " + purchase.getOrderId());
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
                    Log.e(getClass().getSimpleName(), "onAcknowledgePurchaseResponse: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
                }
            }
        });
    }

    private void retryBillingServiceConnectionWithExponentialBackoff() {
        Log.d(getClass().getSimpleName(), "Trying to reconnect to billing service after " + reconnectMilliseconds/1000 + " seconds.");
        handler.postDelayed(() -> billingClient.startConnection(this), reconnectMilliseconds);
        reconnectMilliseconds = Math.min(reconnectMilliseconds * 2,
                RECONNECT_TIMER_MAX_TIME_MILLISECONDS);
    }

    static class BillingException extends Exception {
        public BillingException(String s) {
            super(s);
        }
    }
}
