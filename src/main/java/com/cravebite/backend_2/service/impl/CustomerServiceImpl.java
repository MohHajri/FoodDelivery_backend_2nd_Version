package com.cravebite.backend_2.service.impl;

import java.util.Optional;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cravebite.backend_2.exception.CraveBiteGlobalExceptionHandler;
import com.cravebite.backend_2.models.entities.Customer;
import com.cravebite.backend_2.models.entities.Location;
import com.cravebite.backend_2.models.entities.User;
import com.cravebite.backend_2.repository.CustomerRepository;
import com.cravebite.backend_2.service.CustomerService;
import com.cravebite.backend_2.service.LocationService;
import com.cravebite.backend_2.service.UserService;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LocationService locationService;

    // get customer by id
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CraveBiteGlobalExceptionHandler(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    // get customer by user id
    public Customer getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new CraveBiteGlobalExceptionHandler(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    // create customer from authenticated user
    public Customer createCustomerFromAuthenticatedUser(Long locationId) {
        User authenticatedUser = userService.getAuthenticatedUser();
        Long userId = authenticatedUser.getId();

        Optional<Customer> existingCustomer = customerRepository.findByUserId(userId);
        if (existingCustomer.isPresent()) {
            return existingCustomer.get();
        } else {
            Customer newCustomer = new Customer();
            newCustomer.setLocationId(locationId);
            newCustomer.setUser(authenticatedUser);

            return customerRepository.save(newCustomer);
        }

    }

    public Customer getCustomerFromAuthenticatedUser() {
        User authenticatedUser = userService.getAuthenticatedUser();
        Long userId = authenticatedUser.getId();

        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new CraveBiteGlobalExceptionHandler(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    // update location
    public Customer updateCustomerLocation(Long customerId, Point newLocation) {
        // Customer authCourier = createCustomerFromAuthenticatedUser();
        Customer authCourier = getCustomerFromAuthenticatedUser();
        Location updatedLocation = locationService.updateLocation(authCourier.getLocationId(), newLocation);
        authCourier.setLocationId(updatedLocation.getId());
        return customerRepository.save(authCourier);
    }

}
