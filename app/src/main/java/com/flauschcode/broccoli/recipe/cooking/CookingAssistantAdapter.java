package com.flauschcode.broccoli.recipe.cooking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CookingAssistantAdapter extends FragmentStateAdapter {

    private PageableRecipe pageableRecipe;
    
    CookingAssistantAdapter(CookingAssistantActivity fragmentActivity) {
        super(fragmentActivity);
    }

    void setPageableRecipe(PageableRecipe pageableRecipe) {
        this.pageableRecipe = pageableRecipe;
    }

    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new CookingAssistantFragment();

        Bundle args = new Bundle();

        args.putInt(CookingAssistantFragment.POSITION, position);
        args.putInt(CookingAssistantFragment.MAX_STEPS, getItemCount());

        PageableRecipe.Page currentPage = pageableRecipe.getPages().get(position);
        args.putString(CookingAssistantFragment.TITLE, currentPage.getTitle());
        args.putString(CookingAssistantFragment.TEXT, currentPage.getText());

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return pageableRecipe.getPages().size();
    }
    
}
