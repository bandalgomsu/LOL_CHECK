CREATE TABLE IF NOT EXISTS summoner(
    id bigint not null AUTO_INCREMENT,
    puuid varchar(255) not null,
    game_name varchar(20) not null,
    tag_line varchar(20) not null,
    recent_game timestamp(6),
    created_at timestamp(6) ,
    updated_at timestamp(6) ,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS users(
    id bigint not null AUTO_INCREMENT,
    email varchar(20) not null,
    password varchar(20) not null,
    created_at timestamp(6) ,
    updated_at timestamp(6) ,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS summoner_subscriber(
    id bigint not null AUTO_INCREMENT,
    summoner_id bigint not null,
    subscriber_id bigint not null,
    created_at timestamp(6) ,
    updated_at timestamp(6) ,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS device(
    id bigint not null AUTO_INCREMENT,
    device_token varchar(50) not null
    user_id bigint not null
    created_at timestamp(6) ,
    updated_at timestamp(6) ,
    primary key (id)
    );

ALTER TABLE summoner_subscriber ADD UNIQUE (summoner_id, subscriber_id);
