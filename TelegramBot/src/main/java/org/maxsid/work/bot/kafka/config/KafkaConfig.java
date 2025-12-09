package org.maxsid.work.bot.kafka.config;

//@Configuration
public class KafkaConfig {

//    @Bean
//    NewTopic userSettingsTopic() {
//        return TopicBuilder.name("user-settings-topic")
//                .partitions(3)
//                .replicas(1)
//                .build();
//    } @Bean
//    NewTopic routeCalculateTopic() {
//        return TopicBuilder.name("route-calculate-topic")
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }

//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;

    // Producer Configuration
//    @Bean
//    public ProducerFactory<String, KafkaMessage> producerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
//        return new DefaultKafkaProducerFactory<>(configProps);
//    }
//
//    @Bean
//    public KafkaTemplate<String, KafkaMessage> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }

    // Consumer Configuration
//    @Bean
//    public ConsumerFactory<String, KafkaMessage> consumerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "telegram-bot-service");
//        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//        configProps.put(JsonDeserializer.TYPE_MAPPINGS,
//                "kafkaMessage:org.maxsid.work.dto.KafkaMessage");
//
//        return new DefaultKafkaConsumerFactory<>(
//                configProps,
//                new StringDeserializer(),
//                new JsonDeserializer<>(KafkaMessage.class)
//        );
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessage>
//    kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        return factory;
//    }
}
