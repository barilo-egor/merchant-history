package tgb.cryptoexchange.merchanthistory.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.dto.MerchantDetailsReceiveEvent;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryDTO;
import tgb.cryptoexchange.merchanthistory.dto.MerchantHistoryRequest;
import tgb.cryptoexchange.merchanthistory.entity.MerchantHistory;
import tgb.cryptoexchange.merchanthistory.repository.MerchantHistoryRepository;

import java.time.Instant;
import java.util.List;

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
        merchantHistory.setPaymentLink(event.getPaymentLink());
        merchantHistoryRepository.save(merchantHistory);
    }

    public List<MerchantHistory> findByCreatedAtBefore(Instant createdAt) {
        return merchantHistoryRepository.findByCreatedAtBefore(createdAt);
    }

    public void deleteAll(List<MerchantHistory> merchantHistory) {
        merchantHistoryRepository.deleteAll(merchantHistory);
    }

    public Page<MerchantHistoryDTO> findAll(MerchantHistoryRequest request) {
        String[] sortParams = request.getSort().split(",");
        String sortField = sortParams[0];

        Sort.Direction direction = (sortParams.length > 1 && "asc".equalsIgnoreCase(sortParams[1]))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                request.getPageNumber(),
                request.getPageSize(),
                Sort.by(direction, sortField)
        );
        return merchantHistoryRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.and(
                        request.toPredicates(root, criteriaBuilder).toArray(new Predicate[0])
                ),
                pageable
        ).map(MerchantHistoryDTO::fromEntity);
    }
}
