package com.mall.usermanagement.service;

import com.mall.usermanagement.dto.*;
import com.mall.usermanagement.entity.User;
import com.mall.usermanagement.enums.Role;
import com.mall.usermanagement.exception.DuplicateResourceException;
import com.mall.usermanagement.exception.ResourceNotFoundException;
import com.mall.usermanagement.exception.UnauthorizedException;
import com.mall.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @InjectMocks private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L).name("John Doe").email("john@example.com")
                .password("encodedPassword").phone("9876543210")
                .shippingAddress("123 Main St").role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Registration")
    class RegistrationTests {
        @Test
        @DisplayName("Should register new customer successfully")
        void register_Valid_ReturnsAuth() {
            var req = RegisterRequest.builder().name("John Doe")
                    .email("john@example.com").password("password123").build();
            when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

            AuthResponse res = userService.register(req);
            assertThat(res.getToken()).isEqualTo("jwt-token");
            assertThat(res.getRole()).isEqualTo("CUSTOMER");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw on duplicate email")
        void register_Duplicate_Throws() {
            var req = RegisterRequest.builder().name("J").email("john@example.com").password("p").build();
            when(userRepository.existsByEmail("john@example.com")).thenReturn(true);
            assertThatThrownBy(() -> userService.register(req)).isInstanceOf(DuplicateResourceException.class);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw on invalid role")
        void register_InvalidRole_Throws() {
            var req = RegisterRequest.builder().name("J").email("j@e.com").password("p").role("INVALID").build();
            when(userRepository.existsByEmail("j@e.com")).thenReturn(false);
            assertThatThrownBy(() -> userService.register(req)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should register shop owner with explicit role")
        void register_ShopOwner_Works() {
            var req = RegisterRequest.builder().name("Owner").email("o@e.com").password("p").role("SHOP_OWNER").build();
            var owner = User.builder().id(2L).name("Owner").email("o@e.com").role(Role.SHOP_OWNER).build();
            when(userRepository.existsByEmail("o@e.com")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("enc");
            when(userRepository.save(any(User.class))).thenReturn(owner);
            when(jwtService.generateToken(any(User.class))).thenReturn("t");
            assertThat(userService.register(req).getRole()).isEqualTo("SHOP_OWNER");
        }
    }

    @Nested
    @DisplayName("Login")
    class LoginTests {
        @Test
        @DisplayName("Should login with correct credentials")
        void login_Valid_ReturnsAuth() {
            var req = LoginRequest.builder().email("john@example.com").password("password123").build();
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(jwtService.generateToken(testUser)).thenReturn("jwt-token");
            AuthResponse res = userService.login(req);
            assertThat(res.getToken()).isEqualTo("jwt-token");
            assertThat(res.getMessage()).isEqualTo("Login successful");
        }

        @Test
        @DisplayName("Should throw for non-existing email")
        void login_BadEmail_Throws() {
            var req = LoginRequest.builder().email("no@e.com").password("p").build();
            when(userRepository.findByEmail("no@e.com")).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.login(req)).isInstanceOf(UnauthorizedException.class);
        }

        @Test
        @DisplayName("Should throw for wrong password")
        void login_BadPassword_Throws() {
            var req = LoginRequest.builder().email("john@example.com").password("wrong").build();
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);
            assertThatThrownBy(() -> userService.login(req)).isInstanceOf(UnauthorizedException.class);
        }
    }

    @Nested
    @DisplayName("Profile")
    class ProfileTests {
        @Test
        @DisplayName("Should return profile by ID")
        void getProfile_Exists_Returns() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            var p = userService.getProfile(1L);
            assertThat(p.getEmail()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("Should throw for non-existing profile")
        void getProfile_NotFound_Throws() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.getProfile(999L)).isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should update phone and address")
        void updateProfile_Valid_Updates() {
            var req = UpdateProfileRequest.builder().phone("111").shippingAddress("New St").build();
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            var p = userService.updateProfile(1L, req);
            assertThat(p).isNotNull();
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Partial update only changes provided fields")
        void updateProfile_Partial_PreservesOthers() {
            var req = UpdateProfileRequest.builder().phone("111").build();
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
            userService.updateProfile(1L, req);
            assertThat(testUser.getPhone()).isEqualTo("111");
            assertThat(testUser.getShippingAddress()).isEqualTo("123 Main St");
        }
    }

    @Nested
    @DisplayName("Admin Operations")
    class AdminTests {
        @Test
        @DisplayName("Should return all users")
        void getAllUsers_Returns() {
            when(userRepository.findAll()).thenReturn(List.of(testUser));
            assertThat(userService.getAllUsers()).hasSize(1);
        }

        @Test
        @DisplayName("Should update role")
        void updateRole_Valid_Updates() {
            var req = RoleUpdateRequest.builder().role("SHOP_OWNER").build();
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
            assertThat(userService.updateRole(1L, req).getRole()).isEqualTo(Role.SHOP_OWNER);
        }

        @Test
        @DisplayName("Should throw for invalid role")
        void updateRole_Invalid_Throws() {
            var req = RoleUpdateRequest.builder().role("SUPERUSER").build();
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            assertThatThrownBy(() -> userService.updateRole(1L, req)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("Logout delegates to JwtService")
    void logout_Delegates() {
        userService.logout("token");
        verify(jwtService).blacklistToken("token");
    }

    @Test
    @DisplayName("findByEmail returns user")
    void findByEmail_Found() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        assertThat(userService.findByEmail("john@example.com").getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("findByEmail throws for missing")
    void findByEmail_NotFound() {
        when(userRepository.findByEmail("no@e.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findByEmail("no@e.com")).isInstanceOf(ResourceNotFoundException.class);
    }
}
