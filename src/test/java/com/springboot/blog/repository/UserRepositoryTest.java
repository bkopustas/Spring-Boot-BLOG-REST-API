package com.springboot.blog.repository;


import com.springboot.blog.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @MockBean
    private UserRepository userRepository;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setEmail("user1@user1.com");
        user.setPassword("user1");
    }

    @AfterEach
    void tearDown() {
        user = null;
        Mockito.reset(userRepository);
    }

    @Test
    void findByUsernameShouldReturnSuccessTest(){
         String username = "user1";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> actualUserOptional = userRepository.findByUsername(username);

        assertTrue(actualUserOptional.isPresent());
        assertEquals(user, actualUserOptional.get());
    }

    @Test
    void findByUsernameShouldReturnNotFoundTest(){
        String username = "user123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> actualUserOptional = userRepository.findByUsername(username);

        assertFalse(actualUserOptional.isPresent());
    }

    @Test
    void existsByUsernameShouldReturnSuccessTest(){
        when(userRepository.existsByUsername("user1")).thenReturn(true);

        assertTrue(userRepository.existsByUsername("user1"));

    }
    @Test
    void existsByUsernameShouldReturnNotFoundTest(){
        when(userRepository.existsByUsername("user123")).thenReturn(false);

        assertFalse(userRepository.existsByUsername("user123"));

    }
    @Test
    void existsByEmailShouldReturnSuccessTest(){
        when(userRepository.existsByEmail("user1@user1.com")).thenReturn(true);

        assertTrue(userRepository.existsByEmail("user1@user1.com"));

    }
    @Test
    void existsByEmailShouldReturnNotFoundTest(){
        when(userRepository.existsByEmail("user123@user123.com")).thenReturn(false);

        assertFalse(userRepository.existsByEmail("user123@user123.com"));

    }
}
