package uk.gitsoft.ems.service;

import uk.gitsoft.ems.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {

    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto getEmployeeById(Long employeeId);

    List<EmployeeDto> getAllEmployees();

    EmployeeDto updateEmployee(long employeeId, EmployeeDto employeeDto);

    void deleteEmployeeById(Long employeeId);

    EmployeeDto patchEmployee(Long employeeId, EmployeeDto employeeDto);
}
