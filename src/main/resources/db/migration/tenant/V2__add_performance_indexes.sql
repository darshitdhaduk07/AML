
CREATE INDEX idx_transactions_evaluated_false ON transactions(evaluated) WHERE evaluated = false;

CREATE INDEX idx_transactions_customer_time ON transactions(customer_number, txn_time);


CREATE INDEX idx_cases_status ON cases(case_status);
