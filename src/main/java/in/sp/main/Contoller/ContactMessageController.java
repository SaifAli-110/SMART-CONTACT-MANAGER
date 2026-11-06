package in.sp.main.Contoller;

import in.sp.main.Helper.Message;
import in.sp.main.Model.ContactMessage;
import in.sp.main.Repository.ContactMessageRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ContactMessageController {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("title", "Contact - Smart Contact Manager");
        model.addAttribute("contactMessage", new ContactMessage());
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContactMessage(@Valid @ModelAttribute("contactMessage")
                                           ContactMessage contactMessage,
                                       BindingResult bindingResult,
                                       Model model){
        try{
//            validation
            if (bindingResult.hasErrors()) {
                System.out.println("Binding Result Error : " + bindingResult.toString());
                model.addAttribute("contactMessage", contactMessage);
                return "contact";
            }
            contactMessageRepository.save(contactMessage);
            model.addAttribute("contactMessage", new ContactMessage());
            model.addAttribute("SuccessMessage", new Message("Contact Message Saved","alert-success"));
            return "contact";

        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("contactMessage", contactMessage);
            model.addAttribute("ErrorMessage", new Message("Contact Message Couldn't Saved", "alert-danger"));
            return "contact";
        }
    }

}
