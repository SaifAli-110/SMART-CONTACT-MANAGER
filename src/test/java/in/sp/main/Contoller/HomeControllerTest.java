package in.sp.main.Contoller;

import in.sp.main.Model.User;
import in.sp.main.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpSession httpSession;

    @Mock
    private MultipartFile userImage;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setUp() {

    }


    @Test
    void userSignUp_shouldSuccessfullySignupUser(){
        // Arrange
        User user = new User();
        user.setUserName("Saif");
        user.setUserEmail("saif@gmail.com");
        user.setUserPassword("123456");
        user.setUserAbout("Java Developer");

        when(bindingResult.hasErrors()).thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);


        // Act
        String result = homeController.userSignUp(
                user,
                bindingResult,
                true,
                userImage,
                model,
                httpSession
        );


        // Assert
        assertEquals("signup", result);

        assertEquals("ROLE_USER", user.getUserRole());
        assertTrue(user.isEnable());
        assertEquals("default.png", user.getUserImageUrl());

        verify(userRepository, times(1)).save(user);

        verify(model).addAttribute(eq("user"), any(User.class));

        verify(model).addAttribute(
                eq("Successmessage"),
                any()
        );
    }
}