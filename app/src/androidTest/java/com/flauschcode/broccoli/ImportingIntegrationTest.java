package com.flauschcode.broccoli;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.flauschcode.broccoli.util.CustomViewActions.waitForView;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.flauschcode.broccoli.recipe.crud.CreateAndEditRecipeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/*

Some other recipes that use JSON-LD:
https://veganheaven.org/recipe/one-pan-mexican-quinoa (Yoast, most complicated)
https://www.kochbar.de/rezept/378809/Veganer-Schokokuchen.html
https://www.lecker.de/kleine-kuerbis-toertchen-roh-und-vegan-76085.html
https://eatsmarter.de/rezepte/suesskartoffel-kuerbis-bratlinge-mit-sojajoghurt-dip (instructions as string array)
https://www.springlane.de/magazin/rezeptideen/vegane-kuerbissuppe-mit-kokosmilch/
https://stilettosandsprouts.de/vegane-fenchel-pasta/ (Yoast, most complicated)

 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ImportingIntegrationTest {

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        AccessibilityChecks.enable();
    }

    @After
    public void tearDown() {
        scenario.close();
    }

    @Test
    public void import_new_recipe_from_chefkoch() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAndEditRecipeActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "https://www.chefkoch.de/rezepte/3212051478029180/Vegane-Chocolate-Chip-Cookies.html");
        scenario = launch(intent);

        onView(isRoot()).perform(waitForView(withText("Vegane Chocolate Chip Cookies"), 10000));

        onView(ViewMatchers.withId(R.id.new_title)).check(matches(withText("Vegane Chocolate Chip Cookies")));
        onView(withId(R.id.new_source)).check(matches(withText("https://www.chefkoch.de/rezepte/3212051478029180/Vegane-Chocolate-Chip-Cookies.html")));
        onView(withId(R.id.new_servings)).check(matches(withText("1")));
        onView(withId(R.id.new_preparation_time)).check(matches(withText("35m")));
        onView(withId(R.id.new_description)).check(matches(withSubstring("Vegane Chocolate Chip Cookies - außen kross, innen weich, lecker und vegan, ergibt 35 Stück.")));
        onView(withId(R.id.new_ingredients)).check(matches(withText("20 g Chiasamen\n50 ml Wasser\n190 g Butterersatz oder Margarine, vegan\n200 g Zucker , braun, alternativ Rohrzucker\n2 TL Rübensirup , alternativ Melasse, Ahornsirup oder Agavendicksaft\n2 Pck. Vanillezucker\n300 g Weizenmehl oder Dinkelmehl, oder gemischt\n4 g Natron\nn. B. Salz\n200 g Blockschokolade , zartbitter oder Schokotröpfchen")));
        onView(withId(R.id.new_directions)).check(matches(withText("Den Backofen auf 180 °C Umluft vorheizen. Die Chiasamen und das Wasser in einer kleinen Schüssel vermengen und ca. 10 Minuten quellen lassen.\n\nEin Backblech mit Backpapier auslegen. Vegane Butter bzw. Margarine und Zucker mit den Schneebesen des Rührgeräts cremig verrühren. Dann die gequollenen Chiasamen, den Zuckerrübensirup und beide Päckchen Vanillezucker dazugeben und weiter rühren. Unter weiterem Rühren jetzt zuerst das Mehl hinzugeben und anschließend Natron sowie Salz. Alternativ - oder falls der Teig zu zäh ist - kann alles auch mit den Händen verknetet werden. Abschließend die Schokotröpfchen bzw. die gehackte Blockschokolade untermischen.\n\nDen nun fertigen Teig mit einem Esslöffel oder Eisportionierer klecksweise im Abstand von etwa 5 - 6 cm auf das Backpapier geben. Die Teigkleckse können - müssen jedoch nicht - mit einem Löffel noch etwas rund geformt und flach gedrückt werden.\n\nDie Cookies bei 180 °C Umluft maximal 15 Minuten backen, da sie sonst zu fest werden.")));
    }

    @Test
    public void import_new_recipe_via_yoast_plugin() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAndEditRecipeActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "https://stilettosandsprouts.de/vegane-fenchel-pasta/");
        scenario = launch(intent);

        onView(isRoot()).perform(waitForView(withText("Vegane Fenchel-Pasta"), 10000));

        onView(withId(R.id.new_title)).check(matches(withText("Vegane Fenchel-Pasta")));
        onView(withId(R.id.new_source)).check(matches(withText("https://stilettosandsprouts.de/vegane-fenchel-pasta/")));
        onView(withId(R.id.new_servings)).check(matches(withText("2")));
        onView(withId(R.id.new_preparation_time)).check(matches(withText("10m")));
        onView(withId(R.id.new_description)).check(matches(withSubstring("Pasta mit geröstetem Fenchel und Zitrone – ein herrlich leichtes Pastagericht, in nur 10 Minuten fertig zubereitet.")));
        onView(withId(R.id.new_ingredients)).check(matches(withText("1 Knolle Fenchel\n1  Bio-Zitrone\n1  Knoblauchzehe, geschält und fein gehackt\n1  Schalotte, geschält und fein gehackt\n1 Handvoll Petersilie, frisch, fein gehackt\n5 EL Semmelbrösel\nOlivenöl\nSalz &amp; Pfeffer\n250 g Pasta (z.B. Linguini)")));
        onView(withId(R.id.new_directions)).check(matches(withText("Vom Fenchel die oberen grünen Stängel entfernen. Dabei unbedingt das Fenchelgrün aufbewahren. Das kommt später an die Pasta ran. Die Knolle halbieren, den Strunk in der Mitte keilförmig entfernen und nun die zwei Hälften in dünne Streifen schneiden. Die Fenchel-Streifen waschen und gut abtrocknen.\nDie Pasta nach Packungsanweisung garen.\nIn der Zwischenzeit in einer großen Pfanne Olivenöl erhitzen und den Fenchel darin mit etwas Salz ca. 8 Minuten lang anrösten.\nIn einer kleinen Pfanne etwas Olivenöl erhitzen, Knoblauch- und Schalottenwürfel darin mit etwas Salz glasig andünsten. Zitronenabrieb, Petersilie und die Semmelbrösel hinzugeben und alles ca. 4 Minuten vorsichtig anrösten bis die Semmelbrösel leicht angebräunt sind. Die Mischung vom Herd nehmen.\nDie fertige Pasta abgießen. Dabei etwa ein halbes Wasserglas der Kochflüssigkeit auffangen. Abgetropfte Pasta zum Fenchel geben. Die Semmelbröselmischung dazugeben. Kochflüssigkeit nach Belieben dazugeben, damit die Pasta schön glänzend wird. Pasta ordentlich salzen und pfeffern und nach Geschmack Zitrone hinzugeben. Den Saft einer halben Zitrone verträgt das Gericht mindestens. Mit einem guten Schuss Olivenöl und mit Fenchelgrün bestreut servieren.")));
    }

    @Test
    public void import_new_recipe_with_arrified_json() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAndEditRecipeActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "https://www.gutekueche.de/fladenbrot-grundrezept-rezept-1673");
        scenario = launch(intent);

        onView(isRoot()).perform(waitForView(withText("Fladenbrot Grundrezept"), 10000));

        onView(withId(R.id.new_title)).check(matches(withText("Fladenbrot Grundrezept")));
        onView(withId(R.id.new_source)).check(matches(withText("https://www.gutekueche.de/fladenbrot-grundrezept-rezept-1673")));
        onView(withId(R.id.new_servings)).check(matches(withText("4")));
        onView(withId(R.id.new_preparation_time)).check(matches(withText("25m")));
        onView(withId(R.id.new_description)).check(matches(withSubstring("Dieses Grundrezept für Fladenbrot ohne Hefe passt zu vielen Gerichten. Das einfache und schnelle Rezept ist sehr variabel.")));
        onView(withId(R.id.new_ingredients)).check(matches(withText("200 g Mehl, Type 550\n3 EL Olivenöl (oder Pflanzenöl)\n3 EL Olivenöl (oder Pflanzenöl)\n1 Prise Salz\n100 ml Wasser")));
        onView(withId(R.id.new_directions)).check(matches(withText("Zuerst das Mehl in eine Schüssel geben, Salz, Wasser und Olivenöl dazugeben und alle Zutaten zu einem Teig verkneten - am besten mit der Hand.\nDann den Teig für 10 Minuten quellen lassen und erneut für 5 Minuten kneten, so dass ein glatter Teig entsteht.\nSpäter aus dem Teig 4 dünne Fladen formen, eine gusseiserne Pfanne (=unbeschichtet) ohne Fett erhitzen und die Teigfladen darin nacheinander backen bis sich die ersten braunen Flecken zeigen.\nIm Anschluss die Fladen wenden und auch auf der anderen Seite backen.")));
    }

    @Test
    public void import_new_recipe_from_flauschcode() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateAndEditRecipeActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "https://flauschcode.com/hummus-cremig-und-lecker");
        scenario = launch(intent);

        onView(isRoot()).perform(waitForView(withText("Hummus"), 10000));

        onView(ViewMatchers.withId(R.id.new_title)).check(matches(withText("Hummus")));
        onView(withId(R.id.new_source)).check(matches(withText("https://flauschcode.com/hummus-cremig-und-lecker")));
        onView(withId(R.id.new_servings)).check(matches(withText("8 Portionen")));
        onView(withId(R.id.new_preparation_time)).check(matches(withText("10m")));
        onView(withId(R.id.new_description)).check(matches(withSubstring("#orientalisch #schnellundleicht\nDer beste hausgemachte Hummus: cremig und lecker!")));
        onView(withId(R.id.new_ingredients)).check(matches(withText("250g Kichererbsen, gekocht\n2 EL Tahin\n4 EL Olivenöl\n4 EL Wasser\n1 Zitrone\n1 Knoblauchzehe\n1/2 TL Kümmel, gemahlen\nSalz und Pfeffer nach Geschmack\nPaprikagewürz")));
        onView(withId(R.id.new_directions)).check(matches(withText("Die Kichererbsen abtropfen lassen.\nDie Zitrone auspressen und die Knoblauchzehe grob hacken.\nAlles in die Küchenmaschine geben und solange mixen, bis ein cremiger Dip entsteht.\nMit Salz und Pfeffer abschmecken und mit Paprikagewürz bestreuen.")));
    }
}
