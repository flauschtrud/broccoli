# Broccoli

![Android CI](https://github.com/flauschtrud/broccoli/actions/workflows/build.yml/badge.svg)
![Publish to Google Play Closed Testing](https://github.com/flauschtrud/broccoli/actions/workflows/play-prerelease.yml/badge.svg)
![Provide Signed Release for F-Droid](https://github.com/flauschtrud/broccoli/actions/workflows/fdroid-release.yml/badge.svg)

Broccoli is a free recipe app that lets you build your own personal recipe collection and helps you cook in a more eco-friendly way.
Recipes with seasonal ingredients are highlighted and if you’re looking for inspiration, you can find seasonal ingredients from your region in the seasonal calendar.

You can get Broccoli on
- [Google Play](https://play.google.com/store/apps/details?id=com.flauschcode.broccoli)
- [F-Droid](https://f-droid.org/packages/com.flauschcode.broccoli/)
- [GitHub](https://github.com/flauschtrud/broccoli/releases/latest)

## FAQ

### Why Can Some Recipes Not Be Imported via Their URL Into Broccoli?
Broccoli searches for recipe metadata in the [JSON-LD](https://json-ld.org/) format. Many modern sites and blogs provide this metadata since it helps them with search engine optimization. But some might also choose to not expose it because they do not want any external sites and tools to scrape their content.

If you want to verify if a specific site exposes JSON-LD metadata hit F12 in your browser and search for script tags with the `type="application/ld+json"` property.

### Can I Import Recipes From Text Files Into Broccoli?
Broccoli can only import it's own backup files (which can be created via "Backup & Restore"). But if you are technical adept and have some scripting skills you can create a `broccoli-archive` file yourself.

The `broccoli-archive` is basically a zip file which consists of other zip files (one `broccoli` file for each recipe) and a JSON file for the categories. Each `broccoli` file consists of an optional image file and a JSON file with describes the recipe's metadata. Create an export yourself and have a look at the resulting file.

The following task have to be done:
1. Create the JSON file for each recipe (depending on the structure of your text files)
2. Zip this file (with an optional image file) into a `broccoli` file
3. Create a JSON file which describes your categories (in case you have any)
4. Zip the JSON and all the `broccoli` files into a `broccoli-archive`
5. Import the `*.broccoli-archive` into Broccoli

## Contributions

### Translations
[![Crowdin](https://badges.crowdin.net/broccoli/localized.svg)](https://crowdin.com/project/broccoli)

I set up a project on [Crowdin](https://crowdin.com/project/broccoli) to manage translations. Feel free to join and tell me if you want me to add more languages.

Available languages so far:
- English (done by non-native, feel free to report any issues)
- Spanish (done by non-native, feel free to report any issues)
- French (done by non-native, feel free to report any issues)
- German
- Polish (thanks a lot to [r-tw0](https://github.com/r-tw0) for your help!)
- Italian (thanks a lot to [bruce965](https://github.com/bruce965) for your help!)
- Chinese (thanks a lot to [leongjs98](https://github.com/leongjs98) for your help!)
- Serbian (Latin) (thanks a lot to [dimipage](https://github.com/dimipage) for your help!)
- Turkish (thanks a lot to [mikropsoft](https://github.com/mikropsoft) for your help!)
- Hebrew (thanks a lot to Eldar for your help!)

### Seasonal Calendars
If you want to contribute by researching data for additional seasonal calendars please contact me.
