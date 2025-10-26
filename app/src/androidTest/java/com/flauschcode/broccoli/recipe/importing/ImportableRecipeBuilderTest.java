package com.flauschcode.broccoli.recipe.importing;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.flauschcode.broccoli.recipe.importing.ImportableRecipeExamples.MINIMIZED_RECIPE_JSONLD;
import static com.flauschcode.broccoli.recipe.importing.ImportableRecipeExamples.RECIPE_ARRIFIED_IMAGES;
import static com.flauschcode.broccoli.recipe.importing.ImportableRecipeExamples.RECIPE_ARRIFIED_TYPE;
import static com.flauschcode.broccoli.recipe.importing.ImportableRecipeExamples.RECIPE_CHEFKOCH;
import static com.flauschcode.broccoli.recipe.importing.ImportableRecipeExamples.RECIPE_YOAST;
import static com.flauschcode.broccoli.recipe.importing.ImportableRecipeExamples.RECIPE_YOAST_WITH_SECTIONS;
import static com.flauschcode.broccoli.recipe.importing.ImportableRecipeExamples.URL_ARRIFIED_IMAGES;
import static com.flauschcode.broccoli.recipe.importing.ImportableRecipeExamples.URL_ARRIFIED_TYPE;
import static com.flauschcode.broccoli.recipe.importing.ImportableRecipeExamples.URL_CHEFKOCH;
import static com.flauschcode.broccoli.recipe.importing.ImportableRecipeExamples.URL_YOAST;
import static com.flauschcode.broccoli.recipe.importing.ImportableRecipeExamples.URL_YOAST_WITH_SECTIONS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.DaggerMockApplicationComponent;
import com.flauschcode.broccoli.MockApplicationComponent;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javax.inject.Inject;

/*
    This is not a proper unit test because JSONObject would be the mocked version of the Android SDK in a unit test
 */
@RunWith(AndroidJUnit4.class)
public class ImportableRecipeBuilderTest {

    @Inject
    Application application;

    @Inject
    RecipeImageService recipeImageService;

    ImportableRecipeBuilder recipeBuilder;

    @Before
    public void setUp() {
        AccessibilityChecks.enable();

        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());

        recipeBuilder = new ImportableRecipeBuilder(application, recipeImageService);
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void example_chefkoch() throws JSONException, IOException {
        when(recipeImageService.downloadToCache(new URL("https://img.chefkoch-cdn.de/rezepte/3212051478029180/bilder/1325560/crop-960x540/vegane-chocolate-chip-cookies.jpg"))).thenReturn("blablupp.jpg");

        Optional<Recipe> optionalRecipe = recipeBuilder
                .withRecipeJsonLd(new JSONObject(RECIPE_CHEFKOCH))
                .from(URL_CHEFKOCH)
                .build();

        assertThat(optionalRecipe.isPresent(), is(true));

        Recipe recipe = optionalRecipe.get();
        assertThat(recipe.getTitle(), is("Vegane Chocolate Chip Cookies"));
        assertThat(recipe.getSource(), is(URL_CHEFKOCH));
        assertThat(recipe.getServings(), is("1 Portion(en)"));
        assertThat(recipe.getPreparationTime(), is("45m"));
        assertThat(recipe.getDescription(), is("Vegane Chocolate Chip Cookies - außen kross, innen weich, lecker und vegan, ergibt 35 Stück. Über 110 Bewertungen und für sehr lecker befunden. Mit ► Portionsrechner ► Kochbuch ► Video-Tipps!"));
        assertThat(recipe.getIngredients(), is("20 g Chiasamen\n50 ml Wasser\n190 g Butterersatz oder Margarine, vegan\n200 g Zucker , braun, alternativ Rohrzucker\n2 TL Zuckerrübensirup , alternativ Melasse, Ahornsirup oder Agavendicksaft\n2 Pck. Vanillezucker\n300 g Weizenmehl oder Dinkelmehl, oder gemischt\n4 g Natron\nn. B. Salz\n200 g Blockschokolade , zartbitter oder Schokotröpfchen"));
        assertThat(recipe.getDirections(), is("Den Backofen auf 180 °C Umluft vorheizen. Die Chiasamen und das Wasser in einer kleinen Schüssel vermengen und ca. 10 Minuten quellen lassen.\n\nEin Backblech mit Backpapier auslegen. Vegane Butter bzw. Margarine und Zucker mit den Schneebesen des Rührgeräts cremig verrühren. Dann die gequollenen Chiasamen, den Zuckerrübensirup und beide Päckchen Vanillezucker dazugeben und weiter rühren. Unter weiterem Rühren jetzt zuerst das Mehl hinzugeben und anschließend Natron sowie Salz. Alternativ - oder falls der Teig zu zäh ist - kann alles auch mit den Händen verknetet werden. Abschließend die Schokotröpfchen bzw. die gehackte Blockschokolade untermischen.\n\nDen nun fertigen Teig mit einem Esslöffel oder Eisportionierer klecksweise im Abstand von etwa 5 - 6 cm auf das Backpapier geben. Die Teigkleckse können - müssen jedoch nicht - mit einem Löffel noch etwas rund geformt und flach gedrückt werden.\n\nDie Cookies bei 180 °C Umluft maximal 15 Minuten backen, da sie sonst zu fest werden."));
        assertThat(recipe.getNutritionalValues(), is("Serving: 1\nCalories: 4594 kcal\nFat: 225,86g\nCarbohydrates: 540,65g\nProtein: 77,59g"));
        assertThat(recipe.getImageName(), is("blablupp.jpg"));
    }

