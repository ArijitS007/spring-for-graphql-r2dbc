package com.springforgraphQL;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RestController
@Slf4j
public class GraphQLController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    private final HttpGraphQLClientConfig httpGraphQLClientConfig;

    public GraphQLController(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository,
                             HttpGraphQLClientConfig httpGraphQLClientConfig) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.httpGraphQLClientConfig = httpGraphQLClientConfig;
    }

    // Using Java's Functional Interface to map the input with the employee object type
    Function<AddEmployeeInput, Employee> mapping = aei -> {
        var employee = new Employee();
        employee.setName(aei.getName());
        employee.setSalary(aei.getSalary());
        employee.setDepartmentId(aei.getDepartmentId());

        return employee;
    };

    //@SchemaMapping(typeName = "Mutation", field = "addEmployee")
    @MutationMapping
    public Mono<Employee> addEmployee(@Argument AddEmployeeInput addEmployeeInput) {
        return employeeRepository.save(mapping.apply(addEmployeeInput));
    }

    @QueryMapping
    public Flux<Employee> employeeByName(@Argument String employeeName) {
        return employeeRepository.getEmployeeByName(employeeName);
    }

    // flatMap is a java stream method which returns an array by applying the passed callback function/lamda.
    @MutationMapping
    public Mono<Employee> updateSalary(@Argument UpdateSalaryInput updateSalaryInput) {
        return employeeRepository.findById(updateSalaryInput.getEmployeeId()).flatMap(employee -> {
            employee.setSalary(updateSalaryInput.getSalary());
            return employeeRepository.save(employee);
        });
    }

    // This will get called for the initial query("allDepartment") and internally
    // for employees it will call the below schema mapping
    @QueryMapping
    public Flux<Department> allDepartment() {
        return departmentRepository.findAll();
    }

    // This is not very efficient as it will increase DB calls or network calls
    // based on the number of entities[Department].
    // Call is per department
    // typename defines the higher entity/type that we are looking for and
    // field defines the field/property of the entity
    //@SchemaMapping(typeName= "Department", field="employees")
    public Flux<Employee> employees(Department department) {
        log.debug("DepartmentId - #{}", department.getId());
        return employeeRepository.getAllEmployeeByDepartmentId(department.getId());
    }

    // Fetches for all departments in one shot
    // collectMultiMap: convert's sequence into a Mono<Map> that each Map’s key can be paired with multi-value (in a Collection).
    // single invocation
    @BatchMapping
    public Mono<Map<Department, Collection<Employee>>> employees(List<Department> departments){
        return Flux.fromIterable(departments)
                .flatMap(department ->  employeeRepository.getAllEmployeeByDepartmentId(department.getId()))
                .collectMultimap(employee -> departments.stream().filter(department -> department.getId().equals(employee.getDepartmentId())).findFirst().get());
    }

    // Data is sent back on realtime while the underlying system changes unlike QueryMapping where the response is
    // sent only once
    // subscription works in a websocket.
    // The websocket is a communication channel providing full duplex communication over a single tcp connection
    // define the websocket path in application.properties

    @SubscriptionMapping
    public Flux<Employee> allEmployee(){
        return employeeRepository.findAll().delayElements(Duration.ofSeconds(2));
    }

    // Web will call this client and the client will be calling the graphQL server
    @GetMapping("/employeeByName")
    public Mono<Employee> employeeByName() {
        var document = "query {\n" +
                "#   employeeByName(employeeName: \"Praful\") {\n" +
                "#     id, name, salary, departmentId\n" +
                "#   }\n" +
                "# }";
        return httpGraphQLClientConfig.httpGraphQLClient().document(document)
                .retrieve("employeeByName")
                .toEntity(Employee.class);
    }

}

