package yoonho.demo.reactive.service.customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yoonho.demo.reactive.model.Customer;

public interface CustomerService {
	public Flux<Customer> findAll();
	public Mono<Long> getId();
	public Mono<Customer> save(Customer customer);
	public Mono<Customer> findById(Long id);
}
