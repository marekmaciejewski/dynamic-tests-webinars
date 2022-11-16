package com.altimetrik.dynamic;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

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
}
