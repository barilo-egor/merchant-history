package tgb.cryptoexchange.merchanthistory.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tgb.cryptoexchange.merchanthistory.bean.MerchantHistory;
import tgb.cryptoexchange.merchanthistory.service.MerchantHistoryService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MerchantHistoryController.class)
@ExtendWith(MockitoExtension.class)
class MerchantHistoryControllerTest {

    @MockitoBean
    private MerchantHistoryService merchantHistoryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findByMerchantOrderIdShouldReturn404IfHistoryNotFound() throws Exception {
        when(merchantHistoryService.findByMerchantOrderId(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get("/some-order-id"))
                .andExpect(status().isNotFound());
    }

    @CsvSource("""
            06edd714-8875-4583-b611-9da3236b5ed0
            981c7fae-a571-4c3a-b53e-8b7512b5a6dd
            """)
    @ParameterizedTest
    void findByMerchantOrderIdShouldReturnMerchantHistory(String orderId) throws Exception {
        MerchantHistory merchantHistory = new MerchantHistory();
        merchantHistory.setMerchantOrderId(orderId);
        when(merchantHistoryService.findByMerchantOrderId(orderId)).thenReturn(Optional.of(merchantHistory));
        mockMvc.perform(get("/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.merchantOrderId").value(orderId));
    }

}