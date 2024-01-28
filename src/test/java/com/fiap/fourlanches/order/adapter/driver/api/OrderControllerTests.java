package com.fiap.fourlanches.order.adapter.driver.api;

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
import com.fiap.fourlanches.order.domain.valueobjects.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class OrderControllerTests {

    @Autowired
    private MockMvc mvc;

    @Mock
    private OrderUseCase orderUseCase;

    @InjectMocks
    private OrderController orderController;

    @Autowired
    private JacksonTester<Order> jsonOrder;

    @Autowired
    private JacksonTester<List<Order>> jsonOrders;

    @Autowired
    private JacksonTester<ApiErrorMessage> jsonApiErrorMessage;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setControllerAdvice(new OrderControllerAdvisor())
                .build();
    }

    @Test
    void givenId_whenOrdersAreFound_ThenReturnOrders() throws Exception {
        var expectedOrders = singletonList(Order.builder().id(1234L).status(OrderStatus.CREATED).build());

        when(orderUseCase.getAllPendingOrdersOrderedByStatusAndCreatedAt()).thenReturn(expectedOrders);

        MockHttpServletResponse response = mvc.perform(get("/orders").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        List<Order> someClassList = mapper.readValue(response.getContentAsString() , typeFactory.constructCollectionType(List.class, Order.class));

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someClassList).isEqualTo(expectedOrders);
    }

    @Test
    void givenOrderToBeSaved_whenSaveIsSuccessful_ThenReturnOrder() throws Exception {
        Order expectedOrder = Order.builder().id(1234L).status(OrderStatus.CREATED).build();
        OrderDTO orderToBeSaved = OrderDTO.builder().status(OrderStatus.CREATED).build();

        when(orderUseCase.createOrder(orderToBeSaved)).thenReturn(expectedOrder);

        var response = mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
                .content(getMapper().writeValueAsString(orderToBeSaved))).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(getMapper().writeValueAsString(1234L));
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
    void givenId_whenOrderFinishedNotFound_ThenError() throws Exception {
        var expectedErrorMessage = new ApiErrorMessage(HttpStatus.NOT_FOUND, "Order not found");
        doThrow(new OrderNotFoundException()).when(orderUseCase).orderFinished(1234L);

        var response = mvc.perform(patch("/orders/1234/finished").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonApiErrorMessage.write(expectedErrorMessage).getJson());
    }

    @Test
    void givenId_whenOrderCancelNotFound_ThenError() throws Exception {
        var expectedErrorMessage = new ApiErrorMessage(HttpStatus.NOT_FOUND, "Order not found");
        doThrow(new OrderNotFoundException()).when(orderUseCase).orderCanceled(1234L);

        var response = mvc.perform(patch("/orders/1234/cancel").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonApiErrorMessage.write(expectedErrorMessage).getJson());
    }

    @Test
    void givenOrderToBeSaved_whenSaveFails_ThenError() throws Exception {
        var orderToBeSaved = OrderDTO.builder().status(OrderStatus.CREATED).build();

        var expectedErrorMessage = new ApiErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY, "Order could not be processed");

        when(orderUseCase.createOrder(orderToBeSaved)).thenThrow(InvalidOrderException.class);

        var response = mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(getMapper().writeValueAsString(orderToBeSaved))).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(response.getContentAsString()).isEqualTo(jsonApiErrorMessage.write(expectedErrorMessage).getJson());
    }

    private static ObjectWriter getMapper() {
        return new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

}
