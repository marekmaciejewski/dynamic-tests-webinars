package com.altimetrik.dynamic;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(UsefulService.class)
@WebMvcTest
class SampleControllerIT {

    private static final String ROOT_URL = "http://localhost:8080/alti-bank";
    private static final String AUTH_KEY = "Authorization";
    private static final String VALID_AUTH = "Bearer MjAyMC0wMS0wMSwyMDUwLTAxLTAxLDEyMw==";         // 2020-01-01,2050-01-01,123
    @SpyBean
    private ValidationService validationService;
    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"/clients", "/accounts", "/loans", "/beneficiaries"})
    void endpoints_respondWithOk(String resourceEndpoint) throws Exception {
        mockMvc.perform(get(ROOT_URL + resourceEndpoint)
                        .header(AUTH_KEY, VALID_AUTH))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @TestFactory
    Stream<DynamicNode> endpoints_respondWithUnauthorized() {
        return Stream.of("/clients", "/accounts", "/loans", "/beneficiaries")
                .map(endpoint -> dynamicContainer("Endpoint=" + endpoint, streamTestsCases(endpoint)));
    }

    private Stream<DynamicTest> streamTestsCases(String endpoint) {
        URI invalidAuthsUri = getInvalidAuthsUri();
        return steamInvalidAuth(invalidAuthsUri).map(auth -> dynamicTest(
                generateDisplayName(auth),
                invalidAuthsUri,
                () -> endpointRespondWithUnauthorized(endpoint, auth)));
    }

    private URI getInvalidAuthsUri() {
        try {
            return ClassLoader.getSystemResource("invalid-authorizations.txt").toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<String> steamInvalidAuth(URI invalidAuthsUri) {
        try {
            return Files.lines(Path.of(invalidAuthsUri))
                    .onClose(() -> System.out.println("=====> stream closed"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String generateDisplayName(String auth) {
        return "Auth=[" + auth + ']';
    }

    private void endpointRespondWithUnauthorized(String endpoint, String auth) throws Exception {
        mockMvc.perform(get(ROOT_URL + endpoint)
                        .header(AUTH_KEY, auth))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
