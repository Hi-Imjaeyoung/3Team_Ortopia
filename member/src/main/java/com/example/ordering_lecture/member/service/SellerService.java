package com.example.ordering_lecture.member.service;

import com.example.ordering_lecture.common.ErrorCode;
import com.example.ordering_lecture.common.OrTopiaException;
import com.example.ordering_lecture.member.domain.BannedSeller;
import com.example.ordering_lecture.member.domain.Member;
import com.example.ordering_lecture.member.domain.Seller;
import com.example.ordering_lecture.member.dto.Seller.*;
import com.example.ordering_lecture.member.repository.BannedSellerRepository;
import com.example.ordering_lecture.member.repository.MemberRepository;
import com.example.ordering_lecture.member.repository.SellerRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerService {
    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;
    private final BannedSellerRepository bannedSellerRepository;

    public SellerService(SellerRepository sellerRepository, MemberRepository memberRepository, BannedSellerRepository bannedSellerRepository) {
        this.sellerRepository = sellerRepository;
        this.memberRepository = memberRepository;
        this.bannedSellerRepository = bannedSellerRepository;
    }

    public SellerResponseDto createSeller(Long id, SellerRequestDto sellerRequestDto) throws OrTopiaException{
        Member member = memberRepository.findById(id).orElseThrow(()->new OrTopiaException(ErrorCode.NOT_FOUND_MEMBER));
        Seller seller = sellerRequestDto.toEntity(member);
        sellerRepository.save(seller);
        member.updateRoleToSeller();
        return SellerResponseDto.toDto(seller);
    }

    public SellerResponseDto findById(Long id) throws OrTopiaException{
        Seller seller = sellerRepository.findById(id).orElseThrow(()->new OrTopiaException(ErrorCode.NOT_FOUND_SELLER));
        return SellerResponseDto.toDto(seller);
    }
    // 판매자 전체 조회
    public List<SellerResponseDto> findAllSellers() throws OrTopiaException{
        List<SellerResponseDto> sellerResponseDtos =  sellerRepository.findAll()
                .stream()
                .map(SellerResponseDto::toDto)
                .collect(Collectors.toList());
        if(sellerResponseDtos.isEmpty()){
            throw new OrTopiaException(ErrorCode.NOT_FOUND_SELLERS);
        }
        return sellerResponseDtos;
    }
    // 탈퇴하지 않은 판매자 전체 조회
    public List<SellerResponseDto> findSellers() throws OrTopiaException{
        List<SellerResponseDto> sellerResponseDtos = sellerRepository.findByDelYNFalse()
               .stream()
               .map(SellerResponseDto::toDto)
               .collect(Collectors.toList());
        if(sellerResponseDtos.isEmpty()){
            throw new OrTopiaException(ErrorCode.NOT_FOUND_SELLERS);
        }
        return sellerResponseDtos;
    }
    @Transactional
    public SellerResponseDto updateSeller(Long id, SellerUpdateDto sellerUpdateDto) throws OrTopiaException{
        Seller seller = sellerRepository.findById(id).orElseThrow(()->new OrTopiaException(ErrorCode.NOT_FOUND_SELLER));
        seller.updateSeller(sellerUpdateDto);
        return SellerResponseDto.toDto(seller);
    }

    @Transactional
    public void deleteSeller(Long id) throws OrTopiaException{
        Seller seller = sellerRepository.findById(id).orElseThrow(()-> new OrTopiaException(ErrorCode.NOT_FOUND_SELLER));
        seller.deleteSeller();
    }

    public BannedSellerResponseDto banSeller(Long id, BannedSellerRequestDto bannedSellerRequestDto) throws OrTopiaException{
        Seller seller = sellerRepository.findById(id).orElseThrow(()-> new OrTopiaException(ErrorCode.NOT_FOUND_SELLER));
        BannedSeller bannedSeller = bannedSellerRequestDto.toEntity(seller);
        bannedSellerRepository.save(bannedSeller);
        return BannedSellerResponseDto.toDto(bannedSeller);
    }

    @Transactional
    public void banCancelSeller(Long id) throws OrTopiaException{
        BannedSeller bannedSeller = bannedSellerRepository.findById(id).orElseThrow(
                ()->new OrTopiaException(ErrorCode.NOT_FOUND_BANED_SELLER)
        );
        bannedSeller.banCancel();
    }

    public List<BannedSellerResponseDto> bannedSellers() throws OrTopiaException{
        List<BannedSellerResponseDto> bannedSellerResponseDtos = bannedSellerRepository.findAllByDelYNFalse()
                .stream()
                .map(BannedSellerResponseDto::toDto)
                .collect(Collectors.toList());
        if(bannedSellerResponseDtos.isEmpty()){
            throw new OrTopiaException(ErrorCode.NOT_FOUND_BANED_SELLERS);
        }
        return bannedSellerResponseDtos;
    }
}
