package tgb.cryptoexchange.merchanthistory.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Service;
import tgb.cryptoexchange.merchanthistory.dto.HourDetailsStatisticDTO;

@Service
@Slf4j
public class HourDetailsStatisticDTOFoundProducerListener implements ProducerListener<String, HourDetailsStatisticDTO> {

    @Override
    public void onSuccess(ProducerRecord<String, HourDetailsStatisticDTO> producerRecord, RecordMetadata recordMetadata) {
        log.debug("Успешно отправлен ивент. Key={}, event={}.", producerRecord.key(), producerRecord.value());
    }

    @Override
    public void onError(ProducerRecord<String, HourDetailsStatisticDTO> producerRecord, RecordMetadata recordMetadata, Exception exception) {
        log.error("Ошибка при попытке отправить ивент в топик. Key={}, event={}: {}.",
                producerRecord.key(), producerRecord.value(), exception.getMessage(), exception);
    }
}
