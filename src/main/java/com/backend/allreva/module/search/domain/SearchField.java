package com.backend.allreva.module.search.domain;

public enum SearchField {
    ADDRESS("concert_hall_address.mixed"),
    TITLE("title.mixed");

    private final String fieldName;

    SearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

}
