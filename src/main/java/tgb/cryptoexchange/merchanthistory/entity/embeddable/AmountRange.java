package tgb.cryptoexchange.merchanthistory.entity.embeddable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class AmountRange {

    private Integer minAmount;

    private Integer maxAmount;
}
