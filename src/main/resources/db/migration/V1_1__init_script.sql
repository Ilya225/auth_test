create table user_
(
  id                  bigserial not null
    constraint user__pkey
    primary key,
  account_non_expired boolean   not null,
  account_non_locked  boolean   not null,
  email               varchar(255)
    constraint uk_ha67cvlhy4nk1prswl5gj1y0y
    unique,
  enabled             boolean   not null,
  password            varchar(255)
);

alter table user_
  owner to username;

create table user_role
(
  id   bigserial not null
    constraint user_role_pkey
    primary key,
  role varchar(255)
    constraint uk_s21d8k5lywjjc7inw14brj6ro
    unique
);

alter table user_role
  owner to username;

create table user__user_role
(
  user_id bigint not null
    constraint fk26wr05fb5qvv0h4tgjuvcyhbc
    references user_,
  role_id bigint not null
    constraint fkljnjf3tceist5jsy6r1blmpou
    references user_role
);

alter table user__user_role
  owner to username;

create table verification_token
(
  id      serial not null
    constraint verification_token_pkey
    primary key,
  expired timestamp,
  token   varchar(255),
  user_id bigint not null
    constraint fkktflk7j6dyqw4xhummmbowanl
    references user_
);

alter table verification_token
  owner to username;