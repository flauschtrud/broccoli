package org.flauschhaus.broccoli.ui.settings;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipes.Recipe;
import org.flauschhaus.broccoli.recipes.RecipeRepository;

import java.io.File;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class SettingsFragment extends Fragment {

    @Inject
    RecipeRepository recipeRepository;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        final Button button = root.findViewById(R.id.button_create_demo_recipes);
        button.setOnClickListener(view -> {
            recipeRepository.insert(DemoRecipes.createNusskuchen());
            recipeRepository.insert(DemoRecipes.createKartoffelBlumenkohlPuffer());
            recipeRepository.insert(DemoRecipes.createVeganesMett());
        });

        File externalFilesDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = externalFilesDir.listFiles();
        TextView textView = root.findViewById(R.id.settings_external_files);
        for (int i = 0; i < files.length; i++)
        {
            textView.append(files[i].getName() + "\n");
        }

        File cachedFilesDir = getActivity().getCacheDir();
        File[] cachedFiles = cachedFilesDir.listFiles();
        textView = root.findViewById(R.id.settings_cached_files);
        for (int i = 0; i < cachedFiles.length; i++)
        {
            textView.append(cachedFiles[i].getName() + "\n");
        }

        return root;
    }

    static class DemoRecipes {

        private DemoRecipes() {}

        static Recipe createNusskuchen() {
            Recipe recipe = new Recipe();
            recipe.setTitle("Einfacher Nusskuchen");
            recipe.setDescription("Dieser Nusskuchen kommt mit sehr wenigen Zutaten aus die man eigentlich immer im Haus hat \uD83D\uDE42 Ist also sehr einfach und dennoch mega lecker. Wer mag, kann den Kuchen hinterher ja noch mit geschmolzener Schokolade bestreichen oder mit Puderzucker bestreuen.\nQuelle: https://www.tinesveganebackstube.de/einfacher-nusskuchen/");
            recipe.setIngredients("300 g helles Mehl\n" +
                    "300 g gemahlene Nüsse nach Wahl (ich nahm Haselnüsse)\n" +
                    "250 g Zucker (wer mag weniger nehmen)\n" +
                    "300 ml Pflanzenmilch\n" +
                    "1 Packung Backpulver\n Schokolade zum Überziehen des Kuchens\n" +
                    "Optional Haselnusskrokant");
            recipe.setInstructions("Den Ofen auf 180 Grad Umluft (195 Grad Ober- und Unterhitze) vorheizen.\n" +
                    "Alle Zutaten der Reihe nach miteinander vermischen. Den Teig am besten nur sehr kurz vermischen, denn veganer Teig wird durch langes Umrühren etwas gummiartig.\n" +
                    "Den Teig in eine Kuchenform füllen. Je nachdem wie die Beschichtung von der Form ist, muss der Rand von der Form vorher eingefettet werden.\n" +
                    "Den Kuchen bei oben genannten Temperaturen für 45 Minuten backen. Stäbchenprobe nicht vergessen. Sollte der Kuchen zu dunkel werden, kann man ihn zwischendurch mit Alufolie abdecken.\n" +
                    "Den Kuchen in der Form abkühlen lassen.\n" +
                    "Nach dem Abkühlen vorsichtig aus der Form lösen und mit geschmolzener Schokolade übergießen \uD83D\uDE09");
            return recipe;
        }

        static Recipe createKartoffelBlumenkohlPuffer() {
            Recipe recipe = new Recipe();
            recipe.setTitle("Kartoffel Blumenkohl Puffer");
            recipe.setDescription("Leckere, vegane Kartoffel Blumenkohl Puffer, die garantiert jedem gelingen! Ohne Bindemittel, sojafrei, nussfrei und mit glutenfreier Option. Super für alle, die mit wenigen Zutaten etwas besonderes zaubern möchten. \nQuelle: https://www.healthyongreen.de/kartoffel-blumenkohl-puffer/");
            recipe.setIngredients("500 g festkochende Kartoffeln, geschält\n" +
                    "– 10 EL (glutenfreie) Haferflocken\n" +
                    "– 500 g Blumenkohl\n" +
                    "– 1 TL Kurkuma\n" +
                    "– 1 TL getrocknete Minze\n" +
                    "– Pfeffer und Salz nach Geschmack\n" +
                    "– Chili nach Geschmack (optional)\n" +
                    "– einen Teller voll mit (glutenfreiem) Paniermehl\n" +
                    "– Öl zum Ausbacken");
            recipe.setInstructions("Kartoffeln in Würfel schneiden und in einem Topf garen, bis sie weich sind. Wasser abgießen, die Kartoffeln in eine Schüssel geben und mit einer Gabel zerdrücken. Haferflocken dazugeben, umrühren und alles circa 10 Minuten quellen lassen. In der Zwischenzeit den Blumenkohl weich kochen (er kann ruhig noch ein wenig Biss haben).\n" +
                    "Wasser abgießen und die Blumenkohlröschen zu den Kartoffeln geben und ebenfalls mit der Gabel zerdrücken. Mit Kurkuma, Minze, Pfeffer, Salz und Chili abschmecken.\n" +
                    "\n" +
                    "Aus dem Kartoffel-Blumenkohl-Mix Puffer formen. Falls die Masse an den Händen zu sehr kleben sollte, könnt ihr sie mit ein wenig Mehl bestäuben, um sie besser formen zu können. Die Puffer in Paniermehl wenden und in einer Antihaft Pfanne etwas Öl erhitzen. Die Kartoffel Blumenkohl Puffer von beiden Seiten goldbraun braten und servieren.");
            return recipe;
        }

        static Recipe createVeganesMett() {
            Recipe recipe = new Recipe();
            recipe.setTitle("Veganes Mett");
            recipe.setDescription("Viel veganes Mett für wenig Geld! Sieht gruselig echt aus, schmeckt großartig und es lässt sich sogar ein veganer Mettigel daraus zaubern. \nQuelle: https://www.veganguerilla.de/mett-mett-mett/");
            recipe.setIngredients("100g Naturreiswaffeln\n" +
                    "2-3 kleine Zwiebeln\n" +
                    "ca. 350 ml Wasser\n" +
                    "40g Tomatenmark\n" +
                    "Salz\n" +
                    "Pfeffer\n" +
                    "Optional: weitere Gewürze, z.B. Paprikapulver, Smoked Paprika, Chilipulver, Rauchsalz, Cayenne-Pfeffer");
            recipe.setInstructions("Die Reiswaffeln klein bröseln und in eine Schale geben. Etwa 300 – 400 ml (kaltes) Wasser hinzugeben, bis das ganze eine leicht matschige Konsistenz hat. Lieber zunächst etwas weniger Wasser verwenden und gut durchkneten / stampfen. Gegebenenfalls weiteres Wasser hinzufügen und das Ganze noch einmal wiederholen.\n" +
                    "\n" +
                    "Dann die Zwiebeln klein hacken und ebenfalls zu der Reiswaffelmatsche geben. Für die Farbe etwa 40g Tomatenmark dazu, dann noch eine ganze Menge Salz und Pfeffer dazu. Optional mit weiteren Gewürzen (Vorschläge siehe Zutatenliste) abschmecken.\n" +
                    "\n" +
                    "Alles nochmal gut durchstampfen (am besten mit einem Kartoffelstampfer, alternativ mit einer Gabel oder den Händen), bis ihr eine gleichmäßig gefärbte Mischung erhaltet. Falls das „Mett“ noch zu hell ist, nochmal etwas Tomatenmark hinzugeben und erneut durchmischen.\n" +
                    "\n" +
                    "Das Rezept ergibt in etwa 570g Mett. Also ausreichend, um daraus mit etwas Deko z.B. einen veganen Mettigel, Mettflamingo, Mettvogel, Mettfuß (zu Halloween sehr hübsch) für ein Buffet zu basteln. Alternativ ist das Mett im Kühlschrank aber auch einige Tage haltbar.\n" +
                    "\n" +
                    "Bevor ihr es verspeist sollte die Mischung jedoch mindestens fünf Stunden – oder am besten über Nacht – im Kühlschrank durchziehen, das verbessert Geschmack und Konsistenz allgemein. Habt also etwas Geduld!");
            return recipe;
        }
    }
}