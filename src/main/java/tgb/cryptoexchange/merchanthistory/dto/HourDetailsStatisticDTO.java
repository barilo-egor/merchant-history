package tgb.cryptoexchange.merchanthistory.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import tgb.cryptoexchange.merchanthistory.exception.SerializeEventException;

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

    @Slf4j
    public static class KafkaSerializer implements Serializer<HourDetailsStatisticDTO> {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public byte[] serialize(String topic, HourDetailsStatisticDTO detailsResponse) {
            try {
                if (detailsResponse == null) {
                    return new byte[0];
                }
                return objectMapper.writeValueAsBytes(detailsResponse);
            } catch (JsonProcessingException e) {
                log.error("Ошибка сериализации объекта для отправки в топик {}: {}", topic, detailsResponse);
                throw new SerializeEventException("Error occurred while mapping HourDetailsStatisticDTO", e);
            }
        }
    }
}
