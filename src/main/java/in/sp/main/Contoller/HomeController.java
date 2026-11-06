package in.sp.main.Contoller;

import in.sp.main.Helper.Message;
import in.sp.main.Model.User;
import in.sp.main.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class HomeController {

    //    create a userRepository object to save the data in database
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    // |****************|    sign up page |****************|
    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "SignUp - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    // ****************|    sign up page configuration |****************|
    @PostMapping("/do_signup")
    public String userSignUp(
            @Valid
            @ModelAttribute("user") User user, BindingResult bindingResult,
            @RequestParam(value = "agreement", defaultValue = "false")
            boolean agreement,
            MultipartFile userImage,
            Model model, HttpSession session) {

        try {
            if (!agreement) {
                throw new Exception("you have not agreed the terms and conditions");
            }
//            validation
            if (bindingResult.hasErrors()) {
                System.out.println("Binding Result Error : " + bindingResult.toString());
                model.addAttribute("user", user);
                return "signup";
            }

            user.setUserRole("ROLE_USER");
            user.setEnable(true);
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));

//            check the user image is empty or nut
            if (userImage.isEmpty()) {
//               you can upload the image
                System.out.println("File is Empty");
                user.setUserImageUrl("contacts.png");
            } else {
//                get the original file
                user.setUserImageUrl(userImage.getOriginalFilename());

//                save file on the directory like folder
                File saveFile = new ClassPathResource("static/images").getFile();

                //                find a path of the file.
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + userImage.getOriginalFilename());
//              upload the file on the real folder.
                Files.copy(userImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Save profile image");
            }

//        save the user entity in your database
            User result = this.userRepository.save(user);

//        set model attribute
            model.addAttribute("user", new User());
            model.addAttribute("Successmessage",
                    new Message("Successfully sign-up", "alert-success"));
            return "signup";

        } catch (Exception e) {
            model.addAttribute("user", user);
            model.addAttribute("Errormessage",
                    new Message("something went wrong",
                            e.getMessage() + "alert-danger"));
            return "signup";
        }
    }
    //    Sign-In Page
    @RequestMapping("/signin")
    public String signin(Model model) {
        model.addAttribute("title", "signin - Smart Contact Manager");
        return "signin";
    }
}