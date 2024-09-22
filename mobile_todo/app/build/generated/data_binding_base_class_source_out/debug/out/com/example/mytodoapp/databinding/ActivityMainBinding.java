// Generated by view binder compiler. Do not edit!
package com.example.mytodoapp.databinding;

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
import com.example.mytodoapp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button buttonAddTheme;

  @NonNull
  public final Button buttonChooseSavePath;

  @NonNull
  public final Button buttonCreate;

  @NonNull
  public final Button buttonOpenJson;

  @NonNull
  public final EditText editTextThemeName;

  @NonNull
  public final LinearLayout layoutInput;

  @NonNull
  public final LinearLayout layoutThemes;

  private ActivityMainBinding(@NonNull LinearLayout rootView, @NonNull Button buttonAddTheme,
      @NonNull Button buttonChooseSavePath, @NonNull Button buttonCreate,
      @NonNull Button buttonOpenJson, @NonNull EditText editTextThemeName,
      @NonNull LinearLayout layoutInput, @NonNull LinearLayout layoutThemes) {
    this.rootView = rootView;
    this.buttonAddTheme = buttonAddTheme;
    this.buttonChooseSavePath = buttonChooseSavePath;
    this.buttonCreate = buttonCreate;
    this.buttonOpenJson = buttonOpenJson;
    this.editTextThemeName = editTextThemeName;
    this.layoutInput = layoutInput;
    this.layoutThemes = layoutThemes;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button_add_theme;
      Button buttonAddTheme = ViewBindings.findChildViewById(rootView, id);
      if (buttonAddTheme == null) {
        break missingId;
      }

      id = R.id.button_choose_save_path;
      Button buttonChooseSavePath = ViewBindings.findChildViewById(rootView, id);
      if (buttonChooseSavePath == null) {
        break missingId;
      }

      id = R.id.button_create;
      Button buttonCreate = ViewBindings.findChildViewById(rootView, id);
      if (buttonCreate == null) {
        break missingId;
      }

      id = R.id.button_open_json;
      Button buttonOpenJson = ViewBindings.findChildViewById(rootView, id);
      if (buttonOpenJson == null) {
        break missingId;
      }

      id = R.id.edit_text_theme_name;
      EditText editTextThemeName = ViewBindings.findChildViewById(rootView, id);
      if (editTextThemeName == null) {
        break missingId;
      }

      id = R.id.layout_input;
      LinearLayout layoutInput = ViewBindings.findChildViewById(rootView, id);
      if (layoutInput == null) {
        break missingId;
      }

      id = R.id.layout_themes;
      LinearLayout layoutThemes = ViewBindings.findChildViewById(rootView, id);
      if (layoutThemes == null) {
        break missingId;
      }

      return new ActivityMainBinding((LinearLayout) rootView, buttonAddTheme, buttonChooseSavePath,
          buttonCreate, buttonOpenJson, editTextThemeName, layoutInput, layoutThemes);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
