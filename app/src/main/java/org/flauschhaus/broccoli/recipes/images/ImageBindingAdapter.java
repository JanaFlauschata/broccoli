package org.flauschhaus.broccoli.recipes.images;

import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import org.flauschhaus.broccoli.R;

import javax.inject.Inject;

public class ImageBindingAdapter {

    private RecipeImageService recipeImageService;

    @Inject
    public ImageBindingAdapter(RecipeImageService recipeImageService) {
        this.recipeImageService = recipeImageService;
    }

    @BindingAdapter("imageName")
    public void bind(ImageView imageView, String imageName) {
        if (imageName == null || imageName.length() == 0) {
            imageView.setImageResource(R.drawable.ic_launcher_foreground);
        }
        else {
            imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    imageView.setImageBitmap(recipeImageService.loadBitmapWithName(imageName, imageView.getWidth(), imageView.getHeight()));
                }
            });
        }
    }

}
