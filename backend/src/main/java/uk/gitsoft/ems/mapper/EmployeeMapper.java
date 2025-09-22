package uk.gitsoft.ems.mapper;

import uk.gitsoft.ems.dto.EmployeeDto;
import uk.gitsoft.ems.entity.Employee;

public class EmployeeMapper {
    public static EmployeeDto mapToEmployeeDto(Employee employee) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(employee.getId());
        employeeDto.setFirstName(employee.getFirstname());
        employeeDto.setLastName(employee.getLastname());
        employeeDto.setEmail(employee.getEmail());
        if (employee.getDepartment() != null) {
            employeeDto.setDepartmentId(employee.getDepartment().getId());
        }
        return employeeDto;
    }
    public static Employee mapToEmployee(EmployeeDto employeeDto) {
        Employee employee = new Employee();
        employee.setId(employeeDto.getId());
        employee.setFirstname(employeeDto.getFirstName());
        employee.setLastname(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        return employee;
    }
}
