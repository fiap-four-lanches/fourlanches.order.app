//package com.fiap.fourlanches.order.application.configuration;
//
//import com.fiap.fourlanches.order.adapter.driver.api.consumers.OrderStatusConsumer;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.annotation.EnableRabbit;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableRabbit
//public class RabbitMQConfig {
//
//    @Value("${queue.order.status.name}")
//    String QUEUE_ORDER_STATUS_NAME;
//
//    @Bean
//    public Queue orderStatusQueue() {
//        return new Queue(QUEUE_ORDER_STATUS_NAME);
//    }
//
//    @Bean
//    public FanoutExchange orderStatusExchange() {
//        return new FanoutExchange("orderstatus");
//    }
//
//    @Bean
//    public Binding orderStatusBinding() {
//        return BindingBuilder.bind(orderStatusQueue()).to(orderStatusExchange());
//    }
//
//    @Bean
//    SimpleMessageListenerContainer orderStatusConsumerContainer(ConnectionFactory connectionFactory,
//                                                                @Qualifier("orderStatusConsumerAdapter") MessageListenerAdapter listenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueues(orderStatusQueue());
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }
//
//    @Bean
//    MessageListenerAdapter orderStatusConsumerAdapter(OrderStatusConsumer receiver) {
//        return new MessageListenerAdapter(receiver, "receiveMessage");
//    }
//}
