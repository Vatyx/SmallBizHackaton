package com.trinew.easytime.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Employer")
public class ParseEmployer extends ParseObject {
    // key identifiers
    public static final String EMPLOYER_KEY_NAME = "name";

    public String getName() {
        return getString(EMPLOYER_KEY_NAME);
    }
    public void setFlag(String name) {
        put(EMPLOYER_KEY_NAME, name);
    }
}