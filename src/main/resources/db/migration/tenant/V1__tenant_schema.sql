create table accounts (
                          id uuid not null,
                          created_at timestamp(6) not null,
                          updated_at timestamp(6),
                          account_number varchar(20) not null,
                          account_type varchar(255) not null check ((account_type in ('SAVINGS','CURRENT','LOAN','WALLET','NRE','DEMAT'))),
                          customer_number uuid not null,
                          primary key (id)
);

create table alerts (
                        id uuid not null,
                        created_at timestamp(6) not null,
                        updated_at timestamp(6),
                        risk_score numeric(38,2),
                        "case.id" uuid,
                        transaction_number uuid,
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
                              case_id uuid not null,
                              selected_rule_id uuid not null,
                              primary key (id)
);

create table cases (
                       id uuid not null,
                       created_at timestamp(6) not null,
                       updated_at timestamp(6),
                       case_description varchar(1000),
                       case_name varchar(255) not null,
                       case_status varchar(255) not null check ((case_status in ('OPEN','ASSIGNED','CLOSED','ESCALATED'))),
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
                                rule_template_id uuid,
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
                              transaction_number varchar(255) not null,
                              txn_time timestamp(6) not null,
                              txn_type varchar(255) not null check ((txn_type in ('CASH_DEPOSIT','NEFT','UPI','IMPS','ATM','RTGS'))),
                              account_number uuid not null,
                              customer_number uuid not null,
                              primary key (id)
);

alter table if exists accounts
drop constraint if exists unique_account_number;

alter table if exists accounts
    add constraint unique_account_number unique (account_number);

alter table if exists alerts
drop constraint if exists unique_case_id;

alter table if exists alerts
    add constraint unique_case_id unique ("case.id");

alter table if exists alerts
drop constraint if exists unique_alert_transaction_number;

alter table if exists alerts
    add constraint unique_alert_transaction_number unique (transaction_number);

alter table if exists bank_admins
drop constraint if exists unique_admin_email;

alter table if exists bank_admins
    add constraint unique_admin_email unique (email);

alter table if exists compliance_officers
drop constraint if exists unique_co_email;

alter table if exists compliance_officers
    add constraint unique_co_email unique (email);

alter table if exists customers
drop constraint if exists unique_customer_number;

alter table if exists customers
    add constraint unique_customer_number unique (customer_number);

alter table if exists selected_rules
drop constraint if exists unique_rule_code;

alter table if exists selected_rules
    add constraint unique_rule_code unique (rule_code);

alter table if exists transactions
drop constraint if exists unique_transaction_number;

alter table if exists transactions
    add constraint unique_transaction_number unique (transaction_number);

alter table if exists accounts
    add constraint fk_customer_number
    foreign key (customer_number)
    references customers;

alter table if exists alerts
    add constraint fk_case_id
    foreign key ("case.id")
    references cases;

alter table if exists alerts
    add constraint fk_transaction_number
    foreign key (transaction_number)
    references transactions;

alter table if exists broken_rules
    add constraint fk_case_id
    foreign key (case_id)
    references cases;

alter table if exists broken_rules
    add constraint fk_selected_rule_id
    foreign key (selected_rule_id)
    references selected_rules;

alter table if exists transactions
    add constraint fk_account_number
    foreign key (account_number)
    references accounts;

alter table if exists transactions
    add constraint fk_customer_number
    foreign key (customer_number)
    references customers;