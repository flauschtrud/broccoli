package com.flauschcode.broccoli.recipe.importing;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

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
    RecipeImageService recipeImageService;

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

    private static final String URL_YOAST_WITH_SECTIONS = "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/";
    private static final String RECIPE_YOAST_WITH_SECTIONS = "{\n" +
            "      \"@context\": \"http://schema.org/\",\n" +
            "      \"@type\": \"Recipe\",\n" +
            "      \"name\": \"Saftiger Veganer Zitronenkuchen\",\n" +
            "      \"author\": {\n" +
            "        \"@type\": \"Person\",\n" +
            "        \"name\": \"Karen Wilkening\"\n" +
            "      },\n" +
            "      \"description\": \"&#x25b6; Super saftiger veganer Zitronenkuchen &#x25b6; Einfaches, sehr leckeres Rezept &#x25b6; Kommt immer gut an&#x25b6; Für 30 cm Kastenbackform.\",\n" +
            "      \"datePublished\": \"2019-07-01T20:18:20+00:00\",\n" +
            "      \"image\": [\n" +
            "        \"https://veggie-einhorn.de/wp-content/uploads/Einfacher-veganer-Zitronenkuchen-saftig.jpg\",\n" +
            "        \"https://veggie-einhorn.de/wp-content/uploads/Einfacher-veganer-Zitronenkuchen-saftig-500x500.jpg\",\n" +
            "        \"https://veggie-einhorn.de/wp-content/uploads/Einfacher-veganer-Zitronenkuchen-saftig-500x375.jpg\",\n" +
            "        \"https://veggie-einhorn.de/wp-content/uploads/Einfacher-veganer-Zitronenkuchen-saftig-480x270.jpg\"\n" +
            "      ],\n" +
            "      \"recipeYield\": [\n" +
            "        \"1\",\n" +
            "        \"1 Kuchen\"\n" +
            "      ],\n" +
            "      \"totalTime\": \"PT75M\",\n" +
            "      \"recipeIngredient\": [\n" +
            "        \"300 g Mehl\",\n" +
            "        \"200 g Zucker\",\n" +
            "        \"1 Tüte Backpulver\",\n" +
            "        \"1 Tüte Vanillezucker\",\n" +
            "        \"250 ml Vanille-Sojamilch\",\n" +
            "        \"100 g Öl (Rapsöl oder Sonnenblumenöl)\",\n" +
            "        \"50 ml Zitronensaft\",\n" +
            "        \"geriebene Zitronenschale\",\n" +
            "        \"evtl. Backaroma Zitrone\",\n" +
            "        \"150 g Puderzucker\",\n" +
            "        \"2 EL Zitronensaft\"\n" +
            "      ],\n" +
            "      \"recipeInstructions\": [\n" +
            "        {\n" +
            "          \"@type\": \"HowToSection\",\n" +
            "          \"name\": \"Vorbereiten:\",\n" +
            "          \"itemListElement\": [\n" +
            "            {\n" +
            "              \"@type\": \"HowToStep\",\n" +
            "              \"text\": \"Kastenbackform mit Backpapier auslegen oder einfetten.\",\n" +
            "              \"name\": \"Kastenbackform mit Backpapier auslegen oder einfetten.\",\n" +
            "              \"url\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-0-0\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"@type\": \"HowToStep\",\n" +
            "              \"text\": \"2 Zitronen abreiben und auspressen.\",\n" +
            "              \"name\": \"2 Zitronen abreiben und auspressen.\",\n" +
            "              \"url\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-0-1\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"@type\": \"HowToStep\",\n" +
            "              \"text\": \"Backofen vorheizen auf 180 °C (Ober-Unter-Hitze).\",\n" +
            "              \"name\": \"Backofen vorheizen auf 180 °C (Ober-Unter-Hitze).\",\n" +
            "              \"url\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-0-2\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"@type\": \"HowToSection\",\n" +
            "          \"name\": \"Trockene Zutaten mischen:\",\n" +
            "          \"itemListElement\": [\n" +
            "            {\n" +
            "              \"@type\": \"HowToStep\",\n" +
            "              \"text\": \"In eine Schüssel geben:300 g Mehl 200 g Zucker1 Tüte Backpulver1 Tüte VanillezuckerAbgeriebene ZitronenschaleGut mischen.\",\n" +
            "              \"name\": \"In eine Schüssel geben:300 g Mehl 200 g Zucker1 Tüte Backpulver1 Tüte VanillezuckerAbgeriebene ZitronenschaleGut mischen.\",\n" +
            "              \"url\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-1-0\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"@type\": \"HowToSection\",\n" +
            "          \"name\": \"Feuchte Zutaten zufügen:\",\n" +
            "          \"itemListElement\": [\n" +
            "            {\n" +
            "              \"@type\": \"HowToStep\",\n" +
            "              \"text\": \"250 ml Sojamilch Vanille100 g Öl50 ml Zitronensaft\",\n" +
            "              \"name\": \"250 ml Sojamilch Vanille100 g Öl50 ml Zitronensaft\",\n" +
            "              \"url\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-2-0\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"@type\": \"HowToStep\",\n" +
            "              \"text\": \"Evtl. weiteres Zitronenaroma zufügen(Backaroma oder Zitronenöl)\",\n" +
            "              \"name\": \"Evtl. weiteres Zitronenaroma zufügen(Backaroma oder Zitronenöl)\",\n" +
            "              \"url\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-2-1\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"@type\": \"HowToSection\",\n" +
            "          \"name\": \"Teig mixen und in Backform füllen:\",\n" +
            "          \"itemListElement\": [\n" +
            "            {\n" +
            "              \"@type\": \"HowToStep\",\n" +
            "              \"text\": \"Kurz gut durchmixen.\",\n" +
            "              \"name\": \"Kurz gut durchmixen.\",\n" +
            "              \"url\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-3-0\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"@type\": \"HowToStep\",\n" +
            "              \"text\": \"In die vorbereitete Kastenbackform füllen.\",\n" +
            "              \"name\": \"In die vorbereitete Kastenbackform füllen.\",\n" +
            "              \"url\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-3-1\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"@type\": \"HowToSection\",\n" +
            "          \"name\": \"Backen:\",\n" +
            "          \"itemListElement\": [\n" +
            "            {\n" +
            "              \"@type\": \"HowToStep\",\n" +
            "              \"text\": \"Im vorgeheizten Ofen bei 180 °C (Ober-Unter-Hitze) 60 Minuten backen.(Stäbchenprobe)\",\n" +
            "              \"name\": \"Im vorgeheizten Ofen bei 180 °C (Ober-Unter-Hitze) 60 Minuten backen.(Stäbchenprobe)\",\n" +
            "              \"url\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-4-0\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"@type\": \"HowToStep\",\n" +
            "              \"text\": \"Kuchen in der Form abkühlen lassen.\",\n" +
            "              \"name\": \"Kuchen in der Form abkühlen lassen.\",\n" +
            "              \"url\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-4-1\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        {\n" +
            "          \"@type\": \"HowToSection\",\n" +
            "          \"name\": \"Zitronenglasur:\",\n" +
            "          \"itemListElement\": [\n" +
            "            {\n" +
            "              \"@type\": \"HowToStep\",\n" +
            "              \"text\": \"150 g Puderzucker sieben.Mit 2 EL Zitronensaft verrühren.Auf dem Kuchen verteilen.\",\n" +
            "              \"name\": \"150 g Puderzucker sieben.Mit 2 EL Zitronensaft verrühren.Auf dem Kuchen verteilen.\",\n" +
            "              \"url\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-5-0\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ],\n" +
            "      \"aggregateRating\": {\n" +
            "        \"@type\": \"AggregateRating\",\n" +
            "        \"ratingValue\": \"4.97\",\n" +
            "        \"ratingCount\": \"29\"\n" +
            "      },\n" +
            "      \"recipeCategory\": [\n" +
            "        \"Backen\"\n" +
            "      ],\n" +
            "      \"recipeCuisine\": [\n" +
            "        \"vegan\"\n" +
            "      ],\n" +
            "      \"suitableForDiet\": [\n" +
            "        \"https://schema.org/LowLactoseDiet\",\n" +
            "        \"https://schema.org/VeganDiet\"\n" +
            "      ],\n" +
            "      \"keywords\": \"einfach, mit öl, ohne butter, ohne ei, ohne milch, super saftig\",\n" +
            "      \"nutrition\": {\n" +
            "        \"@type\": \"NutritionInformation\",\n" +
            "        \"servingSize\": \"1 Stück (bei 12 Stücken)\",\n" +
            "        \"calories\": \"292 kcal\",\n" +
            "        \"carbohydrateContent\": \"49 g\",\n" +
            "        \"proteinContent\": \"3.8 g\",\n" +
            "        \"fatContent\": \"8.4 g\",\n" +
            "        \"saturatedFatContent\": \"0.75 g\",\n" +
            "        \"sugarContent\": \"31 g\"\n" +
            "      },\n" +
            "      \"@id\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#recipe\",\n" +
            "      \"isPartOf\": {\n" +
            "        \"@id\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#webpage\"\n" +
            "      },\n" +
            "      \"mainEntityOfPage\": \"https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#webpage\"\n" +
            "    }";

    private static final String URL_ARRIFIED_TYPE ="https://www.allrecipes.com/recipe/147103/delicious-egg-salad-for-sandwiches/";

    private static final String RECIPE_ARRIFIED_TYPE = "{\n" +
            "\"@context\": \"http://schema.org\",\n" +
            "\"@type\": [\"Recipe\",\"NewsArticle\"]\n" +
            ",\"headline\": \"Delicious Egg Salad for Sandwiches\"\n" +
            ",\"datePublished\": \"2007-08-12T01:48:39.000-04:00\"\n" +
            ",\"dateModified\": \"2007-08-12T01:48:39.000-04:00\"\n" +
            ",\"author\": [\n" +
            "{\"@type\": \"Person\"\n" +
            ",\"name\": \"wifeyluvs2cook\"\n" +
            ",\"url\": \"https://www.allrecipes.com/cook/2309128\"\n" +
            "}\n" +
            "]\n" +
            ",\"description\": \"This egg salad is quick to prep and easy to make with hard-cooked eggs, mayonnaise, mustard, paprika, and green onions. Perfect for sandwiches!\"\n" +
            ",\"image\": {\n" +
            "\"@type\": \"ImageObject\",\n" +
            "\"url\": \"https://www.allrecipes.com/thmb/5SN7ROsF_d5tEiEUj5Vay0TD62g=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/4525663-570993c0dac74778a584ff6169bf8038.jpg\",\n" +
            "\"height\": 1500,\n" +
            "\"width\": 1500\n" +
            "}\n" +
            ",\"video\": {\n" +
            "\"@type\": \"VideoObject\",\n" +
            "\"embedUrl\": \"https://players.brightcove.net/1033249144001/default_default/index.html?videoId=1892556557001\",\n" +
            "\"description\": \"AR0742\",\n" +
            "\"duration\": \"PT2M31S\",\n" +
            "\"name\": \"Delicious Egg Salad for Sandwiches\",\n" +
            "\"thumbnailUrl\": \"https://cf-images.us-east-1.prod.boltdns.net/v1/static/1033249144001/952e480b-889d-4527-8f06-537723f5f600/dc9f0925-7fe3-4e43-aa34-14d4f1f18b1d/1280x720/match/image.jpg\",\n" +
            "\"uploadDate\": \"2021-02-27T17:29:07.998-05:00\"}\n" +
            ",\"publisher\": {\n" +
            "\"@type\": \"Organization\",\n" +
            "\"name\": \"Allrecipes\",\n" +
            "\"url\": \"https://www.allrecipes.com\",\n" +
            "\"logo\": {\n" +
            "\"@type\": \"ImageObject\",\n" +
            "\"url\": \"https://www.allrecipes.com/thmb/Z9lwz1y0B5aX-cemPiTgpn5YB0k=/112x112/filters:no_upscale():max_bytes(150000):strip_icc()/allrecipes_logo_schema-867c69d2999b439a9eba923a445ccfe3.png\",\n" +
            "\"width\": 112,\n" +
            "\"height\": 112\n" +
            "},\n" +
            "\"brand\": \"Allrecipes\"\n" +
            ", \"publishingPrinciples\": \"https://www.allrecipes.com/about-us-6648102#toc-editorial-guidelines\"\n" +
            ", \"sameAs\" : [\n" +
            "\"https://www.facebook.com/allrecipes\",\n" +
            "\"https://www.instagram.com/allrecipes/\",\n" +
            "\"https://www.pinterest.com/allrecipes/\",\n" +
            "\"https://www.tiktok.com/@allrecipes\",\n" +
            "\"https://www.youtube.com/user/allrecipes/videos\",\n" +
            "\"https://twitter.com/Allrecipes\",\n" +
            "\"https://flipboard.com/@Allrecipes\",\n" +
            "\"https://en.wikipedia.org/wiki/Allrecipes.com\",\n" +
            "\"https://apps.apple.com/us/app/allrecipes-dinner-spinner/id299515267\",\n" +
            "\"https://play.google.com/store/apps/details?id=com.allrecipes.spinner.free&hl=en_US&gl=US\",\n" +
            "\"https://www.youtube.com/c/foodwishes\",\n" +
            "\"https://www.linkedin.com/company/19312/admin/\"\n" +
            "]\n" +
            "}\n" +
            ",\"name\": \"Delicious Egg Salad for Sandwiches\"\n" +
            ",\"aggregateRating\": {\n" +
            "\"@type\": \"AggregateRating\",\n" +
            "\"ratingValue\": \"4.7\",\n" +
            "\"ratingCount\": \"2210\"\n" +
            "}\n" +
            ",\"cookTime\": \"PT15M\"\n" +
            ",\"nutrition\": {\n" +
            "\"@type\": \"NutritionInformation\"\n" +
            ",\"calories\": \"344 kcal\"\n" +
            ",\"carbohydrateContent\": \"2 g\"\n" +
            ",\"cholesterolContent\": \"383 mg\"\n" +
            ",\"fiberContent\": \"0 g\"\n" +
            ",\"proteinContent\": \"13 g\"\n" +
            ",\"saturatedFatContent\": \"6 g\"\n" +
            ",\"sodiumContent\": \"351 mg\"\n" +
            ",\"sugarContent\": \"1 g\"\n" +
            ",\"fatContent\": \"32 g\"\n" +
            ",\"unsaturatedFatContent\": \"0 g\"\n" +
            "}\n" +
            ",\"prepTime\": \"PT10M\"\n" +
            ", \"recipeCategory\": [\"Lunch\"]\n" +
            ",\"recipeIngredient\": [\n" +
            "\"8 eggs\",\n" +
            "\"0.5 cup mayonnaise\",\n" +
            "\"0.25 cup chopped green onion\",\n" +
            "\"1 teaspoon prepared yellow mustard\",\n" +
            "\"0.25 teaspoon paprika\",\n" +
            "\"salt and pepper to taste\" ]\n" +
            ",\"recipeInstructions\": [\n" +
            "{\n" +
            "\"@type\": \"HowToStep\"\n" +
            ",\"text\": \"Place eggs in a saucepan and cover with cold water. Bring water to a boil and immediately remove from heat. Cover and let eggs stand in hot water for 10 to 12 minutes. Remove from hot water, cool, peel, and chop.\"\n" +
            "} ,{\n" +
            "\"@type\": \"HowToStep\"\n" +
            ",\"image\": [\n" +
            "{\n" +
            "\"@type\": \"ImageObject\",\n" +
            "\"url\": \"https://www.allrecipes.com/thmb/5SN7ROsF_d5tEiEUj5Vay0TD62g=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/4525663-570993c0dac74778a584ff6169bf8038.jpg\"\n" +
            "}\n" +
            "]\n" +
            ",\"text\": \"Place chopped eggs in a bowl; stir in mayonnaise, green onion, and mustard. Season with paprika, salt, and pepper. Stir and serve on your favorite bread or crackers.\"\n" +
            "} ]\n" +
            ",\"recipeYield\": [\"4\"]\n" +
            ",\"totalTime\": \"PT35M\"\n" +
            ",\"mainEntityOfPage\": {\n" +
            "\"@type\": [\"WebPage\"]\n" +
            ",\"@id\": \"https://www.allrecipes.com/recipe/147103/delicious-egg-salad-for-sandwiches/\"\n" +
            ",\"breadcrumb\": {\n" +
            "\"@type\": \"BreadcrumbList\",\n" +
            "\"itemListElement\": [\n" +
            "{\n" +
            "\"@type\": \"ListItem\",\n" +
            "\"position\": 1,\n" +
            "\"item\": {\n" +
            "\"@id\": \"https://www.allrecipes.com/recipes/96/salad/\",\n" +
            "\"name\": \"Salad\"\n" +
            "}\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "\"@type\": \"ListItem\",\n" +
            "\"position\": 2,\n" +
            "\"item\": {\n" +
            "\"@id\": \"https://www.allrecipes.com/recipes/1986/salad/egg-salad/\",\n" +
            "\"name\": \"Egg Salad Recipes\"\n" +
            "}\n" +
            "}\n" +
            ",\n" +
            "{\n" +
            "\"@type\": \"ListItem\",\n" +
            "\"position\": 3,\n" +
            "\"item\": {\n" +
            "\"@id\": \"https://www.allrecipes.com/recipe/147103/delicious-egg-salad-for-sandwiches/\",\n" +
            "\"name\": \"Delicious Egg Salad for Sandwiches\"\n" +
            "}\n" +
            "}\n" +
            "]\n" +
            "}\n" +
            "}\n" +
            ", \"about\": [\n" +
            "]\n" +
            "}";

    @Before
    public void setUp() {
        AccessibilityChecks.enable();

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
        when(recipeImageService.downloadToCache(new URL("https://img.chefkoch-cdn.de/rezepte/3212051478029180/bilder/1325560/crop-960x540/vegane-chocolate-chip-cookies.jpg"))).thenReturn("blablupp.jpg");

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
        assertThat(recipe.getPreparationTime(), is("45m"));
        assertThat(recipe.getDescription(), is("Vegane Chocolate Chip Cookies - außen kross, innen weich, lecker und vegan, ergibt 35 Stück. Über 110 Bewertungen und für sehr lecker befunden. Mit ► Portionsrechner ► Kochbuch ► Video-Tipps!"));
        assertThat(recipe.getIngredients(), is("20 g Chiasamen\n50 ml Wasser\n190 g Butterersatz oder Margarine, vegan\n200 g Zucker , braun, alternativ Rohrzucker\n2 TL Zuckerrübensirup , alternativ Melasse, Ahornsirup oder Agavendicksaft\n2 Pck. Vanillezucker\n300 g Weizenmehl oder Dinkelmehl, oder gemischt\n4 g Natron\nn. B. Salz\n200 g Blockschokolade , zartbitter oder Schokotröpfchen"));
        assertThat(recipe.getDirections(), is("Den Backofen auf 180 °C Umluft vorheizen. Die Chiasamen und das Wasser in einer kleinen Schüssel vermengen und ca. 10 Minuten quellen lassen.\n\nEin Backblech mit Backpapier auslegen. Vegane Butter bzw. Margarine und Zucker mit den Schneebesen des Rührgeräts cremig verrühren. Dann die gequollenen Chiasamen, den Zuckerrübensirup und beide Päckchen Vanillezucker dazugeben und weiter rühren. Unter weiterem Rühren jetzt zuerst das Mehl hinzugeben und anschließend Natron sowie Salz. Alternativ - oder falls der Teig zu zäh ist - kann alles auch mit den Händen verknetet werden. Abschließend die Schokotröpfchen bzw. die gehackte Blockschokolade untermischen.\n\nDen nun fertigen Teig mit einem Esslöffel oder Eisportionierer klecksweise im Abstand von etwa 5 - 6 cm auf das Backpapier geben. Die Teigkleckse können - müssen jedoch nicht - mit einem Löffel noch etwas rund geformt und flach gedrückt werden.\n\nDie Cookies bei 180 °C Umluft maximal 15 Minuten backen, da sie sonst zu fest werden."));
        assertThat(recipe.getImageName(), is ("blablupp.jpg"));
    }

    @Test
    public void example_yoast() throws JSONException, IOException {
        when(recipeImageService.downloadToCache(new URL("https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B.jpg"))).thenReturn("blablupp.jpg");

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
        assertThat(recipe.getPreparationTime(), is("10m"));
        assertThat(recipe.getDescription(), is("Pasta mit geröstetem Fenchel und Zitrone – ein herrlich leichtes Pastagericht, in nur 10 Minuten fertig zubereitet."));
        assertThat(recipe.getIngredients(), is("1 Knolle Fenchel\n1  Bio-Zitrone\n1  Knoblauchzehe, geschält und fein gehackt\n1  Schalotte, geschält und fein gehackt\n1 Handvoll Petersilie, frisch, fein gehackt\n5 EL Semmelbrösel\nOlivenöl\nSalz &amp; Pfeffer\n250 g Pasta (z.B. Linguini)"));
        assertThat(recipe.getDirections(), is("Vom Fenchel die oberen grünen Stängel entfernen. Dabei unbedingt das Fenchelgrün aufbewahren. Das kommt später an die Pasta ran. Die Knolle halbieren, den Strunk in der Mitte keilförmig entfernen und nun die zwei Hälften in dünne Streifen schneiden. Die Fenchel-Streifen waschen und gut abtrocknen.\nDie Pasta nach Packungsanweisung garen.\nIn der Zwischenzeit in einer großen Pfanne Olivenöl erhitzen und den Fenchel darin mit etwas Salz ca. 8 Minuten lang anrösten.\nIn einer kleinen Pfanne etwas Olivenöl erhitzen, Knoblauch- und Schalottenwürfel darin mit etwas Salz glasig andünsten. Zitronenabrieb, Petersilie und die Semmelbrösel hinzugeben und alles ca. 4 Minuten vorsichtig anrösten bis die Semmelbrösel leicht angebräunt sind. Die Mischung vom Herd nehmen.\nDie fertige Pasta abgießen. Dabei etwa ein halbes Wasserglas der Kochflüssigkeit auffangen. Abgetropfte Pasta zum Fenchel geben. Die Semmelbröselmischung dazugeben. Kochflüssigkeit nach Belieben dazugeben, damit die Pasta schön glänzend wird. Pasta ordentlich salzen und pfeffern und nach Geschmack Zitrone hinzugeben. Den Saft einer halben Zitrone verträgt das Gericht mindestens. Mit einem guten Schuss Olivenöl und mit Fenchelgrün bestreut servieren."));
        assertThat(recipe.getImageName(), is ("blablupp.jpg"));
    }

    @Test
    public void example_yoast_with_sections() throws JSONException, IOException {
        when(recipeImageService.downloadToCache(new URL("https://veggie-einhorn.de/wp-content/uploads/Einfacher-veganer-Zitronenkuchen-saftig.jpg"))).thenReturn("blablupp.jpg");

        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(recipeImageService);

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
        assertThat(recipe.getDescription(), is("&#x25b6; Super saftiger veganer Zitronenkuchen &#x25b6; Einfaches, sehr leckeres Rezept &#x25b6; Kommt immer gut an&#x25b6; Für 30 cm Kastenbackform."));
        assertThat(recipe.getIngredients(), is("300 g Mehl\n200 g Zucker\n1 Tüte Backpulver\n1 Tüte Vanillezucker\n250 ml Vanille-Sojamilch\n100 g Öl (Rapsöl oder Sonnenblumenöl)\n50 ml Zitronensaft\ngeriebene Zitronenschale\nevtl. Backaroma Zitrone\n150 g Puderzucker\n2 EL Zitronensaft"));
        assertThat(recipe.getDirections(), is("VORBEREITEN: Kastenbackform mit Backpapier auslegen oder einfetten. 2 Zitronen abreiben und auspressen. Backofen vorheizen auf 180 °C (Ober-Unter-Hitze).\nTROCKENE ZUTATEN MISCHEN: In eine Schüssel geben:300 g Mehl 200 g Zucker1 Tüte Backpulver1 Tüte VanillezuckerAbgeriebene ZitronenschaleGut mischen.\nFEUCHTE ZUTATEN ZUFÜGEN: 250 ml Sojamilch Vanille100 g Öl50 ml Zitronensaft Evtl. weiteres Zitronenaroma zufügen(Backaroma oder Zitronenöl)\nTEIG MIXEN UND IN BACKFORM FÜLLEN: Kurz gut durchmixen. In die vorbereitete Kastenbackform füllen.\nBACKEN: Im vorgeheizten Ofen bei 180 °C (Ober-Unter-Hitze) 60 Minuten backen.(Stäbchenprobe) Kuchen in der Form abkühlen lassen.\nZITRONENGLASUR: 150 g Puderzucker sieben.Mit 2 EL Zitronensaft verrühren.Auf dem Kuchen verteilen."));
        assertThat(recipe.getImageName(), is ("blablupp.jpg"));
    }

    @Test
    public void no_Recipe_JsonLd() {
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

    /*
        see https://github.com/flauschtrud/broccoli/issues/189
     */
    @Test
    public void arrified_type() throws JSONException, IOException {
        when(recipeImageService.downloadToCache(new URL("https://www.allrecipes.com/thmb/5SN7ROsF_d5tEiEUj5Vay0TD62g=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/4525663-570993c0dac74778a584ff6169bf8038.jpg"))).thenReturn("blablupp.jpg");

        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(recipeImageService);

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
        assertThat(recipe.getImageName(), is ("blablupp.jpg"));
    }
}