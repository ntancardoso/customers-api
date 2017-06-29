package com.the9o.api.customers.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import com.the9o.api.customers.domain.Customer;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, String> {

	Iterable<Customer> findAll(Sort sort);
	
	Iterable<Customer> findByFirstName(@Param("firstName") String firstName);
	
	Iterable<Customer> findByLastName(@Param("lastName") String lastName);
	
	Iterable<Customer> findByEmail(@Param("email") String email);
	
	Iterable<Customer> findByGender(@Param("gender") String gender);
	
	Iterable<Customer> findByJobTitle(@Param("jobTitle") String jobTitle);

	Page<Customer> findAll(Pageable pageable);
	
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	<S extends Customer> S save(S entity);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	<S extends Customer> Iterable<S> save(Iterable<S> entities);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void delete(String id);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void delete(Customer entity);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void delete(Iterable<? extends Customer> entities);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void deleteAll();

}
