package br.com.bytebank.api.controller;

import br.com.bytebank.api.domain.user.UserCreationDTO;
import br.com.bytebank.api.domain.user.UserDetailsDTO;
import br.com.bytebank.api.exception.ResourceNotFoundException;
import br.com.bytebank.api.service.AccountService;
import br.com.bytebank.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("Should return status 201 Created when registering a user with valid data")
    void registerUser_WithValidData_ReturnsCreated() throws Exception {
        var creationDTO = new UserCreationDTO(
                "Doug Heffernan",
                "doug@bytebank.com",
                "wW*8uuuu",
                "333.333.333-33"
        );
        var detailsDTO = new UserDetailsDTO(
                1L, creationDTO.name(), creationDTO.email(), creationDTO.documentNumber());

        when(userService.createUser(any(UserCreationDTO.class))).thenReturn(detailsDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationDTO)))
                // .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(detailsDTO.id()))
                .andExpect(jsonPath("$.name").value(detailsDTO.name()));
    }

    @Test
    @DisplayName("Should return status 400 Bad Request when registering a user with invalid data")
    void registerUser_WithInvalidData_ReturnsBadRequest() throws Exception {
        var creationDTO = new UserCreationDTO("", null, "123", "");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return status 200 OK and user details when user exists")
    void getUserDetails_WithExistingId_ReturnsOk() throws Exception {
        var userId = 1L;
        var detailsDTO = new UserDetailsDTO(
                userId, "Carrie Heffernan", "carrie@bytebank.com", "777.777.777-77");

        when(userService.getUserById(userId)).thenReturn(detailsDTO);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(detailsDTO.name()));
    }

    @Test
    @DisplayName("Should return status 404 Not Found when user does not exist")
    void getUserDetails_WithNonExistingId_ReturnsNotFound() throws Exception {
        var userId = 99L;

        when(userService.getUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}
