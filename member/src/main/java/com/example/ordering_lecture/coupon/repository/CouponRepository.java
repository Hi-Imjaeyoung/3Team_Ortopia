package com.example.ordering_lecture.coupon.repository;

import com.example.ordering_lecture.coupon.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon,Long> {
    List<Coupon> findByItemId(Long itemId);
}
