package org.flauschhaus.broccoli.recipes.images;

import android.graphics.Bitmap;
import android.widget.ImageView;

import org.flauschhaus.broccoli.R;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class ImageBindingAdapterTest {

    @Mock
    private RecipeImageService recipeImageService;

    @Mock
    private ImageView imageView;

    @Mock
    private Bitmap bitmap;

    @InjectMocks
    private ImageBindingAdapter imageBindingAdapter;

    @Test
    public void set_placeholder_resource_for_null_imagename() {
        imageBindingAdapter.bind(imageView, null);
        verify(imageView).setImageResource(R.drawable.ic_launcher_foreground);
    }

    @Test
    public void set_placeholder_resource_for_empty_imagename() {
        imageBindingAdapter.bind(imageView, "");
        verify(imageView).setImageResource(R.drawable.ic_launcher_foreground);
    }

    @Test
    public void load_bitmap_for_given_name() {
        when(recipeImageService.loadBitmapWithName("bluppiti.jpg")).thenReturn(bitmap);
        imageBindingAdapter.bind(imageView, "bluppiti.jpg");
        verify(imageView).setImageBitmap(bitmap);
    }

}