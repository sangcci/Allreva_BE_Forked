package com.backend.allreva.common.converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJsonListConverter<T> implements AttributeConverter<List<T>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);

    private final Class<T> elementType;

    protected AbstractJsonListConverter(final Class<T> elementType) {
        this.elementType = elementType;
    }

    @Override
    public String convertToDatabaseColumn(final List<T> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("List를 JSON으로 변환하는 데 실패했습니다.", e);
        }
    }

    @Override
    public List<T> convertToEntityAttribute(final String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(
                    json, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON을 List로 변환하는 데 실패했습니다.", e);
        }
    }
}
