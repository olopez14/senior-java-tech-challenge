package org.demo.seniorjavatechchallenge.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoconfigureM
@DisplayName("Integración mínima API productos y precios")
class ProductPriceIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Crear producto, añadir precio, consultar precio vigente e historial")
    void minimalIntegrationFlow() throws Exception {
        // 1. Crear producto
        String productJson = "{" +
                "\"name\":\"Zapatillas deportivas\"," +
                "\"description\":\"Modelo 2025 edición limitada\"}";
        ResultActions createProduct = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
        String productId = createProduct.andReturn().getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // 2. Añadir precio
        String priceJson = "{" +
                "\"value\":99.99," +
                "\"initDate\":\"2024-01-01\"," +
                "\"endDate\":\"2024-06-30\"}";
        mockMvc.perform(post("/products/" + productId + "/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(priceJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value(99.99));

        // 3. Consultar precio vigente
        mockMvc.perform(get("/products/" + productId + "/prices?date=2024-04-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(99.99));

        // 4. Consultar historial
        mockMvc.perform(get("/products/" + productId + "/prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prices[0].value").value(99.99));
    }
}

