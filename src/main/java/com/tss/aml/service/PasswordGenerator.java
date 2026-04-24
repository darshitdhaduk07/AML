package com.tss.aml.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordGenerator {

    private final String[] WORDS = {
            "Mango", "Tiger", "River", "Sky", "Ocean",
            "Forest", "Cloud", "Stone", "Flame", "Shadow"
    };

    private final SecureRandom RANDOM = new SecureRandom();

    public String generateStrong() {
        String word1 = capitalizeRandom(WORDS[RANDOM.nextInt(WORDS.length)]);
        String word2 = capitalizeRandom(WORDS[RANDOM.nextInt(WORDS.length)]);
        int number = 100 + RANDOM.nextInt(900);

        char symbol = "!@#$%".charAt(RANDOM.nextInt(5));

        return word1 + number + symbol + word2;
    }

    private String capitalizeRandom(String word) {
        return RANDOM.nextBoolean()
                ? word.toUpperCase()
                : word;
    }
}
