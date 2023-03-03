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

INSERT INTO public.plan (uuid, display_name, note, train_id, created_at, updated_at)
VALUES ('4556f410-d400-4bce-b8a3-baf255add4e0', 'SPARQL for COVID Portal',
        'This plan dispatches the COVID-SPARQL train to compatible stations...', 'a1eea8ce-e332-4e7b-b41d-880b53d709f4',
        '2022-04-09 21:04:13.000000', '2022-04-09 21:04:13.000000');

INSERT INTO public.plan_target (uuid, station_id, plan_id, created_at, updated_at)
VALUES ('c3602f76-8018-494d-834f-ccfc5959ca7c', 'a4d6cf81-1e7a-4666-88e7-26fbdd992674',
        '4556f410-d400-4bce-b8a3-baf255add4e0', '2022-04-09 21:04:50.000000', '2022-04-09 21:04:50.000000');
INSERT INTO public.plan_target (uuid, station_id, plan_id, created_at, updated_at)
VALUES ('edf65259-d79f-403c-83a9-452ea3905655', 'f41417ab-ef19-408f-ba69-5d3a1bc42e23',
        '4556f410-d400-4bce-b8a3-baf255add4e0', '2022-04-09 21:05:21.000000', '2022-04-09 21:05:21.000000');

INSERT INTO public.run (uuid, display_name, note, status, should_start_at, started_at, finished_at, plan_id, created_at,
                        updated_at)
VALUES ('53135c4a-fa97-49e8-8c55-5e9a3f10d8c4', 'SPARQL for COVID Portal #1', 'Trying this for the first time...',
        'FINISHED', null, '2022-04-09 21:07:07.000000', '2022-04-09 21:09:16.000000',
        '4556f410-d400-4bce-b8a3-baf255add4e0', '2022-04-09 21:07:26.000000', '2022-04-09 21:07:26.000000');

INSERT INTO public.job (uuid, secret, remote_id, status, started_at, finished_at, plan_target_id, run_id, created_at,
                        updated_at)
VALUES ('633879bd-df36-4c93-b455-6b9a56321771', 'verySecret', 'someRemoteId', 'FINISHED', '2022-04-09 21:10:43.000000',
        '2022-04-09 22:03:47.000000', 'c3602f76-8018-494d-834f-ccfc5959ca7c', '53135c4a-fa97-49e8-8c55-5e9a3f10d8c4',
        '2022-04-09 21:09:07.000000', '2022-04-09 21:09:07.000000');
INSERT INTO public.job (uuid, secret, remote_id, status, started_at, finished_at, plan_target_id, run_id, created_at,
                        updated_at)
VALUES ('0f8fa3ca-02b6-4cd3-b346-93b156166554', 'anotherSecret', 'anotherRemoteId', 'FINISHED',
        '2022-04-09 21:15:40.000000', '2022-04-09 21:56:44.000000', 'edf65259-d79f-403c-83a9-452ea3905655',
        '53135c4a-fa97-49e8-8c55-5e9a3f10d8c4', '2022-04-09 21:09:56.000000', '2022-04-09 21:09:56.000000');

INSERT INTO public.job_event (uuid, type, result_status, occurred_at, message, payload, job_id, created_at, updated_at)
VALUES ('0fc29c4a-f099-46a7-8d66-90cf76eef59b', 'INFO', 'QUEUED', '2022-04-09 21:18:36.000000',
        'Train queued in the data station', '{}', '633879bd-df36-4c93-b455-6b9a56321771', '2022-04-09 21:19:01.000000',
        '2022-04-09 21:19:01.000000');
INSERT INTO public.job_event (uuid, type, result_status, occurred_at, message, payload, job_id, created_at, updated_at)
VALUES ('e035028e-57ab-4baa-adff-2aa5e1fb04c2', 'INFO', 'RUNNING', '2022-04-09 21:20:00.000000',
        'Started processing the train', '{}', '633879bd-df36-4c93-b455-6b9a56321771', '2022-04-09 21:20:27.000000',
        '2022-04-09 21:20:27.000000');
INSERT INTO public.job_event (uuid, type, result_status, occurred_at, message, payload, job_id, created_at, updated_at)
VALUES ('4fe592ca-b36a-46e5-8f3b-c7b169eb0269', 'INFO', null, '2022-04-09 21:20:52.000000',
        'Checking data access permissions', '{}', '633879bd-df36-4c93-b455-6b9a56321771', '2022-04-09 21:21:17.000000',
        '2022-04-09 21:21:17.000000');
INSERT INTO public.job_event (uuid, type, result_status, occurred_at, message, payload, job_id, created_at, updated_at)
VALUES ('0c6f3cca-f1a7-46db-874b-955cdd3abccc', 'INFO', null, '2022-04-09 21:21:53.000000',
        'Access granted, querying data', '{}', '633879bd-df36-4c93-b455-6b9a56321771', '2022-04-09 21:22:17.000000',
        '2022-04-09 21:22:17.000000');
INSERT INTO public.job_event (uuid, type, result_status, occurred_at, message, payload, job_id, created_at, updated_at)
VALUES ('80b6bfbb-d128-4af2-ac90-849b3574735b', 'INFO', 'FINISHED', '2022-04-09 21:22:49.000000',
        'Query executed successfully', '{}', '633879bd-df36-4c93-b455-6b9a56321771', '2022-04-09 21:23:08.000000',
        '2022-04-09 21:23:08.000000');
INSERT INTO public.job_event (uuid, type, result_status, occurred_at, message, payload, job_id, created_at, updated_at)
VALUES ('7a35a0a8-ed96-48a8-84e0-9718778a8f21', 'ARTEFACT', null, '2022-04-09 21:22:50.000000',
        'Sending the result to handler', '{
    "contentType": "text/plain",
    "base64": false,
    "content": "Hello, world!"
  }', '633879bd-df36-4c93-b455-6b9a56321771', '2022-04-09 21:25:13.000000', '2022-04-09 21:25:13.000000');
