package org.flauschhaus.broccoli.recipe.cooking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CookingModeAdapter extends FragmentStateAdapter {

    private PageableRecipe pageableRecipe;
    
    CookingModeAdapter(CookingModeActivity fragmentActivity) {
        super(fragmentActivity);
    }

    void setPageableRecipe(PageableRecipe pageableRecipe) {
        this.pageableRecipe = pageableRecipe;
    }

    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new CookingModeFragment();

        Bundle args = new Bundle();

        args.putInt(CookingModeFragment.POSITION, position + 1);
        args.putInt(CookingModeFragment.MAX_STEPS, getItemCount());

        PageableRecipe.Page currentPage = pageableRecipe.getPages().get(position);
        args.putString(CookingModeFragment.TITLE, currentPage.getTitle());
        args.putString(CookingModeFragment.TEXT, currentPage.getText());

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return pageableRecipe.getPages().size();
    }
    
}
