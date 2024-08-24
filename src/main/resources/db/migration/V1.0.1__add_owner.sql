alter table cash_card
    add column owner varchar(256) not null default 'unknown';
