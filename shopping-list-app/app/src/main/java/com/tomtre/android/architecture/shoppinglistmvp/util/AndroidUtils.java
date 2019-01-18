package com.tomtre.android.architecture.shoppinglistmvp.util;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;

import static com.google.common.base.Preconditions.checkNotNull;

public class AndroidUtils {

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @IdRes int fragmentContainerResId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragmentContainerResId, fragment);
        fragmentTransaction.commit();
    }

    public static boolean hasPosition(int adapterPosition) {
        return adapterPosition != RecyclerView.NO_POSITION;
    }
}
