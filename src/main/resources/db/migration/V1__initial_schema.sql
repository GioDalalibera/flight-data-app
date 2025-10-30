create table flight (
   id                       bigint primary key,
   airline_name             varchar(60) not null,
   supplier_name            varchar(60) not null,
   ticket_fare_cents        bigint not null check ( ticket_fare_cents >= 0 ),
   departure_airport_code   char(3) not null,
   destination_airport_code char(3) not null,
   departure_time_utc       timestamptz not null,
   arrival_time_utc         timestamptz not null,
   constraint chk_airports_differ check ( departure_airport_code <> destination_airport_code ),
   constraint chk_time_order check ( departure_time_utc < arrival_time_utc )
);