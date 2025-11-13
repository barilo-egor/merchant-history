package tgb.cryptoexchange.merchanthistory.bean;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
public class MerchantHistory {

    @Id
    @GeneratedValue
    private Long id;

    private Long createId;

    private Long chatId;

    private LocalDateTime dateTime;

    private String merchant;

    private String orderId;

    private Integer amount;

    private String method;

    private String details;
}
