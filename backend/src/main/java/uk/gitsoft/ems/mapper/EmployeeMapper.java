package uk.gitsoft.ems.mapper;

import uk.gitsoft.ems.dto.EmployeeDto;
import uk.gitsoft.ems.entity.Employee;

public class EmployeeMapper {
    public static EmployeeDto mapToEmployeeDto(Employee employee) {
        return new EmployeeDto(
                employee.getId(), employee.getFirstname(),
                employee.getLastname(), employee.getEmail());
    }
    public static Employee mapToEmployee(EmployeeDto employeeDto) {
        return new Employee(employeeDto.getId(), employeeDto.getFirstName(),
                employeeDto.getLastName(), employeeDto.getEmail());
    }
}
