package in.sp.main.Contoller;

import in.sp.main.Helper.Message;
import in.sp.main.Model.Contact;
import in.sp.main.Model.ContactMessage;
import in.sp.main.Model.User;
import in.sp.main.Repository.ContactMessageRepository;
import in.sp.main.Repository.ContactRepository;
import in.sp.main.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @ModelAttribute
    public void AddCommonData(Model model, Principal principal) {
//        get the user name like userEmail from the database using principal
        String userName = principal.getName();
//        get the user data from the database with the help of userEmail -> userName
        User user = userRepository.findByUserEmail(userName);
//        set the data in model to print in the User DashBoard.
        model.addAttribute("user", user);
    }

    @ModelAttribute
    public void addCurrentUrl(Model model, HttpServletRequest request) {
        model.addAttribute("currentUrl", request.getRequestURI());
    }

    //    HOME - DashBoard
    @GetMapping("/dashboard")
    public String OpenUserDashBoard(Model model, Principal principal) {
        model.addAttribute("title", "Home - Smart Contact Management");
        return "normal/UserDashBoard";
    }

    //    |************| Contact Us  |**************|
    @GetMapping("/contact-us")
    public String OpenContactUsPage(Model model){
        model.addAttribute("title", "Contact - Smart Contact Manager");
        model.addAttribute("contactMessage", new ContactMessage());
        return "normal/ContactUsPage";
    }

    @PostMapping("/contact-us")
    public String SubmitContact(
            @Valid
            @ModelAttribute("contactMessage")
            ContactMessage contactMessage,
            BindingResult bindingResult,
            Model model){

        try{
//            validation
            if (bindingResult.hasErrors()) {
                System.out.println("Binding Result Error : " + bindingResult.toString());
                model.addAttribute("contactMessage", contactMessage);
                return "normal/ContactUsPage";
            }
            contactMessageRepository.save(contactMessage);
            model.addAttribute("contactMessage", new ContactMessage());
            model.addAttribute("SuccessMessage", new Message("Contact Message Sent...","alert-success"));
            return "normal/ContactUsPage";

        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("contactMessage", contactMessage);
            model.addAttribute("ErrorMessage", new Message("Contact Message Couldn't Sent...", "alert-danger"));
            return "normal/ContactUsPage";
        }
    }

    //    View Contact
    @GetMapping("/view-contact/{page}")
    public String OpenViewContact(@PathVariable("page") Integer page, Model model, Principal principal) {

        model.addAttribute("title", "View Contact - Smart Contact Management");
        String userName = principal.getName();
        User user = this.userRepository.findByUserEmail(userName);

//    for pagination
        PageRequest pageable = PageRequest.of(page, 5);
        Page<Contact> contact = this.contactRepository.findContactByUser(user.getUserId(), pageable);

//    contacts are available.
        if (contact.isEmpty()) {
            model.addAttribute("message", "No contacts found!");
        } else {
            model.addAttribute("message", "");
        }
        model.addAttribute("contact", contact);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contact.getTotalPages());
        return "normal/ViewContact";
    }

    //    showing particular contact details -> click on the email and see the a;; details about the contact.
    @GetMapping("/{contactId}/contact")
    public String showParticularContacts(@PathVariable("contactId") Integer contactId, Model model,
                                         Principal principal) {

//        find the contact from the contact repository
        Optional<Contact> optionalContact = this.contactRepository.findById(contactId);
//        get the contact from the contact class and optionalContact
        Contact contact = optionalContact.get();

//        if a user log in so the user access own contact otherwise he can't other login user
//        find the username like email from the login in user
        String userName = principal.getName();
//        find the user details from the user repository
        User user = this.userRepository.findByUserEmail(userName);

//        check
        if (user.getUserId() == contact.getUser().getUserId()) {
            //        add contact inside the model attribute.
            model.addAttribute("contact", contact);
        }
        return "normal/ShowParticularContact";
    }

    //    |**********************| Add Contact Handler |***********************|
    @GetMapping("/add-contact")
    public String OpenAddContactForm(Model model) {

        model.addAttribute("title", "Add Contact - Smart Contact Management");
        model.addAttribute("contact", new Contact());
        return "normal/AddContactForm";
    }

    //   |**********************| Add Contact From handler |**********************|
    @PostMapping("/add-contact")
    public String OpenAddContactsForm(
            @Valid
            @ModelAttribute("contact") Contact contact,
            BindingResult bindingResult,
            @RequestParam(value = "contactImage", required = false) MultipartFile contactImage,
            Principal principal, Model model) {

        try {
//            validation
            if (bindingResult.hasErrors()) {
                System.out.println("Binding Result Error : " + bindingResult.toString());
                model.addAttribute("contact", contact);
                return "normal/AddContactForm";
            }
//            get the name from the main class.
            String userName = principal.getName();
            User user = userRepository.findByUserEmail(userName);

//            check the contact image is empty or not.
            if (contactImage.isEmpty()) {
//               you can upload the image
                System.out.println("File is Empty");
                contact.setContactImageUrl("contacts.png");
            } else {
//                upload the image
                contact.setContactImageUrl(contactImage.getOriginalFilename());
//                save on the image's folder.
                File saveFile = new ClassPathResource("static/images").getFile();

//                find a path of the file.
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + contactImage.getOriginalFilename());
//              upload the file on the real folder.
                Files.copy(contactImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Save profile image");
            }

//        1. save a user on the contact
            contact.setUser(user);
//        2. Add a contact ont the suer contact list.
            user.getContacts().add(contact);
//        3. Add a contact on the daa base.
            this.userRepository.save(user);

            model.addAttribute("contact", new Contact());
            model.addAttribute("Successmessage",
                    new Message("Contact Add Successfully", "alert-success"));
            return "normal/AddContactForm";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("contact", contact);
            model.addAttribute("Errormessage",
                    new Message("something went wrong",
                            e.getMessage() + "alert-danger"));
            return "normal/AddContactForm";
        }
    }

    //    |**********************| delete a contact |**********************|
    @PostMapping("/delete-contact/{contactId}")
    public String deleteContact(@PathVariable("contactId") int contactId, Principal principal) {

        User user = userRepository.findByUserEmail(principal.getName());
        Contact contact = contactRepository.findById(contactId).orElse(null);

        if (contact != null
                && contact.getUser() != null
                && contact.getUser().getUserId() == user.getUserId()) {
            contactRepository.delete(contact);
        }
        return "redirect:/api/users/view-contact/0";
    }

    //    **|| update contact handler ||**.
