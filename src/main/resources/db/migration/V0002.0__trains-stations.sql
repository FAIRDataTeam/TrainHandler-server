--
-- The MIT License
-- Copyright © 2022 FAIR Data Team
--
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
--
-- The above copyright notice and this permission notice shall be included in
-- all copies or substantial portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
-- THE SOFTWARE.
--

ALTER TABLE station_directory
    RENAME COLUMN last_contact TO last_contact_at;
ALTER TABLE train_garage
    RENAME COLUMN last_contact TO last_contact_at;

CREATE TABLE IF NOT EXISTS train_type
(
    uuid       UUID      NOT NULL
        CONSTRAINT train_type_pk PRIMARY KEY,
    uri        TEXT      NOT NULL,
    title      TEXT      NOT NULL,
    note       TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS train
(
    uuid            UUID      NOT NULL
        CONSTRAINT train_pk PRIMARY KEY,
    uri             TEXT      NOT NULL,
    title           TEXT      NOT NULL,
    description     TEXT      NOT NULL,
    keywords        TEXT,
    metadata        TEXT,
    status          VARCHAR(50),
    last_contact_at TIMESTAMP,
    train_garage_id UUID      NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);

ALTER TABLE train
    ADD CONSTRAINT train_train_garage_fk FOREIGN KEY (train_garage_id) REFERENCES train_garage (uuid);

CREATE TABLE IF NOT EXISTS station
(
    uuid                 UUID      NOT NULL
        CONSTRAINT station_pk PRIMARY KEY,
    uri                  TEXT      NOT NULL,
    title                TEXT      NOT NULL,
    description          TEXT      NOT NULL,
    keywords             TEXT,
    metadata             TEXT,
    status               VARCHAR(50),
    last_contact_at      TIMESTAMP,
    station_directory_id UUID      NOT NULL,
    created_at           TIMESTAMP NOT NULL,
    updated_at           TIMESTAMP NOT NULL
);

ALTER TABLE station
    ADD CONSTRAINT station_station_directory_fk FOREIGN KEY (station_directory_id) REFERENCES station_directory (uuid);


CREATE TABLE IF NOT EXISTS train_train_type
(
    train_type_id uuid NOT NULL,
    train_id      uuid NOT NULL
);

ALTER TABLE ONLY train_train_type
    ADD CONSTRAINT train_train_type_pk PRIMARY KEY (train_type_id, train_id);

ALTER TABLE ONLY train_train_type
    ADD CONSTRAINT train_train_type_train_type_fk FOREIGN KEY (train_type_id) REFERENCES train_type (uuid);

ALTER TABLE ONLY train_train_type
    ADD CONSTRAINT train_train_type_train_fk FOREIGN KEY (train_id) REFERENCES train (uuid);


CREATE TABLE IF NOT EXISTS station_train_type
(
    train_type_id uuid NOT NULL,
    station_id    uuid NOT NULL
);

ALTER TABLE ONLY station_train_type
    ADD CONSTRAINT station_train_type_pk PRIMARY KEY (train_type_id, station_id);

ALTER TABLE ONLY station_train_type
    ADD CONSTRAINT station_train_type_train_type_fk FOREIGN KEY (train_type_id) REFERENCES train_type (uuid);

ALTER TABLE ONLY station_train_type
    ADD CONSTRAINT station_train_type_station_fk FOREIGN KEY (station_id) REFERENCES station (uuid);
