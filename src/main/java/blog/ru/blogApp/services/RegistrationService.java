package blog.ru.blogApp.services;

import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.repositories.PeopleRepository;
import blog.ru.blogApp.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;

    @Autowired
    public RegistrationService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder, MailService mailService) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    private String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    public boolean register(Person person){

        Optional<Person> personFromDb = peopleRepository.findByUsername(person.getUsername());

        if (personFromDb.isPresent()) {
            return false;
        }

        person.setPassword(encodePassword(person.getPassword()));
        person.setRoles(Collections.singleton(Role.ROLE_USER));
        person.setActivationCode(UUID.randomUUID().toString());

        peopleRepository.save(person);

        sendMessage(person);

        return true;
    }

    public void update(
            Person person, String username, int age,  String password, String email){

        String userEmail = person.getEmail();

        boolean isEmailChanged =  ((email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email)));

        if (isEmailChanged){
            person.setEmail(email);
            if (!email.isEmpty()){
                person.setActivationCode(UUID.randomUUID().toString());
                person.setActive(false);
            }

        }

        if (!password.isEmpty()){
            person.setPassword(encodePassword(password));
        }

        person.setUsername(username);
        person.setAge(age);
        peopleRepository.save(person);

        if (isEmailChanged){
            sendMessage(person);
        }


    }

    @Transactional
    public void sendMessage(Person person) {
        if (!person.getEmail().isEmpty()){
            String message = String.format(
                    "Hello, %s \n" +
                            "Welcome to GachiVK. Please, visit link to activate your account: \n" +
                            "http://localhost:8080/auth/activate/%s",
                    person.getUsername(),
                    person.getActivationCode()
            );

            mailService.sendEmail(person.getEmail(), "Activation code", message);
        }

    }



    }




