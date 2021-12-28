create table if not exists users
(
    id         bigint auto_increment primary key,
    email      varchar(255)                 not null,
    first_name varchar(50)                  not null,
    last_name  varchar(100)                 not null,
    password   varchar(255)                 not null,
    role       varchar(20) default 'USER'   not null,
    status     varchar(20) default 'ACTIVE' null,
    constraint users_email_uindex unique (email)
);