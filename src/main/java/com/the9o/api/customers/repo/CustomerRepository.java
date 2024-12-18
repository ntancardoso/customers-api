package com.the9o.api.customers.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import com.the9o.api.customers.domain.Customer;

@RepositoryRestResource
public interface CustomerRepository extends JpaRepository<Customer, String> {

	List<Customer> findAll(Sort sort);

	Page<Customer> findAll(Pageable pageable);

	Iterable<Customer> findByFirstName(@Param("firstName") String firstName);

	Iterable<Customer> findByLastName(@Param("lastName") String lastName);

	Iterable<Customer> findByEmail(@Param("email") String email);

	Iterable<Customer> findByGender(@Param("gender") String gender);

	Iterable<Customer> findByJobTitle(@Param("jobTitle") String jobTitle);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	Customer save(Customer customer);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	<S extends Customer> List<S> saveAll(Iterable<S> entities);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void deleteById(String id);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void delete(Customer customer);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void deleteAll(Iterable<? extends Customer> entities);

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void deleteAll();

}
