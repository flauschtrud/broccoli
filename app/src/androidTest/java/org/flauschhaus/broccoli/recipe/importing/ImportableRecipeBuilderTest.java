package org.flauschhaus.broccoli.recipe.importing;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.DaggerMockApplicationComponent;
import org.flauschhaus.broccoli.MockApplicationComponent;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ImportableRecipeBuilderTest {

    @Inject
    RecipeImageService recipeImageService;

    File fileInCache = new File("dir/blablupp.jpg");

    private static final String URL_CHEFKOCH = "https://www.chefkoch.de/rezepte/3212051478029180/Vegane-Chocolate-Chip-Cookies.html";
    private static final String RECIPE_JSONLD_CHEFKOCH = "    {\n" +
            "    \"@context\": \"http://schema.org\",\n" +
            "    \"@type\": \"Recipe\",\n" +
            "            \"image\": \"https://img.chefkoch-cdn.de/rezepte/3212051478029180/bilder/1325560/crop-960x540/vegane-chocolate-chip-cookies.jpg\",\n" +
            "                    \"recipeCategory\": \"Vegan\",\n" +
            "                \"suitableForDiet\": \"Vegan\",\n" +
            "        \"recipeIngredient\": [\n" +
            "                                                                                        \"20 g Chiasamen\",                                                            \"50 ml Wasser\",                                                            \"190 g Butterersatz oder Margarine, vegan\",                                                            \"200 g Zucker , braun, alternativ Rohrzucker\",                                                            \"2 TL Zuckerr\\u00fcbensirup , alternativ Melasse, Ahornsirup oder Agavendicksaft\",                                                            \"2 Pck. Vanillezucker\",                                                            \"300 g Weizenmehl oder Dinkelmehl, oder gemischt\",                                                            \"4 g Natron\",                                                            \"n. B. Salz\",                                                            \"200 g Blockschokolade , zartbitter oder Schokotr\\u00f6pfchen\"                        ],\n" +
            "    \"name\": \"Vegane Chocolate Chip Cookies\",\n" +
            "    \"description\": \"Vegane Chocolate Chip Cookies - au\\u00dfen kross, innen weich, lecker und vegan, ergibt 35 St\\u00fcck. \\u00dcber 110 Bewertungen und f\\u00fcr sehr lecker befunden. Mit \\u25ba Portionsrechner \\u25ba Kochbuch \\u25ba Video-Tipps!\",\n" +
            "    \"recipeInstructions\": \"Den Backofen auf 180 \\u00b0C Umluft vorheizen. Die Chiasamen und das Wasser in einer kleinen Sch\\u00fcssel vermengen und ca. 10 Minuten quellen lassen.\\n\\nEin Backblech mit Backpapier auslegen. Vegane Butter bzw. Margarine und Zucker mit den Schneebesen des R\\u00fchrger\\u00e4ts cremig verr\\u00fchren. Dann die gequollenen Chiasamen, den Zuckerr\\u00fcbensirup und beide P\\u00e4ckchen Vanillezucker dazugeben und weiter r\\u00fchren. Unter weiterem R\\u00fchren jetzt zuerst das Mehl hinzugeben und anschlie\\u00dfend Natron sowie Salz. Alternativ - oder falls der Teig zu z\\u00e4h ist - kann alles auch mit den H\\u00e4nden verknetet werden. Abschlie\\u00dfend die Schokotr\\u00f6pfchen bzw. die gehackte Blockschokolade untermischen.\\n\\nDen nun fertigen Teig mit einem Essl\\u00f6ffel oder Eisportionierer klecksweise im Abstand von etwa 5 - 6 cm auf das Backpapier geben. Die Teigkleckse k\\u00f6nnen - m\\u00fcssen jedoch nicht - mit einem L\\u00f6ffel noch etwas rund geformt und flach gedr\\u00fcckt werden.\\n\\nDie Cookies bei 180 \\u00b0C Umluft maximal 15 Minuten backen, da sie sonst zu fest werden.\",\n" +
            "            \"author\": {\n" +
            "            \"@type\": \"Person\",\n" +
            "             \"name\": \"Esslust\"\n" +
            "        },\n" +
            "        \"publisher\": {\n" +
            "        \"@type\": \"Organization\",\n" +
            "        \"name\": \"Chefkoch.de\"\n" +
            "    },\n" +
            "    \"datePublished\": \"2016-11-03\",\n" +
            "            \"prepTime\": \"P0DT0H20M\",\n" +
            "                \"cookTime\": \"P0DT0H15M\",\n" +
            "        \"totalTime\": \"P0DT0H45M\"\n" +
            "    ,\n" +
            "        \"recipeYield\": \"1 Portion(en)\"\n" +
            "        ,\n" +
            "        \"aggregateRating\": {\n" +
            "            \"@type\": \"AggregateRating\",\n" +
            "                            \"ratingCount\": 110,\n" +
            "                \"ratingValue\": 4.77,\n" +
            "                                        \"reviewCount\": 82,\n" +
            "                        \"worstRating\": 0,\n" +
            "            \"bestRating\": 5\n" +
            "        }\n" +
            "        ,\n" +
            "        \"video\": [{\n" +
            "            \"@type\": \"VideoObject\",\n" +
            "            \"name\": \"Video zu Vegane Chocolate Chip Cookies\",\n" +
            "            \"description\": \"Video zu Vegane Chocolate Chip Cookies\",\n" +
            "            \"thumbnailUrl\": \"https://api.chefkoch.de/v2/images/crop-960x540/videos/2873/preview/org\",\n" +
            "            \"contentUrl\": \"https://www.chefkoch.de/rezepte/3212051478029180/Vegane-Chocolate-Chip-Cookies.html\",\n" +
            "            \"embedUrl\": \"https://video.chefkoch-cdn.de/ck.de/videos/2873-video.mp4\",\n" +
            "            \"uploadDate\": \"2016-11-03\"\n" +
            "        }]\n" +
            "        ,\n" +
            "        \"nutrition\": {\n" +
            "            \"@type\": \"NutritionInformation\",\n" +
            "            \"servingSize\": \"1\",\n" +
            "            \"calories\": \"4594 kcal\",\n" +
            "            \"proteinContent\": \"77,59g\",\n" +
            "            \"fatContent\": \"225,86g\",\n" +
            "            \"carbohydrateContent\": \"540,65g\"\n" +
            "        }\n" +
            "        ,\n" +
            "        \"keywords\": [\"Backen\",\"Vegetarisch\",\"einfach\",\"Kekse\",\"Gluten\",\"Lactose\"]\n" +
            "                            ,\n" +
            "        \"reviews\": [\n" +
            "                    {\n" +
            "                \"@type\": \"Review\",\n" +
            "                \"reviewBody\": \"Super \\ud83d\\udc4d\\ud83c\\udffb lecker\\ud83d\\ude0b \\nFoto ist schon unterwegs \\ud83e\\udd70\",\n" +
            "                \"datePublished\": \"2020-09-15\"\n" +
            "                ,\n" +
            "                    \"author\": {\n" +
            "                        \"@type\": \"Person\",\n" +
            "                        \"name\": \"Neoncupcake\"\n" +
            "                    }\n" +
            "                            },                    {\n" +
            "                \"@type\": \"Review\",\n" +
            "                \"reviewBody\": \"Mega gut! Ich hab 50g braunen Zucker und 100g Birkenzucker verwendet und sie bei 170\\u00b0C ca 12-13min gebacken. N\\u00e4chstes Mal mache ich eine Variante mit Kakao im Teig damit es so einen richtig schweren Schokogeschmack bekommt \\ud83d\\ude07\",\n" +
            "                \"datePublished\": \"2020-06-08\"\n" +
            "                ,\n" +
            "                    \"author\": {\n" +
            "                        \"@type\": \"Person\",\n" +
            "                        \"name\": \"RoxPox\"\n" +
            "                    }\n" +
            "                            },                    {\n" +
            "                \"@type\": \"Review\",\n" +
            "                \"reviewBody\": \"Super lecker, ich habe da die Chiasamen allerdings mit Flosamen ausgetauscht, da ich das da hatte. Habe daf\\u00fcr etwas mehr Wasser f\\u00fcr das Quellverhalten gegeben (80 ml). Hat aber auch klasse funktioniert und die Cookies sind sehr fein geworden. Bei ein paar habe ich auch ein paar Haferflocken untergemischt :)\",\n" +
            "                \"datePublished\": \"2020-06-04\"\n" +
            "                ,\n" +
            "                    \"author\": {\n" +
            "                        \"@type\": \"Person\",\n" +
            "                        \"name\": \"alenalue\"\n" +
            "                    }\n" +
            "                            },                    {\n" +
            "                \"@type\": \"Review\",\n" +
            "                \"reviewBody\": \"Genial!!! Die besten Cookies die ich je gegessen habe. Ich habe einen Test mit 3 verschiedenen Cookie Rezepten durchgef\\u00fchrt und dieses hat mit Abstand gewonnen. Wurde auch von all meinen nicht Veganer Freunden dankend angenommen. Schmeckt auch super mit Vollkorn- oder Dinkelmehl. LG\",\n" +
            "                \"datePublished\": \"2020-05-29\"\n" +
            "                ,\n" +
            "                    \"author\": {\n" +
            "                        \"@type\": \"Person\",\n" +
            "                        \"name\": \"Mrs Sommer\"\n" +
            "                    }\n" +
            "                            },                    {\n" +
            "                \"@type\": \"Review\",\n" +
            "                \"reviewBody\": \"Geschmacklich sind die 1a! Aber Chiasamen als Eiersatz st\\u00f6ren mich immer, weil sie beim Essen unangenehm an den Z\\u00e4hnen kleben bleiben. Ich denke n\\u00e4chstes mal probiere ich es mit gemahlenen Leinsamen.\",\n" +
            "                \"datePublished\": \"2020-05-18\"\n" +
            "                ,\n" +
            "                    \"author\": {\n" +
            "                        \"@type\": \"Person\",\n" +
            "                        \"name\": \"electricblueberry\"\n" +
            "                    }\n" +
            "                            }                ]\n" +
            "    }\n" +
            "\n";

    private static final String MINIMIZED_RECIPE_JSONLD = "    {\n" +
            "    \"@context\": \"http://schema.org\",\n" +
            "    \"@type\": \"Recipe\",\n" +
            "                    \"recipeCategory\": \"Vegan\",\n" +
            "                \"suitableForDiet\": \"Vegan\",\n" +
            "    \"name\": \"Vegane Chocolate Chip Cookies\"\n" +
            "    }\n" +
            "\n";

    private static final String URL_YOAST = "https://stilettosandsprouts.de/vegane-fenchel-pasta/";
    private static final String RECIPE_JSON_LD_YOAST = "{\n" +
            "  \"@context\": \"http://schema.org/\",\n" +
            "  \"@type\": \"Recipe\",\n" +
            "  \"name\": \"Vegane Fenchel-Pasta\",\n" +
            "  \"author\": {\n" +
            "    \"@type\": \"Person\",\n" +
            "    \"name\": \"Katja\"\n" +
            "  },\n" +
            "  \"description\": \"Pasta mit geröstetem Fenchel und Zitrone – ein herrlich leichtes Pastagericht, in nur 10 Minuten fertig zubereitet.\",\n" +
            "  \"datePublished\": \"2018-05-15T07:00:48+00:00\",\n" +
            "  \"image\": [\n" +
            "    \"https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B.jpg\",\n" +
            "    \"https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B-500x500.jpg\",\n" +
            "    \"https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B-500x375.jpg\",\n" +
            "    \"https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B-480x270.jpg\"\n" +
            "  ],\n" +
            "  \"recipeYield\": [\n" +
            "    \"2\",\n" +
            "    \"2 Personen\"\n" +
            "  ],\n" +
            "  \"cookTime\": \"PT10M\",\n" +
            "  \"recipeIngredient\": [\n" +
            "    \"1 Knolle Fenchel\",\n" +
            "    \"1  Bio-Zitrone\",\n" +
            "    \"1  Knoblauchzehe, geschält und fein gehackt\",\n" +
            "    \"1  Schalotte, geschält und fein gehackt\",\n" +
            "    \"1 Handvoll Petersilie, frisch, fein gehackt\",\n" +
            "    \"5 EL Semmelbrösel\",\n" +
            "    \"Olivenöl\",\n" +
            "    \"Salz &amp; Pfeffer\",\n" +
            "    \"250 g Pasta (z.B. Linguini)\"\n" +
            "  ],\n" +
            "  \"recipeInstructions\": [\n" +
            "    {\n" +
            "      \"@type\": \"HowToStep\",\n" +
            "      \"text\": \"Vom Fenchel die oberen grünen Stängel entfernen. Dabei unbedingt das Fenchelgrün aufbewahren. Das kommt später an die Pasta ran. Die Knolle halbieren, den Strunk in der Mitte keilförmig entfernen und nun die zwei Hälften in dünne Streifen schneiden. Die Fenchel-Streifen waschen und gut abtrocknen.\",\n" +
            "      \"name\": \"Vom Fenchel die oberen grünen Stängel entfernen. Dabei unbedingt das Fenchelgrün aufbewahren. Das kommt später an die Pasta ran. Die Knolle halbieren, den Strunk in der Mitte keilförmig entfernen und nun die zwei Hälften in dünne Streifen schneiden. Die Fenchel-Streifen waschen und gut abtrocknen.\",\n" +
            "      \"url\": \"https://stilettosandsprouts.de/vegane-fenchel-pasta/#wprm-recipe-6504-step-0-0\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"@type\": \"HowToStep\",\n" +
            "      \"text\": \"Die Pasta nach Packungsanweisung garen.\",\n" +
            "      \"name\": \"Die Pasta nach Packungsanweisung garen.\",\n" +
            "      \"url\": \"https://stilettosandsprouts.de/vegane-fenchel-pasta/#wprm-recipe-6504-step-0-1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"@type\": \"HowToStep\",\n" +
            "      \"text\": \"In der Zwischenzeit in einer großen Pfanne Olivenöl erhitzen und den Fenchel darin mit etwas Salz ca. 8 Minuten lang anrösten.\",\n" +
            "      \"name\": \"In der Zwischenzeit in einer großen Pfanne Olivenöl erhitzen und den Fenchel darin mit etwas Salz ca. 8 Minuten lang anrösten.\",\n" +
            "      \"url\": \"https://stilettosandsprouts.de/vegane-fenchel-pasta/#wprm-recipe-6504-step-0-2\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"@type\": \"HowToStep\",\n" +
            "      \"text\": \"In einer kleinen Pfanne etwas Olivenöl erhitzen, Knoblauch- und Schalottenwürfel darin mit etwas Salz glasig andünsten. Zitronenabrieb, Petersilie und die Semmelbrösel hinzugeben und alles ca. 4 Minuten vorsichtig anrösten bis die Semmelbrösel leicht angebräunt sind. Die Mischung vom Herd nehmen.\",\n" +
            "      \"name\": \"In einer kleinen Pfanne etwas Olivenöl erhitzen, Knoblauch- und Schalottenwürfel darin mit etwas Salz glasig andünsten. Zitronenabrieb, Petersilie und die Semmelbrösel hinzugeben und alles ca. 4 Minuten vorsichtig anrösten bis die Semmelbrösel leicht angebräunt sind. Die Mischung vom Herd nehmen.\",\n" +
            "      \"url\": \"https://stilettosandsprouts.de/vegane-fenchel-pasta/#wprm-recipe-6504-step-0-3\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"@type\": \"HowToStep\",\n" +
            "      \"text\": \"Die fertige Pasta abgießen. Dabei etwa ein halbes Wasserglas der Kochflüssigkeit auffangen. Abgetropfte Pasta zum Fenchel geben. Die Semmelbröselmischung dazugeben. Kochflüssigkeit nach Belieben dazugeben, damit die Pasta schön glänzend wird. Pasta ordentlich salzen und pfeffern und nach Geschmack Zitrone hinzugeben. Den Saft einer halben Zitrone verträgt das Gericht mindestens. Mit einem guten Schuss Olivenöl und mit Fenchelgrün bestreut servieren.\",\n" +
            "      \"name\": \"Die fertige Pasta abgießen. Dabei etwa ein halbes Wasserglas der Kochflüssigkeit auffangen. Abgetropfte Pasta zum Fenchel geben. Die Semmelbröselmischung dazugeben. Kochflüssigkeit nach Belieben dazugeben, damit die Pasta schön glänzend wird. Pasta ordentlich salzen und pfeffern und nach Geschmack Zitrone hinzugeben. Den Saft einer halben Zitrone verträgt das Gericht mindestens. Mit einem guten Schuss Olivenöl und mit Fenchelgrün bestreut servieren.\",\n" +
            "      \"url\": \"https://stilettosandsprouts.de/vegane-fenchel-pasta/#wprm-recipe-6504-step-0-4\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"aggregateRating\": {\n" +
            "    \"@type\": \"AggregateRating\",\n" +
            "    \"ratingValue\": \"5\",\n" +
            "    \"ratingCount\": \"4\"\n" +
            "  },\n" +
            "  \"@id\": \"https://stilettosandsprouts.de/vegane-fenchel-pasta/#recipe\",\n" +
            "  \"isPartOf\": {\n" +
            "    \"@id\": \"https://stilettosandsprouts.de/vegane-fenchel-pasta/#webpage\"\n" +
            "  },\n" +
            "  \"mainEntityOfPage\": \"https://stilettosandsprouts.de/vegane-fenchel-pasta/#webpage\"\n" +
            "}";

    @Before
    public void setUp() {
        MockApplicationComponent component = DaggerMockApplicationComponent.builder()
                .application(getApplication())
                .build();
        component.inject(this);
        component.inject(getApplication());
    }

    private BroccoliApplication getApplication() {
        return (BroccoliApplication) getInstrumentation()
                .getTargetContext().getApplicationContext();
    }

    @Test
    public void example_chefkoch() throws JSONException, IOException {
        when(recipeImageService.createTemporaryImageFileInCache()).thenReturn(fileInCache);
        when(recipeImageService.downloadToCache("https://img.chefkoch-cdn.de/rezepte/3212051478029180/bilder/1325560/crop-960x540/vegane-chocolate-chip-cookies.jpg", fileInCache)).thenReturn(CompletableFuture.completedFuture(null));

        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(recipeImageService);

        Optional<Recipe> optionalRecipe = recipeBuilder
                .withRecipeJsonLd(new JSONObject(RECIPE_JSONLD_CHEFKOCH))
                .from(URL_CHEFKOCH)
                .build();

        assertThat(optionalRecipe.isPresent(), is(true));

        Recipe recipe = optionalRecipe.get();
        assertThat(recipe.getTitle(), is("Vegane Chocolate Chip Cookies"));
        assertThat(recipe.getSource(), is(URL_CHEFKOCH));
        assertThat(recipe.getServings(), is("1 Portion(en)"));
        assertThat(recipe.getPreparationTime(), is("45 minutes"));
        assertThat(recipe.getDescription(), is("Vegane Chocolate Chip Cookies - außen kross, innen weich, lecker und vegan, ergibt 35 Stück. Über 110 Bewertungen und für sehr lecker befunden. Mit ► Portionsrechner ► Kochbuch ► Video-Tipps!"));
        assertThat(recipe.getIngredients(), is("20 g Chiasamen\n50 ml Wasser\n190 g Butterersatz oder Margarine, vegan\n200 g Zucker , braun, alternativ Rohrzucker\n2 TL Zuckerrübensirup , alternativ Melasse, Ahornsirup oder Agavendicksaft\n2 Pck. Vanillezucker\n300 g Weizenmehl oder Dinkelmehl, oder gemischt\n4 g Natron\nn. B. Salz\n200 g Blockschokolade , zartbitter oder Schokotröpfchen"));
        assertThat(recipe.getDirections(), is("Den Backofen auf 180 °C Umluft vorheizen. Die Chiasamen und das Wasser in einer kleinen Schüssel vermengen und ca. 10 Minuten quellen lassen.\n\nEin Backblech mit Backpapier auslegen. Vegane Butter bzw. Margarine und Zucker mit den Schneebesen des Rührgeräts cremig verrühren. Dann die gequollenen Chiasamen, den Zuckerrübensirup und beide Päckchen Vanillezucker dazugeben und weiter rühren. Unter weiterem Rühren jetzt zuerst das Mehl hinzugeben und anschließend Natron sowie Salz. Alternativ - oder falls der Teig zu zäh ist - kann alles auch mit den Händen verknetet werden. Abschließend die Schokotröpfchen bzw. die gehackte Blockschokolade untermischen.\n\nDen nun fertigen Teig mit einem Esslöffel oder Eisportionierer klecksweise im Abstand von etwa 5 - 6 cm auf das Backpapier geben. Die Teigkleckse können - müssen jedoch nicht - mit einem Löffel noch etwas rund geformt und flach gedrückt werden.\n\nDie Cookies bei 180 °C Umluft maximal 15 Minuten backen, da sie sonst zu fest werden."));
        assertThat(recipe.getImageName(), is ("blablupp.jpg"));
    }

    @Test
    public void example_yoast() throws JSONException, IOException {
        when(recipeImageService.createTemporaryImageFileInCache()).thenReturn(fileInCache);
        when(recipeImageService.downloadToCache("https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B.jpg", fileInCache)).thenReturn(CompletableFuture.completedFuture(null));

        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(recipeImageService);

        Optional<Recipe> optionalRecipe = recipeBuilder
                .withRecipeJsonLd(new JSONObject(RECIPE_JSON_LD_YOAST))
                .from(URL_YOAST)
                .build();

        assertThat(optionalRecipe.isPresent(), is(true));

        Recipe recipe = optionalRecipe.get();
        assertThat(recipe.getTitle(), is("Vegane Fenchel-Pasta"));
        assertThat(recipe.getSource(), is(URL_YOAST));
        assertThat(recipe.getServings(), is("2"));
        assertThat(recipe.getPreparationTime(), is("10 minutes"));
        assertThat(recipe.getDescription(), is("Pasta mit geröstetem Fenchel und Zitrone – ein herrlich leichtes Pastagericht, in nur 10 Minuten fertig zubereitet."));
        assertThat(recipe.getIngredients(), is("1 Knolle Fenchel\n1  Bio-Zitrone\n1  Knoblauchzehe, geschält und fein gehackt\n1  Schalotte, geschält und fein gehackt\n1 Handvoll Petersilie, frisch, fein gehackt\n5 EL Semmelbrösel\nOlivenöl\nSalz &amp; Pfeffer\n250 g Pasta (z.B. Linguini)"));
        assertThat(recipe.getDirections(), is("Vom Fenchel die oberen grünen Stängel entfernen. Dabei unbedingt das Fenchelgrün aufbewahren. Das kommt später an die Pasta ran. Die Knolle halbieren, den Strunk in der Mitte keilförmig entfernen und nun die zwei Hälften in dünne Streifen schneiden. Die Fenchel-Streifen waschen und gut abtrocknen.\nDie Pasta nach Packungsanweisung garen.\nIn der Zwischenzeit in einer großen Pfanne Olivenöl erhitzen und den Fenchel darin mit etwas Salz ca. 8 Minuten lang anrösten.\nIn einer kleinen Pfanne etwas Olivenöl erhitzen, Knoblauch- und Schalottenwürfel darin mit etwas Salz glasig andünsten. Zitronenabrieb, Petersilie und die Semmelbrösel hinzugeben und alles ca. 4 Minuten vorsichtig anrösten bis die Semmelbrösel leicht angebräunt sind. Die Mischung vom Herd nehmen.\nDie fertige Pasta abgießen. Dabei etwa ein halbes Wasserglas der Kochflüssigkeit auffangen. Abgetropfte Pasta zum Fenchel geben. Die Semmelbröselmischung dazugeben. Kochflüssigkeit nach Belieben dazugeben, damit die Pasta schön glänzend wird. Pasta ordentlich salzen und pfeffern und nach Geschmack Zitrone hinzugeben. Den Saft einer halben Zitrone verträgt das Gericht mindestens. Mit einem guten Schuss Olivenöl und mit Fenchelgrün bestreut servieren."));
        assertThat(recipe.getImageName(), is ("blablupp.jpg"));
    }

    @Test
    public void no_Recipe_JsonLd() throws JSONException {
        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(recipeImageService);

        Optional<Recipe> optionalRecipe = recipeBuilder
                .build();

        assertThat(optionalRecipe.isPresent(), is(false));
    }

    @Test
    public void missing_attributes() throws JSONException {
        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(recipeImageService);

        Optional<Recipe> optionalRecipe = recipeBuilder
                .withRecipeJsonLd(new JSONObject(MINIMIZED_RECIPE_JSONLD))
                .from(URL_CHEFKOCH)
                .build();

        assertThat(optionalRecipe.isPresent(), is(true));

        Recipe recipe = optionalRecipe.get();
        assertThat(recipe.getTitle(), is("Vegane Chocolate Chip Cookies"));
        assertThat(recipe.getSource(), is(URL_CHEFKOCH));
    }
    
}