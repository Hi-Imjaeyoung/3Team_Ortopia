package com.example.ordering_lecture.orderdetail.dto;

import com.example.ordering_lecture.order.entity.Ordering;
import com.example.ordering_lecture.orderdetail.entity.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRequestDto {
    private Ordering ordering;
    private Long id;
    private int count;
    private Long sellerId;
    public OrderDetail toEntity(Ordering ordering){
        return OrderDetail.builder()
                .ordering(ordering)
                .itemId(this.id)
                .sellerId(this.sellerId)
                .quantity(this.count)
                .build();
    }
}
