//package com.investmentapp.investment_app.config;
//
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//
//@Converter(autoApply = true)
//public class LocalDateTimeAttributeConverter
//        implements AttributeConverter<LocalDateTime, Timestamp> {
//
//    @Override
//    public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
//        if (attribute == null) {
//            return null;
//        }
//        // strip off nano‐precision beyond milliseconds
//        int mos = attribute.getNano() / 1_000_000;
//        LocalDateTime truncated = attribute.withNano(mos * 1_000_000);
//        return Timestamp.valueOf(truncated);
//    }
//
//    @Override
//    public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
//        return dbData == null ? null : dbData.toLocalDateTime();
//    }
//}
