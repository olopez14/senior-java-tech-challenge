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
@DisplayName("Integración API productos y precios - errores y validaciones")
class ProductPriceErrorIntegrationTest {

    @Autowired
    WebApplicationContext wac;

    @Test
    @DisplayName("Crear producto con nombre nulo debe fallar")
    void createProduct_nullName_returnsBadRequest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        String productJson = "{" +
                "\"name\":null," +
                "\"description\":\"desc\"}";
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Crear precio con valor nulo debe fallar")
    void createPrice_nullValue_returnsBadRequest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        // Crear producto válido primero
        String productJson = "{" +
                "\"name\":\"P\"," +
                "\"description\":\"D\"}";
        ResultActions createProduct = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
        String productId = createProduct.andReturn().getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");
        // Intentar crear precio inválido
        String priceJson = "{" +
                "\"value\":null," +
                "\"initDate\":\"2024-01-01\"}";
        mockMvc.perform(post("/products/" + productId + "/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(priceJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Consultar producto inexistente devuelve 404")
    void getProduct_notFound_returns404() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(get("/products/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Consultar historial de precios de producto inexistente devuelve 404")
    void getProductPriceHistory_notFound_returns404() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mockMvc.perform(get("/products/999999/prices"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Crear precio con fechas inválidas debe fallar")
    void createPrice_invalidDates_returnsBadRequest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        // Crear producto válido primero
        String productJson = "{" +
                "\"name\":\"P\"," +
                "\"description\":\"D\"}";
        ResultActions createProduct = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
        String productId = createProduct.andReturn().getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");
        // Intentar crear precio con fechas inválidas
        String priceJson = "{" +
                "\"value\":99.99," +
                "\"initDate\":\"2024-06-30\"," +
                "\"endDate\":\"2024-01-01\"}";
        mockMvc.perform(post("/products/" + productId + "/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(priceJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Crear precio solapado debe fallar")
    void createPrice_overlapping_returnsConflict() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        // Crear producto válido primero
        String productJson = "{" +
                "\"name\":\"P\"," +
                "\"description\":\"D\"}";
        ResultActions createProduct = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
        String productId = createProduct.andReturn().getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");
        // Crear primer precio
        String priceJson1 = "{" +
                "\"value\":99.99," +
                "\"initDate\":\"2024-01-01\"," +
                "\"endDate\":\"2024-06-30\"}";
        mockMvc.perform(post("/products/" + productId + "/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(priceJson1))
                .andExpect(status().isCreated());
        // Crear segundo precio solapado
        String priceJson2 = "{" +
                "\"value\":88.88," +
                "\"initDate\":\"2024-05-01\"," +
                "\"endDate\":\"2024-12-31\"}";
        mockMvc.perform(post("/products/" + productId + "/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(priceJson2))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Consultar historial de precios devuelve todos los precios en orden")
    void getProductPriceHistory_returnsAllPricesOrdered() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        // Crear producto válido primero
        String productJson = "{" +
                "\"name\":\"P\"," +
                "\"description\":\"D\"}";
        ResultActions createProduct = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
        String productId = createProduct.andReturn().getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");
        // Crear dos precios
        String priceJson1 = "{" +
                "\"value\":99.99," +
                "\"initDate\":\"2024-01-01\"," +
                "\"endDate\":\"2024-06-30\"}";
        String priceJson2 = "{" +
                "\"value\":88.88," +
                "\"initDate\":\"2024-07-01\"," +
                "\"endDate\":null}";
        mockMvc.perform(post("/products/" + productId + "/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(priceJson1))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/products/" + productId + "/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(priceJson2))
                .andExpect(status().isCreated());
        // Consultar historial
        mockMvc.perform(get("/products/" + productId + "/prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prices[0].value").value(99.99))
                .andExpect(jsonPath("$.prices[1].value").value(88.88));
    }
}
