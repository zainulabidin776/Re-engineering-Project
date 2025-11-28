package com.sgtech.pos.repository;

import com.sgtech.pos.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    public void setUp() {
        testCustomer = new Customer();
        testCustomer.setPhone("1234567890");
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        entityManager.persistAndFlush(testCustomer);
    }

    @Test
    public void testFindByPhone() {
        Optional<Customer> found = customerRepository.findByPhone("1234567890");
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    public void testFindByPhoneNotFound() {
        Optional<Customer> found = customerRepository.findByPhone("9999999999");
        assertFalse(found.isPresent());
    }

    @Test
    public void testExistsByPhone() {
        assertTrue(customerRepository.existsByPhone("1234567890"));
        assertFalse(customerRepository.existsByPhone("9999999999"));
    }
}

