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

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BillingService implements BillingClientStateListener, PurchasesUpdatedListener, ProductDetailsResponseListener {

    private final BillingClient billingClient;

    private final Handler handler;
    private static final long RECONNECT_TIMER_START_MILLISECONDS = 1000L;
    private static final long RECONNECT_TIMER_MAX_TIME_MILLISECONDS = 1000L * 60L * 15L; // 15 mins
    private long reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS;

    private ProductDetails cookieDetails;
    private final MutableLiveData<String> cookiePrice = new MutableLiveData<>();

    private ProductDetails coffeeDetails;
    private final MutableLiveData<String> coffeePrice = new MutableLiveData<>();

    private ProductDetails burgerDetails;
    private final MutableLiveData<String> burgerPrice = new MutableLiveData<>();

    private static final String PREMIUM_SKU_NAME = "premium";
    private static final String PRODUCT_ID_COOKIE = "cookie";
    private static final String PRODUCT_ID_COFFEE = "coffee";
    private static final String PRODUCT_ID_BURGER = "burger";

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

        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                List.of(getProduct(PRODUCT_ID_COOKIE), getProduct(PRODUCT_ID_COFFEE), getProduct(PRODUCT_ID_BURGER))
                        )
                        .build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, this);
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

        list.forEach(this::handlePurchase);
    }

    @Override
    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
        if (BillingClient.BillingResponseCode.OK != billingResult.getResponseCode()) {
            Log.e(getClass().getSimpleName(), "onSkuDetailsResponse: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
            return;
        }

        list.stream().filter(productDetails -> productDetails.getProductId().equals(PRODUCT_ID_COOKIE)).findFirst().ifPresent(productDetails -> {
            cookieDetails = productDetails;
            cookiePrice.postValue(productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice());
        });

        list.stream().filter(productDetails -> productDetails.getProductId().equals(PRODUCT_ID_COFFEE)).findFirst().ifPresent(productDetails -> {
            coffeeDetails = productDetails;
            coffeePrice.postValue(productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice());
        });

        list.stream().filter(productDetails -> productDetails.getProductId().equals(PRODUCT_ID_BURGER)).findFirst().ifPresent(productDetails -> {
            burgerDetails = productDetails;
            burgerPrice.postValue(productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice());
        });
    }

    public LiveData<String> getCookiePrice() {
        return cookiePrice;
    }

    public LiveData<String> getCoffeePrice() {
        return coffeePrice;
    }

    public LiveData<String> getBurgerPrice() {
        return burgerPrice;
    }

    public void purchaseCookie(Activity activity) throws BillingException {
        purchase(activity, cookieDetails);
    }

    public void purchaseCoffee(Activity activity) throws BillingException {
        purchase(activity, coffeeDetails);
    }

    public void purchaseBurger(Activity activity) throws BillingException {
        purchase(activity, burgerDetails);
    }

    private QueryProductDetailsParams.Product getProduct(String productId) {
        return QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build();
    }

    private void purchase(Activity activity, ProductDetails productDetails) throws BillingException {
        if (!billingClient.isReady()) {
            Log.e(getClass().getSimpleName(), "purchaseSupporterEdition: A purchase has been requested but the billing service is not ready yet.");
            throw new BillingException("The Billing service is not ready yet.");
        }

        if (productDetails == null) {
            Log.e(getClass().getSimpleName(), "purchaseSupporterEdition: Could not find " + PREMIUM_SKU_NAME + " SKU.");
            return;
        }

        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                List.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        BillingResult billingResultBilling = billingClient.launchBillingFlow(activity, billingFlowParams);
        if (billingResultBilling.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.e(getClass().getSimpleName(), "Billing flow failed: " + billingResultBilling.getResponseCode() + " " + billingResultBilling.getDebugMessage());
        }
    }

    private void handlePurchase(Purchase purchase) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
            if (BillingClient.BillingResponseCode.OK != billingResult.getResponseCode()) {
                Log.e(getClass().getSimpleName(), "Could not handle purchase: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
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
