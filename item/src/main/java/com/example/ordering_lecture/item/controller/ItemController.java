package com.example.ordering_lecture.item.controller;

import com.example.ordering_lecture.common.OrTopiaResponse;
import com.example.ordering_lecture.item.dto.ItemRequestDto;
import com.example.ordering_lecture.item.dto.ItemResponseDto;
import com.example.ordering_lecture.item.dto.ItemUpdateDto;
import com.example.ordering_lecture.item.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrTopiaResponse> createItem(@Valid @ModelAttribute ItemRequestDto itemRequestDto){
        ItemResponseDto itemResponseDto = itemService.createItem(itemRequestDto);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("create success",itemResponseDto);
        return new ResponseEntity<>(orTopiaResponse,HttpStatus.CREATED);
    }

    @GetMapping("/items")
    public ResponseEntity<OrTopiaResponse> showAllItems(){
        List<ItemResponseDto> itemResponseDtos = itemService.showAllItem();
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("read success",itemResponseDtos);
        return new ResponseEntity<>(orTopiaResponse,HttpStatus.OK);
    }
    @GetMapping("/find_item_email/{sellerEmail}")
    public ResponseEntity<OrTopiaResponse> findItemByEmail(@PathVariable String sellerEmail){
        List<ItemResponseDto> itemResponseDtos = itemService.findItemByEmail(sellerEmail);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("read success",itemResponseDtos);
        return new ResponseEntity<>(orTopiaResponse,HttpStatus.OK);
    }
    @PatchMapping("/update_item/{id}")
    public ResponseEntity<OrTopiaResponse> updateItem(@PathVariable Long id,@ModelAttribute ItemUpdateDto itemUpdateDto){
        ItemResponseDto itemResponseDto = itemService.updateItem(id,itemUpdateDto);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("update success",itemResponseDto);
        return new ResponseEntity<>(orTopiaResponse,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<OrTopiaResponse> deleteItem(@PathVariable Long id){
        itemService.deleteItem(id);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("delete success",null);
        return new ResponseEntity<>(orTopiaResponse,HttpStatus.OK);
    }
    @PatchMapping("/ban_items/{sellerEmail}")
    public ResponseEntity<OrTopiaResponse> banItems(@PathVariable String sellerEmail){
        List<ItemResponseDto> itemResponseDtos = itemService.banItem(sellerEmail);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("ban success",itemResponseDtos);
        return new ResponseEntity<>(orTopiaResponse,HttpStatus.OK);
    }
    @PatchMapping("/ban_canceled_items/{sellerEmail}")
    public ResponseEntity<OrTopiaResponse> banCanceled(@PathVariable String sellerEmail){
        List<ItemResponseDto> itemResponseDtos = itemService.releaseBanItem(sellerEmail);
        OrTopiaResponse orTopiaResponse = new OrTopiaResponse("ban canceled success",itemResponseDtos);
        return new ResponseEntity<>(orTopiaResponse,HttpStatus.OK);
    }
}