// Open Update Contact Form
    @GetMapping("/update-contact/{contactId}")
    public String showUpdateForm(
            @PathVariable("contactId") int contactId,
            Model model,
            Principal principal) {

        User user = userRepository.findByUserEmail(principal.getName());
        Contact contact = contactRepository.findById(contactId)
                .orElse(null);

        // Check that the contact belongs to the logged-in user
        if (contact == null || contact.getUser() == null ||
                contact.getUser().getUserId() != user.getUserId()) {

            return "redirect:/api/users/view-contact/0";
        }

        model.addAttribute("title", "Update Contact");
        model.addAttribute("contact", contact);

        return "normal/UpdateContactForm";
    }


    //   |*****************************| save update contact |*****************************|
// Save Updated Contact
    @PostMapping("/update-contact/{contactId}")
    public String updateContact(
            @PathVariable("contactId") int contactId,
            @ModelAttribute("contact") Contact formContact,
            @RequestParam(value = "contactImage", required = false)
            MultipartFile contactImage,
            Principal principal,
            Model model) {

        User user = userRepository.findByUserEmail(principal.getName());

        Contact existingContact = contactRepository.findById(contactId)
                .orElse(null);

        // Check contact ownership
        if (existingContact == null || existingContact.getUser() == null ||
                existingContact.getUser().getUserId() != user.getUserId()) {

            return "redirect:/api/users/view-contact/0";
        }

        try {
            // Update normal fields
            existingContact.setFirstName(formContact.getFirstName());
            existingContact.setLastName(formContact.getLastName());
            existingContact.setContactEmail(formContact.getContactEmail());
            existingContact.setContactPhone(formContact.getContactPhone());
            existingContact.setWork(formContact.getWork());
            existingContact.setContactDescription(
                    formContact.getContactDescription());

            // Update image only if a new image was selected
            if (contactImage != null && !contactImage.isEmpty()) {

                String fileName = java.util.UUID.randomUUID()
                        + "_" + org.springframework.util.StringUtils
                        .cleanPath(contactImage.getOriginalFilename());

                File directory = new ClassPathResource(
                        "static/images").getFile();

                Path path = Paths.get(
                        directory.getAbsolutePath(),
                        fileName);

                Files.copy(
                        contactImage.getInputStream(),
                        path,
                        StandardCopyOption.REPLACE_EXISTING);

                existingContact.setContactImageUrl(fileName);
            }

            // Save existing record, not a new Contact
            contactRepository.save(existingContact);

            return "redirect:/api/users/view-contact/0";

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("title", "Update Contact");
            model.addAttribute("contact", existingContact);
            model.addAttribute("Errormessage",
                    new Message("Unable to update contact.",
                            "alert-danger"));

            return "normal/UpdateContactForm";
        }
    }

    //    |******************| User Profile Page |******************|
    @GetMapping("/profile")
    public String OpenProfilePage(Model model, Principal principal) {

//        get the login user
        String userName = principal.getName();

//        find the user from the database
        User user = this.userRepository.findByUserEmail(userName);

//        add user in the model
        model.addAttribute("user", user);
        model.addAttribute("title", "Profile - Smart Contact Manager");
        return "normal/ProfilePage";
    }


    //    **************************| Update and See User Profile |********************************
    @GetMapping("/update-profile")
    public String updateProfile(Model model, Principal principal) {

        String userName = principal.getName();
        User user = this.userRepository.findByUserEmail(userName);

        model.addAttribute("user", user);
        model.addAttribute("title", "Update Profile - Smart Contact Manager");
        return "normal/UpdateProfile";
    }

    //    **************************| Update And save User Profile |********************************

    @PostMapping("/update-profile")
    public String updateProfile(
            @ModelAttribute("user") User formUser,
            @RequestParam(value = "userImage", required = false)
            MultipartFile userImage,
            Principal principal,
            Model model,
            HttpSession session) {

        if (principal == null) {
            return "redirect:/signin";
        }

        // Fetch the logged-in user
        User existingUser =
                userRepository.findByUserEmail(principal.getName());

        if (existingUser == null) {
            return "redirect:/signin";
        }

        try {
            // Validate name
            if (formUser.getUserName() == null ||
                    formUser.getUserName().trim().length() < 3 ||
                    formUser.getUserName().trim().length() > 20) {

                model.addAttribute("user", existingUser);
                model.addAttribute("title", "Edit Profile");
                model.addAttribute("errorMessage",
                        "Name must contain between 3 and 20 characters.");

                return "normal/UpdateProfile";
            }

            // Validate email
            if (formUser.getUserEmail() == null ||
                    !formUser.getUserEmail().trim().matches(
                            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                model.addAttribute("user", existingUser);
                model.addAttribute("title", "Edit Profile");
                model.addAttribute("errorMessage",
                        "Please enter a valid email address.");

                return "normal/UpdateProfile";
            }

            String newEmail = formUser.getUserEmail().trim();

            // Check if another user already has this email
            User emailUser = userRepository.findByUserEmail(newEmail);

            if (emailUser != null &&
                    emailUser.getUserId() != existingUser.getUserId()) {

                model.addAttribute("user", existingUser);
                model.addAttribute("title", "Edit Profile");
                model.addAttribute("errorMessage",
                        "This email address is already registered.");

                return "normal/UpdateProfile";
            }

            // Update basic profile details
            existingUser.setUserName(formUser.getUserName().trim());
            existingUser.setUserEmail(newEmail);
            existingUser.setUserAbout(formUser.getUserAbout());

            // Update password only when a new password is entered
            String newPassword = formUser.getUserPassword();

            if (newPassword != null && !newPassword.isBlank()) {

                boolean validPassword = newPassword.matches(
                        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$");

                if (!validPassword) {
                    model.addAttribute("user", existingUser);
                    model.addAttribute("title", "Edit Profile");
                    model.addAttribute("errorMessage",
                            "Password must contain an uppercase letter, " +
                                    "a lowercase letter, a digit and a special character.");

                    return "normal/UpdateProfile";
                }

                existingUser.setUserPassword(
                        passwordEncoder.encode(newPassword));
            }

            // Update profile image only if a new image is selected
            if (userImage != null && !userImage.isEmpty()) {

                String originalName = userImage.getOriginalFilename();
                String extension = "";

                if (originalName != null &&
                        originalName.lastIndexOf('.') >= 0) {

                    extension = originalName.substring(
                            originalName.lastIndexOf('.')).toLowerCase();
                }

                if (!extension.matches("\\.(jpg|jpeg|png|webp)")) {
                    model.addAttribute("user", existingUser);
                    model.addAttribute("title", "Edit Profile");
                    model.addAttribute("errorMessage",
                            "Only JPG, JPEG, PNG and WEBP images are allowed.");

                    return "normal/UpdateProfile";
                }

//                File imageDirectory =
//                        new File("src/main/resources/static/images");

                File imageDirectory = new ClassPathResource(
                        "static/images").getFile();

                if (!imageDirectory.exists() &&
                        !imageDirectory.mkdirs()) {

                    throw new java.io.IOException(
                            "Unable to create image directory.");
                }

                String imageName = UUID.randomUUID() + extension;

                Path imagePath = Paths.get(
                        imageDirectory.getAbsolutePath(), imageName);

                Files.copy(
                        userImage.getInputStream(),
                        imagePath,
                        StandardCopyOption.REPLACE_EXISTING);

                existingUser.setUserImageUrl(imageName);
            }

            // Save the existing user record
            userRepository.save(existingUser);
            return "normal/ProfilePage";
        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("user", existingUser);
            model.addAttribute("title", "Edit Profile");
            model.addAttribute("errorMessage",
                    "Unable to update profile. Please try again.");

            return "normal/UpdateProfile";
        }
    }


    // *********************|   Help Handler |******************
    @GetMapping("/Help")
    public String OpenHelpPage(Model model) {
        model.addAttribute("title", "Help - Smart Contact Management");
        return "normal/Help";
    }
}
