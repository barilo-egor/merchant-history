package tgb.cryptoexchange.merchanthistory.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.Data;
import tgb.cryptoexchange.merchanthistory.bean.MerchantHistory;

import java.time.Instant;

@Data
public class MerchantHistoryDTO {

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
    @JsonSerialize(using = InstantSerializer.class)
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

    public static MerchantHistoryDTO fromEntity(MerchantHistory merchantHistory) {
        MerchantHistoryDTO dto = new MerchantHistoryDTO();
        dto.setDealId(merchantHistory.getDealId());
        dto.setUserId(merchantHistory.getUserId());
        dto.setInitiatorApp(merchantHistory.getInitiatorApp());
        dto.setCreatedAt(merchantHistory.getCreatedAt());
        dto.setMerchant(merchantHistory.getMerchant());
        dto.setMerchantOrderId(merchantHistory.getMerchantOrderId());
        dto.setRequestedAmount(merchantHistory.getRequestedAmount());
        dto.setMerchantAmount(merchantHistory.getMerchantAmount());
        dto.setMethod(merchantHistory.getMethod());
        dto.setDetails(merchantHistory.getDetails());
        return dto;
    }
}
