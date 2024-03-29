package yoonho.demo.reactive.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yoonho.demo.reactive.model.Customer;
import yoonho.demo.reactive.service.customer.CustomerService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
	private final CustomerService customerService;
	
	@GetMapping("/list")
	public Flux<Customer> getCustomers() {
		log.info("controller start");
		return customerService.findAll();
	}
	
	@PostMapping("/add")
	public Mono<Customer> addCustomers(@RequestBody Customer customer) {
		Mono<Customer> custMono = Mono.just(customer).log();
		
		return Mono.zip(custMono, customerService.getId())
		    .map(tuple -> {
		    	tuple.getT1().setId(tuple.getT2());
		    	tuple.getT1().setNewFlag(true);
		    	return tuple.getT1();
		    })
		    .flatMap(c ->  customerService.save(c))
		    ;
		
	}
	
	@PostMapping("/add2")
	public Mono<Customer> addCustomer2(@RequestBody Customer customer) {
		customer.setNewFlag(true);
		return customerService.save(customer);
	}
	
	
	@PutMapping("/update")
	public Mono<Customer> updateCustomer(@RequestBody Customer customer) {
		return customerService
				.findById(customer.getId())
				.map(c -> {
					c.setName(customer.getName());
					c.setCcrdNo(customer.getCcrdNo());
					return c;
				})
				.flatMap(c -> customerService.save(c))
				;	
	}
	
}
