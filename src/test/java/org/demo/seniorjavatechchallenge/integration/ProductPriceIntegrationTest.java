package org.demo.seniorjavatechchallenge.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("IntegraciÃ³n  API productos y precios (MockMvc via WebApplicationContext)")
class ProductPriceIntegrationTest {

    @Autowired
    WebApplicationContext wac;

    @Test
    @DisplayName("Crear producto, aÃ±adir precio, consultar precio vigente e historial")
    void minimalIntegrationFlow() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        
        String productJson = "{" +
                "\"name\":\"Zapatillas deportivas\"," +
                "\"description\":\"Modelo 2025 ediciÃ³n limitada\"}";
        ResultActions createProduct = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
        String productId = createProduct.andReturn().getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        
        String priceJson = "{" +
                "\"value\":99.99," +
                "\"initDate\":\"2024-01-01\"," +
                "\"endDate\":\"2024-06-30\"}";
        mockMvc.perform(post("/products/" + productId + "/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(priceJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value(99.99));

        
        mockMvc.perform(get("/products/" + productId + "/prices?date=2024-04-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(99.99));

        
        mockMvc.perform(get("/products/" + productId + "/prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prices[0].value").value(99.99));
    }
}


