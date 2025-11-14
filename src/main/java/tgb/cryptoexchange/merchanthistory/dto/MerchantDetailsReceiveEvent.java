package tgb.cryptoexchange.merchanthistory.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import org.apache.kafka.common.serialization.Deserializer;
import tgb.cryptoexchange.merchanthistory.exception.DeserializeEventException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Data
public class MerchantDetailsReceiveEvent {


    /**
     * Идентификатор сделки, по которому были запрошены реквизиты
     */
    private Long dealId;

    /**
     * Идентификатор пользователя, для которого были запрошены реквизиты
     */
    private Long userId;

    /**
     * Идентификатор приложения, запросившее реквизиты
     */
    private String initiatorApp;

    /**
     * Дата и время получения реквизитов
     */
    private Instant createdAt;

    /**
     * Идентификатор мерчанта выдавшего реквизиты
     */
    private String merchant;

    /**
     * Идентификатор ордера в системе мерчанта
     */
    private String merchantOrderId;

    /**
     * Сумма, на которую были запрошены реквизиты
     */
    private Integer requestedAmount;

    /**
     * Обновленная мерчантом сумма
     */
    private Integer merchantAmount;

    /**
     * Тип реквизитов
     */
    private String method;

    /**
     * Реквизиты
     */
    private String details;

    public static class KafkaDeserializer implements Deserializer<MerchantDetailsReceiveEvent> {

        private final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        @Override
        public MerchantDetailsReceiveEvent deserialize(String topic, byte[] data) {
            try {
                if (data == null) return null;
                return objectMapper.readValue(data, MerchantDetailsReceiveEvent.class);
            } catch (Exception e) {
                throw new DeserializeEventException("Error occurred while deserializer value: " + new String(data, StandardCharsets.UTF_8), e);
            }
        }
    }
}
