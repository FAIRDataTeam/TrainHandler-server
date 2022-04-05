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

CREATE TABLE IF NOT EXISTS train_instance
(
    uuid         UUID      NOT NULL
        CONSTRAINT train_instance_pkey PRIMARY KEY,
    display_name TEXT      NOT NULL,
    note         TEXT      NOT NULL,
    train_id     UUID      NOT NULL,
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP NOT NULL
);

ALTER TABLE train_instance
    ADD CONSTRAINT train_instance_train_fk FOREIGN KEY (train_id) REFERENCES train (uuid);

CREATE TABLE IF NOT EXISTS train_run
(
    uuid              UUID        NOT NULL
        CONSTRAINT train_run_pkey PRIMARY KEY,
    status            VARCHAR(50) NOT NULL,
    station_id        UUID        NOT NULL,
    train_instance_id UUID        NOT NULL,
    created_at        TIMESTAMP   NOT NULL,
    updated_at        TIMESTAMP   NOT NULL
);

ALTER TABLE train_run
    ADD CONSTRAINT train_run_station_fk FOREIGN KEY (station_id) REFERENCES station (uuid);

ALTER TABLE train_run
    ADD CONSTRAINT train_run_train_instance_fk FOREIGN KEY (train_instance_id) REFERENCES train_instance (uuid);
