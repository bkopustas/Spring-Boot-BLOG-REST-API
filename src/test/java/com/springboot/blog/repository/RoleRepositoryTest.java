package com.springboot.blog.repository;

import com.springboot.blog.entity.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {

    @MockBean
    private RoleRepository roleRepository;

    Role role;

    @BeforeEach
    void setUp(){
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
    }

    @AfterEach
    void tearDown() {
        role = null;
        Mockito.reset(roleRepository); // Reset mock after each test
    }

    @Test
    void findByNameShouldReturnSuccessTest() {

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        Optional<Role> foundRoleOptional = roleRepository.findByName("ROLE_USER");

        assertTrue(foundRoleOptional.isPresent());
        assertEquals(role, foundRoleOptional.get());
    }

    @Test
    void findByNameShouldReturnEmptyOptionalWhenRoleDoesNotExist() {

        when(roleRepository.findByName("NON_EXISTENT_ROLE")).thenReturn(Optional.empty());

        Optional<Role> foundRoleOptional = roleRepository.findByName("NON_EXISTENT_ROLE");

        assertFalse(foundRoleOptional.isPresent());
    }

}
