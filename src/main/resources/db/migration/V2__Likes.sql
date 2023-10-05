create table message_likes(
    person_id int not null references person,
    message_id int not null references message,
    primary key (person_id, message_id)
)