package org.flauschhaus.broccoli.recipes.images;

import android.widget.ImageView;

import org.flauschhaus.broccoli.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ImageBindingAdapterTest {

    @Mock
    private ImageView imageView;

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

}