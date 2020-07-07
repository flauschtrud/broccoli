package org.flauschhaus.broccoli.recipe.cooking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.flauschhaus.broccoli.R;

public class CookingModeFragment extends Fragment {

    static final String POSITION = "cooking_mode_position";
    static final String MAX_STEPS = "cooking_mode_max_steps";
    static final String TITLE = "cooking_mode_title";
    static final String TEXT = "cooking_mode_text";

    private SeekBar seekbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cooking_mode_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();

        ((TextView) view.findViewById(R.id.cooking_mode_title))
                .setText(args.getString(TITLE));
        ((TextView) view.findViewById(R.id.cooking_mode_text))
                .setText(args.getString(TEXT));

        seekbar = view.findViewById(R.id.cooking_mode_seekbar);
        seekbar.setMax(args.getInt(MAX_STEPS) - 1);
        seekbar.setOnSeekBarChangeListener((CookingModeActivity) getActivity());
        seekbar.setProgress(args.getInt(POSITION) - 1, false);

        if (args.getInt(MAX_STEPS) == 1) {
            seekbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle args = getArguments();
        seekbar.setProgress(args.getInt(POSITION) - 1, false);
    }

}
