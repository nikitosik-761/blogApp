create table person (
    active boolean,
     age integer check (age>=10),
      banned boolean,
       id serial not null,
        username varchar(100),
         password varchar(500),
          activation_code varchar(255),
           email varchar(255) unique,
            primary key (id)

                    );

create table message (
    id serial not null,
     person_id integer,
      tag varchar(100),
       text varchar(2000),
        filename varchar(500),
         primary key (id));


create table user_role (
    user_id integer not null,
     roles varchar(255) check (roles in ('ROLE_USER','ROLE_ADMIN'))
                       );

create table user_subscriptions (
    channel_id integer not null,
     subscriber_id integer not null,
      primary key (channel_id, subscriber_id)
                                );

alter table if exists message add constraint FKrjg2ug55rdo338ks6514fw9qy foreign key (person_id) references person;
alter table if exists user_role add constraint FKs92q0x8xfwil0ac1k3ucsnr93 foreign key (user_id) references person;
alter table if exists user_subscriptions add constraint FKmnev58ng7sh12r27qgil73228 foreign key (channel_id) references person;
alter table if exists user_subscriptions add constraint FKb17cbmgr57u9rulc3ciji5291 foreign key (subscriber_id) references person;
