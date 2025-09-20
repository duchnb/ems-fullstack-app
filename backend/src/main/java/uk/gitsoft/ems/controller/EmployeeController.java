package uk.gitsoft.ems.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gitsoft.ems.dto.EmployeeDto;
import uk.gitsoft.ems.service.EmployeeService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    //build add Employee REST API


    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody @jakarta.validation.Valid EmployeeDto employeeDto) {
        EmployeeDto saved = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable @Min(1) Long id,
                                                      @RequestBody  @jakarta.validation.Valid EmployeeDto dto) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDto> patchEmployee(@PathVariable @Min(1) Long id,
                                                     @RequestBody EmployeeDto dto) {
        return ResponseEntity.ok(employeeService.patchEmployee(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable @Min(1) Long id) {
        employeeService.deleteEmployeeById(id);
        return ResponseEntity.noContent().build();
    }
}


