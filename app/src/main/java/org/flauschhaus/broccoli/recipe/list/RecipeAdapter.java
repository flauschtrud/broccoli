package org.flauschhaus.broccoli.recipe.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.flauschhaus.broccoli.databinding.RecipeItemBinding;
import org.flauschhaus.broccoli.recipe.Recipe;

public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.RecipeHolder> {

    private OnListFragmentInteractionListener listener;

    RecipeAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Recipe> DIFF_CALLBACK = new DiffUtil.ItemCallback<Recipe>() {

        @Override
        public boolean areItemsTheSame(Recipe oldItem, Recipe newItem) {
            return oldItem.getRecipeId() == newItem.getRecipeId();
        }

        @Override
        public boolean areContentsTheSame(Recipe oldItem, Recipe newItem) {
            return oldItem.getImageName().equals(newItem.getImageName()) &&
                    oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription());
        }

    };

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecipeItemBinding itemBinding = RecipeItemBinding.inflate(layoutInflater, parent, false);
        return new RecipeHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        Recipe currentRecipe = getItem(position);
        holder.bind(currentRecipe);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListFragmentInteraction(currentRecipe);
            }
        });
    }

    void setListener(OnListFragmentInteractionListener listener) {
        this.listener = listener;
    }

    class RecipeHolder extends RecyclerView.ViewHolder {

        private final RecipeItemBinding binding;

        RecipeHolder(RecipeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Object obj) {
            binding.setVariable(org.flauschhaus.broccoli.BR.recipe, obj);
            binding.executePendingBindings();
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Recipe recipe);
    }
}
