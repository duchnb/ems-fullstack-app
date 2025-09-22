package uk.gitsoft.ems.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gitsoft.ems.dto.DepartmentDto;
import uk.gitsoft.ems.entity.Department;
import uk.gitsoft.ems.exception.ResourceNotFoundException;
import uk.gitsoft.ems.mapper.DepartmentMapper;
import uk.gitsoft.ems.repository.DepartmentRepository;
import uk.gitsoft.ems.service.DepartmentService;

import java.util.List;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private DepartmentRepository departmentRepository;

    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        Department department = DepartmentMapper.mapToDepartment(departmentDto);
        Department savedDepartment =departmentRepository.save(department);

        return DepartmentMapper.mapToDepartmentDto(savedDepartment);
    }

    @Override
    public DepartmentDto getDepartmentById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(()-> new ResourceNotFoundException("Department not found: " + departmentId));
        return DepartmentMapper.mapToDepartmentDto(department);
    }

    @Override
    public DepartmentDto updateDepartment(Long departmentId, DepartmentDto departmentDto) {
        if (departmentId == null || departmentId <= 0) throw new IllegalArgumentException("Department ID must be provided and greater than zero");
        if (departmentDto.getId() != null && !departmentDto.getId().equals(departmentId)) throw new IllegalArgumentException("ID in path and body must match");
        Department department = departmentRepository.findById(departmentId).orElseThrow(()-> new ResourceNotFoundException("Department not found: " + departmentId));
        if (departmentDto.getDepartmentName() != null)
            department.setDepartmentName(departmentDto.getDepartmentName());
        if (departmentDto.getDepartmentDescription() != null)
            department.setDepartmentDescription(departmentDto.getDepartmentDescription());
        Department updatedDepartment = departmentRepository.save(department);
        return DepartmentMapper.mapToDepartmentDto(updatedDepartment);
    }

    @Override
    public List<DepartmentDto> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
         return departments.stream().map(DepartmentMapper::mapToDepartmentDto).toList();

    }

    @Override
    public void deleteDepartmentById(Long departmentId) {
        if(departmentRepository.existsById(departmentId)) {
            departmentRepository.deleteById(departmentId);
        } else {
            throw new ResourceNotFoundException("Department not found: " + departmentId);
        }

    }


}
