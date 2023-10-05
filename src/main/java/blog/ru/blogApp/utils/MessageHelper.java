package blog.ru.blogApp.utils;

import blog.ru.blogApp.models.Person;

public class MessageHelper {
    public static String getPersonName(Person person){
        return  person!= null ? person.getUsername() : "<Anonymous>";
    }

}
