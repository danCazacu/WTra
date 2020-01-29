package com.wtra.client.entity;

import java.util.Map;
import java.util.Set;

public class Sign {

    // constants
    public static final String hasBackgroundColor = "hasBackgroundColor";
    public static final String hasBorderColor = "hasBorderColor";
    public static final String hasForm = "hasForm";
    public static final String hasLegalRegulations = "hasLegalRegulations";
    public static final String applicableTo = "applicableTo";
    public static final String hasImageLink = "hasImageLink";
    public static final String country = "country";
    public static final String type = "type";
    public static final String description = "description";

    private String name;
    private String signDescription;
    private String regulations;
    private String backgroundColor;
    private String borderColor;
    private String form;
    private String imageLink;
    private String applicableFor;
    private String countries;
    private String types;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignDescription() {
        return signDescription;
    }

    public void setSignDescription(String signDescription) {
        this.signDescription = signDescription;
    }

    public String getRegulations() {
        return regulations;
    }

    public void setRegulations(String regulations) {
        this.regulations = regulations;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getApplicableFor() {
        return applicableFor;
    }

    public void setApplicableFor(String applicableFor) {
        this.applicableFor = applicableFor;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public static Sign createSign(String signName, Map<String, Set<String>> signProperties) {

        Sign sign = new Sign();
        sign.setName(signName.substring(0, 1).toUpperCase().concat(signName.substring(1).replace("_", " ")));
        sign.setBackgroundColor(getStringFromSet(signProperties, hasBackgroundColor));
        sign.setBorderColor(getStringFromSet(signProperties, hasBorderColor));
        sign.setForm(getStringFromSet(signProperties, hasForm));
        sign.setSignDescription(getStringFromSet(signProperties, description));
        sign.setRegulations(getStringFromSet(signProperties, hasLegalRegulations));
        sign.setImageLink(getStringFromSet(signProperties, hasImageLink));
        sign.setApplicableFor(getStringFromSet(signProperties, applicableTo));
        sign.setTypes(getStringFromSet(signProperties, type));
        sign.setCountries(getStringFromSet(signProperties, country));

        return sign;
    }

    private static String getStringFromSet(Map<String, Set<String>> signProperties, String property) {
        String string = "";
        if(signProperties.get(property) == null) return string;

        for (String s : signProperties.get(property)) {
            if (!property.equals(hasImageLink)) {
                s = s.substring(0, 1).toUpperCase() + s.substring(1);
                string += s.replace("_", " ") + ", ";
            } else {
                string = s + ", ";
            }
        }
        return string.length() > 1 ? string.substring(0, string.length() - 2) : string;
    }
}
