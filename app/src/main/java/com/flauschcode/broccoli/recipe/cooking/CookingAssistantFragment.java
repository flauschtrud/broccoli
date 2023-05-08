package com.flauschcode.broccoli.recipe.cooking;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.databinding.FragmentCookingAssistantPageBinding;

import dagger.android.support.AndroidSupportInjection;

public class CookingAssistantFragment extends Fragment {

    static final String POSITION = "cooking_assistant_position";
    static final String MAX_STEPS = "cooking_assistant_max_steps";
    static final String TITLE = "cooking_assistant_title";
    static final String TEXT = "cooking_assistant_text";

    private FragmentCookingAssistantPageBinding binding;
    private CookingAssistantFragmentViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cooking_assistant_page, container, true);
        viewModel = new ViewModelProvider(this).get(CookingAssistantFragmentViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();

        TextView yourTextView = view.findViewById(R.id.cooking_assistant_text);
        yourTextView.setMovementMethod(new ScrollingMovementMethod());

        viewModel.setTitle(args.getString(TITLE));
        viewModel.setPosition(args.getInt(POSITION));
        viewModel.setMaxSteps(args.getInt(MAX_STEPS));
        viewModel.setText(args.getString(TEXT));

        binding.setViewModel(viewModel);

        CookingAssistantControls controls = view.findViewById(R.id.cooking_assistant_controls);
        controls.setOnCookingAssistantControlsInteractionListener((CookingAssistantActivity) getActivity());
    }

}
