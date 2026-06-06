package com.manhnguyen.repository;

import com.manhnguyen.model.Role;
import com.manhnguyen.model.User;
import com.manhnguyen.support.AbstractPostgresIntegrationTest;
import com.manhnguyen.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class UserRepositoryTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void save_persistsUser() {
        User saved = userRepository.save(TestDataFactory.user("save@example.com", Role.USER));

        assertNotNull(saved.getId());
        assertEquals("save@example.com", saved.getEmail());
        assertTrue(saved.isActive());
    }

    @Test
    void findByEmail_returnsUser() {
        userRepository.save(TestDataFactory.user("find@example.com", Role.USER));

        User found = userRepository.findByEmail("find@example.com").orElseThrow();

        assertEquals("find@example.com", found.getEmail());
        assertEquals(Role.USER, found.getRole());
    }

    @Test
    void existsByEmail_returnsTrueWhenPresent() {
        userRepository.save(TestDataFactory.user("exists@example.com", Role.USER));

        assertTrue(userRepository.existsByEmail("exists@example.com"));
        assertFalse(userRepository.existsByEmail("missing@example.com"));
    }

    @Test
    void delete_removesUser() {
        User saved = userRepository.save(TestDataFactory.user("delete@example.com", Role.USER));

        userRepository.delete(saved);

        assertFalse(userRepository.existsByEmail("delete@example.com"));
    }

    @Test
    void save_enforcesUniqueEmailConstraint() {
        userRepository.save(TestDataFactory.user("unique@example.com", Role.USER));

        User duplicate = TestDataFactory.user("unique@example.com", Role.ADMIN);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(duplicate));
    }

    @Test
    void findByEmail_usesEmailIndex() {
        userRepository.save(TestDataFactory.user("indexed@example.com", Role.USER));

        assertTrue(userRepository.findByEmail("indexed@example.com").isPresent());
    }
}
