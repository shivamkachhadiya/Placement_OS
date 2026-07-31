package placement_OS.demo.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ProfanityFilterService {

    // Common English, Hindi, and Hinglish abusive words list
    private static final Set<String> BANNED_WORDS = new HashSet<>(Arrays.asList(
            // English Abuses
            "fuck", "fucking", "fucker", "shit", "bitch", "bastard", "asshole",
            "dick", "pussy", "cunt", "whore", "slut", "motherfucker", "bullshit",

            // Hinglish / Hindi Transliterated Abuses
            "bc", "mc", "mcbc", "bkl", "bsdk", "gandu", "gand", "chutiya", "chutiye",
            "chut", "bhosdike", "bhosdika", "bhosadi", "madarchod", "behenchod",
            "behencode", "bhenchod", "gaand", "harami", "hramkhor", "kamina", "kamine",
            "lauda", "lode", "loda", "lund", "tatte", "saala", "saale", "raand",

            // Hindi Devanagari Abuses
            "चूतिया", "गांड", "भोसडीके", "मादरचोद", "बहनचोद", "हरामी", "लौड़ा", "लंड"
    ));

    /**
     * Checks if the given text contains any restricted/profane words.
     */
    public boolean containsProfanity(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        // Clean text: lowercase and replace punctuation with spaces
        String cleanedText = text.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", " ");
        String[] words = cleanedText.split("\\s+");

        for (String word : words) {
            if (BANNED_WORDS.contains(word)) {
                return true;
            }
        }

        // Substring / Pattern check for concatenated abuse words (e.g. "bhosdike123")
        for (String banned : BANNED_WORDS) {
            if (banned.length() > 2 && Pattern.compile("\\b" + Pattern.quote(banned), Pattern.CASE_INSENSITIVE).matcher(text).find()) {
                return true;
            }
        }

        return false;
    }
}