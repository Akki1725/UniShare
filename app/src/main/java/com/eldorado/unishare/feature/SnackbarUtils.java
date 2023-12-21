package com.eldorado.unishare.feature;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.eldorado.unishare.R;

public class SnackbarUtils {

    public static void showSnackbar(
            Context context,
            View view,
            String title,
            int length
    ) {
        showSnackbar(context, view, title, length, null, null);
    }

    public static void showSnackbar(
            Context context,
            View view,
            String title,
            int length,
            String actionText,
            Runnable action
    ) {

        Snackbar snackbar = Snackbar.make(view, title, length);

        if (actionText != null && action != null) {
            snackbar.setAction(actionText, v -> action.run());
        }

        View snackbarView = snackbar.getView();
        ViewGroup.LayoutParams params = snackbarView.getLayoutParams();

        if (params instanceof ViewGroup.MarginLayoutParams) {
            int marginInPixels = context.getResources().getDimensionPixelSize(R.dimen.bottom_margin);

            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
            marginParams.setMargins(
                    marginParams.leftMargin,
                    marginParams.topMargin,
                    marginParams.rightMargin,
                    marginParams.bottomMargin + marginInPixels
            );

            snackbarView.setLayoutParams(marginParams);
        }

        snackbar.show();
    }
}
