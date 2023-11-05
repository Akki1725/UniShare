// Generated by view binder compiler. Do not edit!
package com.michaelians.bluteeth.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.flexbox.FlexboxLayout;
import com.michaelians.bluteeth.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityVoiceCallBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageView blueIcon;

  @NonNull
  public final ImageView callEnd;

  @NonNull
  public final LinearLayout controlBar;

  @NonNull
  public final TextView deviceAddress;

  @NonNull
  public final ImageView deviceIcon;

  @NonNull
  public final TextView deviceName;

  @NonNull
  public final FlexboxLayout iconContainer;

  @NonNull
  public final ImageView keypadBtn;

  @NonNull
  public final TextView keypadText;

  @NonNull
  public final ImageView moreBtn;

  @NonNull
  public final TextView moreText;

  @NonNull
  public final ImageView muteBtn;

  @NonNull
  public final TextView muteText;

  @NonNull
  public final ImageView speakerBtn;

  @NonNull
  public final TextView speakerText;

  @NonNull
  public final TextView statusText;

  @NonNull
  public final FlexboxLayout textContainer;

  private ActivityVoiceCallBinding(@NonNull ConstraintLayout rootView, @NonNull ImageView blueIcon,
      @NonNull ImageView callEnd, @NonNull LinearLayout controlBar, @NonNull TextView deviceAddress,
      @NonNull ImageView deviceIcon, @NonNull TextView deviceName,
      @NonNull FlexboxLayout iconContainer, @NonNull ImageView keypadBtn,
      @NonNull TextView keypadText, @NonNull ImageView moreBtn, @NonNull TextView moreText,
      @NonNull ImageView muteBtn, @NonNull TextView muteText, @NonNull ImageView speakerBtn,
      @NonNull TextView speakerText, @NonNull TextView statusText,
      @NonNull FlexboxLayout textContainer) {
    this.rootView = rootView;
    this.blueIcon = blueIcon;
    this.callEnd = callEnd;
    this.controlBar = controlBar;
    this.deviceAddress = deviceAddress;
    this.deviceIcon = deviceIcon;
    this.deviceName = deviceName;
    this.iconContainer = iconContainer;
    this.keypadBtn = keypadBtn;
    this.keypadText = keypadText;
    this.moreBtn = moreBtn;
    this.moreText = moreText;
    this.muteBtn = muteBtn;
    this.muteText = muteText;
    this.speakerBtn = speakerBtn;
    this.speakerText = speakerText;
    this.statusText = statusText;
    this.textContainer = textContainer;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityVoiceCallBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityVoiceCallBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_voice_call, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityVoiceCallBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.blueIcon;
      ImageView blueIcon = ViewBindings.findChildViewById(rootView, id);
      if (blueIcon == null) {
        break missingId;
      }

      id = R.id.callEnd;
      ImageView callEnd = ViewBindings.findChildViewById(rootView, id);
      if (callEnd == null) {
        break missingId;
      }

      id = R.id.controlBar;
      LinearLayout controlBar = ViewBindings.findChildViewById(rootView, id);
      if (controlBar == null) {
        break missingId;
      }

      id = R.id.deviceAddress;
      TextView deviceAddress = ViewBindings.findChildViewById(rootView, id);
      if (deviceAddress == null) {
        break missingId;
      }

      id = R.id.deviceIcon;
      ImageView deviceIcon = ViewBindings.findChildViewById(rootView, id);
      if (deviceIcon == null) {
        break missingId;
      }

      id = R.id.deviceName;
      TextView deviceName = ViewBindings.findChildViewById(rootView, id);
      if (deviceName == null) {
        break missingId;
      }

      id = R.id.iconContainer;
      FlexboxLayout iconContainer = ViewBindings.findChildViewById(rootView, id);
      if (iconContainer == null) {
        break missingId;
      }

      id = R.id.keypadBtn;
      ImageView keypadBtn = ViewBindings.findChildViewById(rootView, id);
      if (keypadBtn == null) {
        break missingId;
      }

      id = R.id.keypadText;
      TextView keypadText = ViewBindings.findChildViewById(rootView, id);
      if (keypadText == null) {
        break missingId;
      }

      id = R.id.moreBtn;
      ImageView moreBtn = ViewBindings.findChildViewById(rootView, id);
      if (moreBtn == null) {
        break missingId;
      }

      id = R.id.moreText;
      TextView moreText = ViewBindings.findChildViewById(rootView, id);
      if (moreText == null) {
        break missingId;
      }

      id = R.id.muteBtn;
      ImageView muteBtn = ViewBindings.findChildViewById(rootView, id);
      if (muteBtn == null) {
        break missingId;
      }

      id = R.id.muteText;
      TextView muteText = ViewBindings.findChildViewById(rootView, id);
      if (muteText == null) {
        break missingId;
      }

      id = R.id.speakerBtn;
      ImageView speakerBtn = ViewBindings.findChildViewById(rootView, id);
      if (speakerBtn == null) {
        break missingId;
      }

      id = R.id.speakerText;
      TextView speakerText = ViewBindings.findChildViewById(rootView, id);
      if (speakerText == null) {
        break missingId;
      }

      id = R.id.statusText;
      TextView statusText = ViewBindings.findChildViewById(rootView, id);
      if (statusText == null) {
        break missingId;
      }

      id = R.id.textContainer;
      FlexboxLayout textContainer = ViewBindings.findChildViewById(rootView, id);
      if (textContainer == null) {
        break missingId;
      }

      return new ActivityVoiceCallBinding((ConstraintLayout) rootView, blueIcon, callEnd,
          controlBar, deviceAddress, deviceIcon, deviceName, iconContainer, keypadBtn, keypadText,
          moreBtn, moreText, muteBtn, muteText, speakerBtn, speakerText, statusText, textContainer);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}