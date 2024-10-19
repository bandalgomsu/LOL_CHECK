CREATE TABLE IF NOT EXISTS summoner(
    id bigint not null,
    puuid varchar(50),
    game_name varchar(20),
    tag_line varchar(20),
    recent_game timestamp(6),
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS users(
    id bigint not null,
    email varchar(20),
    password varchar(20),
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS summoner_subscriber(
    id bigint not null,
    summoner_id bigint not null,
    user_id bigint not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    primary key (id)
);