    @Test
    public void example_yoast() throws JSONException, IOException {
        when(recipeImageService.downloadToCache(new URL("https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B.jpg"))).thenReturn("blablupp.jpg");

        Optional<Recipe> optionalRecipe = recipeBuilder
                .withRecipeJsonLd(new JSONObject(RECIPE_YOAST))
                .from(URL_YOAST)
                .build();

        assertThat(optionalRecipe.isPresent(), is(true));

        Recipe recipe = optionalRecipe.get();
        assertThat(recipe.getTitle(), is("Vegane Fenchel-Pasta"));
        assertThat(recipe.getSource(), is(URL_YOAST));
        assertThat(recipe.getServings(), is("2"));
        assertThat(recipe.getPreparationTime(), is("10m"));
        assertThat(recipe.getDescription(), is("Pasta mit geröstetem Fenchel und Zitrone – ein herrlich leichtes Pastagericht, in nur 10 Minuten fertig zubereitet."));
        assertThat(recipe.getIngredients(), is("1 Knolle Fenchel\n1 Bio-Zitrone\n1 Knoblauchzehe, geschält und fein gehackt\n1 Schalotte, geschält und fein gehackt\n1 Handvoll Petersilie, frisch, fein gehackt\n5 EL Semmelbrösel\nOlivenöl\nSalz & Pfeffer\n250 g Pasta (z.B. Linguini)"));
        assertThat(recipe.getDirections(), is("Vom Fenchel die oberen grünen Stängel entfernen. Dabei unbedingt das Fenchelgrün aufbewahren. Das kommt später an die Pasta ran. Die Knolle halbieren, den Strunk in der Mitte keilförmig entfernen und nun die zwei Hälften in dünne Streifen schneiden. Die Fenchel-Streifen waschen und gut abtrocknen.\nDie Pasta nach Packungsanweisung garen.\nIn der Zwischenzeit in einer großen Pfanne Olivenöl erhitzen und den Fenchel darin mit etwas Salz ca. 8 Minuten lang anrösten.\nIn einer kleinen Pfanne etwas Olivenöl erhitzen, Knoblauch- und Schalottenwürfel darin mit etwas Salz glasig andünsten. Zitronenabrieb, Petersilie und die Semmelbrösel hinzugeben und alles ca. 4 Minuten vorsichtig anrösten bis die Semmelbrösel leicht angebräunt sind. Die Mischung vom Herd nehmen.\nDie fertige Pasta abgießen. Dabei etwa ein halbes Wasserglas der Kochflüssigkeit auffangen. Abgetropfte Pasta zum Fenchel geben. Die Semmelbröselmischung dazugeben. Kochflüssigkeit nach Belieben dazugeben, damit die Pasta schön glänzend wird. Pasta ordentlich salzen und pfeffern und nach Geschmack Zitrone hinzugeben. Den Saft einer halben Zitrone verträgt das Gericht mindestens. Mit einem guten Schuss Olivenöl und mit Fenchelgrün bestreut servieren."));
        assertThat(recipe.getNutritionalValues(), is(""));
        assertThat(recipe.getImageName(), is("blablupp.jpg"));
    }

