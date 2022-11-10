package com.springforgraphQL;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Integer> {
    Flux<Employee> getEmployeeByName(String employeeName);
    Flux<Employee> getAllEmployeeByDepartmentId(Integer departmentId);
}
