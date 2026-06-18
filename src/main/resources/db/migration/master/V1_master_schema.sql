create table master.admins (
                               id uuid not null,
                               created_at timestamp(6) not null,
                               updated_at timestamp(6),
                               email varchar(150) not null,
                               failed_login_attempts integer not null,
                               is_locked boolean not null,
                               lock_time timestamp(6),
                               password varchar(255) not null,
                               primary key (id)
);

create table master.blacklisted_tokens (
                                           id uuid not null,
                                           blacklisted_at timestamp(6) not null,
                                           expires_at timestamp(6) not null,
                                           jti uuid not null,
                                           tenant_id varchar(255),
                                           user_id uuid not null,
                                           primary key (id)
);

create table master.tenants (
                                id uuid not null,
                                created_at timestamp(6) not null,
                                updated_at timestamp(6),
                                db_name varchar(100) not null,
                                schema_name varchar(100) not null,
                                tenant_name varchar(100) not null,
                                tenant_status varchar(255) not null check ((tenant_status in ('ACTIVE','DISABLED'))),
                                primary key (id)
);

alter table if exists master.admins
drop constraint if exists unique_email;

alter table if exists master.admins
    add constraint unique_email unique (email);

alter table if exists master.blacklisted_tokens
drop constraint if exists unique_jti;

alter table if exists master.blacklisted_tokens
    add constraint unique_jti unique (jti);

alter table if exists master.tenants
drop constraint if exists unique_schema_name;

alter table if exists master.tenants
    add constraint unique_schema_name unique (schema_name);