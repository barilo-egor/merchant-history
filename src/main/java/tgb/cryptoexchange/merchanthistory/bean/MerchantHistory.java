package tgb.cryptoexchange.merchanthistory.bean;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Data
public class MerchantHistory {

    /**
     * Уникальный идентификатор истории в сервисе merchant-history
     */
    @Id
    @GeneratedValue
    private Long id;

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
}
