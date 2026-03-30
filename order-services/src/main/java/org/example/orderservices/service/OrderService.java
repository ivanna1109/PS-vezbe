package org.example.orderservices.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.example.orderservices.client.RestaurantClient;
import org.example.orderservices.dto.*;
import org.example.orderservices.exceptions.OrderProcessingException;
import org.example.orderservices.exceptions.RestaurantNotFoundException;
import org.example.orderservices.model.Order;
import org.example.orderservices.model.OrderItem;
import org.example.orderservices.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order saveOrder(Order order) {
        // Ovde ćemo kasnije dodati logiku za proveru cena iz restaurant-service
        // Za sada samo računamo totalAmount prosto (ako ga dobijemo sa frontenda)
        if (order.getItems() != null) {
            double total = order.getItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            order.setTotalAmount(total);
        }
        return orderRepository.save(order);
    }

    //V2

    public List<OrderResponseDTO> getAllDTOs() {
        return orderRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public OrderResponseDTO createOrder(OrderCreateDTO dto) {
        Order order = new Order();
        order.setRestaurantId(dto.getRestaurantId());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Računanje ukupne cene (Simulacija dok nemamo komunikaciju)
        double calculatedTotal = dto.getItems().stream()
                .mapToDouble(i -> i.getQuantity() * 500.0) // Fiktivna cena
                .sum();

        order.setTotalAmount(calculatedTotal);

        Order saved = orderRepository.save(order);
        return convertToResponseDTO(saved);
    }

    private OrderResponseDTO convertToResponseDTO(Order order) {
        OrderResponseDTO resp = new OrderResponseDTO();
        resp.setId(order.getId());
        resp.setTotalAmount(order.getTotalAmount());
        resp.setStatus(order.getStatus());
        resp.setCreatedAt(order.getCreatedAt());
        return resp;
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Porudžbina sa ID " + id + " ne postoji!");
        }
        orderRepository.deleteById(id);
    }

    //V3

    private final RestaurantClient restaurantClient;

    public OrderResponseDTO createOrderV3(OrderCreateDTO dto) {
        // Ako restoran ne postoji, ovde će pući FeignException (404)
        RestaurantDTO restaurant = restaurantClient.getRestaurantById(dto.getRestaurantId());

        //System.out.println("Kreiram porudžbinu za restoran: " + restaurant.getName());

        return createOrderNew(dto, restaurant);
    }

    public OrderResponseDTO createOrderNew(OrderCreateDTO dto, RestaurantDTO rdto) {
        Order order = new Order();
        order.setRestaurantId(rdto.getId());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Računanje ukupne cene (Simulacija dok nemamo komunikaciju)
        double calculatedTotal = dto.getItems().stream()
                .mapToDouble(i -> i.getQuantity() * 500.0) // Fiktivna cena
                .sum();

        order.setTotalAmount(calculatedTotal);

        Order saved = orderRepository.save(order);
        return convertToResponseDTO(saved);
    }

    //v4

    @CircuitBreaker(name = "restaurantServiceCB", fallbackMethod = "fallbackCreateOrder")
    @Transactional
    public OrderResponseDTO createOrderV4(OrderCreateDTO request) {
        RestaurantDTO restaurant = restaurantClient.getRestaurantById(request.getRestaurantId());

        for (OrderItemRequestDTO requestedItem : request.getItems()) {
            // Pronaći jelo u meniju restorana
            ItemDTO actualItem = restaurant.getMenuItems().stream()
                    .filter(i -> i.getId().equals(requestedItem.getItemId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jelo ne postoji!"));

            // Poredi se cena iz zahteva i cena iz restorana
            if (!actualItem.getPrice().equals(requestedItem.getPriceAtBooking())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Cena za jelo se promenila! Trenutna cena je: " + actualItem.getPrice());
            }
        }
        return createOrderInDatabaseV4(request, restaurant);
    }

    public OrderResponseDTO createOrderInDatabaseV4(OrderCreateDTO dto, RestaurantDTO rdto) {
        Order order = new Order();
        order.setRestaurantId(rdto.getId());
        order.setStatus("PENDING");
        order.setCustomerName("Test test"); //hardkodovano
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;

        // Za svaku stavku iz zahteva (DTO) praviti model OrderItem
        for (OrderItemRequestDTO itemRequest : dto.getItems()) {

            // Tražiti jelo u meniju koji nam je stigao iz Restoran servisa
            ItemDTO menuItem = rdto.getMenuItems().stream()
                    .filter(m -> m.getId().equals(itemRequest.getItemId())).findFirst().get();

            // Kreirati entitet OrderItem za bazu
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemId(menuItem.getId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(menuItem.getPrice()); // Uzimamo PRAVU cenu iz restorana, ne fiktivnu!

            totalAmount += orderItem.getPrice() * orderItem.getQuantity();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order saved = orderRepository.save(order);
        return convertToResponseDTO(saved);
    }

    public OrderResponseDTO fallbackCreateOrder(OrderCreateDTO dto, Throwable t) {
        if (t instanceof feign.FeignException.NotFound) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Restoran sa ID-jem " + dto.getRestaurantId() + " nije pronađen."
            );
        }

        // Problem sa cenom ili loš zahtev (400 ili 409)
        if (t instanceof ResponseStatusException) {
            throw (ResponseStatusException) t;
        }

        // Pravi pad sistema (Connection Refused, Timeout, ili je CB već OPEN)
        // Kada stvarno možemo reći da servis nije dostupan
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Sistem za restorane je trenutno nedostupan ili preopterećen."
        );
    }

    //v5
    //@RateLimiter(name = "restaurantServiceRateLimiter", fallbackMethod = "fallbackRateLimit")
    //@CircuitBreaker(name = "restaurantServiceCB", fallbackMethod = "fallbackCreateOrderV5")
    @CircuitBreaker(name = "restaurantServiceCB", fallbackMethod = "fallbackCreateOrderV6")
    @Transactional
    public OrderResponseDTO createOrderV5(OrderCreateDTO request) {
        RestaurantDTO restaurant = restaurantClient.getRestaurantById(request.getRestaurantId());

        for (OrderItemRequestDTO requestedItem : request.getItems()) {
            ItemDTO actualItem = restaurant.getMenuItems().stream()
                    .filter(i -> i.getId().equals(requestedItem.getItemId()))
                    .findFirst()
                    .orElseThrow(() -> new OrderProcessingException("Jelo ne postoji u meniju!", HttpStatus.NOT_FOUND));

            if (!actualItem.getPrice().equals(requestedItem.getPriceAtBooking())) {
                // Umesto ResponseStatusException, bacamo custom izuzetak
                throw new OrderProcessingException("Cena se promenila! Trenutna: " + actualItem.getPrice(), HttpStatus.CONFLICT);
            }
        }

        if (request.getRestaurantId() == 99) {
            throw new OrderProcessingException("Korisnik nema dovoljno sredstava (Simulacija 402)", HttpStatus.PAYMENT_REQUIRED);
        }

        return createOrderInDatabaseV4(request, restaurant);
    }

    // fallback method v5
    public OrderResponseDTO fallbackCreateOrderV5(OrderCreateDTO dto, Throwable t) {
        if (t instanceof OrderProcessingException) {
            throw (OrderProcessingException) t;
        }

        if (t instanceof feign.FeignException.NotFound) {
            throw new OrderProcessingException("Restoran nije pronađen preko Feign-a", HttpStatus.NOT_FOUND);
        }

        throw new OrderProcessingException("Sistem restorana je nedostupan (Circuit Breaker)", HttpStatus.SERVICE_UNAVAILABLE);
    }

    public OrderResponseDTO fallbackRateLimit(OrderCreateDTO dto, Throwable t) {
        throw new OrderProcessingException("Previše zahteva! (Rate Limit).", HttpStatus.TOO_MANY_REQUESTS);
    }

    public OrderResponseDTO fallbackCreateOrderV6(OrderCreateDTO dto, Throwable t) {
        Throwable actualCause = (t instanceof java.lang.reflect.UndeclaredThrowableException)
                ? t.getCause() : t;

        if (actualCause instanceof OrderProcessingException) {
            throw (OrderProcessingException) actualCause;
        }

        if (actualCause.getClass().getSimpleName().equals("CallNotPermittedException")) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Circuit Breaker je otvoren!");
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Greška: " + actualCause.getMessage());
    }

    //metod za test u Half-open stanju:
    @CircuitBreaker(name = "restaurantServiceCB", fallbackMethod = "fallbackSafe")
    public RestaurantDTO getRestaurantSafe(Long id) {
        return restaurantClient.getRestaurantById(id);
    }

    // Fallback koji se aktivira kada CB detektuje problem (Open stanje)
    public RestaurantDTO fallbackSafe(Long id, Throwable t) {
        // Log greške
        System.out.println("Circuit Breaker aktiviran zbog: " + t.getMessage());

        if (t instanceof feign.FeignException.NotFound)
            throw new RestaurantNotFoundException("Restoran nije pronadjen!", HttpStatus.NOT_FOUND);
        throw new OrderProcessingException(
                "Restoran servis je trenutno nedostupan. Molimo pokušajte kasnije.",
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }
}
