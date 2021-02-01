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
import com.flauschcode.broccoli.databinding.FragmentCookingModePageBinding;

public class CookingModeFragment extends Fragment {

    static final String POSITION = "cooking_mode_position";
    static final String MAX_STEPS = "cooking_mode_max_steps";
    static final String TITLE = "cooking_mode_title";
    static final String TEXT = "cooking_mode_text";

    private FragmentCookingModePageBinding binding;
    private CookingModeFragmentViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cooking_mode_page, container, true);
        viewModel = new ViewModelProvider(this).get(CookingModeFragmentViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();

        TextView yourTextView = view.findViewById(R.id.cooking_mode_text);
        yourTextView.setMovementMethod(new ScrollingMovementMethod());

        viewModel.setTitle(args.getString(TITLE));
        viewModel.setText(args.getString(TEXT));
        viewModel.setPosition(args.getInt(POSITION));
        viewModel.setMaxSteps(args.getInt(MAX_STEPS));
        binding.setViewModel(viewModel);

        CookingModeControls controls = view.findViewById(R.id.cooking_mode_controls);
        controls.setOnCookingModeControlsInteractionListener((CookingModeActivity) getActivity());
    }

}
