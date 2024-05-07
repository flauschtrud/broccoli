package com.flauschcode.broccoli.recipe;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsIterableContaining.hasItem;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.lifecycle.LiveData;

import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;
import com.flauschcode.broccoli.seasons.SeasonalCalendar;
import com.flauschcode.broccoli.seasons.SeasonalCalendarHolder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RunWith(MockitoJUnitRunner.class)
public class RecipeRepositoryTest {

    @Mock
    private RecipeDAO recipeDAO;

    @Mock
    private RecipeImageService recipeImageService;

    @Mock
    private SeasonalCalendarHolder seasonalCalendarHolder;

    @Mock
    private SeasonalCalendar seasonalCalendar;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LiveData<List<Recipe>> recipes;

    @InjectMocks
    private RecipeRepository recipeRepository;

    private final ArgumentCaptor<RecipeCategoryAssociation> associationCaptor = ArgumentCaptor.forClass(RecipeCategoryAssociation.class);
    private final ArgumentCaptor<List<Boolean>> favoriteStatesCaptor = ArgumentCaptor.forClass(List.class);
    private RecipeRepository.SearchCriteria criteria;

    private final Category CATEGORY_ALL = new Category(-1, "All recipes");
    private final Category CATEGORY_FAVORITES = new Category(-2, "Favorites");
    private final Category CATEGORY_UNASSIGNED = new Category(-3, "Unassigned recipes");
    private final Category CATEGORY_SEASONAL = new Category(-4, "Seasonal recipes");

    private static final Category newCategory = new Category("neu");
    static {
        newCategory.setCategoryId(5L);
    }

    @Before
    public void setUp() {
        when(categoryRepository.getAllRecipesCategory()).thenReturn(CATEGORY_ALL);
        when(categoryRepository.getFavoritesCategory()).thenReturn(CATEGORY_FAVORITES);
        when(categoryRepository.getUnassignedRecipesCategory()).thenReturn(CATEGORY_UNASSIGNED);
        when(categoryRepository.getSeasonalRecipesCategory()).thenReturn(CATEGORY_SEASONAL);

        criteria = new RecipeRepository.SearchCriteria(CATEGORY_ALL, "", new ArrayList<>());
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

        criteria.setCategory(CATEGORY_FAVORITES);
        LiveData<List<Recipe>> result = recipeRepository.find(criteria);

        List<Boolean> favoriteStates = favoriteStatesCaptor.getValue();
        assertThat(favoriteStates.size(), is(1));
        assertThat(favoriteStates, hasItem(Boolean.TRUE));
        assertThat(result, is(recipes));
    }

    @Test
    public void find_all_unassigned() {
        when(recipeDAO.findUnassigned()).thenReturn(recipes);

        criteria.setCategory(CATEGORY_UNASSIGNED);

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
    public void search_for_leading_hyphen() {
        when(recipeDAO.searchFor(eq("bla blupp*"), favoriteStatesCaptor.capture())).thenReturn(recipes);

        criteria.setSearchTerm("-bla \"blupp\"");

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
        criteria.setCategory(CATEGORY_FAVORITES);

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

        criteria.setCategory(CATEGORY_UNASSIGNED);
        criteria.setSearchTerm("bla");

        LiveData<List<Recipe>> result = recipeRepository.find(criteria);
        assertThat(result, is(recipes));
    }

    @Test
    public void find_seasonal_recipes() {
        when(recipeDAO.findSeasonal("\"apples\" OR \"leek\"")).thenReturn(recipes);
        when(seasonalCalendarHolder.get()).thenReturn(Optional.of(seasonalCalendar));
        Set<String> searchTerms = new HashSet<>();
        searchTerms.add("apples");
        searchTerms.add("leek");
        when(seasonalCalendar.getSearchTermsForCurrentMonth()).thenReturn(searchTerms);

        criteria.setCategory(CATEGORY_SEASONAL);
        criteria.setSearchTerm("");

        LiveData<List<Recipe>> result = recipeRepository.find(criteria);

        assertThat(result, is(recipes));
    }

    @Test
    public void find_and_search_for_seasonal_recipes() {
        when(recipeDAO.searchForSeasonal("\"apples\" OR \"leek\"", "bla*")).thenReturn(recipes);
        when(seasonalCalendarHolder.get()).thenReturn(Optional.of(seasonalCalendar));
        Set<String> searchTerms = new HashSet<>();
        searchTerms.add("apples");
        searchTerms.add("leek");
        when(seasonalCalendar.getSearchTermsForCurrentMonth()).thenReturn(searchTerms);

        criteria.setCategory(CATEGORY_SEASONAL);
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
        
        CompletableFuture<Long> completableFuture = recipeRepository.insertOrUpdate(recipe);
        Long recipeId = completableFuture.get();

        assertThat(recipeId, is(12L));

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

        CompletableFuture<Long> completableFuture = recipeRepository.insertOrUpdate(recipe);
        Long recipeId = completableFuture.get();

        assertThat(recipeId, is(12L));
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