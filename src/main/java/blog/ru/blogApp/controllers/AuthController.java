package blog.ru.blogApp.controllers;

import blog.ru.blogApp.models.dto.CaptchaResponseDTO;
import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.services.PeopleService;
import blog.ru.blogApp.services.RegistrationService;
import blog.ru.blogApp.utils.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;


@Controller
@RequestMapping("/auth")
public class AuthController {
    private static final String CAPTCHA_URL= "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Value("${recaptcha.secret}")
    private String secret;

    private final RegistrationService registrationService;
    private final PeopleService peopleService;
    private final PersonValidator personValidator;
    private final RestTemplate restTemplate;

    @Autowired
    public AuthController(RegistrationService registrationService, PeopleService peopleService, PersonValidator personValidator, RestTemplate restTemplate) {
        this.registrationService = registrationService;
        this.peopleService = peopleService;
        this.personValidator = personValidator;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/login")
    public String loginPage(){
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person){
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String performRegistration(
            @ModelAttribute("person") @Valid Person person,
            @RequestParam("g-recaptcha-response") String captchaResponse,
            BindingResult bindingResult,
            Model model
    ){
        String url = String.format(CAPTCHA_URL,secret, captchaResponse);
        CaptchaResponseDTO response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDTO.class);

        if (!response.isSuccess()){
            model.addAttribute("captchaError", "Fill captcha");
        }


        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors() || !response.isSuccess()){

            Map<String,String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "/auth/registration";
        }

        if (!registrationService.register(person)) {
            model.addAttribute("usernameError", "User exists!");
            return "/auth/registration";
        }


        return "redirect:/auth/login";

    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable("code") String code, Model model){

        boolean isActivated  = peopleService.activateUser(code);
        if (isActivated){
            model.addAttribute("message", "User successfully activated!");

        }else {
            model.addAttribute("message", "Activation code is not found");
        }

        return "auth/login";
    }


}
