create table app_role (role_id  bigserial not null,
    role_name varchar(30) not null,
    primary key (role_id)
);

create table app_user (user_id  bigserial not null,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    email varchar(128) not null,
    enabled boolean not null,
    encryted_password varchar(128) not null,
    first_name varchar(36),
    last_name varchar(36),
    user_name varchar(36) not null,
    primary key (user_id)
);

create table checker_job (job_id int8,
    checker_id int8 not null,
    primary key (checker_id)
);

create table job_logic (job_id  bigserial not null,
    job_checker int4,
    job_name varchar(128) not null,
    job_status int4 not null,
    job_value int4 not null,
    job_worker int4,
    primary key (job_id)
);

create table persistent_logins (series varchar(64) not null,
    last_used timestamp not null,
    token varchar(64) not null,
    username varchar(64) not null,
    primary key (series)
);

create table user_connections (userid varchar(255) not null,
    provideruserid varchar(255) not null,
    providerid varchar(255) not null,
    accesstoken varchar(512),
    displayname varchar(255),
    expiretime int8,
    imageurl varchar(512),
    profileurl varchar(512),
    rank int4 not null,
    refreshtoken varchar(512),
    secret varchar(512),
    primary key (userid, provideruserid, providerid)
);

create table user_role (role_id int8,
    user_id int8 not null,
    primary key (user_id)
);

create table verification_tokens (id  bigserial not null,
    created_date date,
    expiry_date date,
    token varchar(255),
    user_id int8 not null,
    primary key (id)
);

create table worker_job (job_id int8,
    worker_id int8 not null,
    primary key (worker_id)
);

alter table app_role add constraint APP_ROLE_UK unique (role_name);
alter table app_user add constraint APP_USER_UK unique (user_name);
alter table app_user add constraint APP_USER_UK2 unique (email);
alter table job_logic add constraint JOB_LOGIC_UK unique (job_name);
alter table checker_job add constraint FK21meb11umar0jexplopbkxi5d foreign key (job_id) references app_user;
alter table checker_job add constraint FKb3hes22wywg3nuc2v239tnmnp foreign key (checker_id) references job_logic;
alter table user_role add constraint FKp6m37g6n6c288s096400uw8fw foreign key (role_id) references app_role;
alter table user_role add constraint FKg7fr1r7o0fkk41nfhnjdyqn7b foreign key (user_id) references app_user;
alter table verification_tokens add constraint FK93spwk89ipbftmp48wmkjipql foreign key (user_id) references app_user;
alter table worker_job add constraint FKgpq2bma5m05ykvorv6xvf2tjk foreign key (job_id) references app_user;
alter table worker_job add constraint FKt7e5v55jb4awhdkstv9ej4mh9 foreign key (worker_id) references job_logic;
insert into app_role (role_name) values ('ROLE_USER');
insert into app_role (role_name) values ('ROLE_ADMIN');
insert into app_role (role_name) values ('ROLE_MANAGER');
