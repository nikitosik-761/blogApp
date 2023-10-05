delete from message;
insert into message(id, person_id, tag, text) values
(1,2,'my-tag','first'),
(2,2,'tag','second'),
(3,2,'my-tag','third'),
(4,2,'tag','fourth');

alter sequence message_id_seq restart with 10;

