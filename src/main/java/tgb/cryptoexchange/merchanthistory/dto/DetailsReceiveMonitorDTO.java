package tgb.cryptoexchange.merchanthistory.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import org.apache.kafka.common.serialization.Deserializer;
import tgb.cryptoexchange.merchanthistory.entity.DetailsReceiveMonitor;
import tgb.cryptoexchange.merchanthistory.entity.MerchantAttempt;
import tgb.cryptoexchange.merchanthistory.exception.DeserializeEventException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Data
public class DetailsReceiveMonitorDTO {

    private Long dealId;

    private Integer amount;

    private Instant startTime;

    private Instant endTime;

    private boolean success;

    private List<MerchantAttemptDTO> attempts;

    @Data
    public static class MerchantAttemptDTO {

        private String merchant;

        private String method;

        private Instant startTime;

        private Instant endTime;

        private boolean success;

        private boolean error;
    }

    public DetailsReceiveMonitor toEntity() {
        DetailsReceiveMonitor detailsReceiveMonitor = new DetailsReceiveMonitor();
        detailsReceiveMonitor.setDealId(dealId);
        detailsReceiveMonitor.setAmount(amount);
        detailsReceiveMonitor.setStartTime(startTime);
        detailsReceiveMonitor.setEndTime(endTime);
        detailsReceiveMonitor.setSuccess(success);
        if (Objects.nonNull(amount)) {
            for (MerchantAttemptDTO attemptDTO : attempts) {
                MerchantAttempt merchantAttempt = new MerchantAttempt();
                merchantAttempt.setMerchant(attemptDTO.getMerchant());
                merchantAttempt.setMethod(attemptDTO.getMethod());
                merchantAttempt.setStartTime(attemptDTO.getStartTime());
                merchantAttempt.setEndTime(attemptDTO.getEndTime());
                merchantAttempt.setSuccess(attemptDTO.isSuccess());
                merchantAttempt.setError(attemptDTO.isError());
                detailsReceiveMonitor.addAttempt(merchantAttempt);
            }
        }
        return detailsReceiveMonitor;
    }

    public static class KafkaDeserializer implements Deserializer<DetailsReceiveMonitorDTO> {

        private final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        @Override
        public DetailsReceiveMonitorDTO deserialize(String topic, byte[] data) {
            try {
                if (data == null) return null;
                return objectMapper.readValue(data, DetailsReceiveMonitorDTO.class);
            } catch (Exception e) {
                throw new DeserializeEventException("Error occurred while deserializer value: " + new String(data, StandardCharsets.UTF_8), e);
            }
        }
    }
}
