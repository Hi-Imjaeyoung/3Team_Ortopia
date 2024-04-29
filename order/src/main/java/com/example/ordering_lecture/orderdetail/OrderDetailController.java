package com.example.ordering_lecture.orderdetail;

import com.example.ordering_lecture.common.OrTopiaResponse;
import com.example.ordering_lecture.orderdetail.controller.MemberServiceClient;
import com.example.ordering_lecture.orderdetail.dto.*;
import com.example.ordering_lecture.orderdetail.service.OrderDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    private final MemberServiceClient memberServiceClient;

    public OrderDetailController(OrderDetailService orderDetailService, MemberServiceClient memberServiceClient) {
        this.orderDetailService = orderDetailService;
        this.memberServiceClient = memberServiceClient;
    }

    @GetMapping("check/review/{orderDetailId}")
    public ResponseEntity<OrTopiaResponse> checkReview(@PathVariable Long orderDetailId){
        orderDetailService.updateReview(orderDetailId);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("update success");
        return new ResponseEntity<>(orTopiaResponse, HttpStatus.OK);
    }
    @PostMapping("updateStatus")
    public ResponseEntity<OrTopiaResponse> updateStatue(@RequestBody OrderDetailUpdateDto orderDetailUpdateDto) {
        orderDetailService.updateOrder(orderDetailUpdateDto);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("update success");
        return new ResponseEntity<>(orTopiaResponse, HttpStatus.OK);
    }

    @GetMapping("/total_price")
    public ResponseEntity<OrTopiaResponse> totalPrice(@RequestHeader("myEmail") String email){
        List<BuyerGraphPriceData> buyerGraphPriceDatas = orderDetailService.getBuyerGraphPriceData(email);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("read success",buyerGraphPriceDatas);
        return new ResponseEntity<>(orTopiaResponse,HttpStatus.OK);
    }
    @GetMapping("/total_count")
    public ResponseEntity<OrTopiaResponse> totalCount(@RequestHeader("myEmail") String email){
        List<BuyerGraphCountData> buyerGraphCountDatas = orderDetailService.getBuyerGraphCountData(email);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("read success",buyerGraphCountDatas);
        return new ResponseEntity<>(orTopiaResponse,HttpStatus.OK);
    }
    @GetMapping("/total_price/seller")
    public ResponseEntity<OrTopiaResponse> totalPriceBySeller(@RequestHeader("myEmail") String email){
        Long sellerId = memberServiceClient.searchIdByEmail(email);
        List<SellerGraphPriceData> sellerGraphPriceDatas = orderDetailService.getSellerGraphPriceData(sellerId);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("read success",sellerGraphPriceDatas);
        return new ResponseEntity<>(orTopiaResponse,HttpStatus.OK);
    }
    @GetMapping("/total_count/seller")
    public ResponseEntity<OrTopiaResponse> totalCountBySeller(@RequestHeader("myEmail") String email){
        Long sellerId = memberServiceClient.searchIdByEmail(email);
        List<SellerGraphCountData> sellerGraphCountDatas = orderDetailService.getSellerGraphCountData(sellerId);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("read success",sellerGraphCountDatas);
        return new ResponseEntity<>(orTopiaResponse,HttpStatus.OK);
    }
}
