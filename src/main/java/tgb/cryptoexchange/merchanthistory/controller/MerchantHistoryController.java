package tgb.cryptoexchange.merchanthistory.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tgb.cryptoexchange.controller.ApiController;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryDTO;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryResponse;
import tgb.cryptoexchange.merchanthistory.service.MerchantHistoryService;

@RestController
public class MerchantHistoryController extends ApiController {

    private final MerchantHistoryService merchantHistoryService;

    public MerchantHistoryController(MerchantHistoryService merchantHistoryService) {
        this.merchantHistoryService = merchantHistoryService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<MerchantHistoryResponse> findByMerchantOrderId(@PathVariable String orderId) {
        return merchantHistoryService.findByMerchantOrderId(orderId)
                .map(merchantHistory -> new ResponseEntity<>(
                        new MerchantHistoryResponse(MerchantHistoryDTO.from(merchantHistory)), HttpStatus.OK)
                )
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
