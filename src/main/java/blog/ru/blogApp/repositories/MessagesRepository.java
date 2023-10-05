package blog.ru.blogApp.repositories;

import blog.ru.blogApp.models.Message;
import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.models.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagesRepository extends JpaRepository<Message, Integer> {

    @Query("select new blog.ru.blogApp.models.dto.MessageDto(" +
            "m, " +
            " count(ml), " +
            " sum (case when ml = :person then 1 else 0 end) > 0" +
            ") " +
            " from Message m left join m.likes as ml " +
            " group by m")
    Page<MessageDto> findAll(Pageable pageable, @Param("person") Person person);
    @Query("select new blog.ru.blogApp.models.dto.MessageDto(" +
            "m, " +
            " count(ml), " +
            " sum (case when ml = :person then 1 else 0 end) > 0" +
            ") " +
            " from Message m left join m.likes as ml " +
            " where m.tag = :tag " +
            " group by m")
    Page<MessageDto> findByTagStartingWith(@Param("tag") String query, Pageable pageable, @Param("person") Person person);
    @Query("select new blog.ru.blogApp.models.dto.MessageDto(" +
            "m, " +
            " count(ml), " +
            " sum (case when ml = :person then 1 else 0 end) > 0" +
            ") " +
            " from Message m left join m.likes as ml " +
            " where m.person = :person " +
            " group by m")
    Page<MessageDto> findByPerson(Pageable pageable, @Param("person") Person person);

}
