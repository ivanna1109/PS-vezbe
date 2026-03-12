package org.example.restaurantservice.service;

import lombok.RequiredArgsConstructor;
import org.example.restaurantservice.dto.ItemDTO;
import org.example.restaurantservice.dto.RestaurantDTO;
import org.example.restaurantservice.model.Restaurant;
import org.example.restaurantservice.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    // Mapiramo listu entiteta u listu DTO-ova
    public List<RestaurantDTO> getAll() {
        return restaurantRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public RestaurantDTO create(RestaurantDTO dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setAddress(dto.getAddress());

        Restaurant saved = restaurantRepository.save(restaurant);
        return convertToDTO(saved);
    }

    private RestaurantDTO convertToDTO(Restaurant res) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(res.getId());
        dto.setName(res.getName());
        dto.setAddress(res.getAddress());
        return dto;
    }

    public RestaurantDTO update(Long id, RestaurantDTO dto) {
        Restaurant existing = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restoran nije pronađen!"));
        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());

        return convertToDTO(restaurantRepository.save(existing));
    }

    //V3

    public RestaurantDTO getById(Long id) {
        return restaurantRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restoran ne postoji"));
    }
}
