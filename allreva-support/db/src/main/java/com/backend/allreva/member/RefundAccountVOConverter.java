package com.backend.allreva.member;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RefundAccountVOConverter implements AttributeConverter<RefundAccountVO, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(final RefundAccountVO attribute) {
        if (attribute == null) {
            return DELIMITER;
        }
        String bank = attribute.getBank() != null ? attribute.getBank() : "";
        String number = attribute.getNumber() != null ? attribute.getNumber() : "";
        return String.join(DELIMITER, bank, number);
    }

    @Override
    public RefundAccountVO convertToEntityAttribute(final String dbData) {
        if (dbData == null || dbData.equals(DELIMITER)) {
            return new RefundAccountVO("", "");
        }
        String[] split = dbData.split(DELIMITER, -1);
        return new RefundAccountVO(split[0], split.length > 1 ? split[1] : "");
    }
}
