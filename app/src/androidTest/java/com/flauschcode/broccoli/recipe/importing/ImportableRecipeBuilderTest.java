package com.flauschcode.broccoli.recipe.importing;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
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

    private static final String URL_CHEFKOCH = "https://www.chefkoch.de/rezepte/3212051478029180/Vegane-Chocolate-Chip-Cookies.html";
    private static final String RECIPE_JSONLD_CHEFKOCH = """
                {
                "@context": "http://schema.org",
                "@type": "Recipe",
                        "image": "https://img.chefkoch-cdn.de/rezepte/3212051478029180/bilder/1325560/crop-960x540/vegane-chocolate-chip-cookies.jpg",
                                "recipeCategory": "Vegan",
                            "suitableForDiet": "Vegan",
                    "recipeIngredient": [
                                                                                                    "20 g Chiasamen",                                                            "50 ml Wasser",                                                            "190 g Butterersatz oder Margarine, vegan",                                                            "200 g Zucker , braun, alternativ Rohrzucker",                                                            "2 TL Zuckerr\\u00fcbensirup , alternativ Melasse, Ahornsirup oder Agavendicksaft",                                                            "2 Pck. Vanillezucker",                                                            "300 g Weizenmehl oder Dinkelmehl, oder gemischt",                                                            "4 g Natron",                                                            "n. B. Salz",                                                            "200 g Blockschokolade , zartbitter oder Schokotr\\u00f6pfchen"                        ],
                "name": "Vegane Chocolate Chip Cookies",
                "description": "Vegane Chocolate Chip Cookies - au\\u00dfen kross, innen weich, lecker und vegan, ergibt 35 St\\u00fcck. \\u00dcber 110 Bewertungen und f\\u00fcr sehr lecker befunden. Mit \\u25ba Portionsrechner \\u25ba Kochbuch \\u25ba Video-Tipps!",
                "recipeInstructions": "Den Backofen auf 180 \\u00b0C Umluft vorheizen. Die Chiasamen und das Wasser in einer kleinen Sch\\u00fcssel vermengen und ca. 10 Minuten quellen lassen.\\n\\nEin Backblech mit Backpapier auslegen. Vegane Butter bzw. Margarine und Zucker mit den Schneebesen des R\\u00fchrger\\u00e4ts cremig verr\\u00fchren. Dann die gequollenen Chiasamen, den Zuckerr\\u00fcbensirup und beide P\\u00e4ckchen Vanillezucker dazugeben und weiter r\\u00fchren. Unter weiterem R\\u00fchren jetzt zuerst das Mehl hinzugeben und anschlie\\u00dfend Natron sowie Salz. Alternativ - oder falls der Teig zu z\\u00e4h ist - kann alles auch mit den H\\u00e4nden verknetet werden. Abschlie\\u00dfend die Schokotr\\u00f6pfchen bzw. die gehackte Blockschokolade untermischen.\\n\\nDen nun fertigen Teig mit einem Essl\\u00f6ffel oder Eisportionierer klecksweise im Abstand von etwa 5 - 6 cm auf das Backpapier geben. Die Teigkleckse k\\u00f6nnen - m\\u00fcssen jedoch nicht - mit einem L\\u00f6ffel noch etwas rund geformt und flach gedr\\u00fcckt werden.\\n\\nDie Cookies bei 180 \\u00b0C Umluft maximal 15 Minuten backen, da sie sonst zu fest werden.",
                        "author": {
                        "@type": "Person",
                         "name": "Esslust"
                    },
                    "publisher": {
                    "@type": "Organization",
                    "name": "Chefkoch.de"
                },
                "datePublished": "2016-11-03",
                        "prepTime": "P0DT0H20M",
                            "cookTime": "P0DT0H15M",
                    "totalTime": "P0DT0H45M"
                ,
                    "recipeYield": "1 Portion(en)"
                    ,
                    "aggregateRating": {
                        "@type": "AggregateRating",
                                        "ratingCount": 110,
                            "ratingValue": 4.77,
                                                    "reviewCount": 82,
                                    "worstRating": 0,
                        "bestRating": 5
                    }
                    ,
                    "video": [{
                        "@type": "VideoObject",
                        "name": "Video zu Vegane Chocolate Chip Cookies",
                        "description": "Video zu Vegane Chocolate Chip Cookies",
                        "thumbnailUrl": "https://api.chefkoch.de/v2/images/crop-960x540/videos/2873/preview/org",
                        "contentUrl": "https://www.chefkoch.de/rezepte/3212051478029180/Vegane-Chocolate-Chip-Cookies.html",
                        "embedUrl": "https://video.chefkoch-cdn.de/ck.de/videos/2873-video.mp4",
                        "uploadDate": "2016-11-03"
                    }]
                    ,
                    "nutrition": {
                        "@type": "NutritionInformation",
                        "servingSize": "1",
                        "calories": "4594 kcal",
                        "proteinContent": "77,59g",
                        "fatContent": "225,86g",
                        "carbohydrateContent": "540,65g"
                    }
                    ,
                    "keywords": ["Backen","Vegetarisch","einfach","Kekse","Gluten","Lactose"]
                                        ,
                    "reviews": [
                                {
                            "@type": "Review",
                            "reviewBody": "Super \\ud83d\\udc4d\\ud83c\\udffb lecker\\ud83d\\ude0b \\nFoto ist schon unterwegs \\ud83e\\udd70",
                            "datePublished": "2020-09-15"
                            ,
                                "author": {
                                    "@type": "Person",
                                    "name": "Neoncupcake"
                                }
                                        },                    {
                            "@type": "Review",
                            "reviewBody": "Mega gut! Ich hab 50g braunen Zucker und 100g Birkenzucker verwendet und sie bei 170\\u00b0C ca 12-13min gebacken. N\\u00e4chstes Mal mache ich eine Variante mit Kakao im Teig damit es so einen richtig schweren Schokogeschmack bekommt \\ud83d\\ude07",
                            "datePublished": "2020-06-08"
                            ,
                                "author": {
                                    "@type": "Person",
                                    "name": "RoxPox"
                                }
                                        },                    {
                            "@type": "Review",
                            "reviewBody": "Super lecker, ich habe da die Chiasamen allerdings mit Flosamen ausgetauscht, da ich das da hatte. Habe daf\\u00fcr etwas mehr Wasser f\\u00fcr das Quellverhalten gegeben (80 ml). Hat aber auch klasse funktioniert und die Cookies sind sehr fein geworden. Bei ein paar habe ich auch ein paar Haferflocken untergemischt :)",
                            "datePublished": "2020-06-04"
                            ,
                                "author": {
                                    "@type": "Person",
                                    "name": "alenalue"
                                }
                                        },                    {
                            "@type": "Review",
                            "reviewBody": "Genial!!! Die besten Cookies die ich je gegessen habe. Ich habe einen Test mit 3 verschiedenen Cookie Rezepten durchgef\\u00fchrt und dieses hat mit Abstand gewonnen. Wurde auch von all meinen nicht Veganer Freunden dankend angenommen. Schmeckt auch super mit Vollkorn- oder Dinkelmehl. LG",
                            "datePublished": "2020-05-29"
                            ,
                                "author": {
                                    "@type": "Person",
                                    "name": "Mrs Sommer"
                                }
                                        },                    {
                            "@type": "Review",
                            "reviewBody": "Geschmacklich sind die 1a! Aber Chiasamen als Eiersatz st\\u00f6ren mich immer, weil sie beim Essen unangenehm an den Z\\u00e4hnen kleben bleiben. Ich denke n\\u00e4chstes mal probiere ich es mit gemahlenen Leinsamen.",
                            "datePublished": "2020-05-18"
                            ,
                                "author": {
                                    "@type": "Person",
                                    "name": "electricblueberry"
                                }
                                        }                ]
                }
    """;

    private static final String MINIMIZED_RECIPE_JSONLD = """
                {
                "@context": "http://schema.org",
                "@type": "Recipe",
                                "recipeCategory": "Vegan",
                            "suitableForDiet": "Vegan",
                "name": "Vegane Chocolate Chip Cookies"
                }
    """;

    private static final String URL_YOAST = "https://stilettosandsprouts.de/vegane-fenchel-pasta/";
    private static final String RECIPE_JSON_LD_YOAST = """
            {
              "@context": "http://schema.org/",
              "@type": "Recipe",
              "name": "Vegane Fenchel-Pasta",
              "author": {
                "@type": "Person",
                "name": "Katja"
              },
              "description": "Pasta mit geröstetem Fenchel und Zitrone – ein herrlich leichtes Pastagericht, in nur 10 Minuten fertig zubereitet.",
              "datePublished": "2018-05-15T07:00:48+00:00",
              "image": [
                "https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B.jpg",
                "https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B-500x500.jpg",
                "https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B-500x375.jpg",
                "https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B-480x270.jpg"
              ],
              "recipeYield": [
                "2",
                "2 Personen"
              ],
              "cookTime": "PT10M",
              "recipeIngredient": [
                "1 Knolle Fenchel",
                "1  Bio-Zitrone",
                "1  Knoblauchzehe, geschält und fein gehackt",
                "1  Schalotte, geschält und fein gehackt",
                "1 Handvoll Petersilie, frisch, fein gehackt",
                "5 EL Semmelbrösel",
                "Olivenöl",
                "Salz &amp; Pfeffer",
                "250 g Pasta (z.B. Linguini)"
              ],
              "recipeInstructions": [
                {
                  "@type": "HowToStep",
                  "text": "Vom Fenchel die oberen grünen Stängel entfernen. Dabei unbedingt das Fenchelgrün aufbewahren. Das kommt später an die Pasta ran. Die Knolle halbieren, den Strunk in der Mitte keilförmig entfernen und nun die zwei Hälften in dünne Streifen schneiden. Die Fenchel-Streifen waschen und gut abtrocknen.",
                  "name": "Vom Fenchel die oberen grünen Stängel entfernen. Dabei unbedingt das Fenchelgrün aufbewahren. Das kommt später an die Pasta ran. Die Knolle halbieren, den Strunk in der Mitte keilförmig entfernen und nun die zwei Hälften in dünne Streifen schneiden. Die Fenchel-Streifen waschen und gut abtrocknen.",
                  "url": "https://stilettosandsprouts.de/vegane-fenchel-pasta/#wprm-recipe-6504-step-0-0"
                },
                {
                  "@type": "HowToStep",
                  "text": "Die Pasta nach Packungsanweisung garen.",
                  "name": "Die Pasta nach Packungsanweisung garen.",
                  "url": "https://stilettosandsprouts.de/vegane-fenchel-pasta/#wprm-recipe-6504-step-0-1"
                },
                {
                  "@type": "HowToStep",
                  "text": "In der Zwischenzeit in einer großen Pfanne Olivenöl erhitzen und den Fenchel darin mit etwas Salz ca. 8 Minuten lang anrösten.",
                  "name": "In der Zwischenzeit in einer großen Pfanne Olivenöl erhitzen und den Fenchel darin mit etwas Salz ca. 8 Minuten lang anrösten.",
                  "url": "https://stilettosandsprouts.de/vegane-fenchel-pasta/#wprm-recipe-6504-step-0-2"
                },
                {
                  "@type": "HowToStep",
                  "text": "In einer kleinen Pfanne etwas Olivenöl erhitzen, Knoblauch- und Schalottenwürfel darin mit etwas Salz glasig andünsten. Zitronenabrieb, Petersilie und die Semmelbrösel hinzugeben und alles ca. 4 Minuten vorsichtig anrösten bis die Semmelbrösel leicht angebräunt sind. Die Mischung vom Herd nehmen.",
                  "name": "In einer kleinen Pfanne etwas Olivenöl erhitzen, Knoblauch- und Schalottenwürfel darin mit etwas Salz glasig andünsten. Zitronenabrieb, Petersilie und die Semmelbrösel hinzugeben und alles ca. 4 Minuten vorsichtig anrösten bis die Semmelbrösel leicht angebräunt sind. Die Mischung vom Herd nehmen.",
                  "url": "https://stilettosandsprouts.de/vegane-fenchel-pasta/#wprm-recipe-6504-step-0-3"
                },
                {
                  "@type": "HowToStep",
                  "text": "Die fertige Pasta abgießen. Dabei etwa ein halbes Wasserglas der Kochflüssigkeit auffangen. Abgetropfte Pasta zum Fenchel geben. Die Semmelbröselmischung dazugeben. Kochflüssigkeit nach Belieben dazugeben, damit die Pasta schön glänzend wird. Pasta ordentlich salzen und pfeffern und nach Geschmack Zitrone hinzugeben. Den Saft einer halben Zitrone verträgt das Gericht mindestens. Mit einem guten Schuss Olivenöl und mit Fenchelgrün bestreut servieren.",
                  "name": "Die fertige Pasta abgießen. Dabei etwa ein halbes Wasserglas der Kochflüssigkeit auffangen. Abgetropfte Pasta zum Fenchel geben. Die Semmelbröselmischung dazugeben. Kochflüssigkeit nach Belieben dazugeben, damit die Pasta schön glänzend wird. Pasta ordentlich salzen und pfeffern und nach Geschmack Zitrone hinzugeben. Den Saft einer halben Zitrone verträgt das Gericht mindestens. Mit einem guten Schuss Olivenöl und mit Fenchelgrün bestreut servieren.",
                  "url": "https://stilettosandsprouts.de/vegane-fenchel-pasta/#wprm-recipe-6504-step-0-4"
                }
              ],
              "aggregateRating": {
                "@type": "AggregateRating",
                "ratingValue": "5",
                "ratingCount": "4"
              },
              "@id": "https://stilettosandsprouts.de/vegane-fenchel-pasta/#recipe",
              "isPartOf": {
                "@id": "https://stilettosandsprouts.de/vegane-fenchel-pasta/#webpage"
              },
              "mainEntityOfPage": "https://stilettosandsprouts.de/vegane-fenchel-pasta/#webpage"
            }
    """;

    private static final String URL_YOAST_WITH_SECTIONS = "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/";
    private static final String RECIPE_YOAST_WITH_SECTIONS = """
            {
                  "@context": "http://schema.org/",
                  "@type": "Recipe",
                  "name": "Saftiger Veganer Zitronenkuchen",
                  "author": {
                    "@type": "Person",
                    "name": "Karen Wilkening"
                  },
                  "description": "&#x25b6; Super saftiger veganer Zitronenkuchen &#x25b6; Einfaches, sehr leckeres Rezept &#x25b6; Kommt immer gut an&#x25b6; Für 30 cm Kastenbackform.",
                  "datePublished": "2019-07-01T20:18:20+00:00",
                  "image": [
                    "https://veggie-einhorn.de/wp-content/uploads/Einfacher-veganer-Zitronenkuchen-saftig.jpg",
                    "https://veggie-einhorn.de/wp-content/uploads/Einfacher-veganer-Zitronenkuchen-saftig-500x500.jpg",
                    "https://veggie-einhorn.de/wp-content/uploads/Einfacher-veganer-Zitronenkuchen-saftig-500x375.jpg",
                    "https://veggie-einhorn.de/wp-content/uploads/Einfacher-veganer-Zitronenkuchen-saftig-480x270.jpg"
                  ],
                  "recipeYield": [
                    "1",
                    "1 Kuchen"
                  ],
                  "totalTime": "PT75M",
                  "recipeIngredient": [
                    "300 g Mehl",
                    "200 g Zucker",
                    "1 Tüte Backpulver",
                    "1 Tüte Vanillezucker",
                    "250 ml Vanille-Sojamilch",
                    "100 g Öl (Rapsöl oder Sonnenblumenöl)",
                    "50 ml Zitronensaft",
                    "geriebene Zitronenschale",
                    "evtl. Backaroma Zitrone",
                    "150 g Puderzucker",
                    "2 EL Zitronensaft"
                  ],
                  "recipeInstructions": [
                    {
                      "@type": "HowToSection",
                      "name": "Vorbereiten:",
                      "itemListElement": [
                        {
                          "@type": "HowToStep",
                          "text": "Kastenbackform mit Backpapier auslegen oder einfetten.",
                          "name": "Kastenbackform mit Backpapier auslegen oder einfetten.",
                          "url": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-0-0"
                        },
                        {
                          "@type": "HowToStep",
                          "text": "2 Zitronen abreiben und auspressen.",
                          "name": "2 Zitronen abreiben und auspressen.",
                          "url": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-0-1"
                        },
                        {
                          "@type": "HowToStep",
                          "text": "Backofen vorheizen auf 180 °C (Ober-Unter-Hitze).",
                          "name": "Backofen vorheizen auf 180 °C (Ober-Unter-Hitze).",
                          "url": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-0-2"
                        }
                      ]
                    },
                    {
                      "@type": "HowToSection",
                      "name": "Trockene Zutaten mischen:",
                      "itemListElement": [
                        {
                          "@type": "HowToStep",
                          "text": "In eine Schüssel geben:300 g Mehl 200 g Zucker1 Tüte Backpulver1 Tüte VanillezuckerAbgeriebene ZitronenschaleGut mischen.",
                          "name": "In eine Schüssel geben:300 g Mehl 200 g Zucker1 Tüte Backpulver1 Tüte VanillezuckerAbgeriebene ZitronenschaleGut mischen.",
                          "url": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-1-0"
                        }
                      ]
                    },
                    {
                      "@type": "HowToSection",
                      "name": "Feuchte Zutaten zufügen:",
                      "itemListElement": [
                        {
                          "@type": "HowToStep",
                          "text": "250 ml Sojamilch Vanille100 g Öl50 ml Zitronensaft",
                          "name": "250 ml Sojamilch Vanille100 g Öl50 ml Zitronensaft",
                          "url": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-2-0"
                        },
                        {
                          "@type": "HowToStep",
                          "text": "Evtl. weiteres Zitronenaroma zufügen(Backaroma oder Zitronenöl)",
                          "name": "Evtl. weiteres Zitronenaroma zufügen(Backaroma oder Zitronenöl)",
                          "url": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-2-1"
                        }
                      ]
                    },
                    {
                      "@type": "HowToSection",
                      "name": "Teig mixen und in Backform füllen:",
                      "itemListElement": [
                        {
                          "@type": "HowToStep",
                          "text": "Kurz gut durchmixen.",
                          "name": "Kurz gut durchmixen.",
                          "url": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-3-0"
                        },
                        {
                          "@type": "HowToStep",
                          "text": "In die vorbereitete Kastenbackform füllen.",
                          "name": "In die vorbereitete Kastenbackform füllen.",
                          "url": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-3-1"
                        }
                      ]
                    },
                    {
                      "@type": "HowToSection",
                      "name": "Backen:",
                      "itemListElement": [
                        {
                          "@type": "HowToStep",
                          "text": "Im vorgeheizten Ofen bei 180 °C (Ober-Unter-Hitze) 60 Minuten backen.(Stäbchenprobe)",
                          "name": "Im vorgeheizten Ofen bei 180 °C (Ober-Unter-Hitze) 60 Minuten backen.(Stäbchenprobe)",
                          "url": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-4-0"
                        },
                        {
                          "@type": "HowToStep",
                          "text": "Kuchen in der Form abkühlen lassen.",
                          "name": "Kuchen in der Form abkühlen lassen.",
                          "url": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-4-1"
                        }
                      ]
                    },
                    {
                      "@type": "HowToSection",
                      "name": "Zitronenglasur:",
                      "itemListElement": [
                        {
                          "@type": "HowToStep",
                          "text": "150 g Puderzucker sieben.Mit 2 EL Zitronensaft verrühren.Auf dem Kuchen verteilen.",
                          "name": "150 g Puderzucker sieben.Mit 2 EL Zitronensaft verrühren.Auf dem Kuchen verteilen.",
                          "url": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#wprm-recipe-36837-step-5-0"
                        }
                      ]
                    }
                  ],
                  "aggregateRating": {
                    "@type": "AggregateRating",
                    "ratingValue": "4.97",
                    "ratingCount": "29"
                  },
                  "recipeCategory": [
                    "Backen"
                  ],
                  "recipeCuisine": [
                    "vegan"
                  ],
                  "suitableForDiet": [
                    "https://schema.org/LowLactoseDiet",
                    "https://schema.org/VeganDiet"
                  ],
                  "keywords": "einfach, mit öl, ohne butter, ohne ei, ohne milch, super saftig",
                  "nutrition": {
                    "@type": "NutritionInformation",
                    "servingSize": "1 Stück (bei 12 Stücken)",
                    "calories": "292 kcal",
                    "carbohydrateContent": "49 g",
                    "proteinContent": "3.8 g",
                    "fatContent": "8.4 g",
                    "saturatedFatContent": "0.75 g",
                    "sugarContent": "31 g"
                  },
                  "@id": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#recipe",
                  "isPartOf": {
                    "@id": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#webpage"
                  },
                  "mainEntityOfPage": "https://veggie-einhorn.de/saftiger-veganer-zitronenkuchen/#webpage"
                }
    """;

    private static final String URL_ARRIFIED_TYPE = "https://www.allrecipes.com/recipe/147103/delicious-egg-salad-for-sandwiches/";

    private static final String RECIPE_ARRIFIED_TYPE = """
            {
            "@context": "http://schema.org",
            "@type": ["Recipe","NewsArticle"]
            ,"headline": "Delicious Egg Salad for Sandwiches"
            ,"datePublished": "2007-08-12T01:48:39.000-04:00"
            ,"dateModified": "2007-08-12T01:48:39.000-04:00"
            ,"author": [
            {"@type": "Person"
            ,"name": "wifeyluvs2cook"
            ,"url": "https://www.allrecipes.com/cook/2309128"
            }
            ]
            ,"description": "This egg salad is quick to prep and easy to make with hard-cooked eggs, mayonnaise, mustard, paprika, and green onions. Perfect for sandwiches!"
            ,"image": {
            "@type": "ImageObject",
            "url": "https://www.allrecipes.com/thmb/5SN7ROsF_d5tEiEUj5Vay0TD62g=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/4525663-570993c0dac74778a584ff6169bf8038.jpg",
            "height": 1500,
            "width": 1500
            }
            ,"video": {
            "@type": "VideoObject",
            "embedUrl": "https://players.brightcove.net/1033249144001/default_default/index.html?videoId=1892556557001",
            "description": "AR0742",
            "duration": "PT2M31S",
            "name": "Delicious Egg Salad for Sandwiches",
            "thumbnailUrl": "https://cf-images.us-east-1.prod.boltdns.net/v1/static/1033249144001/952e480b-889d-4527-8f06-537723f5f600/dc9f0925-7fe3-4e43-aa34-14d4f1f18b1d/1280x720/match/image.jpg",
            "uploadDate": "2021-02-27T17:29:07.998-05:00"}
            ,"publisher": {
            "@type": "Organization",
            "name": "Allrecipes",
            "url": "https://www.allrecipes.com",
            "logo": {
            "@type": "ImageObject",
            "url": "https://www.allrecipes.com/thmb/Z9lwz1y0B5aX-cemPiTgpn5YB0k=/112x112/filters:no_upscale():max_bytes(150000):strip_icc()/allrecipes_logo_schema-867c69d2999b439a9eba923a445ccfe3.png",
            "width": 112,
            "height": 112
            },
            "brand": "Allrecipes"
            , "publishingPrinciples": "https://www.allrecipes.com/about-us-6648102#toc-editorial-guidelines"
            , "sameAs" : [
            "https://www.facebook.com/allrecipes",
            "https://www.instagram.com/allrecipes/",
            "https://www.pinterest.com/allrecipes/",
            "https://www.tiktok.com/@allrecipes",
            "https://www.youtube.com/user/allrecipes/videos",
            "https://twitter.com/Allrecipes",
            "https://flipboard.com/@Allrecipes",
            "https://en.wikipedia.org/wiki/Allrecipes.com",
            "https://apps.apple.com/us/app/allrecipes-dinner-spinner/id299515267",
            "https://play.google.com/store/apps/details?id=com.allrecipes.spinner.free&hl=en_US&gl=US",
            "https://www.youtube.com/c/foodwishes",
            "https://www.linkedin.com/company/19312/admin/"
            ]
            }
            ,"name": "Delicious Egg Salad for Sandwiches"
            ,"aggregateRating": {
            "@type": "AggregateRating",
            "ratingValue": "4.7",
            "ratingCount": "2210"
            }
            ,"cookTime": "PT15M"
            ,"nutrition": {
            "@type": "NutritionInformation"
            ,"calories": "344 kcal"
            ,"carbohydrateContent": "2 g"
            ,"cholesterolContent": "383 mg"
            ,"fiberContent": "0 g"
            ,"proteinContent": "13 g"
            ,"saturatedFatContent": "6 g"
            ,"sodiumContent": "351 mg"
            ,"sugarContent": "1 g"
            ,"fatContent": "32 g"
            ,"unsaturatedFatContent": "0 g"
            }
            ,"prepTime": "PT10M"
            , "recipeCategory": ["Lunch"]
            ,"recipeIngredient": [
            "8 eggs",
            "0.5 cup mayonnaise",
            "0.25 cup chopped green onion",
            "1 teaspoon prepared yellow mustard",
            "0.25 teaspoon paprika",
            "salt and pepper to taste" ]
            ,"recipeInstructions": [
            {
            "@type": "HowToStep"
            ,"text": "Place eggs in a saucepan and cover with cold water. Bring water to a boil and immediately remove from heat. Cover and let eggs stand in hot water for 10 to 12 minutes. Remove from hot water, cool, peel, and chop."
            } ,{
            "@type": "HowToStep"
            ,"image": [
            {
            "@type": "ImageObject",
            "url": "https://www.allrecipes.com/thmb/5SN7ROsF_d5tEiEUj5Vay0TD62g=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/4525663-570993c0dac74778a584ff6169bf8038.jpg"
            }
            ]
            ,"text": "Place chopped eggs in a bowl; stir in mayonnaise, green onion, and mustard. Season with paprika, salt, and pepper. Stir and serve on your favorite bread or crackers."
            } ]
            ,"recipeYield": ["4"]
            ,"totalTime": "PT35M"
            ,"mainEntityOfPage": {
            "@type": ["WebPage"]
            ,"@id": "https://www.allrecipes.com/recipe/147103/delicious-egg-salad-for-sandwiches/"
            ,"breadcrumb": {
            "@type": "BreadcrumbList",
            "itemListElement": [
            {
            "@type": "ListItem",
            "position": 1,
            "item": {
            "@id": "https://www.allrecipes.com/recipes/96/salad/",
            "name": "Salad"
            }
            }
            ,
            {
            "@type": "ListItem",
            "position": 2,
            "item": {
            "@id": "https://www.allrecipes.com/recipes/1986/salad/egg-salad/",
            "name": "Egg Salad Recipes"
            }
            }
            ,
            {
            "@type": "ListItem",
            "position": 3,
            "item": {
            "@id": "https://www.allrecipes.com/recipe/147103/delicious-egg-salad-for-sandwiches/",
            "name": "Delicious Egg Salad for Sandwiches"
            }
            }
            ]
            }
            }
            , "about": [
            ]
            }
    """;

    private static final String URL_ARRIFIED_IMAGES = "https://https://www.ndr.de/ratgeber/kochen/rezepte/Auberginen-Curry-mit-Reis,auberginencurry104.html";

    private static final String RECIPE_ARRIFIED_IMAGES = """
            {
              "@context" : "https://schema.org",
              "@type" : "Recipe",
              "name" : "Auberginen-Curry mit Reis",
              "description" : "Die Auberginen schmoren bei diesem veganen Gericht mit Kokosmilch und Tomaten und werden nach Belieben pikant gewürzt.",
              "recipeCategory" : "gesund, vegan, vegetarisch, Gemüse, Reis",
              "dateModified" : "2025-10-14T10:42:36.751Z",
              "datePublished" : "2025-10-14T10:42:36.699Z",
              "image" : [ {
                "@type" : "ImageObject",
                "url" : "https://images.ndr.de/image/5265d7c2-3817-4d1f-b8f0-116ecbb1352d/AAABlL-u-ik/AAABmgWmh8Q/16x9-big/auberginencurry100.jpg?width=1920",
                "author" : "NDR, Claudia Timmann",
                "width" : 1920,
                "height" : 1080,
                "datePublished" : "2025-02-01T04:45:31.202+01:00",
                "description" : "Eine Schale mit einem Auberginen-Curry und Reis."
              }, {
                "@type" : "ImageObject",
                "url" : "https://images.ndr.de/image/5265d7c2-3817-4d1f-b8f0-116ecbb1352d/AAABlL-u-ik/AAABmgWmzQQ/1x1-big/auberginencurry100.jpg?width=1400",
                "author" : "NDR, Claudia Timmann",
                "width" : 1400,
                "height" : 1400,
                "datePublished" : "2025-02-01T04:45:31.202+01:00",
                "description" : "Eine Schale mit einem Auberginen-Curry und Reis."
              }, {
                "@type" : "ImageObject",
                "url" : "https://images.ndr.de/image/5265d7c2-3817-4d1f-b8f0-116ecbb1352d/AAABlL-u-ik/AAABmgWm7Uc/4x3/auberginencurry100.jpg?width=1280",
                "author" : "NDR, Claudia Timmann",
                "width" : 1280,
                "height" : 960,
                "datePublished" : "2025-02-01T04:45:31.202+01:00",
                "description" : "Eine Schale mit einem Auberginen-Curry und Reis."
              } ],
              "publisher" : {
                "@type" : "newsMediaOrganization",
                "name" : "ndr.de",
                "url" : "https://www.ndr.de",
                "alternateName" : "Norddeutscher Rundfunk",
                "correctionsPolicy" : "https://www.ndr.de/home/korrekturuebersicht100.html",
                "diversityPolicy" : "https://www.ndr.de/der_ndr/unternehmen/Charta-der-Vielfalt-im-NDR,charta107.html",
                "sameAs" : "https://www.facebook.com/NDR.de, https://twitter.com/ndr, https://www.instagram.com/ndr.de/?hl=de",
                "logo" : {
                  "@type" : "ImageObject",
                  "url" : "https://www.ndr.de/favicon.svg"
                }
              },
              "totalTime" : "PT35M",
              "recipeYield" : 4,
              "expires" : "2027-09-22T19:00:00.000Z",
              "recipeIngredient" : [ "2 Auberginen", "2 EL Knoblauchöl", "400 g stückige Tomaten", "400 ml Kokosmilch", "200 g Vollkorn-Basmatireis", "Currypulver", "Gewürze", "Kräuter" ],
              "recipeInstructions" : [ "Auberginen waschen, putzen und in mundgerechte Stücke schneiden. In einer großen Pfanne Knoblauchöl erhitzen und Auberginen darin anbraten. Bei mittlerer Hitze circa 20 Minuten schmoren lassen, dabei regelmäßig wenden. \\n\\nIn der Zwischenzeit den Reis nach Packungsanweisung gar kochen. Die Kokosmilch und die Tomaten zu den Auberginen geben. Nach Belieben würzen - z.B. mit Kurkuma, Ingwer, Paprika oder frischen Kräutern wie Koriander - und alles unter Rühren etwa 10 Minuten einkochen lassen. \\n\\nDen Reis mit dem Auberginen-Curry auf Tellern anrichten. Wer mag, streut Sesam darüber." ],
              "author" : {
                "@type" : "Organization",
                "name" : "ndr.de"
              },
              "isAccessibleForFree" : "true"
            }
            """;

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

        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(application, recipeImageService);

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
        assertThat(recipe.getNutritionalValues(), is("Serving: 1\nCalories: 4594 kcal\nFat: 225,86g\nCarbohydrates: 540,65g\nProtein: 77,59g"));
        assertThat(recipe.getImageName(), is("blablupp.jpg"));
    }

    @Test
    public void example_yoast() throws JSONException, IOException {
        when(recipeImageService.downloadToCache(new URL("https://stilettosandsprouts.de/wp-content/uploads/2018/05/Vegane_Fenchel_Pasta_02_B.jpg"))).thenReturn("blablupp.jpg");

        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(application, recipeImageService);

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
        assertThat(recipe.getIngredients(), is("1 Knolle Fenchel\n1 Bio-Zitrone\n1 Knoblauchzehe, geschält und fein gehackt\n1 Schalotte, geschält und fein gehackt\n1 Handvoll Petersilie, frisch, fein gehackt\n5 EL Semmelbrösel\nOlivenöl\nSalz & Pfeffer\n250 g Pasta (z.B. Linguini)"));
        assertThat(recipe.getDirections(), is("Vom Fenchel die oberen grünen Stängel entfernen. Dabei unbedingt das Fenchelgrün aufbewahren. Das kommt später an die Pasta ran. Die Knolle halbieren, den Strunk in der Mitte keilförmig entfernen und nun die zwei Hälften in dünne Streifen schneiden. Die Fenchel-Streifen waschen und gut abtrocknen.\nDie Pasta nach Packungsanweisung garen.\nIn der Zwischenzeit in einer großen Pfanne Olivenöl erhitzen und den Fenchel darin mit etwas Salz ca. 8 Minuten lang anrösten.\nIn einer kleinen Pfanne etwas Olivenöl erhitzen, Knoblauch- und Schalottenwürfel darin mit etwas Salz glasig andünsten. Zitronenabrieb, Petersilie und die Semmelbrösel hinzugeben und alles ca. 4 Minuten vorsichtig anrösten bis die Semmelbrösel leicht angebräunt sind. Die Mischung vom Herd nehmen.\nDie fertige Pasta abgießen. Dabei etwa ein halbes Wasserglas der Kochflüssigkeit auffangen. Abgetropfte Pasta zum Fenchel geben. Die Semmelbröselmischung dazugeben. Kochflüssigkeit nach Belieben dazugeben, damit die Pasta schön glänzend wird. Pasta ordentlich salzen und pfeffern und nach Geschmack Zitrone hinzugeben. Den Saft einer halben Zitrone verträgt das Gericht mindestens. Mit einem guten Schuss Olivenöl und mit Fenchelgrün bestreut servieren."));
        assertThat(recipe.getNutritionalValues(), is(""));
        assertThat(recipe.getImageName(), is("blablupp.jpg"));
    }

    @Test
    public void example_yoast_with_sections() throws JSONException, IOException {
        when(recipeImageService.downloadToCache(new URL("https://veggie-einhorn.de/wp-content/uploads/Einfacher-veganer-Zitronenkuchen-saftig.jpg"))).thenReturn("blablupp.jpg");

        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(application, recipeImageService);

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
        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(application, recipeImageService);

        Optional<Recipe> optionalRecipe = recipeBuilder
                .build();

        assertThat(optionalRecipe.isPresent(), is(false));
    }

    @Test
    public void missing_attributes() throws JSONException {
        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(application, recipeImageService);

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

        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(application, recipeImageService);

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

        ImportableRecipeBuilder recipeBuilder = new ImportableRecipeBuilder(application, recipeImageService);

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
        assertThat(recipe.getImageName(), is("blablupp.jpg")); // TODO doesn't work yet
    }

}