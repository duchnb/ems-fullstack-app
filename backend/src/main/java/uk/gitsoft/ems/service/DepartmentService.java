package uk.gitsoft.ems.service;

import uk.gitsoft.ems.dto.DepartmentDto;

import java.util.List;

public interface DepartmentService {
    DepartmentDto createDepartment(DepartmentDto departmentDto);

    DepartmentDto getDepartmentById(Long departmentId);

    DepartmentDto updateDepartment(Long departmentId, DepartmentDto departmentDto);

    List<DepartmentDto> getAllDepartments();

    void deleteDepartmentById(Long departmentId);
}
