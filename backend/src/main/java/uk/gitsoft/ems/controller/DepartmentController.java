package uk.gitsoft.ems.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gitsoft.ems.dto.DepartmentDto;
import uk.gitsoft.ems.service.DepartmentService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto departmentDto) {
        DepartmentDto createdDepartment = departmentService.createDepartment(departmentDto);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }
@GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable("id") Long departmentId) {
        DepartmentDto department = departmentService.getDepartmentById(departmentId);
        return ResponseEntity.ok(department);
    }
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable("id") Long departmentId, @RequestBody DepartmentDto departmentDto) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(departmentId, departmentDto);
        return ResponseEntity.ok(updatedDepartment);
    }
    @GetMapping
    public ResponseEntity<Iterable<DepartmentDto>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable("id") Long departmentId) {
        departmentService.deleteDepartmentById(departmentId);
        return ResponseEntity.noContent().build();
    }
}

