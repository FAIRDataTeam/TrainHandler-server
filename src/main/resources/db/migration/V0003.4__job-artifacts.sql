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

ALTER TABLE job_event
    DROP COLUMN type;

ALTER TABLE job_event
    DROP COLUMN payload;

CREATE TYPE artifact_storage AS ENUM (
    'POSTGRES',
    'S3',
    'LOCALFS'
    );

CREATE CAST (character varying AS artifact_storage) WITH INOUT AS ASSIGNMENT;

CREATE TABLE IF NOT EXISTS job_artifact
(
    uuid           UUID               NOT NULL
        CONSTRAINT job_artifact_pk PRIMARY KEY,
    display_name   VARCHAR            NOT NULL,
    filename       VARCHAR            NOT NULL,
    bytesize       BIGINT             NOT NULL,
    hash           VARCHAR(64)        NOT NULL,
    content_type   VARCHAR            NOT NULL,
    storage        artifact_storage   NOT NULL,
    occurred_at    TIMESTAMP          NOT NULL,
    data           BYTEA,
    job_id         UUID               NOT NULL,
    created_at     TIMESTAMP          NOT NULL,
    updated_at     TIMESTAMP          NOT NULL
);

ALTER TABLE ONLY job_artifact
    ADD CONSTRAINT job_artifact_job_fk FOREIGN KEY (job_id) REFERENCES job (uuid);
