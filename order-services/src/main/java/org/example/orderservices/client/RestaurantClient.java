package org.example.orderservices.client;

import org.example.orderservices.dto.ItemDTO;
import org.example.orderservices.dto.RestaurantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name je proizvoljno, url mora biti tačan port Restaurant servisa
@FeignClient(name = "restaurant-client", url = "http://localhost:8080/api/v2/restaurants")
public interface RestaurantClient {

    @GetMapping("/{id}")
    RestaurantDTO getRestaurantById(@PathVariable("id") Long id);

    //v4
    //sporiji način - svaki item se posebno dobavlja iz restoran servisa
    @GetMapping("/{restaurantId}/items/{itemId}")
    ItemDTO getItemFromRestaurant(@PathVariable Long restaurantId, @PathVariable Long itemId);
}
