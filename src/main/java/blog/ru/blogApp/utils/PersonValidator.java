package blog.ru.blogApp.utils;

import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        //todo Переписать здесь код для email

        int personId = person.getId();


        Optional<Person> foundByUsernamePerson = peopleService.findByUsername(person.getUsername());
        Optional<Person> foundByEmailPerson = peopleService.findByEmail(person.getEmail());



        if (foundByUsernamePerson.isPresent()){
            int foundPersonByUsernameId = foundByUsernamePerson.get().getId();
            if (personId != foundPersonByUsernameId){
                errors.rejectValue("username", "", "This name has already been taken");
            }
        }

        if (foundByEmailPerson.isPresent()){
            int foundPersonByEmailId = foundByEmailPerson.get().getId();
            if(personId != foundPersonByEmailId){
                errors.rejectValue("email","", "This email has already been taken!");
            }
        }


    }



}
