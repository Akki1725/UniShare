// Generated by view binder compiler. Do not edit!
package com.michaelians.bluteeth.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.michaelians.bluteeth.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemSendBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout marginLayout;

  @NonNull
  public final LinearLayout msgContainer;

  @NonNull
  public final TextView sendBox;

  private ItemSendBinding(@NonNull ConstraintLayout rootView,
      @NonNull ConstraintLayout marginLayout, @NonNull LinearLayout msgContainer,
      @NonNull TextView sendBox) {
    this.rootView = rootView;
    this.marginLayout = marginLayout;
    this.msgContainer = msgContainer;
    this.sendBox = sendBox;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemSendBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemSendBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_send, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemSendBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      ConstraintLayout marginLayout = (ConstraintLayout) rootView;

      id = R.id.msgContainer;
      LinearLayout msgContainer = ViewBindings.findChildViewById(rootView, id);
      if (msgContainer == null) {
        break missingId;
      }

      id = R.id.sendBox;
      TextView sendBox = ViewBindings.findChildViewById(rootView, id);
      if (sendBox == null) {
        break missingId;
      }

      return new ItemSendBinding((ConstraintLayout) rootView, marginLayout, msgContainer, sendBox);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}