create table photo
(
    photo_id         BINARY(16) not null,
    path             varchar(255),
    created_date     datetime(6),
    modified_date    datetime(6),
    review_review_id BINARY(16),
    primary key (photo_id)
) engine = InnoDB;

create table place
(
    place_id      BINARY(16) not null,
    address       varchar(255),
    created_date  datetime(6),
    modified_date datetime(6),
    primary key (place_id)
) engine = InnoDB;

create table point_history
(
    point_id      BINARY(16)   not null,
    type          varchar(255) not null,
    action        varchar(255) not null,
    value         integer      not null,
    created_date  datetime(6),
    modified_date datetime(6),
    review_id     BINARY(16),
    user_id       BINARY(16)   not null,
    primary key (point_id)
) engine = InnoDB;

create table review
(
    review_id     BINARY(16) not null,
    created_date  datetime(6),
    modified_date datetime(6),
    content       varchar(255),
    place_id      BINARY(16),
    user_id       BINARY(16),
    primary key (review_id)
) engine = InnoDB;

create table user
(
    user_id       BINARY(16) not null,
    created_date  datetime(6),
    modified_date datetime(6),
    mileage       integer    not null,
    primary key (user_id)
) engine = InnoDB;

alter table point_history
    add constraint point_history_user
        foreign key (user_id) references user (user_id);

alter table photo
    add constraint photo_review
        foreign key (review_review_id) references review (review_id);

alter table review
    add constraint review_place
        foreign key (place_id) references place (place_id);

alter table review
    add constraint review_user
        foreign key (user_id) references user (user_id);

alter table point_history
    add index (review_id, created_date);

# 인덱스 추가해야함
