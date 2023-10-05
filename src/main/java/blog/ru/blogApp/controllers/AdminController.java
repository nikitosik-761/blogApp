package blog.ru.blogApp.controllers;

import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.services.AdminService;
import blog.ru.blogApp.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final PeopleService peopleService;
    private final AdminService adminService;


    @Autowired
    public AdminController(PeopleService peopleService, AdminService adminService) {
        this.peopleService = peopleService;
        this.adminService = adminService;
    }



    @GetMapping
    public String userList(Model model){
        model.addAttribute("users", peopleService.findAll());
        return "admin/admin";
    }

    @GetMapping("/users/{id}")
    public String showUser(@PathVariable("id") int id, @ModelAttribute("person") Person person, Model model){
        model.addAttribute("foundPerson",peopleService.findOne(id));
        return "admin/show";
    }

    @PatchMapping("/users/{id}/assign")
    public String signAdmin(@PathVariable("id") int id){
        adminService.signAdmin(id);
        return "redirect:/admin";
    }

    @PatchMapping("/users/{id}/deprive")
    public String depriveAdmin(@PathVariable("id") int id){
        adminService.depriveAdmin(id);
        return "redirect:/admin";
    }
    @PatchMapping("/users/{id}/ban")
    public String ban(
            @PathVariable("id") int id
    ){
        adminService.banUser(id);
        return "redirect:/admin";
    }

    @PatchMapping("/users/{id}/unban")
    public String unban(
            @PathVariable("id") int id
    ){
        adminService.unbanUser(id);
        return "redirect:/admin";
    }


}
