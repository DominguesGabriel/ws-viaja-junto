package com.viajajunto.api.core.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    @Test
    @DisplayName("Deve instanciar CorsFilter com configurações de CORS")
    void shouldCreateCorsFilter() {
        CorsConfig corsConfig = new CorsConfig();
        CorsFilter filter = corsConfig.corsFilter();

        assertNotNull(filter);
    }
}
