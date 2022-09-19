CREATE TABLE CRYPTO(
  ID BIGSERIAL PRIMARY KEY,
  TSTAMP TIMESTAMP NOT NULL,
  SYMBOL VARCHAR(10) NOT NULL,
  PRICE  NUMERIC(20, 3) NOT NULL
);

CREATE INDEX TSTAMP_INDEX ON CRYPTO(TSTAMP, SYMBOL, PRICE);
CREATE INDEX SYMB_INDEX ON CRYPTO(SYMBOL)