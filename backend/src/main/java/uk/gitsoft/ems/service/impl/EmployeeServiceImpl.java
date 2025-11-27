package uk.gitsoft.ems.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gitsoft.ems.dto.EmployeeDto;
import uk.gitsoft.ems.entity.Department;
import uk.gitsoft.ems.entity.Employee;
import uk.gitsoft.ems.exception.ResourceNotFoundException;
import uk.gitsoft.ems.mapper.EmployeeMapper;
import uk.gitsoft.ems.repository.DepartmentRepository;
import uk.gitsoft.ems.repository.EmployeeRepository;
import uk.gitsoft.ems.service.EmployeeService;

import java.util.List;


@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository DepartmentRepository;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {

        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
        Department department = DepartmentRepository.findById(employeeDto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + employeeDto.getDepartmentId()));
        employee.setDepartment(department);
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(savedEmployee);
    }

    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found: " + employeeId));

        return EmployeeMapper.mapToEmployeeDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(EmployeeMapper::mapToEmployeeDto)
                .toList();
    }

    @Override
    public EmployeeDto updateEmployee(long employeeId, EmployeeDto employeeDto) {
        // Optional: validate id match if body.id present
        if (employeeDto.getId() != null && !employeeDto.getId().equals(employeeId)) {
            throw new IllegalArgumentException("ID in path and body must match");
        }
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee path ID must be provided and greater than zero");
        }
        employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found: " + employeeId));
        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);

        employee.setId(employeeId);

        Department department = DepartmentRepository.findById(employeeDto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + employeeDto.getDepartmentId()));
        employee.setDepartment(department);

        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(updatedEmployee);
    }


    @Override
    public void deleteEmployeeById(Long employeeId) {
        if(employeeRepository.existsById(employeeId))
            employeeRepository.deleteById(employeeId);
        else
            throw new ResourceNotFoundException("Employee not found: " + employeeId);
    }

    @Override
    public EmployeeDto patchEmployee(Long employeeId, EmployeeDto employeeDto) {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be provided and greater than zero");
        }
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found: " + employeeId));

        if (employeeDto.getFirstName() != null) {
            employee.setFirstname(employeeDto.getFirstName());
        }
        if (employeeDto.getLastName() != null) {
            employee.setLastname(employeeDto.getLastName());
        }
        if (employeeDto.getEmail() != null) {
            employee.setEmail(employeeDto.getEmail());
        }
        if (employeeDto.getDepartmentId() != null) {
            Department department = DepartmentRepository.findById(employeeDto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + employeeDto.getDepartmentId()));
            employee.setDepartment(department);
        }
        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(updatedEmployee);
    }

}
