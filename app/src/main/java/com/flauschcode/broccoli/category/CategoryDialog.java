package com.flauschcode.broccoli.category;

import static android.content.DialogInterface.BUTTON_NEUTRAL;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.databinding.DialogAddCategoryBinding;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class CategoryDialog extends AppCompatDialogFragment {

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

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_category, null);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(CategoryViewModel.class);
        category = (Category) getArguments().getSerializable("category");

        DialogAddCategoryBinding binding = DataBindingUtil.bind(view);
        binding.setCategory(category);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle(category.getCategoryId() == 0? R.string.dialog_add_category : R.string.dialog_edit_category)
                .setPositiveButton(R.string.action_save, (dialog, id) -> viewModel.insertOrUpdate(category))
                .setNegativeButton(R.string.cancel, (dialog, id) -> {});

        if(category.getCategoryId() != 0) {
             builder.setNeutralButton(R.string.action_delete, (dialog, id) -> {});
        }

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        AlertDialog dialog = (AlertDialog) getDialog();

        dialog.getButton(BUTTON_NEUTRAL).setOnClickListener(v -> {
            TextView warning = dialog.findViewById(R.id.delete_category_warning);
            if (warning.getVisibility() == View.VISIBLE) {
                viewModel.delete(category);
                dialog.dismiss();
            }
            warning.setVisibility(View.VISIBLE);

            dialog.getButton(BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(dialog.getContext(), R.color.design_default_color_error));
        });
    }

}