package com.mall.usermanagement.repository;

import com.mall.usermanagement.entity.User;
import com.mall.usermanagement.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for UserRepository using an embedded H2 database.
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("hashedPassword")
                .phone("9876543210")
                .shippingAddress("123 Main St")
                .role(Role.CUSTOMER)
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_ExistingEmail_ReturnsUser() {
        Optional<User> found = userRepository.findByEmail("john@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
        assertThat(found.get().getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    @DisplayName("Should return empty for non-existing email")
    void findByEmail_NonExistingEmail_ReturnsEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return true when email exists")
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        boolean exists = userRepository.existsByEmail("john@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void existsByEmail_NonExistingEmail_ReturnsFalse() {
        boolean exists = userRepository.existsByEmail("nobody@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should save user with auto-generated ID and timestamps")
    void save_NewUser_GeneratesIdAndTimestamps() {
        User newUser = User.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .password("hashedPwd")
                .role(Role.SHOP_OWNER)
                .build();

        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find all saved users")
    void findAll_MultipleUsers_ReturnsAll() {
        User user2 = User.builder()
                .name("Admin User")
                .email("admin@mall.com")
                .password("adminPwd")
                .role(Role.ADMIN)
                .build();
        userRepository.save(user2);

        assertThat(userRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Should delete user by ID")
    void deleteById_ExistingUser_RemovesUser() {
        userRepository.deleteById(testUser.getId());

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }
}
