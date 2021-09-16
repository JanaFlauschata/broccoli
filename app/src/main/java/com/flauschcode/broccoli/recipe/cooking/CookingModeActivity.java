package com.flauschcode.broccoli.recipe.cooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.flauschcode.broccoli.FeatureDiscoveryTargetBuilder;
import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.recipe.Recipe;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CookingModeActivity extends AppCompatActivity implements CookingModeControls.OnCookingModeControlsInteractionListener {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_cooking_mode);

        viewPager = findViewById(R.id.cooking_mode_pager);

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(view -> finish());

        Button scalingButton = findViewById(R.id.button_scaling);
        scalingButton.setOnClickListener(view -> showScalingDialog());

        FeatureDiscoveryTargetBuilder.buildInContextOf(this)
                .withTitle(getString(R.string.scale_the_ingredients))
                .withDescription(getString(R.string.scaling_question))
                .withTag("discover-scaling")
                .discoverIfNew(scalingButton);

        Recipe recipe = (Recipe) getIntent().getSerializableExtra(Recipe.class.getName());

        CookingModeViewModel viewModel = new ViewModelProvider(this, viewModelFactory).get(CookingModeViewModel.class);
        viewModel.setRecipe(recipe);
        viewModel.getPageableRecipe().observe(this, this::setPageableRecipe);
    }

    private void setPageableRecipe(PageableRecipe pageableRecipe) {
        CookingModeAdapter adapter = new CookingModeAdapter(this);
        adapter.setPageableRecipe(pageableRecipe != null? pageableRecipe : new PageableRecipe());

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
    public void onCookingModeControlsInteraction(int position) {
        viewPager.setCurrentItem(position);
    }

    public void navigateToSupportPage(View view) {
        Intent intent = new Intent();
        intent.putExtra("navigateToSupportPage", true);
        setResult(RESULT_OK, intent);
        finish();
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
}
