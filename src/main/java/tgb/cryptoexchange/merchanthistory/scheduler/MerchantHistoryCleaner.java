package tgb.cryptoexchange.merchanthistory.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.cryptoexchange.merchanthistory.bean.MerchantHistory;
import tgb.cryptoexchange.merchanthistory.service.MerchantHistoryService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
public class MerchantHistoryCleaner {

    private final MerchantHistoryService merchantHistoryService;

    public MerchantHistoryCleaner(MerchantHistoryService merchantHistoryService) {
        this.merchantHistoryService = merchantHistoryService;
    }

    @Scheduled(cron = "0 0 03 * * *")
    @Transactional
    @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 60000))
    public void cleanMerchantHistory() {
        List<MerchantHistory> merchantHistories = merchantHistoryService.findByCreatedAtBefore(
                LocalDateTime.now().minusWeeks(2).atZone(ZoneId.systemDefault()).toInstant()
        );
        if (merchantHistories.isEmpty()) {
            return;
        }
        log.debug("Было найдено {} историй мерчантов для автоматического удаления.", merchantHistories.size());
        merchantHistoryService.deleteAll(merchantHistories);
    }
}
