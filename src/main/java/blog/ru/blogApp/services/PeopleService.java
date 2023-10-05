package blog.ru.blogApp.services;

import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.repositories.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PeopleService {

    private final PeopleRepository peopleRepository;


    @Autowired
    public PeopleService(PeopleRepository peopleRepository, RegistrationService registrationService) {
        this.peopleRepository = peopleRepository;
    }


    public List<Person> findAll(){
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> person = peopleRepository.findById(id);
        return person.orElse(null);
    }
    public Optional<Person> findByUsername(String username){
        return peopleRepository.findByUsername(username);
    }

    public Optional<Person> findByEmail(String email){
        return peopleRepository.findByEmail(email);
    }


    public boolean activateUser(String code) {

        Optional<Person> person = peopleRepository.findByActivationCode(code);

            if (person.isPresent()) {
                person.get().setActivationCode(null);
                person.get().setActive(true);

                peopleRepository.save(person.get());

                return true;
            }

            return false;
    }

    public void subscribe(Person currentPerson, Person person){
        person.getSubscribers().add(currentPerson);
        peopleRepository.save(person);
    }

    public void unsubscribe(Person currentPerson, Person person){
        person.getSubscribers().remove(currentPerson);
        peopleRepository.save(person);
    }

    public void delete(int id){
        peopleRepository.deleteById(id);
    }

}
