package com.example.employee.service;

import com.example.employee.entity.Employee;
import com.example.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createEmployee_savesAndReturnsEmployee() {
        Employee input = new Employee(null, "Alice", "alice@example.com", "Engineering", 90000.0);
        Employee saved = new Employee(1L, "Alice", "alice@example.com", "Engineering", 90000.0);
        when(employeeRepository.save(any(Employee.class))).thenReturn(saved);

        Employee result = employeeService.createEmployee(input);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Alice");
        verify(employeeRepository).save(input);
    }

    @Test
    void createEmployee_ignoresClientSuppliedId() {
        Employee input = new Employee(99L, "Bob", "bob@example.com", "HR", 50000.0);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee result = employeeService.createEmployee(input);

        assertThat(result.getId()).isNull();
    }

    @Test
    void getAllEmployees_returnsAll() {
        List<Employee> employees = List.of(
                new Employee(1L, "Alice", "alice@example.com", "Engineering", 90000.0),
                new Employee(2L, "Bob", "bob@example.com", "HR", 50000.0));
        when(employeeRepository.findAll()).thenReturn(employees);

        assertThat(employeeService.getAllEmployees()).hasSize(2).isEqualTo(employees);
    }

    @Test
    void getEmployeeById_found() {
        Employee employee = new Employee(1L, "Alice", "alice@example.com", "Engineering", 90000.0);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThat(employeeService.getEmployeeById(1L)).isEqualTo(employee);
    }

    @Test
    void getEmployeeById_notFound_throws() {
        when(employeeRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(42L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("42");
    }

    @Test
    void deleteEmployee_exists_deletes() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void deleteEmployee_notFound_throws() {
        when(employeeRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.deleteEmployee(42L))
                .isInstanceOf(EmployeeNotFoundException.class);
        verify(employeeRepository, never()).deleteById(any());
    }
}
