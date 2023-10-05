package blog.ru.blogApp.models;

import blog.ru.blogApp.security.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Size(min = 1, max = 100, message = "Name should contain 1..100 characters!")
    @NotEmpty(message = "Name should not be empty!")
    @Column(name = "username")
    private String username;

    @Min(value = 10, message = "Your age should be greater than 10!")
    @Column(name = "age")
    private int age;

    @Size(min = 4, max = 500, message = "Password should be more than 3 characters and less than 500!")
    @NotEmpty(message = "Password cannot be empty!")
    @Column(name = "password")
    private String password;


    @Column(name = "active")
    private boolean isActive;

    @Column(name = "banned")
    private boolean isBanned;

    @Column(name = "email")
    @Email(message = "Wrong pattern of email")
    @NotEmpty(message = "Email should not be empty!")
    private String email;

    @Column(name = "activation_code")
    private String activationCode;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    private Set<Role> roles;

    @OneToMany(mappedBy = "person")
    private List<Message> messages;

    @ManyToMany
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "subscriber_id")
    )
    private Set<Person> subscribers = new HashSet<>();


    @ManyToMany
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id")
    )
    private Set<Person> subscriptions = new HashSet<>();


    public Person(){

    }

    public Person(String username, int age) {
        this.username = username;
        this.age = age;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public boolean isAdmin(){
        return roles.contains(Role.ROLE_ADMIN);
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Set<Person> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<Person> subscribers) {
        this.subscribers = subscribers;
    }

    public Set<Person> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Person> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                ", password='" + password + '\'' +
                ", isActive=" + isActive +
                ", email='" + email + '\'' +
                '}';
    }
}
