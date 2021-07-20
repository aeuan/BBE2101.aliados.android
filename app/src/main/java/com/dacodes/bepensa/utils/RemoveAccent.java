package com.dacodes.bepensa.utils;

import java.text.Normalizer;

public class RemoveAccent {
    public static String unaccent(String src) {
        return Normalizer
                .normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}
