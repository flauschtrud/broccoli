package com.flauschcode.broccoli.recipe.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

// Timer-Import
import android.widget.TextView;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;
import com.flauschcode.broccoli.databinding.DialogTimerInputBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.app.AlertDialog;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import android.util.Log;

public class CookingAssistantActivity extends AppCompatActivity implements CookingAssistantControls.OnCookingAssistantControlsInteractionListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ViewPager2 viewPager;

    // Timer-Felder
    private TextView timerLabel;
    private Button buttonTimer;
    private int remainingSeconds;
    private int totalSeconds;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        setContentView(R.layout.activity_cooking_assistant);

        viewPager = findViewById(R.id.cooking_assistant_pager);
        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(view -> finish());

        Button scalingButton = findViewById(R.id.button_scaling);
        scalingButton.setOnClickListener(view -> showScalingDialog());

        Recipe recipe = (Recipe) getIntent().getSerializableExtra(Recipe.class.getName());
        CookingAssistantViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(CookingAssistantViewModel.class);
        viewModel.setRecipe(recipe);
        viewModel.getPageableRecipe().observe(this, this::setPageableRecipe);

        timerLabel = findViewById(R.id.timerLabel);
        buttonTimer = findViewById(R.id.button_timer);
        buttonTimer.setOnClickListener(v -> showTimerDialog());
    }

    private void setPageableRecipe(PageableRecipe pageableRecipe) {
        CookingAssistantAdapter adapter = new CookingAssistantAdapter(this);
        adapter.setPageableRecipe(pageableRecipe != null ? pageableRecipe : new PageableRecipe());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    public void onCookingAssistantControlsInteraction(int position) {
        viewPager.setCurrentItem(position);
    }

    public void navigateToSupportPage(View view) {
        Intent intent = new Intent();
        intent.putExtra("navigateToSupportPage", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private void showScalingDialog() {
        ScalingDialog scalingDialog = new ScalingDialog();
        scalingDialog.show(getSupportFragmentManager(), "ScalingDialogFragment");
    }

    private void hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (insetsController != null) {
            insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            insetsController.hide(WindowInsetsCompat.Type.statusBars());
            insetsController.hide(WindowInsetsCompat.Type.navigationBars());
        }
    }

    private void updateTimerLabel() {
        int safeSeconds = Math.max(0, remainingSeconds);
        int h = safeSeconds / 3600;
        int m = (safeSeconds % 3600) / 60;
        int s = safeSeconds % 60;
        if (timerLabel != null) {
            timerLabel.setText(getString(R.string.timer_label, h, m, s));
        }
    }

    private void showTimerDialog() {
        DialogTimerInputBinding binding = DialogTimerInputBinding.inflate(getLayoutInflater());
        configureNumberPickers(binding);

        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.timer_set_title))
            .setView(binding.getRoot())
            .setPositiveButton(getString(R.string.timer_start), (dialog, which) -> startTimer(binding))
            .setNegativeButton(getString(R.string.cancel_action), null)
            .show();
    }

    private void configureNumberPickers(DialogTimerInputBinding binding) {
        binding.pickerHours.setMinValue(0);
        binding.pickerHours.setMaxValue(23);
        binding.pickerMinutes.setMinValue(0);
        binding.pickerMinutes.setMaxValue(59);
        binding.pickerSeconds.setMinValue(0);
        binding.pickerSeconds.setMaxValue(59);
    }

    private void startTimer(DialogTimerInputBinding binding) {
        int hours = binding.pickerHours.getValue();
        int minutes = binding.pickerMinutes.getValue();
        int seconds = binding.pickerSeconds.getValue();
        totalSeconds = remainingSeconds = hours * 3600 + minutes * 60 + seconds;

        if (totalSeconds <= 0) {
            Toast.makeText(this, getString(R.string.timer_zero_error), Toast.LENGTH_SHORT).show();
            return;
        }

        updateTimerLabel();
        if (timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }

        timerRunnable = createTimerRunnable();
        handler.postDelayed(timerRunnable, 1000);
    }

    private Runnable createTimerRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (remainingSeconds > 0) {
                    remainingSeconds--;
                    updateTimerLabel();
                    handler.postDelayed(this, 1000);
                } else {
                    remainingSeconds = 0;
                    updateTimerLabel();
                    onTimerFinished();
                }
            }
        };
    }

    private void onTimerFinished() {
        Toast.makeText(this, getString(R.string.timer_ended), Toast.LENGTH_SHORT).show();
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        if (ringtone != null) {
            try {
                ringtone.play();
            } catch (Exception e) {
                Log.e("CookingAssistantActivity", getString(R.string.ringtone_play_error), e);
            }

            new Handler().postDelayed(() -> {
                if (ringtone.isPlaying()) {
                    ringtone.stop();
                }
            }, 6000);
        }
    }
}