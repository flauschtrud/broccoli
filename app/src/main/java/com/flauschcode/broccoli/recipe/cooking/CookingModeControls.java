package com.flauschcode.broccoli.recipe.cooking;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.R;

import java.util.stream.IntStream;

public class CookingModeControls extends LinearLayout {

    private int position = 0;
    private int maxSteps = 0;

    private OnCookingModeControlsInteractionListener listener;

    public CookingModeControls(Context context) {
        super(context);
    }

    public CookingModeControls(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAttributes(context, attrs, 0, 0);
        buildControl();
    }

    public CookingModeControls(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeAttributes(context, attrs, defStyleAttr, 0);
        buildControl();
    }

    public CookingModeControls(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeAttributes(context, attrs, defStyleAttr, defStyleRes);
        buildControl();
    }

    public void setPosition(int position) {
        this.position = position;
        buildControl();
        invalidate();
        requestLayout();
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
        buildControl();
        invalidate();
        requestLayout();
    }

    public void setOnCookingModeControlsInteractionListener(OnCookingModeControlsInteractionListener listener) {
        this.listener = listener;
    }

    private void initializeAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CookingModeControls, defStyleAttr, defStyleRes);
        try {
            position = a.getInteger(R.styleable.CookingModeControls_position, 0);
            maxSteps = a.getInteger(R.styleable.CookingModeControls_maxSteps, 0);
        } finally {
            a.recycle();
        }
    }

    private void buildControl() {
        removeAllViews();

        if (maxSteps == 1) {
            setVisibility(View.GONE);
            return;
        }

        IntStream.range(0, maxSteps).forEach(integer -> {
            LayoutInflater.from(getContext()).inflate(R.layout.cooking_mode_controls_item, this, true);
            ImageView imageView = getChildAt(integer).findViewById(R.id.cooking_mode_control);
            imageView.setContentDescription(String.valueOf(integer));
            if (integer == position) {
                imageView.setImageTintList(ContextCompat.getColorStateList(BroccoliApplication.getContext(), R.color.colorPrimary));
            }
            imageView.setOnClickListener(v -> listener.onCookingModeControlsInteraction(integer));
        });
    }

    public interface OnCookingModeControlsInteractionListener {
        void onCookingModeControlsInteraction(int position);
    }

}
