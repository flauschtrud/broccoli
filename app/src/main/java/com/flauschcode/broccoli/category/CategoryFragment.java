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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.flauschcode.broccoli.R;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class CategoryFragment extends Fragment implements CategoryDialog.OnChangeListener, CategoryAdapter.OnListFragmentInteractionListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CategoryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_categories, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        CategoryAdapter adapter = new CategoryAdapter();
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this, viewModelFactory).get(CategoryViewModel.class);
        viewModel.getCategories().observe(getViewLifecycleOwner(), adapter::submitList);

        FloatingActionButton fab = root.findViewById(R.id.fab_categories);
        fab.setOnClickListener(view -> {
            CategoryDialog dialog = new CategoryDialog(this, new Category(""));
            dialog.show(getParentFragmentManager(), "CategoryDialogTag");
        });

        return root;
    }

    @Override
    public void saveCategory(Category category) {
        viewModel.insertOrUpdate(category);
    }

    @Override
    public void deleteCategory(Category category) {
        viewModel.delete(category);
    }

    @Override
    public void onListFragmentInteraction(Category category) {
        CategoryDialog dialog = new CategoryDialog(this, category);
        dialog.show(getParentFragmentManager(), "CategoryDialogTag");
    }
}
