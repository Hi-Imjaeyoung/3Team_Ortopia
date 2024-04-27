package com.example.ordering_lecture.order.service;

import com.example.ordering_lecture.common.ErrorCode;
import com.example.ordering_lecture.common.OrTopiaException;
import com.example.ordering_lecture.feign.MemberServiceClient;
import com.example.ordering_lecture.order.dto.*;
import com.example.ordering_lecture.order.entity.Ordering;
import com.example.ordering_lecture.order.repository.OrderRepository;
import com.example.ordering_lecture.orderdetail.dto.OrderDetailRequestDto;
import com.example.ordering_lecture.orderdetail.dto.OrderDetailResponseDto;
import com.example.ordering_lecture.orderdetail.entity.OrderDetail;
import com.example.ordering_lecture.orderdetail.repository.OrderDetailRepository;
import com.example.ordering_lecture.payment.controller.ItemServiceClient;
import com.example.ordering_lecture.payment.dto.ItemOptionDto;
import com.example.ordering_lecture.redis.RedisService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderingService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final RedisService redisService;
    private final MemberServiceClient memberServiceClient;
    private final ItemServiceClient itemServiceClient;
    public OrderingService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, RedisService redisService, MemberServiceClient memberServiceClient, ItemServiceClient itemServiceClient) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.redisService = redisService;
        this.memberServiceClient = memberServiceClient;
        this.itemServiceClient = itemServiceClient;
    }

    @Transactional
    // 더티 체킹 설정
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto,String email) {
        // 정상 주문 진행
        if(redisService.getValues(email).equals(orderRequestDto.getPgToken())){
            Ordering ordering = orderRequestDto.toEntity();
            orderRepository.save(ordering);
            for(OrderDetailRequestDto orderDetailRequestDto:orderRequestDto.getOrderDetailRequestDtoList()){
                StringBuilder sb = new StringBuilder();
                for(ItemOptionDto itemOptionDto :orderDetailRequestDto.getOptions()){
                    String optionName =  itemOptionDto.getName();
                    String optionValue = itemOptionDto.getValue();
                    sb.append(optionName);
                    sb.append(" : "+optionValue+" ");
                }
                OrderDetail orderDetail = orderDetailRequestDto.toEntity(ordering,sb.toString());
                orderDetailRepository.save(orderDetail);
            }
            return OrderResponseDto.toDto(ordering);
        }
        throw new OrTopiaException(ErrorCode.ACCESS_DENIED);
    }

    public List<OrderResponseDto> showAllOrder() {
        List<Ordering> orderings = orderRepository.findAll();
        return orderings.stream()
                .map(OrderResponseDto::toDto)
                .collect(Collectors.toList());
    }

    public List<OrderResponseDto> showAllOrdersDetail() {
        List<Ordering> orderings = orderRepository.findAll();
        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();
        for(Ordering ordering : orderings){
            OrderResponseDto orderResponseDto = OrderResponseDto.toDto(ordering);
            List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderingId(ordering.getId());
            List<OrderDetailResponseDto> orderDetailResponseDtoList = orderDetails.stream()
                    .map(OrderDetailResponseDto::toDto)
                    .collect(Collectors.toList());
            orderResponseDto.setOrderDetailResponseDtoList(orderDetailResponseDtoList);
            orderResponseDtos.add(orderResponseDto);
        }
        return orderResponseDtos;
    }

    public List<OrderResponseDto> showMyOrders(String email) {
        List<Ordering> orderings = orderRepository.findAllByEmail(email);
        return orderings.stream()
                .map(OrderResponseDto::toDto)
                .collect(Collectors.toList());
    }

    public List<OrderResponseDto> showMyOrdersDetail(String email) {
        List<Ordering> orderings = orderRepository.findAllByEmail(email);
        List<OrderResponseDto> orderResponseDtos = new ArrayList<>();
        for(Ordering ordering : orderings){
            OrderResponseDto orderResponseDto = OrderResponseDto.toDto(ordering);
            List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrderingId(ordering.getId());
            List<OrderDetailResponseDto> orderDetailResponseDtoList = orderDetails.stream()
                    .map(OrderDetailResponseDto::toDto)
                    .collect(Collectors.toList());
            orderResponseDto.setOrderDetailResponseDtoList(orderDetailResponseDtoList);
            orderResponseDtos.add(orderResponseDto);
        }
        return orderResponseDtos;
    }

    public List<OrderResponseForSellerDto> findMyAllSales(String email) {
        Long sellerId = memberServiceClient.searchIdByEmail(email);
        List<OrderDetail> orderDetails = orderRepository.findAllBySeller(sellerId);
        if(orderDetails.isEmpty()){
            throw new OrTopiaException(ErrorCode.NOT_FOUND_ORDERDETAIL);
        }
        List<OrderResponseForSellerDto> orderResponseDtos = new ArrayList<>();
        for(OrderDetail orderDetail : orderDetails) {
            OrderResponseForSellerDto orderResponseForSellerDto = OrderResponseForSellerDto.toDto(orderDetail);
            // 상품 id로 상품명 가져오기
            String itemName = itemServiceClient.findNameById(orderDetail.getItemId());
            orderResponseForSellerDto.setItemName(itemName);
            orderResponseDtos.add(orderResponseForSellerDto);
        }
        return orderResponseDtos;
    }
//    // 일별 구매 금액을 위한 데이터
//    public List<BuyerGraphPriceData> getBuyerGraphPriceData(String email) {
//        LocalDateTime endDate = LocalDateTime.now();
//        LocalDateTime startDate = endDate.minusWeeks(2);
//        System.out.println("startDate = " + startDate);
//        System.out.println("endDate = " + endDate);
//        return orderRepository.findSumPriceByDateBetweenAndStatueAndEmail(startDate, endDate, email);
//    }
//    // 일별 구매 건수를 위한 데이터
//    public List<BuyerGraphCountData> getBuyerGraphCountData(String email) {
//        LocalDateTime endDate = LocalDateTime.now();
//        LocalDateTime startDate = endDate.minusWeeks(2);
//        return orderRepository.findCompletedOrdersByEmailAndDateRange(startDate, endDate, email);
//    }
//    // 일별 판매 금액을 위한 데이터
//    public List<SellerGraphPriceData> getSellerGraphPriceData(Long sellerId) {
//        LocalDateTime endDate = LocalDateTime.now();
//        LocalDateTime startDate = endDate.minusWeeks(2);
//        return orderDetailRepository.findSalesData(startDate, endDate, sellerId);
//    }
//    // 일별 판매 건수를 위한 데이터
//    public List<SellerGraphCountData> getSellerGraphCountData(Long sellerId) {
//        LocalDateTime endDate = LocalDateTime.now();
//        LocalDateTime startDate = endDate.minusWeeks(2);
//        return orderDetailRepository.findSalesDataBySellerIdAndDateRange(startDate, endDate, sellerId);
//    }
}
