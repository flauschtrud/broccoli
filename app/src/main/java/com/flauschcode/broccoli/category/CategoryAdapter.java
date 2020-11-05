package com.flauschcode.broccoli.category;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.flauschcode.broccoli.BR;
import com.flauschcode.broccoli.databinding.CategoryItemBinding;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryHolder> { // TODO generalize this and RecipeAdapter

    private CategoryAdapter.OnListFragmentInteractionListener listener;

    CategoryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK = new DiffUtil.ItemCallback<Category>() {

        @Override
        public boolean areItemsTheSame(Category oldItem, Category newItem) {
            return oldItem.getCategoryId() == newItem.getCategoryId();
        }

        @Override
        public boolean areContentsTheSame(Category oldItem, Category newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

    };

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CategoryItemBinding itemBinding = CategoryItemBinding.inflate(layoutInflater, parent, false);
        return new CategoryHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        Category currentCategory = getItem(position);
        holder.bind(currentCategory); holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListFragmentInteraction(currentCategory);
            }
        });
    }

    void setListener(OnListFragmentInteractionListener listener) {
        this.listener = listener;
    }

    class CategoryHolder extends RecyclerView.ViewHolder {

        private final CategoryItemBinding binding;

        CategoryHolder(CategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Object obj) {
            binding.setVariable(BR.category, obj);
            binding.executePendingBindings();
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Category category);
    }

}
