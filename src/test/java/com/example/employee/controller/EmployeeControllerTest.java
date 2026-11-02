package com.example.employee.controller;

import com.example.employee.entity.Employee;
import com.example.employee.service.EmployeeNotFoundException;
import com.example.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void createEmployee_returns201() throws Exception {
        Employee saved = new Employee(1L, "Alice", "alice@example.com", "Engineering", 90000.0);
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(saved);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Alice","email":"alice@example.com","department":"Engineering","salary":90000}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void createEmployee_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllEmployees_returns200() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(
                new Employee(1L, "Alice", "alice@example.com", "Engineering", 90000.0),
                new Employee(2L, "Bob", "bob@example.com", "HR", 50000.0)));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    void getEmployeeById_returns200() throws Exception {
        when(employeeService.getEmployeeById(1L))
                .thenReturn(new Employee(1L, "Alice", "alice@example.com", "Engineering", 90000.0));

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void getEmployeeById_notFound_returns404() throws Exception {
        when(employeeService.getEmployeeById(42L)).thenThrow(new EmployeeNotFoundException(42L));

        mockMvc.perform(get("/api/employees/42"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEmployee_returns204() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(1L);
    }

    @Test
    void deleteEmployee_notFound_returns404() throws Exception {
        doThrow(new EmployeeNotFoundException(42L)).when(employeeService).deleteEmployee(42L);

        mockMvc.perform(delete("/api/employees/42"))
                .andExpect(status().isNotFound());
    }
}
