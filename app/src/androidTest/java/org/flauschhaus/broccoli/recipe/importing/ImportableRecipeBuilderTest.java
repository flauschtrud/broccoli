package org.flauschhaus.broccoli.recipe.importing;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.flauschhaus.broccoli.recipe.Recipe;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ImportableRecipeBuilderTest {

    private static final String URL = "https://www.chefkoch.de/rezepte/3212051478029180/Vegane-Chocolate-Chip-Cookies.html";

    private static final String ORGANIZATION_JSONLD = "{\n" +
            "    \"@context\": \"http://schema.org\",\n" +
            "    \"@type\": \"WebSite\",\n" +
            "    \"name\": \"Chefkoch\",\n" +
            "    \"url\": \"https://www.chefkoch.de\",\n" +
            "    \"publisher\": {\n" +
            "        \"@type\": \"Organization\",\n" +
            "        \"name\": \"Chefkoch.de\",\n" +
            "        \"logo\": {\n" +
            "            \"@type\": \"ImageObject\",\n" +
            "            \"url\": \"https://img.chefkoch-cdn.de/img/ck.de/ck-logo-20-20-de.png\",\n" +
            "            \"width\": 20,\n" +
            "            \"height\": 20\n" +
            "        }\n" +
            "    }\n" +
            "}\n";

    private static final String RECIPE_JSONLD = "    {\n" +
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

    @Test
    public void example() throws JSONException {
        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder();

        Optional<Recipe> optionalRecipe = recipeBuilder
                .withJsonLd(new JSONObject(ORGANIZATION_JSONLD))
                .withJsonLd(new JSONObject(RECIPE_JSONLD))
                .from(URL)
                .build();

        assertThat(optionalRecipe.isPresent(), is(true));

        Recipe recipe = optionalRecipe.get();
        assertThat(recipe.getTitle(), is("Vegane Chocolate Chip Cookies"));
        assertThat(recipe.getSource(), is(URL));
    }

    @Test
    public void no_Recipe_JsonLd() throws JSONException {
        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder();

        Optional<Recipe> optionalRecipe = recipeBuilder
                .withJsonLd(new JSONObject(ORGANIZATION_JSONLD))
                .build();

        assertThat(optionalRecipe.isPresent(), is(false));
    }
    
}