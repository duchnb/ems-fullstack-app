package uk.gitsoft.ems.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gitsoft.ems.dto.EmployeeDto;
import uk.gitsoft.ems.service.EmployeeService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    //build add Employee REST API
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) {
       EmployeeDto savedEmployee = employeeService.createEmployee(employeeDto);
       return new ResponseEntity<>(savedEmployee,HttpStatus.CREATED);
    }
    //build get Employee by id REST API
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id) {
        // (Optional) quick guard for clearly invalid IDs → 400
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive");
        }
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

}
