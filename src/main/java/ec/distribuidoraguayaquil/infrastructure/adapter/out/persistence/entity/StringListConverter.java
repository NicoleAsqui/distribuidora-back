package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SEP = "||";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return String.join(SEP, attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(dbData.split("\\|\\|", -1))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
