package org.flauschhaus.broccoli.recipe.images;

import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Environment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import id.zelory.compressor.Compressor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecipeImageServiceTest {

    @Rule
    public TemporaryFolder picturesDirectory = new TemporaryFolder();

    @Rule
    public TemporaryFolder cacheDirectory = new TemporaryFolder();

    @Rule
    public TemporaryFolder publicDirectory = new TemporaryFolder();

    @Mock
    private Application application;

    @Mock
    private ContentResolver contentResolver;

    @Mock
    private Compressor compressor;

    @InjectMocks
    private RecipeImageService recipeImageService;

    private File savedImage;
    private File cachedImage;

    @Before
    public void setUp() throws IOException {
        when(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).thenReturn(picturesDirectory.getRoot());
        when(application.getCacheDir()).thenReturn(cacheDirectory.getRoot());

        savedImage = picturesDirectory.newFile("bla.jpg");
        cachedImage = cacheDirectory.newFile("blupp.jpg");
    }

    @Test
    public void move_image() throws ExecutionException, InterruptedException, IOException {
        File compressedImage = cacheDirectory.newFile("compressed.jpg");
        when(compressor.compressToFile(cachedImage)).thenReturn(compressedImage);

        CompletableFuture<Void> completableFuture = recipeImageService.moveImage("blupp.jpg");
        completableFuture.get();

        assertThat(new File(picturesDirectory.getRoot().getPath() + File.separator + "blupp.jpg").exists(), is(true));
        assertThat(cachedImage.exists(), is(false));
        assertThat(compressedImage.exists(), is(false));
    }

    @Test
    public void copy_image() throws ExecutionException, InterruptedException, IOException {
        Uri uri = Uri.EMPTY;
        File pickedImage = publicDirectory.newFile("picked.jpg");
        when(application.getContentResolver()).thenReturn(contentResolver);
        when(contentResolver.openInputStream(uri)).thenReturn(new FileInputStream(pickedImage));

        CompletableFuture<String> completableFuture = recipeImageService.copyImage(uri);
        String imageName = completableFuture.get();

        assertThat(new File(cacheDirectory.getRoot().getPath() + File.separator + imageName).exists(), is(true));
    }

    @Test
    public void find_saved_image() {
        File image = recipeImageService.findImage("bla.jpg");
        assertThat(image, is(savedImage));
    }

    @Test
    public void find_temporary_image() {
        File image = recipeImageService.findImage("blupp.jpg");
        assertThat(image, is(cachedImage));
    }

    @Test
    public void delete_saved_image() throws IOException, ExecutionException, InterruptedException {
        File deleteMe = picturesDirectory.newFile("deleteMe.jpg");

        CompletableFuture<Boolean> completableFuture = recipeImageService.deleteImage("deleteMe.jpg");
        completableFuture.get();

        assertThat(deleteMe.exists(), is(false));
    }

    @Test
    public void delete_temporary_image() throws IOException, ExecutionException, InterruptedException {
        File deleteMe = cacheDirectory.newFile("deleteMe.jpg");

        CompletableFuture<Boolean> completableFuture = recipeImageService.deleteTemporaryImage("deleteMe.jpg");
        completableFuture.get();

        assertThat(deleteMe.exists(), is(false));
    }
}