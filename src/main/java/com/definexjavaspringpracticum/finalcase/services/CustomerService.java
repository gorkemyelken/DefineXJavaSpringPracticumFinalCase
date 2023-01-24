package com.definexjavaspringpracticum.finalcase.services;

import com.definexjavaspringpracticum.finalcase.modals.Customer;
import com.definexjavaspringpracticum.finalcase.repositories.CustomerRepository;
import com.definexjavaspringpracticum.finalcase.requests.CustomerCreateRequest;
import com.definexjavaspringpracticum.finalcase.requests.CustomerUpdateRequest;
import com.definexjavaspringpracticum.finalcase.responses.CustomerResponse;
import com.definexjavaspringpracticum.finalcase.utilities.mapping.ModelMapperService;
import com.definexjavaspringpracticum.finalcase.utilities.results.DataResult;
import com.definexjavaspringpracticum.finalcase.utilities.results.ErrorDataResult;
import com.definexjavaspringpracticum.finalcase.utilities.results.SuccessDataResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelMapperService modelMapperService;
    @Autowired
    public CustomerService(CustomerRepository customerRepository, ModelMapperService modelMapperService) {
        this.customerRepository = customerRepository;
        this.modelMapperService = modelMapperService;
    }

    public DataResult<List<CustomerResponse>> getAllCustomers() {
        List<Customer> customers = this.customerRepository.findAll();
        List<CustomerResponse> result = customers.stream().map(customer -> this.modelMapperService.forDto().map(customer,CustomerResponse.class)).collect(Collectors.toList());
        return new SuccessDataResult<>(result,"Customers are listed.");
    }

    public DataResult<CustomerResponse> createCustomer(CustomerCreateRequest customerCreateRequest){
        if(checkIfIdentityNumberExists(customerCreateRequest.getIdentityNumber())){
            return new ErrorDataResult<>("Identity number is already exist.");
        }
        else{
            Customer customer = this.customerRepository.save(new Customer(
                    customerCreateRequest.getIdentityNumber(),
                    customerCreateRequest.getFirstName(),
                    customerCreateRequest.getLastName(),
                    customerCreateRequest.getMonthlyIncome(),
                    customerCreateRequest.getPhoneNumber(),
                    customerCreateRequest.getBirthDate(),
                    customerCreateRequest.getCreditScore()));
            return new SuccessDataResult<>(new CustomerResponse(customer),"Customer is added.");
        }
    }

    public DataResult<CustomerResponse> updateCustomer(Long customerId, CustomerUpdateRequest customerUpdateRequest){
        if(checkIfCustomerIdExists(customerId)){
            return new ErrorDataResult<>("Customer id is not found.");
        }
        else{
            Customer customer = this.customerRepository.findByCustomerId(customerId);
            customer.setFirstName(customerUpdateRequest.getFirstName());
            customer.setLastName(customerUpdateRequest.getLastName());
            customer.setBirthDate(customerUpdateRequest.getBirthDate());
            customer.setMonthlyIncome(customerUpdateRequest.getMonthlyIncome());
            customer.setIdentityNumber(customerUpdateRequest.getIdentityNumber());
            customer.setPhoneNumber(customerUpdateRequest.getPhoneNumber());
            customer.setCreditScore(customerUpdateRequest.getCreditScore());
            customerRepository.save(customer);
            return new SuccessDataResult<>(new CustomerResponse(customer),"Customer is updated.");
        }
    }



    public DataResult<CustomerResponse> delete(Long customerId){
        if(checkIfCustomerIdExists(customerId)){
            return new ErrorDataResult<>("Customer id is not found.");
        }
        else{
            Customer customer = this.customerRepository.findByCustomerId(customerId);
            this.customerRepository.deleteById(customerId);
            return new SuccessDataResult<>(new CustomerResponse(customer),"Customer is deleted.");
        }
    }


    private boolean checkIfIdentityNumberExists(String identityNumber) {
        return this.customerRepository.existsByIdentityNumber(identityNumber);
    }

    private boolean checkIfCustomerIdExists(Long customerId) {
        return !this.customerRepository.existsByCustomerId(customerId);
    }
}
