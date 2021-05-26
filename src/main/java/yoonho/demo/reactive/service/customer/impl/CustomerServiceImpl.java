package yoonho.demo.reactive.service.customer.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yoonho.demo.reactive.model.Customer;
import yoonho.demo.reactive.repository.CustomerRepository;
import yoonho.demo.reactive.service.customer.CustomerService;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
	private final CustomerRepository customerRepository;

	public Flux<Customer> findAll() {
		return customerRepository.findAll();
	}
	
	public Mono<Long> getId() {
		return customerRepository.getId();
	}
	
	public Mono<Customer> save(Customer customer) {
		return customerRepository.save(customer);
	}
	
	public Mono<Customer> findById(Long id) {
		return customerRepository.findById(id);
	}
}
