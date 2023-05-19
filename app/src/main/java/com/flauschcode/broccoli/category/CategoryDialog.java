package com.flauschcode.broccoli.category;

import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.databinding.DialogAddCategoryBinding;
import com.google.android.material.color.MaterialColors;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class CategoryDialog extends DialogFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private Category category;
    private CategoryViewModel viewModel;

    public static CategoryDialog newInstance(Category category) {
        Bundle args = new Bundle();
        args.putSerializable("category", new Category(category.getCategoryId(), category.getName()));
        CategoryDialog f = new CategoryDialog();
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_category, null);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(CategoryViewModel.class);
        category = (Category) getArguments().getSerializable("category");

        DialogAddCategoryBinding binding = DataBindingUtil.bind(view);
        binding.setCategory(category);

        EditText editText = view.findViewById(R.id.category_name);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // intentionally empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // intentionally empty
            }

            @Override
            public void afterTextChanged(Editable s) {
                AlertDialog dialog = (AlertDialog) requireDialog();
                dialog.getButton(BUTTON_POSITIVE).setEnabled(s.length() > 0);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle(category.getCategoryId() == 0? R.string.add_category : R.string.edit_category)
                .setPositiveButton(R.string.save_action, (dialog, id) -> viewModel.insertOrUpdate(category))
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> {});

        if(category.getCategoryId() != 0) {
             builder.setNeutralButton(R.string.delete_action, (dialog, id) -> {});
        }

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        AlertDialog dialog = (AlertDialog) requireDialog();

        EditText editText = getDialog().findViewById(R.id.category_name);
        dialog.getButton(BUTTON_POSITIVE).setEnabled(editText.getText().length() > 0);

        dialog.getButton(BUTTON_NEUTRAL).setOnClickListener(v -> {
            TextView warning = dialog.findViewById(R.id.delete_category_warning);
            if (warning.getVisibility() == View.VISIBLE) {
                viewModel.delete(category);
                dialog.dismiss();
            }
            warning.setVisibility(View.VISIBLE);

            dialog.getButton(BUTTON_NEUTRAL).setTextColor(MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorError, Color.RED));
        });
    }

}