package com.flauschcode.broccoli.recipe.cooking;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.databinding.DialogScalingBinding;
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

        DialogScalingBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_scaling, null, true);

        ScalingDialogViewModel scalingDialogViewModel = new ViewModelProvider(this).get(ScalingDialogViewModel.class);
        binding.setViewModel(scalingDialogViewModel);

        CookingModeViewModel viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(CookingModeViewModel.class);
        scalingDialogViewModel.setRecipe(viewModel.getRecipe());

        TextInputLayout textInputLayout = binding.layoutSimpleScaling;
        textInputLayout.setStartIconOnClickListener(v -> scalingDialogViewModel.decrementNumberOfServings());
        textInputLayout.setEndIconOnClickListener(v -> scalingDialogViewModel.incrementNumberOfServings());

        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.adjust_ingredients)
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, (dialog, id) -> scalingDialogViewModel.computeScaleFactor().ifPresent(viewModel::onScale))
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> {})
                .create();
    }

}
