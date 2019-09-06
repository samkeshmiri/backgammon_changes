package com.skeshmiri.backgammon.utils;

import java.net.URL;

public class ResourceLoader {

    public static URL getPathOf(String path) {
        return ResourceLoader.class.getClassLoader().getResource(path);
    }

}