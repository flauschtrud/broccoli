package com.flauschcode.broccoli.recipe.cooking;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.flauschcode.broccoli.R;
import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class ScalingDialog extends DialogFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_scaling, null);

        CookingModeViewModel viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(CookingModeViewModel.class);
        Servings servings = Servings.createFrom(viewModel.getRecipe().getServings());

        EditText inputScaleFactor = view.findViewById(R.id.input_scale_factor);
        inputScaleFactor.setText(String.valueOf(servings.getQuantity()));

        TextInputLayout textInputLayout = view.findViewById(R.id.input_scale_layout);

        if (!servings.getLabel().isEmpty()) {
            textInputLayout.setHint(servings.getLabel());
        }

        textInputLayout.setStartIconOnClickListener(v -> {
            int value = Integer.parseInt(inputScaleFactor.getText().toString());
            value--;
            inputScaleFactor.setText(String.valueOf(value));
        });

        textInputLayout.setEndIconOnClickListener(v -> {
            int value = Integer.parseInt(inputScaleFactor.getText().toString());
            value++;
            inputScaleFactor.setText(String.valueOf(value));
        });

        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.scale_the_ingredients)
                .setMessage(R.string.scaling_question)
                .setView(view)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    if (!TextUtils.isEmpty(inputScaleFactor.getText())) {
                        float selectedServings = Float.parseFloat(inputScaleFactor.getText().toString());
                        viewModel.onScale(selectedServings/servings.getQuantity());
                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> {})
                .create();
    }

}
