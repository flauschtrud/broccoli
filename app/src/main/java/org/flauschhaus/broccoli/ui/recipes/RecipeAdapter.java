package org.flauschhaus.broccoli.ui.recipes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.Recipe;

public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.RecipeHolder> {

    private OnListFragmentInteractionListener listener;

    RecipeAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Recipe> DIFF_CALLBACK = new DiffUtil.ItemCallback<Recipe>() {

        @Override
        public boolean areItemsTheSame(Recipe oldItem, Recipe newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(Recipe oldItem, Recipe newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription());
        }

    };

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);
        return new RecipeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        Recipe currentRecipe = getItem(position);
        holder.textViewTitle.setText(currentRecipe.getTitle());
        holder.textViewDescription.setText(currentRecipe.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListFragmentInteraction(getItem(position));
            }
        });
    }

    void setListener(OnListFragmentInteractionListener listener) {
        this.listener = listener;
    }

    class RecipeHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewDescription;

        RecipeHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.card_text_view_title);
            textViewDescription = itemView.findViewById(R.id.card_text_view_description);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Recipe recipe);
    }
}
