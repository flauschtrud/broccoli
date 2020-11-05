package com.flauschcode.broccoli.category;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.databinding.DataBindingUtil;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.databinding.DialogAddCategoryBinding;

import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class CategoryDialog extends AppCompatDialogFragment {

    private OnChangeListener onChangeListener;
    private Category category;

    public CategoryDialog(OnChangeListener onChangeListener, Category category) {
        this.onChangeListener = onChangeListener;
        this.category = new Category(category.getCategoryId(), category.getName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_category, null);

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
                AlertDialog dialog = (AlertDialog) getDialog();
                dialog.getButton(BUTTON_POSITIVE).setEnabled(s.length() > 0);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle(category.getCategoryId() == 0? R.string.dialog_add_category : R.string.dialog_edit_category)
                .setPositiveButton(R.string.action_save, (dialog, id) -> onChangeListener.saveCategory(category))
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
        dialog.getButton(BUTTON_POSITIVE).setEnabled(false);

        dialog.getButton(BUTTON_NEUTRAL).setOnClickListener(v -> {
            TextView warning = dialog.findViewById(R.id.delete_category_warning);
            if (warning.getVisibility() == View.VISIBLE) {
                onChangeListener.deleteCategory(category);
                dialog.dismiss();
            }
            warning.setVisibility(View.VISIBLE);
            dialog.getButton(BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.design_default_color_error));
        });
    }

    interface OnChangeListener {
        void saveCategory(Category category);
        void deleteCategory(Category category);
    }

}