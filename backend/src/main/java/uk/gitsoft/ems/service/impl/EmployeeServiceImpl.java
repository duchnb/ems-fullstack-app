package uk.gitsoft.ems.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gitsoft.ems.dto.EmployeeDto;
import uk.gitsoft.ems.entity.Employee;
import uk.gitsoft.ems.mapper.EmployeeMapper;
import uk.gitsoft.ems.repository.EmployeeRepository;
import uk.gitsoft.ems.service.EmployeeService;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private EmployeeService employeeService;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {

        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
       Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(savedEmployee);
    }
}
