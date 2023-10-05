delete from user_role;
delete from person;

insert into person(id, username, age, password, active, banned, email, activation_code) values
(1,'user', 20, '$2a$10$baWHVDmbtPqtpGgQ2gGzgur5.0EHLl2JI4mjJFmW.V4EXD9GKbqXi', true, false, 'df@gmail.com', null),
(2,'user2', 22, '$2a$10$baWHVDmbtPqtpGgQ2gGzgur5.0EHLl2JI4mjJFmW.V4EXD9GKbqXi', true, false, 'sddfk@gmail.com', null);

insert into user_role(user_id, roles) values
(1, 'ROLE_USER'), (1, 'ROLE_ADMIN'), (2, 'ROLE_USER');