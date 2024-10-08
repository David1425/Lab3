package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private static final String ALPHA = "alpha3";

    private Map<String, List<String>> languageCodes;
    private List<String> countries;
    private Map<String, Map<String, String>> countryTranslations;

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {
            languageCodes = new HashMap<>();
            countries = new ArrayList<>();
            countryTranslations = new HashMap<>();

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String country = jsonObject.getString(ALPHA);
                List<String> codes = new ArrayList<>();
                Iterator<String> keys = jsonObject.keys();
                Map<String, String> translations = new HashMap<>();

                while (keys.hasNext()) {
                    String key = keys.next();
                    if ("id".equals(key) || "alpha2".equals(key) || ALPHA.equals(key)) {
                        continue;
                    }
                    codes.add(key);
                    translations.put(key, jsonObject.getString(key));
                }

                languageCodes.put(country, codes);
                countries.add(country);
                countryTranslations.put(country, translations);
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        return languageCodes.get(country.toLowerCase());
    }

    @Override
    public List<String> getCountries() {
        return countries;
    }

    @Override
    public String translate(String country, String language) {
        return countryTranslations.get(country.toLowerCase()).get(language.toLowerCase());
    }
}
