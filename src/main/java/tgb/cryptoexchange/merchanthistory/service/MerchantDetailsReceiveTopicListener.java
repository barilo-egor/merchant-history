package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tgb.cryptoexchange.merchanthistory.dto.MerchantDetailsReceiveEvent;

@Component
@Profile("!disable-kafka")
public class MerchantDetailsReceiveTopicListener {

    private final MerchantHistoryService merchantHistoryService;

    public MerchantDetailsReceiveTopicListener(MerchantHistoryService merchantHistoryService) {
        this.merchantHistoryService = merchantHistoryService;
    }

    @KafkaListener(topics = "${kafka.topic.merchant-details.receive}", groupId = "${kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void receive(MerchantDetailsReceiveEvent event) {
        merchantHistoryService.save(event);
    }
}
