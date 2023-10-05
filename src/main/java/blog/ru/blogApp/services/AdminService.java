package blog.ru.blogApp.services;

import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.repositories.PeopleRepository;
import blog.ru.blogApp.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminService {
    private final PeopleRepository peopleRepository;
    private final MailService mailService;

    @Autowired
    public AdminService(PeopleRepository peopleRepository, MailService mailService) {
        this.peopleRepository = peopleRepository;
        this.mailService = mailService;
    }


    @Transactional
    public void signAdmin(int id){
        peopleRepository.findById(id).ifPresent(
                person -> {
                    person.getRoles().add(Role.ROLE_ADMIN);
                    System.out.println(person.getRoles());
                }
        );
    }

    @Transactional
    public void depriveAdmin(int id){
        peopleRepository.findById(id).ifPresent(
                person -> {
                    person.getRoles().clear();
                    person.setRoles(Collections.singleton(Role.ROLE_USER));
                }
        );
    }

    @Transactional
    public void banUser(int id){
        peopleRepository.findById(id).ifPresent(
                person -> {
                    person.setBanned(true);
                    //sendMessage(person, true);
                }
        );
    }

    @Transactional
    public void unbanUser(int id){
        peopleRepository.findById(id).ifPresent(
                person -> {
                    person.setBanned(false);
                    //sendMessage(person, false);
                }
        );
    }

    @Transactional
    public void sendMessage(Person person, boolean isBanned) {
        if (!person.getEmail().isEmpty()){
            String message;
            if (isBanned){
                message = String.format(
                        "Hello, %s \n" +
                                "Your account has been banned!",
                        person.getUsername()
                );
            }else {
                message = String.format(
                        "Hello, %s \n" +
                                "Your account has been unbanned!",
                        person.getUsername()
                );
            }


            mailService.sendEmail(person.getEmail(), "Notification", message);
        }

    }

}
