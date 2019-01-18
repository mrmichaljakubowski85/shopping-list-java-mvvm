package com.tomtre.android.architecture.shoppinglistmvp.util;

import android.support.test.espresso.IdlingResource;

public class EspressoIdlingResource {
    private static final String RESOURCE_NAME = "GLOBAL";

    //can't be used because of tests fail
//    private static final CountingIdlingResource COUNTING_IDLING_RESOURCE = new CountingIdlingResource(RESOURCE_NAME);

    private static final CustomCountingIdlingResource COUNTING_IDLING_RESOURCE = new CustomCountingIdlingResource(RESOURCE_NAME);

    public static void increment() {
        COUNTING_IDLING_RESOURCE.increment();
    }

    public static void decrement() {
        COUNTING_IDLING_RESOURCE.decrement();
    }

    public static void decrementWithIdleCheck() {
        if (!COUNTING_IDLING_RESOURCE.isIdleNow())
            COUNTING_IDLING_RESOURCE.decrement();
    }

    public static IdlingResource getIdlingResource() {
        return COUNTING_IDLING_RESOURCE;
    }
}
