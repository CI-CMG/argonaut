create table ${schema_name}.ocean
(
    name  varchar(40)
        constraint ocean_pk primary key,
    shape geometry(Geometry, 4326)
        constraint ocean_shape_nn not null
);

create index ocean_shape_idx on ${schema_name}.ocean using SPGIST (shape);