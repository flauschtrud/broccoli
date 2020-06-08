package org.flauschhaus.broccoli.recipe;

import androidx.lifecycle.LiveData;

import org.flauschhaus.broccoli.category.Category;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecipeRepositoryTest {

    @Mock
    private RecipeDAO recipeDAO;

    @Mock
    private RecipeImageService recipeImageService;

    @Mock
    private LiveData<List<Recipe>> allRecipes;

    private RecipeRepository recipeRepository;

    private ArgumentCaptor<RecipeCategoryAssociation> associationCaptor = ArgumentCaptor.forClass(RecipeCategoryAssociation.class);

    private static Category newCategory = new Category("neu");
    {
        newCategory.setCategoryId(5L);
    }

    @Before
    public void setUp() {
        when(recipeDAO.findAll()).thenReturn(allRecipes);
        recipeRepository = new RecipeRepository(recipeDAO, recipeImageService);
    }

    @Test
    public void find_all_recipes() {
        LiveData<List<Recipe>> result = recipeRepository.findAll();
        assertThat(result, is(allRecipes));
    }

    @Test
    public void insert() throws ExecutionException, InterruptedException {
        Recipe recipe = new Recipe();
        recipe.addCategory(newCategory);
        doNothing().when(recipeDAO).insert(associationCaptor.capture());
        when(recipeDAO.insert(recipe.getCoreRecipe())).thenReturn(12L);
        
        CompletableFuture<RecipeRepository.InsertionType> completableFuture = recipeRepository.insertOrUpdate(recipe);
        RecipeRepository.InsertionType insertionType = completableFuture.get();

        assertThat(insertionType, is(RecipeRepository.InsertionType.INSERT));

        RecipeCategoryAssociation categoryAssociation = associationCaptor.getValue();
        assertThat(categoryAssociation.getCategoryId(), is(5L));
        assertThat(categoryAssociation.getRecipeId(), is(12L));
    }

    @Test
    public void update() throws ExecutionException, InterruptedException {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(12);
        recipe.addCategory(newCategory);

        doNothing().when(recipeDAO).insert(associationCaptor.capture());

        List<RecipeCategoryAssociation> oldAssociations = new ArrayList<>();
        RecipeCategoryAssociation oldAssociation = new RecipeCategoryAssociation(12L, 5L);
        oldAssociations.add(oldAssociation);
        when(recipeDAO.getCategoriesFor(12)).thenReturn(oldAssociations);

        CompletableFuture<RecipeRepository.InsertionType> completableFuture = recipeRepository.insertOrUpdate(recipe);
        RecipeRepository.InsertionType insertionType = completableFuture.get();

        assertThat(insertionType, is(RecipeRepository.InsertionType.UPDATE));
        verify(recipeDAO).update(recipe.getCoreRecipe());
        verify(recipeDAO).delete(oldAssociation);

        RecipeCategoryAssociation categoryAssociation = associationCaptor.getValue();
        assertThat(categoryAssociation.getCategoryId(), is(5L));
        assertThat(categoryAssociation.getRecipeId(), is(12L));
    }

    @Test
    public void delete() throws ExecutionException, InterruptedException {
        Recipe recipe = new Recipe();
        recipe.setImageName("blupp.jpg");

        when(recipeImageService.deleteImage("blupp.jpg")).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Void> completableFuture = recipeRepository.delete(recipe);
        completableFuture.get();

        verify(recipeImageService).deleteImage("blupp.jpg");
        verify(recipeDAO).delete(recipe.getCoreRecipe());
    }

}