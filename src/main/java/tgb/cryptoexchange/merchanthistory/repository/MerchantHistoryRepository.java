package tgb.cryptoexchange.merchanthistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tgb.cryptoexchange.merchanthistory.bean.MerchantHistory;

public interface MerchantHistoryRepository extends JpaRepository<MerchantHistory, Long> {
}
