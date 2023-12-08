package blog.ru.blogApp.services;

import blog.ru.blogApp.models.Message;
import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.models.dto.MessageDto;
import blog.ru.blogApp.repositories.MessagesRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class MessagesService {

    private final EntityManager entityManager;
    private final MessagesRepository messagesRepository;

    @Autowired
    public MessagesService(EntityManager entityManager, MessagesRepository messagesRepository){
        this.entityManager = entityManager;
        this.messagesRepository = messagesRepository;
    }

    public Page<MessageDto> findAll(Pageable pageable, Person person){
        return messagesRepository.findAll(pageable, person);
    }

    public Page<MessageDto> findByTag(String tag, Pageable pageable, Person person){
        return messagesRepository.findByTagStartingWith(tag, pageable, person);
    }

    public Optional<Message> findById(int id){
        return messagesRepository.findById(id);
    }
    @Transactional
    public void update(int id, Message updatedMessage){
        Optional<Message> message = messagesRepository.findById(id);
        if (message.isPresent()){

            if (message.get().getText() == null || message.get().getText().isBlank() || message.get().getText().isEmpty()){
                return;
            }

            if (updatedMessage.getTag() == null || updatedMessage.getTag().isEmpty()){
                updatedMessage.setTag("<none>");
            }

            message.get().setTag(updatedMessage.getTag());
            message.get().setText(updatedMessage.getText());

            if (!(updatedMessage.getFilename() == null)){
                message.get().setFilename(updatedMessage.getFilename());
            }

            messagesRepository.save(message.get());
        }
    }

    @Transactional
    public void save(Message message, Person person){
        if (message.getTag().isEmpty() || message.getTag() == null){
            message.setTag("<none>");
        }
        if (message.getText().isEmpty() || message.getText() == null){
            return;
        }
        message.setPerson(person);
        messagesRepository.save(message);
    }

    @Transactional
    public void delete(int id){
        messagesRepository.deleteById(id);
    }


    public Page<MessageDto> messageUserList(Pageable pageable, Person person) {
        return messagesRepository.findByPerson(pageable, person);
    }

    @Transactional
    public void doLike(int id, Person person){

        Optional<Message> message = messagesRepository.findById(id);

        if (message.isPresent()){
            Set<Person> likes = message.get().getLikes();

            if (likes.contains(person)){
                likes.remove(person);
            }else {
                likes.add(person);
            }

            messagesRepository.save(message.get());
        }

    }
}
