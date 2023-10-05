package blog.ru.blogApp.repositories;

import blog.ru.blogApp.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByUsername(String username);



    Optional<Person> findByActivationCode(String code);

    Optional<Person> findByEmail(String email);

}
