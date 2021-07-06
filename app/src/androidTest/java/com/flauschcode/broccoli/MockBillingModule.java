package com.flauschcode.broccoli;

import static org.mockito.Mockito.mock;

import com.flauschcode.broccoli.support.BillingService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MockBillingModule {

    @Provides
    @Singleton
    BillingService billingService() {
        return mock(BillingService.class);
    }

}
