package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.bean.MerchantHistory;
import tgb.cryptoexchange.merchanthistory.dto.MerchantDetailsReceiveEvent;
import tgb.cryptoexchange.merchanthistory.repository.MerchantHistoryRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    public Optional<MerchantHistory> findByMerchantOrderId(String merchantOrderId) {
        return merchantHistoryRepository.findByMerchantOrderId(merchantOrderId);
    }

    public List<MerchantHistory> findByCreatedAtBefore(Instant createdAt) {
        return merchantHistoryRepository.findByCreatedAtBefore(createdAt);
    }

    public void delete(MerchantHistory merchantHistory) {
        merchantHistoryRepository.delete(merchantHistory);
    }
}
