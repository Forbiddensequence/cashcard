alter table cash_card
    drop column owner;

delete
from flyway_schema_history
where version = '1.0.1';