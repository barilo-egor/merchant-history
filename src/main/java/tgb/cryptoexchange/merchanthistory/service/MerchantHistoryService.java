package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.bean.MerchantHistory;
import tgb.cryptoexchange.merchanthistory.dto.MerchantDetailsReceiveEvent;
import tgb.cryptoexchange.merchanthistory.repository.MerchantHistoryRepository;

@Service
public class MerchantHistoryService {

    private final MerchantHistoryRepository merchantHistoryRepository;

    public MerchantHistoryService(MerchantHistoryRepository merchantHistoryRepository) {
        this.merchantHistoryRepository = merchantHistoryRepository;
    }

    public void save(MerchantDetailsReceiveEvent event) {
        MerchantHistory merchantHistory = new MerchantHistory();
        merchantHistory.setDealId(event.getDealId());
        merchantHistory.setUserId(event.getUserId());
        merchantHistory.setInitiatorApp(event.getInitiatorApp());
        merchantHistory.setCreatedAt(event.getCreatedAt());
        merchantHistory.setMerchant(event.getMerchant());
        merchantHistory.setMerchantOrderId(event.getMerchantOrderId());
        merchantHistory.setRequestedAmount(event.getRequestedAmount());
        merchantHistory.setMerchantAmount(event.getMerchantAmount());
        merchantHistory.setMethod(event.getMethod());
        merchantHistory.setDetails(event.getDetails());
        merchantHistoryRepository.save(merchantHistory);
    }
}
