package com.flauschcode.broccoli.recipe;

import androidx.lifecycle.LiveData;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.RecipeCategoryAssociation;
import com.flauschcode.broccoli.recipe.RecipeDAO;
import com.flauschcode.broccoli.recipe.RecipeRepository;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;
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
import static org.hamcrest.core.IsIterableContaining.hasItem;
import static org.mockito.ArgumentMatchers.eq;
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
    private LiveData<List<Recipe>> recipes;

    private RecipeRepository recipeRepository;

    private ArgumentCaptor<RecipeCategoryAssociation> associationCaptor = ArgumentCaptor.forClass(RecipeCategoryAssociation.class);
    private ArgumentCaptor<List<Boolean>> favoriteStatesCaptor = ArgumentCaptor.forClass(List.class);
    private RecipeRepository.SearchCriteria criteria;

    private static Category newCategory = new Category("neu");
    static {
        newCategory.setCategoryId(5L);
    }

    @Before
    public void setUp() {
        recipeRepository = new RecipeRepository(recipeDAO, recipeImageService);
        criteria = new RecipeRepository.SearchCriteria();
    }

    @Test
    public void find_all_recipes() {
        when(recipeDAO.findAll(favoriteStatesCaptor.capture())).thenReturn(recipes);

        LiveData<List<Recipe>> result = recipeRepository.find(criteria);

        List<Boolean> favoriteStates = favoriteStatesCaptor.getValue();
        assertThat(favoriteStates.size(), is(2));
        assertThat(favoriteStates, hasItem(Boolean.TRUE));
        assertThat(favoriteStates, hasItem(Boolean.FALSE));
        assertThat(result, is(recipes));
    }

    @Test
    public void find_all_favorites() {
        when(recipeDAO.findAll(favoriteStatesCaptor.capture())).thenReturn(recipes);

        criteria.setCategory(Category.FAVORITES);
        LiveData<List<Recipe>> result = recipeRepository.find(criteria);

        List<Boolean> favoriteStates = favoriteStatesCaptor.getValue();
        assertThat(favoriteStates.size(), is(1));
        assertThat(favoriteStates, hasItem(Boolean.TRUE));
        assertThat(result, is(recipes));
    }

    @Test
    public void find_all_unassigned() {
        when(recipeDAO.findUnassigned()).thenReturn(recipes);

        criteria.setCategory(Category.UNASSIGNED);

        LiveData<List<Recipe>> result = recipeRepository.find(criteria);
        assertThat(result, is(recipes));
    }

    @Test
    public void filter_by() {
        when(recipeDAO.filterBy(5L)).thenReturn(recipes);

        criteria.setCategory(new Category(5L, "blupp"));

        LiveData<List<Recipe>> result = recipeRepository.find(criteria);
        assertThat(result, is(recipes));
    }

    @Test
    public void search_for() {
        when(recipeDAO.searchFor(eq("bla*"), favoriteStatesCaptor.capture())).thenReturn(recipes);

        criteria.setSearchTerm("bla");

        LiveData<List<Recipe>> result = recipeRepository.find(criteria);

        List<Boolean> favoriteStates = favoriteStatesCaptor.getValue();
        assertThat(favoriteStates.size(), is(2));
        assertThat(favoriteStates, hasItem(Boolean.TRUE));
        assertThat(favoriteStates, hasItem(Boolean.FALSE));
        assertThat(result, is(recipes));
    }

    @Test
    public void search_in_favorites() {
        when(recipeDAO.searchFor(eq("bla*"), favoriteStatesCaptor.capture())).thenReturn(recipes);

        criteria.setSearchTerm("bla");
        criteria.setCategory(Category.FAVORITES);

        LiveData<List<Recipe>> result = recipeRepository.find(criteria);

        List<Boolean> favoriteStates = favoriteStatesCaptor.getValue();
        assertThat(favoriteStates.size(), is(1));
        assertThat(favoriteStates, hasItem(Boolean.TRUE));
        assertThat(result, is(recipes));
    }

    @Test
    public void filter_and_search_for() {
        when(recipeDAO.filterByAndSearchFor(5L, "bla*")).thenReturn(recipes);

        criteria.setCategory(new Category(5L, "blupp"));
        criteria.setSearchTerm("bla");

        LiveData<List<Recipe>> result = recipeRepository.find(criteria);
        assertThat(result, is(recipes));
    }

    @Test
    public void search_for_unassigned() {
        when(recipeDAO.searchForUnassigned("bla*")).thenReturn(recipes);

        criteria.setCategory(Category.UNASSIGNED);
        criteria.setSearchTerm("bla");

        LiveData<List<Recipe>> result = recipeRepository.find(criteria);
        assertThat(result, is(recipes));
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