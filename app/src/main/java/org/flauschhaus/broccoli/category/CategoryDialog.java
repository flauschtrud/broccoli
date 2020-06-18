package org.flauschhaus.broccoli.category;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.databinding.DataBindingUtil;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.databinding.DialogAddCategoryBinding;

public class CategoryDialog extends AppCompatDialogFragment {

    private OnSaveListener onSaveListener;
    private Category category;

    public CategoryDialog(OnSaveListener onSaveListener, Category category) {
        this.onSaveListener = onSaveListener;
        this.category = new Category(category.getCategoryId(), category.getName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_category, null);

        DialogAddCategoryBinding binding = DataBindingUtil.bind(view);
        binding.setCategory(category);

        EditText editText = view.findViewById(R.id.new_category_name);
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
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() > 0);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle(R.string.dialog_add_or_edit_category)
                .setPositiveButton(R.string.action_save, (dialog, id) -> onSaveListener.saveCategory(binding.getCategory()))
                .setNegativeButton(R.string.cancel, (dialog, id) -> {});

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    interface OnSaveListener {
        void saveCategory(Category category);
    }

}