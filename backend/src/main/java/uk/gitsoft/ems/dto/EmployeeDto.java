package uk.gitsoft.ems.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long departmentId;
}
