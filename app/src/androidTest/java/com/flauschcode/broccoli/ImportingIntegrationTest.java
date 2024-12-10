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
