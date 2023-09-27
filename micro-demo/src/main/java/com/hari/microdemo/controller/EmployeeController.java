package com.hari.microdemo.controller;

import com.hari.microdemo.model.Employee;
import com.hari.microdemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/employees")
public class EmployeeController {

    @Autowired
    @Qualifier("employeeServiceImpl")
    private EmployeeService employeeService;
    @PostMapping
    public Employee save(@RequestBody Employee employee) {
        return employeeService.save(employee);
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable String id) {
        return employeeService.getEmployeeById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteEmployeeById(@PathVariable String id) {
        return employeeService.deleteEmployeeById(id);
    }
}
