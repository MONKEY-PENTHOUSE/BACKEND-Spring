package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.component.OrderIdGenerator;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.entity.*;
import com.monkeypenthouse.core.exception.CommonException;
import com.monkeypenthouse.core.repository.OrderProductRepository;
import com.monkeypenthouse.core.repository.OrderRepository;
import com.monkeypenthouse.core.repository.TicketRepository;
import com.monkeypenthouse.core.repository.UserRepository;
import com.monkeypenthouse.core.vo.CreateOrderRequestVo;
import com.monkeypenthouse.core.vo.CreateOrderResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final TicketRepository ticketRepository;
    private final OrderIdGenerator orderIdGenerator;
    private final UserService userService;

    @Override
    @Transactional
    public CreateOrderResponseVo createOrder(final UserDetails userDetails, final CreateOrderRequestVo requestVo) {

        // 주문 수량에 대해 티켓 재고를 확인하는 유효성 검증 로직 (레디스를 사용할 예정)

        // 티켓 리스트 불러오기
        final List<Long> ticketIdList = requestVo.getOrderProductVoList().stream()
                .map(orderProductVo -> orderProductVo.getTicketId())
                .collect(Collectors.toList());

        final List<Ticket> ticketList = (List<Ticket>) ticketRepository.findAllById(ticketIdList);

        // 티켓 리스트 유효성 검증
        if (ticketList.size() != requestVo.getOrderProductVoList().size()) {
            throw new CommonException(ResponseCode.TICKET_NOT_FOUND);
        }

        // 티켓 ID : 구매 개수 HashMap 구성
        final HashMap<Long, Integer> quantityMap = new HashMap<>();
        requestVo.getOrderProductVoList().stream().map(
                orderProductVo -> quantityMap.put(orderProductVo.getTicketId(), orderProductVo.getQuantity()));

        // amount 측정
        int amount = ticketList.stream()
                .mapToInt(t -> quantityMap.get(t.getId()) * t.getPrice())
                .sum();

        // orderId 생성
        final String orderId = orderIdGenerator.generate();

        // orderName 생성
        String orderName = ticketList.get(0).getName();
        if (ticketList.size() > 1) {
            orderName += " 외 " + (ticketList.size() - 1) + "건";
        }

        // Order 엔티티 생성
        // 여기서 User 엔티티를 find하지 않고 바로 foreign key로 주입할 수 있는 방법이 있나요..?
        final User user = userService.getUserByEmail(userDetails.getUsername());

        final Order order = new Order(user, orderId, orderName, amount, OrderStatus.IN_PROGRESS);
        orderRepository.save(order);

        // OrderProduct 엔티티 생성
        ticketList.stream().map(ticket ->
                        orderProductRepository.save(new OrderProduct(order, ticket, quantityMap.get(ticket.getId())))
                );

        return CreateOrderResponseVo.builder()
                .amount(amount)
                .orderId(orderId)
                .orderName(orderName)
                .build();
    }
}
