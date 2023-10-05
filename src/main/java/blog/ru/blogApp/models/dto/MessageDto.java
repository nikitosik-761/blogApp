package blog.ru.blogApp.models.dto;

import blog.ru.blogApp.models.Message;
import blog.ru.blogApp.models.Person;
import blog.ru.blogApp.utils.MessageHelper;

public class MessageDto {
    private int id;
    private String text;
    private String tag;
    private String filename;
    private Person person;
    private Long likes;
    private Boolean meLikes;

    public MessageDto(Message message, Long likes, Boolean meLikes) {
        this.id = message.getId();
        this.text = message.getText();
        this.text = message.getTag();
        this.filename = message.getFilename();
        this.person = message.getPerson();
        this.likes = likes;
        this.meLikes = meLikes;
    }

    public String getPersonName(){
        return MessageHelper.getPersonName(person);
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Boolean getMeLikes() {
        return meLikes;
    }

    public void setMeLikes(Boolean meLikes) {
        this.meLikes = meLikes;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "id=" + id +
                ", person=" + person +
                ", likes=" + likes +
                ", meLikes=" + meLikes +
                '}';
    }
}
