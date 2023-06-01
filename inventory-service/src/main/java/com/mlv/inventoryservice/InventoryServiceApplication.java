package com.mlv.inventoryservice;

import com.mlv.inventoryservice.entity.Inventory;
import com.mlv.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {

        return args -> {
            Inventory inventory0 = new Inventory();
            inventory0.setSkuCode("SKUIPHONE13");
            inventory0.setQuantity(100);

            Inventory inventory1 = new Inventory();
            inventory1.setSkuCode("SKUIPHONE14");
            inventory1.setQuantity(0);

            inventoryRepository.save(inventory0);
            inventoryRepository.save(inventory1);
        };
    }

}
