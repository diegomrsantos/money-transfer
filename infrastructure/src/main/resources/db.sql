CREATE TABLE account (
  id bigint auto_increment PRIMARY KEY,
  user_id bigint,
  balance decimal (15, 2) DEFAULT 0.00
);

CREATE TABLE transfer (
  id bigint auto_increment PRIMARY KEY,
  from_account_id bigint,
  to_account_id bigint,
  amount decimal (15, 2),
  foreign key (from_account_id) references account(id),
  foreign key (to_account_id) references account(id)
);