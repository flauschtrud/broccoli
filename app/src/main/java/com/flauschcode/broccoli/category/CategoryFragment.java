package com.flauschcode.broccoli.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.flauschcode.broccoli.BR;
import com.flauschcode.broccoli.FeatureDiscoveryTargetBuilder;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class CategoryFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_categories, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        View emptyMessageTextView = root.findViewById(R.id.categories_empty);
        ListAdapter<Category, RecyclerViewAdapter<Category>.Holder> adapter = new RecyclerViewAdapter<Category>() {
            @Override
            protected int getLayoutResourceId() {
                return R.layout.category_item;
            }

            @Override
            protected int getBindingVariableId() {
                return BR.category;
            }

            @Override
            protected void onItemClick(Category item) {
                onListInteraction(item);
            }

            @Override
            protected void onAdapterDataChanged(int itemCount) {
                emptyMessageTextView.setVisibility(itemCount == 0? View.VISIBLE : View.GONE);
            }
        };

        recyclerView.setAdapter(adapter);

        CategoryViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(CategoryViewModel.class);
        viewModel.getCategories().observe(getViewLifecycleOwner(), adapter::submitList);

        FloatingActionButton fab = root.findViewById(R.id.fab_categories);
        fab.setOnClickListener(view -> CategoryDialog.newInstance(new Category("")).show(getParentFragmentManager(), "CategoryDialogFragment"));

        return root;
    }

    public void onListInteraction(Category category) {
        CategoryDialog.newInstance(category).show(getParentFragmentManager(), "CategoryDialogFragment");
    }
}
