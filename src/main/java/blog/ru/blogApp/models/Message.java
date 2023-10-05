package blog.ru.blogApp.models;

import blog.ru.blogApp.utils.MessageHelper;
import jakarta.persistence.*;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Length(max = 2000, message = "Length should be less than 2000 characters!")
    @Column(name = "text")
    private String text;

    //@Pattern(regexp = "#[A-Za-z]\\w+", message = "Tag should be like \"#TAG\"!")
    //@Size(min = 1, max = 20, message = "Tag should be 1 < tag < 20!")
    //@NotEmpty(message = "Tag should not be empty")
    @Length(max = 100, message = "Tag should be less than 100 characters!")
    @Column(name = "tag")
    private String tag;

    @Column(name = "filename")
    private String filename;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    @ManyToMany
    @JoinTable(
            name = "message_likes",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")}
    )
    private Set<Person> likes = new HashSet<>();


    public Message(){

    }

    public Message(String text, String tag) {
        this.text = text;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Person getPerson() {
        return person;
    }

    public String getPersonName(){
        return MessageHelper.getPersonName(person);
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Set<Person> getLikes() {
        return likes;
    }

    public void setLikes(Set<Person> likes) {
        this.likes = likes;
    }
}