    @Test
    public void example_yoast_with_sections() throws JSONException, IOException {
        when(recipeImageService.downloadToCache(new URL("https://veggie-einhorn.de/wp-content/uploads/Einfacher-veganer-Zitronenkuchen-saftig.jpg"))).thenReturn("blablupp.jpg");

        Optional<Recipe> optionalRecipe = recipeBuilder
                .withRecipeJsonLd(new JSONObject(RECIPE_YOAST_WITH_SECTIONS))
                .from(URL_YOAST_WITH_SECTIONS)
                .build();

        assertThat(optionalRecipe.isPresent(), is(true));

        Recipe recipe = optionalRecipe.get();
        assertThat(recipe.getTitle(), is("Saftiger Veganer Zitronenkuchen"));
        assertThat(recipe.getSource(), is(URL_YOAST_WITH_SECTIONS));
        assertThat(recipe.getServings(), is("1"));
        assertThat(recipe.getPreparationTime(), is("1h 15m"));
        assertThat(recipe.getDescription(), is("▶ Super saftiger veganer Zitronenkuchen ▶ Einfaches, sehr leckeres Rezept ▶ Kommt immer gut an▶ Für 30 cm Kastenbackform."));
        assertThat(recipe.getIngredients(), is("300 g Mehl\n200 g Zucker\n1 Tüte Backpulver\n1 Tüte Vanillezucker\n250 ml Vanille-Sojamilch\n100 g Öl (Rapsöl oder Sonnenblumenöl)\n50 ml Zitronensaft\ngeriebene Zitronenschale\nevtl. Backaroma Zitrone\n150 g Puderzucker\n2 EL Zitronensaft"));
        assertThat(recipe.getDirections(), is("VORBEREITEN: Kastenbackform mit Backpapier auslegen oder einfetten. 2 Zitronen abreiben und auspressen. Backofen vorheizen auf 180 °C (Ober-Unter-Hitze).\nTROCKENE ZUTATEN MISCHEN: In eine Schüssel geben:300 g Mehl 200 g Zucker1 Tüte Backpulver1 Tüte VanillezuckerAbgeriebene ZitronenschaleGut mischen.\nFEUCHTE ZUTATEN ZUFÜGEN: 250 ml Sojamilch Vanille100 g Öl50 ml Zitronensaft Evtl. weiteres Zitronenaroma zufügen(Backaroma oder Zitronenöl)\nTEIG MIXEN UND IN BACKFORM FÜLLEN: Kurz gut durchmixen. In die vorbereitete Kastenbackform füllen.\nBACKEN: Im vorgeheizten Ofen bei 180 °C (Ober-Unter-Hitze) 60 Minuten backen.(Stäbchenprobe) Kuchen in der Form abkühlen lassen.\nZITRONENGLASUR: 150 g Puderzucker sieben.Mit 2 EL Zitronensaft verrühren.Auf dem Kuchen verteilen."));
        assertThat(recipe.getNutritionalValues(), is("Serving: 1 Stück (bei 12 Stücken)\nCalories: 292 kcal\nFat: 8.4 g\nCarbohydrates: 49 g\nProtein: 3.8 g"));
        assertThat(recipe.getImageName(), is("blablupp.jpg"));
    }

    @Test
    public void no_Recipe_JsonLd() {
        Optional<Recipe> optionalRecipe = recipeBuilder.build();
        assertThat(optionalRecipe.isPresent(), is(false));
    }

    @Test
    public void missing_attributes() throws JSONException {
        Optional<Recipe> optionalRecipe = recipeBuilder
                .withRecipeJsonLd(new JSONObject(MINIMIZED_RECIPE_JSONLD))
                .from(URL_CHEFKOCH)
                .build();

        assertThat(optionalRecipe.isPresent(), is(true));

        Recipe recipe = optionalRecipe.get();
        assertThat(recipe.getTitle(), is("Vegane Chocolate Chip Cookies"));
        assertThat(recipe.getSource(), is(URL_CHEFKOCH));
    }

