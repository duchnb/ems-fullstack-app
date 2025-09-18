package uk.gitsoft.ems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gitsoft.ems.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
