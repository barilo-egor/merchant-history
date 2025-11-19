package tgb.cryptoexchange.merchanthistory.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import tgb.cryptoexchange.controller.ApiController;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoriesResponse;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryDTO;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryRequest;
import tgb.cryptoexchange.merchanthistory.service.MerchantHistoryService;

@RestController
public class MerchantHistoryController extends ApiController {

    private final MerchantHistoryService merchantHistoryService;

    public MerchantHistoryController(MerchantHistoryService merchantHistoryService) {
        this.merchantHistoryService = merchantHistoryService;
    }

    @GetMapping
    public ResponseEntity<MerchantHistoriesResponse> get(@Valid @ModelAttribute MerchantHistoryRequest request) {
        Page<MerchantHistoryDTO> page = merchantHistoryService.findAll(
                PageRequest.of(request.getPageNumber(), request.getPageSize()), request
        );
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(page.getTotalElements()))
                .body(new MerchantHistoriesResponse(page.getContent()));
    }
}
