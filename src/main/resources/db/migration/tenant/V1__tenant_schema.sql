create table accounts (
                          id uuid not null,
                          created_at timestamp(6) not null,
                          updated_at timestamp(6),
                          ifsc varchar(255) not null,
                          account_number varchar(20) not null,
                          account_type varchar(255) not null check ((account_type in ('SAVINGS','CURRENT','LOAN','WALLET','NRE','DEMAT'))),
                          customer_number varchar(255) not null,
                          primary key (id)
);

create table compliance_investigation_assignment (
                        id uuid not null,
                        created_at timestamp(6) not null,
                        updated_at timestamp(6),
                        is_open boolean,
                        risk_score numeric(38,2),
                        compliance_officer_id uuid,
                        customer_number varchar(255),
                        primary key (id)
);
create table authority (
                           id uuid not null,
                           created_at timestamp(6) not null,
                           updated_at timestamp(6),
                           authority_name varchar(255) check ((authority_name in ('MAKER','CHECKER'))),
                           primary key (id)
);
create table authority_mapper (
                                  id uuid not null,
                                  created_at timestamp(6) not null,
                                  updated_at timestamp(6),
                                  authority_id uuid,
                                  co_officer_id uuid,
                                  primary key (id)
);
create table bank_admins (
                             id uuid not null,
                             created_at timestamp(6) not null,
                             updated_at timestamp(6),
                             email varchar(255) not null,
                             failed_login_attempts integer not null,
                             is_locked boolean not null,
                             lock_time timestamp(6),
                             password varchar(255) not null,
                             primary key (id)
);

create table broken_rules (
                              id uuid not null,
                              created_at timestamp(6) not null,
                              updated_at timestamp(6),
                              active boolean,
                              false_positive boolean,
                              group_id uuid,
                              customer_number varchar(255) not null,
                              selected_rule_id uuid not null,
                              transaction_id uuid not null,
                              primary key (id)
);
create table cases (
                       id uuid not null,
                       created_at timestamp(6) not null,
                       updated_at timestamp(6),
                       case_description varchar(1000),
                       case_name varchar(255) not null,
                       case_status varchar(255) not null check ((case_status in ('OPEN','ASSIGNED','CLOSED','ESCALATED'))),
                       investigated_customer uuid not null,
                       primary key (id)
);
create table compliance_officers (
                                     id uuid not null,
                                     created_at timestamp(6) not null,
                                     updated_at timestamp(6),
                                     email varchar(255) not null,
                                     failed_login_attempts integer not null,
                                     is_locked boolean not null,
                                     is_suspended boolean not null,
                                     lock_time timestamp(6),
                                     password varchar(255) not null,
                                     primary key (id)
);
create table customers (
                           id uuid not null,
                           created_at timestamp(6) not null,
                           updated_at timestamp(6),
                           country_of_birth varchar(3) not null,
                           customer_number varchar(255) not null,
                           dob date not null,
                           family_code varchar(255),
                           first_name varchar(255) not null,
                           income numeric(19,4) not null,
                           last_name varchar(255) not null,
                           middle_name varchar(255),
                           nationality_country varchar(3) not null,
                           net_worth numeric(19,4) not null,
                           occupation varchar(255) not null,
                           primary key (id)
);

create table selected_rules (
                                id uuid not null,
                                created_at timestamp(6) not null,
                                updated_at timestamp(6),
                                description varchar(255),
                                parameters jsonb,
                                rule_code varchar(255) not null,
                                weight integer,
                                primary key (id)
);

create table transactions (
                              id uuid not null,
                              created_at timestamp(6) not null,
                              updated_at timestamp(6),
                              ifsc varchar(255) not null,
                              account_type varchar(255) not null check ((account_type in ('SAVINGS','CURRENT','LOAN','WALLET','NRE','DEMAT'))),
                              amount numeric(19,4) not null,
                              country varchar(3) not null,
                              direction varchar(255) not null check ((direction in ('CR','DR'))),
                              evaluated boolean default false not null,
                              transaction_number varchar(255) not null,
                              txn_time timestamp(6) not null,
                              txn_type varchar(255) not null check ((txn_type in ('CASH_DEPOSIT','NEFT','UPI','IMPS','ATM','RTGS'))),
                              account_number varchar(20) not null,
                              customer_number varchar(255) not null,
                              primary key (id)
);
alter table if exists accounts
drop constraint if exists uk_account_number;

alter table if exists accounts
    add constraint uk_account_number unique (account_number);

alter table if exists compliance_investigation_assignment
drop constraint if exists UK94eehv22l33uvmut9jvb61nr3;

alter table if exists compliance_investigation_assignment
    add constraint UK94eehv22l33uvmut9jvb61nr3 unique (customer_number);

alter table if exists bank_admins
drop constraint if exists UKjchx5c1ch69p23ybse300w1qk;

alter table if exists bank_admins
    add constraint UKjchx5c1ch69p23ybse300w1qk unique (email);

alter table if exists cases
drop constraint if exists UKaaa78ncvyqn4wo8uty1a766tc;

alter table if exists cases
    add constraint UKaaa78ncvyqn4wo8uty1a766tc unique (investigated_customer);

alter table if exists compliance_officers
drop constraint if exists UKqhk9hcyhhnv3pue19ikp8d8l3;

alter table if exists compliance_officers
    add constraint UKqhk9hcyhhnv3pue19ikp8d8l3 unique (email);

alter table if exists customers
drop constraint if exists UKt74y58jagthxqxysuw9l0jx6y;

alter table if exists customers
    add constraint UKt74y58jagthxqxysuw9l0jx6y unique (customer_number);

alter table if exists transactions
drop constraint if exists UK3w93192dhkdixcb3xncuf84pj;

alter table if exists transactions
    add constraint UK3w93192dhkdixcb3xncuf84pj unique (transaction_number);

alter table if exists accounts
    add constraint FKpkh474o4gfwkrygw1bksoe13a
    foreign key (customer_number)
    references customers (customer_number);

alter table if exists compliance_investigation_assignment
    add constraint FKavxn2q8sy21pm7bujba18uf0l
    foreign key (compliance_officer_id)
    references compliance_officers;

alter table if exists compliance_investigation_assignment
    add constraint FK4lyhp3jd4mmj51xh57afx62ts
    foreign key (customer_number)
    references customers (customer_number);

alter table if exists authority_mapper
    add constraint FKdmteynf7sieidylxr4o4d702g
    foreign key (authority_id)
    references authority;

alter table if exists authority_mapper
    add constraint FK8u6l5bxuww6afs1imvcnvvh2r
    foreign key (co_officer_id)
    references compliance_officers;

alter table if exists broken_rules
    add constraint FK4i05sthc21v22nl2ldg1etdry
    foreign key (customer_number)
    references customers (customer_number);

alter table if exists broken_rules
    add constraint FKdnoxurpbrxlv3abm32bc3qwql
    foreign key (selected_rule_id)
    references selected_rules;

alter table if exists broken_rules
    add constraint FK90pg57ph9ghub4qyneaq3opfi
    foreign key (transaction_id)
    references transactions;

alter table if exists cases
    add constraint FKn7uf36iinopxwtor9o8n4his3
    foreign key (investigated_customer)
    references compliance_investigation_assignment;

alter table if exists transactions
    add constraint FK2u05e77l3gh3l82xcjk2iyhr2
    foreign key (account_number)
    references accounts (account_number);

alter table if exists transactions
    add constraint FKpdsuhyf6271ychpdxvce6krcx
    foreign key (customer_number)
    references customers (customer_number);