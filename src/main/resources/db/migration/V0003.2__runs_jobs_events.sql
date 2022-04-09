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

CREATE TYPE run_status AS ENUM (
    'SCHEDULED',
    'PREPARED',
    'RUNNING',
    'FINISHED',
    'ABORTING',
    'ABORTED',
    'ERRORED',
    'FAILED'
    );

CREATE CAST (character varying AS run_status) WITH INOUT AS ASSIGNMENT;

CREATE TYPE job_status AS ENUM (
    'PREPARED',
    'QUEUED',
    'RUNNING',
    'FINISHED',
    'ABORTING',
    'ABORTED',
    'ERRORED',
    'FAILED'
    );

CREATE CAST (character varying AS job_status) WITH INOUT AS ASSIGNMENT;

CREATE TYPE job_event_type AS ENUM (
    'ARTEFACT',
    'INIT',
    'INFO'
    );

CREATE CAST (character varying AS job_event_type) WITH INOUT AS ASSIGNMENT;

CREATE TABLE IF NOT EXISTS run
(
    uuid            UUID         NOT NULL
        CONSTRAINT run_pk PRIMARY KEY,
    display_name    VARCHAR(255) NOT NULL,
    note            TEXT         NOT NULL,
    status          run_status   NOT NULL,
    should_start_at TIMESTAMP,
    started_at      TIMESTAMP,
    finished_at     TIMESTAMP,
    plan_id         UUID         NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL
);

ALTER TABLE ONLY run
    ADD CONSTRAINT run_plan_fk FOREIGN KEY (plan_id) REFERENCES plan (uuid);

CREATE TABLE IF NOT EXISTS job
(
    uuid           UUID         NOT NULL
        CONSTRAINT job_pk PRIMARY KEY,
    secret         VARCHAR(255) NOT NULL,
    remote_id      TEXT,
    status         job_status   NOT NULL,
    started_at     TIMESTAMP,
    finished_at    TIMESTAMP,
    plan_target_id UUID         NOT NULL,
    run_id         UUID         NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL
);

ALTER TABLE ONLY job
    ADD CONSTRAINT job_plan_target_fk FOREIGN KEY (plan_target_id) REFERENCES plan_target (uuid);

ALTER TABLE ONLY job
    ADD CONSTRAINT job_run_fk FOREIGN KEY (run_id) REFERENCES run (uuid);

CREATE TABLE IF NOT EXISTS job_event
(
    uuid          UUID           NOT NULL
        CONSTRAINT job_event_pk PRIMARY KEY,
    type          job_event_type NOT NULL,
    result_status job_status,
    occurred_at   TIMESTAMP,
    message       TEXT           NOT NULL,
    payload       JSON,
    job_id        UUID           NOT NULL,
    created_at    TIMESTAMP      NOT NULL,
    updated_at    TIMESTAMP      NOT NULL
);

ALTER TABLE ONLY job_event
    ADD CONSTRAINT job_event_job_fk FOREIGN KEY (job_id) REFERENCES job (uuid);



