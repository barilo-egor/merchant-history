package tgb.cryptoexchange.merchanthistory.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.backoff.FixedBackOff;
import tgb.cryptoexchange.merchanthistory.dto.DetailsReceiveMonitorDTO;
import tgb.cryptoexchange.merchanthistory.dto.HourDetailsStatisticDTO;
import tgb.cryptoexchange.merchanthistory.dto.MerchantDetailsReceiveEvent;
import tgb.cryptoexchange.merchanthistory.error.MerchantDetailsReceiveErrorService;
import tgb.cryptoexchange.merchanthistory.kafka.HourDetailsStatisticDTOFoundProducerListener;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAsync
@EnableScheduling
public class CommonConfiguration {

    private final KafkaProperties kafkaProperties;

    private final MerchantDetailsReceiveErrorService errorService;

    public CommonConfiguration(KafkaProperties kafkaProperties, MerchantDetailsReceiveErrorService errorService) {
        this.kafkaProperties = kafkaProperties;
        this.errorService = errorService;
    }


    @Bean
    public ConsumerFactory<String, MerchantDetailsReceiveEvent> consumerFactory() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MerchantDetailsReceiveEvent.KafkaDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MerchantDetailsReceiveEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MerchantDetailsReceiveEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(defaultErrorHandler());
        return factory;
    }

    @Bean
    public DefaultErrorHandler defaultErrorHandler() {
        return new DefaultErrorHandler(
                errorService::handle,
                new FixedBackOff(60000, 1)
        );
    }

    @Bean
    public ConsumerFactory<String, DetailsReceiveMonitorDTO> monitorConsumerFactory() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, DetailsReceiveMonitorDTO.KafkaDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DetailsReceiveMonitorDTO> monitorKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DetailsReceiveMonitorDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(monitorConsumerFactory());
        factory.setCommonErrorHandler(defaultErrorHandler());
        return factory;
    }

    @Bean
    public ProducerFactory<String, HourDetailsStatisticDTO> detailsResponseProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, HourDetailsStatisticDTO.KafkaSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, HourDetailsStatisticDTO> statisticKafkaTemplate(HourDetailsStatisticDTOFoundProducerListener listener,
                                                                                       KafkaProperties kafkaProperties) {
        KafkaTemplate<String, HourDetailsStatisticDTO> kafkaTemplate = new KafkaTemplate<>(detailsResponseProducerFactory(kafkaProperties));
        kafkaTemplate.setProducerListener(listener);
        return kafkaTemplate;
    }
}
