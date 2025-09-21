package uk.gitsoft.ems.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gitsoft.ems.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
