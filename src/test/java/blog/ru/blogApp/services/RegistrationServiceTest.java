package blog.ru.blogApp.services;

import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.repositories.PeopleRepository;
import blog.ru.blogApp.security.Role;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;
import java.util.Optional;


@SpringBootTest
class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;

    @MockBean
    private PeopleRepository peopleRepository;

    @Autowired
    private PeopleService peopleService;

    @MockBean
    private MailService mailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void registerSuccessTest() {
        Person person = new Person();
        person.setEmail("test@mail.com");

        boolean isCreated = registrationService.register(person);

        Assertions.assertTrue(isCreated);
        Assertions.assertNotNull(person.getActivationCode());
        Assertions.assertTrue(CoreMatchers.is(person.getRoles()).matches(Collections.singleton(Role.ROLE_USER)));

        Mockito.verify(peopleRepository, Mockito.times(1)).save(person);
        Mockito.verify(mailService, Mockito.times(1))
                .sendEmail(ArgumentMatchers.eq(person.getEmail()),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString());

                        //ArgumentMatchers.eq("Activation code"),
                        //ArgumentMatchers.contains("Welcome to GachiVK."));
    }

    @Test
    public void registerFailTest(){
        Person person = new Person();
        person.setUsername("Test");

        Mockito.doReturn(Optional.of(person))
                .when(peopleRepository)
                .findByUsername("Test");

        boolean isCreated = registrationService.register(person);

        Assertions.assertFalse(isCreated);

        Mockito.verify(peopleRepository, Mockito.times(0)).save(ArgumentMatchers.any(Person.class));
        Mockito.verify(mailService, Mockito.times(0))
                .sendEmail(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );

    }

    @Test
    public void activateUser() {
        Person person = new Person("Test", 28);

        person.setActivationCode("bingo!");

        Mockito.doReturn(Optional.of(person))
                .when(peopleRepository)
                .findByActivationCode("activate");

        boolean isUserActivated = peopleService.activateUser("activate");


        Assertions.assertTrue(isUserActivated);
        Assertions.assertNull(person.getActivationCode());

        Mockito.verify(peopleRepository, Mockito.times(1)).save(person);
    }

    @Test
    public void activatePersonFailure(){
        boolean isPersonActivated = peopleService.activateUser("my activate");

        Assertions.assertFalse(isPersonActivated);
        Mockito.verify(peopleRepository, Mockito.times(0)).save(ArgumentMatchers.any(Person.class));
    }

}