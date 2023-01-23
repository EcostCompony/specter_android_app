// Generated by view binder compiler. Do not edit!
package com.ecost.specter.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.ecost.specter.R;
import java.lang.NullPointerException;
import java.lang.Override;

public final class ActivityAuthBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final FrameLayout fragmentContainerView;

  private ActivityAuthBinding(@NonNull FrameLayout rootView,
      @NonNull FrameLayout fragmentContainerView) {
    this.rootView = rootView;
    this.fragmentContainerView = fragmentContainerView;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityAuthBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityAuthBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_auth, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityAuthBinding bind(@NonNull View rootView) {
    if (rootView == null) {
      throw new NullPointerException("rootView");
    }

    FrameLayout fragmentContainerView = (FrameLayout) rootView;

    return new ActivityAuthBinding((FrameLayout) rootView, fragmentContainerView);
  }
}
