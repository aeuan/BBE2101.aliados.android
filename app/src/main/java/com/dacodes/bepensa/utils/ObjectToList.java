package com.dacodes.bepensa.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ObjectToList {
    public static List<?> ObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray() ) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        }
        return list;
    }
}
