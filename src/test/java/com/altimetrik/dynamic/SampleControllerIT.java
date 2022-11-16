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
    private static final String MISSING_BEARER_PREFIX_AUTH = "missing 'Bearer' prefix";
    private static final String NON_BASE_64_AUTH = "Bearer non-base64";
    private static final String TOO_SHORT_TOKEN_AUTH = "Bearer MTIz";                                      // 123
    private static final String MISSING_PARTS_TOKEN_AUTH = "Bearer MjAyMC0wMS0wMSAxMjowMDowMC4wLDEyMw==";  // 2020-01-01 12:00:00.0,123
    private static final String NOT_YET_VALID_TOKEN_AUTH = "Bearer MjA0MC0wMS0wMSwyMDUwLTAxLTAxLDEyMw==";  // 2040-01-01,2050-01-01,123
    private static final String EXPIRED_TOKEN_AUTH = "Bearer MjAyMC0wMS0wMSwyMDIxLTAxLTAxLDEyMw==";        // 2020-01-01,2021-01-01,123
    private static final String WRONG_SIGNATURE_TOKEN_AUTH = "Bearer MjAyMC0wMS0wMSwyMDUwLTAxLTAxLDQ1Ng==";// 2020-01-01,2050-01-01,456
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

    @ParameterizedTest
    @ValueSource(strings = {
            MISSING_BEARER_PREFIX_AUTH,
            NON_BASE_64_AUTH,
            TOO_SHORT_TOKEN_AUTH,
            MISSING_PARTS_TOKEN_AUTH,
            NOT_YET_VALID_TOKEN_AUTH,
            EXPIRED_TOKEN_AUTH,
            WRONG_SIGNATURE_TOKEN_AUTH
    })
    void getClients_respondsWithUnauthorized(String auth) throws Exception {
        mockMvc.perform(get(ROOT_URL + "/clients")
                        .header(AUTH_KEY, auth))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            MISSING_BEARER_PREFIX_AUTH,
            NON_BASE_64_AUTH,
            TOO_SHORT_TOKEN_AUTH,
            MISSING_PARTS_TOKEN_AUTH,
            NOT_YET_VALID_TOKEN_AUTH,
            EXPIRED_TOKEN_AUTH,
            WRONG_SIGNATURE_TOKEN_AUTH
    })
    void getAccounts_respondsWithUnauthorized(String auth) throws Exception {
        mockMvc.perform(get(ROOT_URL + "/accounts")
                        .header(AUTH_KEY, auth))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            MISSING_BEARER_PREFIX_AUTH,
            NON_BASE_64_AUTH,
            TOO_SHORT_TOKEN_AUTH,
            MISSING_PARTS_TOKEN_AUTH,
            NOT_YET_VALID_TOKEN_AUTH,
            EXPIRED_TOKEN_AUTH,
            WRONG_SIGNATURE_TOKEN_AUTH
    })
    void getLoans_respondsWithUnauthorized(String auth) throws Exception {
        mockMvc.perform(get(ROOT_URL + "/loans")
                        .header(AUTH_KEY, auth))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            MISSING_BEARER_PREFIX_AUTH,
            NON_BASE_64_AUTH,
            TOO_SHORT_TOKEN_AUTH,
            MISSING_PARTS_TOKEN_AUTH,
            NOT_YET_VALID_TOKEN_AUTH,
            EXPIRED_TOKEN_AUTH,
            WRONG_SIGNATURE_TOKEN_AUTH
    })
    void getBeneficiaries_respondsWithUnauthorized(String auth) throws Exception {
        mockMvc.perform(get(ROOT_URL + "/beneficiaries")
                        .header(AUTH_KEY, auth))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
