package uk.gitsoft.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    // Required for create/update (controllers use @Valid); PATCH does not enforce this
    @NotNull(message = "Department is required")
    private Long departmentId;
    // Convenience field for read operations to display department name in UI
    private String departmentName;
}
