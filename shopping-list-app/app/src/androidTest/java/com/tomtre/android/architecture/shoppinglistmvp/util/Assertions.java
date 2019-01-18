package com.tomtre.android.architecture.shoppinglistmvp.util;

import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.util.HumanReadables;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

public class Assertions {
    public static ViewAssertion isNotDisplayed() {
        return (view, noView) -> {
            if (view != null && isDisplayed().matches(view)) {
                throw new AssertionError("View is present in the hierarchy and Displayed: "
                        + HumanReadables.describe(view));
            }
        };
    }

}
