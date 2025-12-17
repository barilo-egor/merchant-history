package tgb.cryptoexchange.merchanthistory.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.dto.DetailsReceiveMonitorDTO;

@Service
public class DetailsReceiveMonitorTopicListener {

    private final DetailsReceiveMonitorService detailsReceiveMonitorService;

    public DetailsReceiveMonitorTopicListener(DetailsReceiveMonitorService detailsReceiveMonitorService) {
        this.detailsReceiveMonitorService = detailsReceiveMonitorService;
    }

    @KafkaListener(topics = "${kafka.topic.merchant-details.monitor}", groupId = "${kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void receive(DetailsReceiveMonitorDTO dto) {
        detailsReceiveMonitorService.save(dto);
    }
}
