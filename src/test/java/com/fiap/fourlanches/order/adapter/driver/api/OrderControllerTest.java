package com.fiap.fourlanches.order.adapter.driver.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fiap.fourlanches.order.adapter.driver.api.controllers.OrderController;
import com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor.ApiErrorMessage;
import com.fiap.fourlanches.order.adapter.driver.api.controllersAdvisor.OrderControllerAdvisor;
import com.fiap.fourlanches.order.application.dto.OrderDTO;
import com.fiap.fourlanches.order.domain.entities.Order;
import com.fiap.fourlanches.order.domain.exception.InvalidOrderException;
import com.fiap.fourlanches.order.domain.exception.OrderNotFoundException;
import com.fiap.fourlanches.order.domain.usecases.OrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.fiap.fourlanches.order.application.constants.HeaderConstant.X_REQUEST_ID;
import static com.fiap.fourlanches.order.domain.valueobjects.OrderStatus.CREATED;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ContextConfiguration(classes = TestConfiguration.class)
@ExtendWith(SpringExtension.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    private OrderUseCase orderUseCase;

    @Mock
    private AmqpTemplate queueSender;

    @InjectMocks
    private OrderController orderController;

    @Autowired
    private JacksonTester<Order> jsonOrder;

    @Autowired
    private JacksonTester<List<Order>> jsonOrders;

    @Autowired
    private JacksonTester<ApiErrorMessage> jsonApiErrorMessage;

    @BeforeEach
    void setup() {
        this.mvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setControllerAdvice(new OrderControllerAdvisor())
                .build();
    }

    @Test
    void givenId_whenOrdersAreFound_ThenReturnOrders() throws Exception {
        var expectedOrders = singletonList(Order.builder().id(1234L).status(CREATED).build());

        when(orderUseCase.getAllPendingOrdersOrderedByStatusAndCreatedAt()).thenReturn(expectedOrders);

        MockHttpServletResponse response = mvc.perform(get("/orders").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Order> orderList = getOrdersFromResponse(response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(orderList).isEqualTo(expectedOrders);
    }

    @Test
    void givenOrderToBeSaved_whenSaveIsSuccessful_ThenReturnId() throws Exception {
        OrderDTO orderToBeSaved = OrderDTO.builder().status(CREATED).build();
        ;

        var wantedOrder = orderToBeSaved.toNewOrder();
        wantedOrder.setId(1234L);
        when(orderUseCase.createOrder(eq(orderToBeSaved))).thenReturn(wantedOrder);
        doNothing().when(queueSender).convertAndSend(anyString(), Optional.ofNullable(any()));

        var xRequestId = "request-id-123";
        var reqHeaders = new HttpHeaders();
        reqHeaders.add(X_REQUEST_ID, xRequestId);
        var response = mvc.perform(post("/orders")
                        .headers(reqHeaders)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(orderToBeSaved)))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).contains(getMapper().writeValueAsString("created"));
        assertThat(response.getContentAsString()).contains(getMapper().writeValueAsString(1234L));
    }

    @Test
    void givenId_whenOrderInPreparationIsSuccessful_ThenReturnNoContent() throws Exception {
        var response = mvc.perform(patch("/orders/1234/in_preparation").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        verify(orderUseCase).orderInPreparation(1234L);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenId_whenOrderInPreparationNotFound_ThenError() throws Exception {
        var expectedErrorMessage = new ApiErrorMessage(HttpStatus.NOT_FOUND, "Order not found");
        doThrow(new OrderNotFoundException()).when(orderUseCase).orderInPreparation(1234L);

        var response = mvc.perform(patch("/orders/1234/in_preparation").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonApiErrorMessage.write(expectedErrorMessage).getJson());
    }

    @Test
    void givenId_whenOrderNotReadyFound_ThenError() throws Exception {
        var expectedErrorMessage = new ApiErrorMessage(HttpStatus.NOT_FOUND, "Order not found");
        doThrow(new OrderNotFoundException()).when(orderUseCase).orderReady(1234L);

        var response = mvc.perform(patch("/orders/1234/ready").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonApiErrorMessage.write(expectedErrorMessage).getJson());
    }

    @Test
    void givenId_whenOrderReadyIsSuccessful_ThenReturnNoContent() throws Exception {
        var response = mvc.perform(patch("/orders/1234/ready").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        verify(orderUseCase).orderReady(1234L);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }


    @Test
    void givenId_whenOrderFinishedNotFound_ThenError() throws Exception {
        var expectedErrorMessage = new ApiErrorMessage(HttpStatus.NOT_FOUND, "Order not found");
        doThrow(new OrderNotFoundException()).when(orderUseCase).orderFinished(1234L);

        var response = mvc.perform(patch("/orders/1234/finished").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonApiErrorMessage.write(expectedErrorMessage).getJson());
    }

    @Test
    void givenId_whenOrderFinishedIsSuccessful_ThenReturnNoContent() throws Exception {
        var response = mvc.perform(patch("/orders/1234/finished").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        verify(orderUseCase).orderFinished(1234L);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenId_whenOrderCancelNotFound_ThenError() throws Exception {
        var expectedErrorMessage = new ApiErrorMessage(HttpStatus.NOT_FOUND, "Order not found");
        doThrow(new OrderNotFoundException()).when(orderUseCase).orderCanceled(1234L);

        var xRequestId = "request-id-123";
        var reqHeaders = new HttpHeaders();
        reqHeaders.add(X_REQUEST_ID, xRequestId);
        var response = mvc.perform(patch("/orders/1234/cancel")
                        .headers(reqHeaders)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonApiErrorMessage.write(expectedErrorMessage).getJson());
    }

    @Test
    void givenId_whenOrderCancelIsSuccessful_ThenReturnNoContent() throws Exception {
        Long orderId = 1234L;
        var toBeCanceledOrder = Order.builder()
                .id(orderId)
                .build();
        when(orderUseCase.orderCanceled(eq(orderId))).thenReturn(toBeCanceledOrder);
        doNothing().when(queueSender).convertAndSend(anyString(), Optional.ofNullable(any()));

        var xRequestId = "request-id-123";
        var reqHeaders = new HttpHeaders();
        reqHeaders.add(X_REQUEST_ID, xRequestId);
        var response = mvc.perform(patch("/orders/1234/cancel")
                        .headers(reqHeaders)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenOrderToBeSaved_whenSaveFails_ThenError() throws Exception {
        var orderToBeSaved = OrderDTO.builder().status(CREATED).build();
        Map<String, Object> headers = new HashMap<>();
        var xRequestId = "request-id-123";
        headers.put(X_REQUEST_ID, xRequestId);

        var expectedErrorMessage = new ApiErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, "Order could not be processed");

        when(orderUseCase.createOrder(eq(orderToBeSaved))).thenThrow(InvalidOrderException.class);

        var reqHeaders = new HttpHeaders();
        reqHeaders.add(X_REQUEST_ID, xRequestId);
        var response = mvc.perform(post("/orders")
                        .headers(reqHeaders)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(orderToBeSaved)))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonApiErrorMessage.write(expectedErrorMessage).getJson());
    }

    @Test
    void givenStatus_whenOrdersAreFound_ThenReturnOrders() throws Exception {
        var expectedOrders = singletonList(Order.builder().id(1234L).status(CREATED).build());

        when(orderUseCase.getOrdersByStatus(eq(CREATED))).thenReturn(expectedOrders);

        MockHttpServletResponse response = mvc.perform(get("/orders/status/" + CREATED)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        List<Order> orderList = getOrdersFromResponse(response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(orderList).isEqualTo(expectedOrders);
    }

    private static List<Order> getOrdersFromResponse(MockHttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        return mapper.readValue(response.getContentAsString(), typeFactory.constructCollectionType(List.class, Order.class));
    }

    private static ObjectWriter getMapper() {
        return new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

}
