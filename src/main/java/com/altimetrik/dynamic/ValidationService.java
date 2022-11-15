package com.altimetrik.dynamic;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
class ValidationService {

    void validate(String authorization) {
        if (!isValid(authorization)) {
            throw new SecurityException();
        }
    }

    private boolean isValid(String authorization) {
        return Optional.of(authorization)
                .filter(ValidationService::isPrefixValid)
                .map(ValidationService::stripPrefix)
                .filter(Base64::isBase64)
                .map(Base64::decodeBase64)
                .filter(ValidationService::isLengthCorrect)
                .map(String::new)
                .map(ValidationService::splitToParts)
                .filter(ValidationService::isLengthCorrect)
                .filter(ValidationService::isAlreadyValid)
                .filter(ValidationService::hasNotExpired)
                .filter(ValidationService::isSigned)
                .isPresent();
    }

    private static boolean isPrefixValid(String auth) {
        return auth.startsWith("Bearer ");
    }

    private static String stripPrefix(String auth) {
        return auth.substring(7);
    }

    private static boolean isLengthCorrect(byte[] authBytes) {
        return authBytes.length == 25;
    }

    private static String[] splitToParts(String token) {
        return token.split(",");
    }

    private static boolean isLengthCorrect(String[] tokenParts) {
        return tokenParts.length == 3;
    }

    private static boolean isAlreadyValid(String[] tokenParts) {
        return !LocalDate.now().isBefore(LocalDate.parse(tokenParts[0]));
    }

    private static boolean hasNotExpired(String[] tokenParts) {
        return !LocalDate.now().isAfter(LocalDate.parse(tokenParts[1]));
    }

    private static boolean isSigned(String[] tokenParts) {
        return "123".equals(tokenParts[2]);
    }
}
