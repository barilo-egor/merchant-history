package tgb.cryptoexchange.merchanthistory.dto;

import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Data
public class HourDetailsStatisticDTO {

    private Long id;

    private Instant startTime;

    private Duration avgDuration;

    private Integer count;

    private Integer successCount;

    private List<DetailsReceiveDuration> amountDurations;

    private List<HourMerchantReceiveStatistic> merchantStatistics;

    @Data
    public static class DetailsReceiveDuration {

        private Long id;

        private Duration duration;

        private AmountRange amountRange;
    }

    @Data
    public static class HourMerchantReceiveStatistic {

        private Long id;

        private String merchant;

        private Duration avgDuration;

        private List<MerchantReceiveDuration> receiveDurations;

        private Integer count;

        private Integer successCount;

        private Integer errorCount;

        @Data
        public static class MerchantReceiveDuration {

            private Long id;

            private Duration duration;

            private AmountRange amountRange;
        }
    }

    @Data
    public static class AmountRange {

        private Integer minAmount;

        private Integer maxAmount;
    }
}
