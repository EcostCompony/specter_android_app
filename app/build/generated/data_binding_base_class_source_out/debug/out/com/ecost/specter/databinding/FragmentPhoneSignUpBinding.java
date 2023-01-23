// Generated by view binder compiler. Do not edit!
package com.ecost.specter.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.ecost.specter.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentPhoneSignUpBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonContinue;

  @NonNull
  public final EditText inputPhone;

  private FragmentPhoneSignUpBinding(@NonNull LinearLayout rootView, @NonNull Button buttonContinue,
      @NonNull EditText inputPhone) {
    this.rootView = rootView;
    this.buttonContinue = buttonContinue;
    this.inputPhone = inputPhone;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentPhoneSignUpBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentPhoneSignUpBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_phone_sign_up, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentPhoneSignUpBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button_continue;
      Button buttonContinue = ViewBindings.findChildViewById(rootView, id);
      if (buttonContinue == null) {
        break missingId;
      }

      id = R.id.input_phone;
      EditText inputPhone = ViewBindings.findChildViewById(rootView, id);
      if (inputPhone == null) {
        break missingId;
      }

      return new FragmentPhoneSignUpBinding((LinearLayout) rootView, buttonContinue, inputPhone);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
