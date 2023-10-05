package blog.ru.blogApp.controllers;

import blog.ru.blogApp.models.Message;
import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.security.PersonDetails;
import blog.ru.blogApp.services.PeopleService;
import blog.ru.blogApp.services.RegistrationService;
import blog.ru.blogApp.utils.PersonValidator;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class PeopleController {
    private final PeopleService peopleService;
    private final RegistrationService registrationService;
    private final PersonValidator personValidator;


    @Autowired
    public PeopleController(PeopleService peopleService, RegistrationService registrationService, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.registrationService = registrationService;
        this.personValidator = personValidator;
    }


    @GetMapping
    public String profile(
            @AuthenticationPrincipal PersonDetails personDetails,
            Model model
            ){



        Person person = peopleService.findOne(personDetails.getPerson().getId());


        model.addAttribute("isBanned", personDetails.getPerson().isBanned());
        model.addAttribute("user", personDetails.getPerson());
        model.addAttribute("Subscriptions", person.getSubscriptions().size());
        model.addAttribute("Subscribers", person.getSubscribers().size());


        return "/profile/profile";
    }

    @GetMapping("/{id}")
    public String channel(
            @PathVariable("id") int id,
            @AuthenticationPrincipal PersonDetails personDetails,
            Model model
    ){
        Person person = peopleService.findOne(id);
        List<Message> messages = peopleService.findOne(id).getMessages();

        model.addAttribute("Myacc", personDetails.getPerson());
        model.addAttribute("Subscriptions", person.getSubscriptions().size());
        model.addAttribute("Subscribers", person.getSubscribers().size());
        model.addAttribute("isCurrentUser", personDetails.getPerson().equals(person));
        model.addAttribute("isSubscriber", person.getSubscribers().contains(personDetails.getPerson()));
        model.addAttribute("userChannel", person);
        model.addAttribute("messages", messages);

        return "/profile/channel";
    }

    @DeleteMapping("/{id}")
    public String deleteAccount(
            @PathVariable("id") int id,
            @AuthenticationPrincipal PersonDetails personDetails
    ){
        Person person = peopleService.findOne(id);
        if (personDetails.getPerson().equals(person)){
            peopleService.delete(id);
        }
        return "redirect:/";
    }

    @GetMapping("/{id}/subscribers")
    public String subscribers(
            @PathVariable("id") int id,
            Model model
    ){
        Person person = peopleService.findOne(id);
        Set<Person> subscribers = person.getSubscribers();
        model.addAttribute("subscribers", subscribers);

        return "/profile/subscribers";
    }

    @GetMapping("/{id}/subscriptions")
    public String subscriptions(
            @PathVariable("id") int id,
            Model model
    ){
        Person person = peopleService.findOne(id);
        Set<Person> subscriptions = person.getSubscriptions();
        model.addAttribute("subscriptions", subscriptions);

        return "/profile/subscriptions";
    }


    @PostMapping("/{id}/subscribe")
    public String subscribe(
            @PathVariable("id") int id,
            @AuthenticationPrincipal PersonDetails personDetails
    ){
        Person person = peopleService.findOne(id);
        peopleService.subscribe(personDetails.getPerson(), person);
        return "redirect:/user";
    }

    @PostMapping("/{id}/unsubscribe")
    public String unsubscribe(
            @PathVariable("id") int id,
            @AuthenticationPrincipal PersonDetails personDetails
    ){
        Person person = peopleService.findOne(id);
        peopleService.unsubscribe(personDetails.getPerson(), person);
        return "redirect:/user";
    }

    @GetMapping("{id}/edit")
    public String edit(
            @PathVariable("id") int id,
            Model model
    ){
       model.addAttribute("foundUser", peopleService.findOne(id));
       return "/profile/edit";
    }

    @PatchMapping("{id}/edit")
    public String update(
            @ModelAttribute("foundUser") @Valid Person person,
            BindingResult bindingResult,
            @AuthenticationPrincipal PersonDetails personDetails

    ){


     personValidator.validate(person, bindingResult);

     if (bindingResult.hasErrors()){
         return "/profile/edit";
     }

     registrationService.update(personDetails.getPerson(), person.getUsername(), person.getAge(), person.getPassword(), person.getEmail());

     return "redirect:/auth/login";

    }


}