    /*
        see https://github.com/flauschtrud/broccoli/issues/189
     */
    @Test
    public void arrified_type() throws JSONException, IOException {
        when(recipeImageService.downloadToCache(new URL("https://www.allrecipes.com/thmb/5SN7ROsF_d5tEiEUj5Vay0TD62g=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/4525663-570993c0dac74778a584ff6169bf8038.jpg"))).thenReturn("blablupp.jpg");

        Optional<Recipe> optionalRecipe = recipeBuilder
                .withRecipeJsonLd(new JSONObject(RECIPE_ARRIFIED_TYPE))
                .from(URL_ARRIFIED_TYPE)
                .build();

        assertThat(optionalRecipe.isPresent(), is(true));

        Recipe recipe = optionalRecipe.get();
        assertThat(recipe.getTitle(), is("Delicious Egg Salad for Sandwiches"));
        assertThat(recipe.getSource(), is(URL_ARRIFIED_TYPE));
        assertThat(recipe.getServings(), is("4"));
        assertThat(recipe.getPreparationTime(), is("35m"));
        assertThat(recipe.getDescription(), is("This egg salad is quick to prep and easy to make with hard-cooked eggs, mayonnaise, mustard, paprika, and green onions. Perfect for sandwiches!"));
        assertThat(recipe.getIngredients(), is("8 eggs\n0.5 cup mayonnaise\n0.25 cup chopped green onion\n1 teaspoon prepared yellow mustard\n0.25 teaspoon paprika\nsalt and pepper to taste"));
        assertThat(recipe.getDirections(), is("Place eggs in a saucepan and cover with cold water. Bring water to a boil and immediately remove from heat. Cover and let eggs stand in hot water for 10 to 12 minutes. Remove from hot water, cool, peel, and chop.\nPlace chopped eggs in a bowl; stir in mayonnaise, green onion, and mustard. Season with paprika, salt, and pepper. Stir and serve on your favorite bread or crackers."));
        assertThat(recipe.getNutritionalValues(), is("Calories: 344 kcal\nFat: 32 g\nCarbohydrates: 2 g\nProtein: 13 g"));
        assertThat(recipe.getImageName(), is("blablupp.jpg"));
    }

    /*
        see https://github.com/flauschtrud/broccoli/issues/316
     */
    @Test
    public void arrified_images() throws JSONException, IOException {
        when(recipeImageService.downloadToCache(new URL("https://images.ndr.de/image/5265d7c2-3817-4d1f-b8f0-116ecbb1352d/AAABlL-u-ik/AAABmgWmh8Q/16x9-big/auberginencurry100.jpg?width=1920"))).thenReturn("blablupp.jpg");

        Optional<Recipe> optionalRecipe = recipeBuilder
                .withRecipeJsonLd(new JSONObject(RECIPE_ARRIFIED_IMAGES))
                .from(URL_ARRIFIED_IMAGES)
                .build();

        assertThat(optionalRecipe.isPresent(), is(true));

        Recipe recipe = optionalRecipe.get();
        assertThat(recipe.getTitle(), is("Auberginen-Curry mit Reis"));
        assertThat(recipe.getSource(), is(URL_ARRIFIED_IMAGES));
        assertThat(recipe.getServings(), is("4"));
        assertThat(recipe.getPreparationTime(), is("35m"));
        assertThat(recipe.getDescription(), is("Die Auberginen schmoren bei diesem veganen Gericht mit Kokosmilch und Tomaten und werden nach Belieben pikant gewürzt."));
        assertThat(recipe.getIngredients(), is("2 Auberginen\n2 EL Knoblauchöl\n400 g stückige Tomaten\n400 ml Kokosmilch\n200 g Vollkorn-Basmatireis\nCurrypulver\nGewürze\nKräuter"));
        assertThat(recipe.getDirections(), is("Auberginen waschen, putzen und in mundgerechte Stücke schneiden. In einer großen Pfanne Knoblauchöl erhitzen und Auberginen darin anbraten. Bei mittlerer Hitze circa 20 Minuten schmoren lassen, dabei regelmäßig wenden. \n\nIn der Zwischenzeit den Reis nach Packungsanweisung gar kochen. Die Kokosmilch und die Tomaten zu den Auberginen geben. Nach Belieben würzen - z.B. mit Kurkuma, Ingwer, Paprika oder frischen Kräutern wie Koriander - und alles unter Rühren etwa 10 Minuten einkochen lassen. \n\nDen Reis mit dem Auberginen-Curry auf Tellern anrichten. Wer mag, streut Sesam darüber."));
        assertThat(recipe.getNutritionalValues(), is(""));
        assertThat(recipe.getImageName(), is("blablupp.jpg"));
    }

}