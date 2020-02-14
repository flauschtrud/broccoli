package org.flauschhaus.broccoli.ui.recipes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecipesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RecipesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hello Broccoli!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}