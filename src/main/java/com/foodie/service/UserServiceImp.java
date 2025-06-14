package com.foodie.service;

import com.foodie.Config.JwtProvider;
import com.foodie.Config.JwtUtil;
import com.foodie.dto.AddressDTO;
import com.foodie.dto.IngredientDTO;
import com.foodie.dto.MenuItemDTO;
import com.foodie.dto.OrderDTO;
import com.foodie.dto.OrderItemDTO;
import com.foodie.dto.RestaurantOwnerDTO;
import com.foodie.dto.UserProfileDTO;
import com.foodie.model.Address;
import com.foodie.model.Cart;
import com.foodie.model.Ingredients;
import com.foodie.model.MenuItem;
import com.foodie.model.Order;
import com.foodie.model.Restaurant;
import com.foodie.model.Role;
import com.foodie.model.User;
import com.foodie.repository.AddressRepository;
import com.foodie.repository.CartRepository;
import com.foodie.repository.OrderRepository;
import com.foodie.repository.UserRepository;
import com.foodie.request.AddressRequest;
import com.foodie.request.RegisterRequest;
import com.foodie.request.UpdateProfileRequest;
import com.foodie.request.UpdateSecurityRequest;
import com.foodie.util.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserServices {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserProfileDTO createUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        User user = convertToUserObject(request);
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        return convertToUserResponseDTO(user);
    }

    @Override
    public UserProfileDTO getUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return convertToUserResponseDTO(user);
    }

    @Override
    public UserProfileDTO getProfile(String email) {
        return getUser(email);
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserProfileDTO updateProfile(String email, UpdateProfileRequest request) throws Exception {
        User user = findUserByEmail(email);

        if (!email.equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        userRepository.save(user);

        return convertToUserResponseDTO(user);
    }

    @Override
    public List<OrderDTO> getOrderHistory(String email) {
        User user = findUserByEmail(email);
        List<Order> orders = orderRepository.findByUser(user);
        if (orders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found for the user");
        }

        return orders.stream().map(order -> {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(order.getId());
            orderDTO.setOrderDate(order.getOrderDate());
            orderDTO.setTotalAmount(order.getTotalAmount());
            orderDTO.setStatus(order.getStatus());
            orderDTO.setItems(order.getItems().stream().map(item -> {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setId(item.getId());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getPrice());
                itemDTO.setItemName(convertToMenuItemDTO(item.getMenuItem()));
                return itemDTO;
            }).collect(Collectors.toList()));
            return orderDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AddressDTO> getSavedAddresses(String email) throws Exception {
        User user = findUserByEmail(email);
        return user.getAddresses().stream().map(address -> {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setId(address.getId());
            addressDTO.setStreetAddress(address.getStreet());
            addressDTO.setCity(address.getCity());
            addressDTO.setState(address.getState());
            addressDTO.setZipCode(address.getZipCode());
            addressDTO.setLandmark(address.getLandmark());
            addressDTO.setDefault(address.isDefault());
            return addressDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDTO addAddress(String email, AddressRequest addressRequest) {
        User user = findUserByEmail(email);

        if (addressRequest.isDefault()) {
            user.getAddresses().forEach(addr -> addr.setDefault(false));
            addressRepository.saveAll(user.getAddresses());
        }

        Address address = new Address();
        address.setStreet(addressRequest.getStreetAddress());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setZipCode(addressRequest.getZipCode());
        address.setLandmark(addressRequest.getLandmark());
        address.setDefault(addressRequest.isDefault());
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(savedAddress.getId());
        addressDTO.setStreetAddress(savedAddress.getStreet());
        addressDTO.setCity(savedAddress.getCity());
        addressDTO.setState(savedAddress.getState());
        addressDTO.setZipCode(savedAddress.getZipCode());
        addressDTO.setLandmark(savedAddress.getLandmark());
        addressDTO.setDefault(savedAddress.isDefault());

        return addressDTO;
    }

    @Override
    @Transactional
    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already verified");
        }

        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        long expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24 hours

        user.setVerifyOtp(otp);
        user.setVerifyOtpExpireAt(expiryTime);
        userRepository.save(user);

        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send OTP email");
        }
    }

    @Override
    @Transactional
    public void verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.getVerifyOtp() == null || !user.getVerifyOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }

        if (user.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        user.setEmailVerified(true);
        user.setVerifyOtp(null);
        user.setVerifyOtpExpireAt(0L);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void sendResetOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000); // 15 minutes

        user.setResetOtp(otp);
        user.setResetOtpExpireAt(expiryTime);
        userRepository.save(user);

        try {
            emailService.sendResetOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send reset OTP email");
        }
    }

    @Override
    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }

        if (user.getResetOtpExpireAt() < System.currentTimeMillis()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setResetOtpExpireAt(0L);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateSecurity(String email, UpdateSecurityRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found: " + email));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        // Uncomment and implement if 2FA is supported
        // user.setTwoFactorEnabled(request.isEnable2FA());

        userRepository.save(user);
    }

    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        if (jwt == null || jwt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JWT is missing");
        }

        String email = jwtUtil.extractEmail(jwt);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private User convertToUserObject(RegisterRequest request) {
        return User.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .role(Role.ROLE_CUSTOMER)
                .build();
    }

    private UserProfileDTO convertToUserResponseDTO(User user) {
        return UserProfileDTO.builder()
                .email(user.getEmail())
                .userId(user.getUserId())
                .name(user.getUsername())
                .isAccountVerified(user.isEmailVerified())
                .role(user.getRole())
                .build();
    }

    private MenuItemDTO convertToMenuItemDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setAvailable(menuItem.isAvailable());
        dto.setImages(menuItem.getImages());
        List<IngredientDTO> dtos = menuItem.getIngredients().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dto.setIngredients(dtos);
        return dto;
    }

    private RestaurantOwnerDTO convertToRestaurantOwnerDTO(Restaurant restaurant) {
        RestaurantOwnerDTO dto = new RestaurantOwnerDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setCity(restaurant.getCity());
        dto.setPhone(restaurant.getPhone());
        dto.setStatus(restaurant.isOpen());
        dto.setDescription(restaurant.getDescription());
        dto.setCuisineType(restaurant.getCuisineType());
        dto.setImages(restaurant.getImages());
        dto.setIngredients(restaurant.getIngredients().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        dto.setMenuItems(restaurant.getMenuItems().stream()
                .map(this::convertToMenuItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private IngredientDTO convertToDTO(Ingredients ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setQuantityInStock(ingredient.getQuantityInStock());
        dto.setUnit(ingredient.getUnit());
        return dto;
    }
}