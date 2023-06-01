package com.mlv.inventoryservice.service;

import com.mlv.inventoryservice.dto.InventoryResponse;
import com.mlv.inventoryservice.entity.Inventory;
import com.mlv.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {

        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .IsInStock(inventory.getQuantity() > 0)
                        .build()
                ).toList();

    }
}
