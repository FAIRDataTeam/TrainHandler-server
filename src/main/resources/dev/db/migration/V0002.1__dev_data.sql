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

-- Station Directories
INSERT INTO station_directory (uuid, uri, display_name, note, metadata, status, last_contact_at, created_at, updated_at)
VALUES ('6dfc348a-3ceb-49b2-ac5a-eff83bdf7eae', 'http://example.com/fds/station-directory1',
        'Station Directory 1: Health', 'Note for Station Directory 1 which is an example...', '', 'SYNCED', NOW(),
        NOW(), NOW());

INSERT INTO station_directory (uuid, uri, display_name, note, metadata, status, last_contact_at, created_at, updated_at)
VALUES ('4475579a-0497-4b4d-babe-56f6b7a38ee7', 'http://example.com/fds/station-directory2',
        'Station Directory 2: COVID', 'Note for Station Directory 2 which is an example...', '', 'SYNCED', NOW(), NOW(),
        NOW());

INSERT INTO station_directory (uuid, uri, display_name, note, metadata, status, last_contact_at, created_at, updated_at)
VALUES ('1f50ad0e-aa8a-41b8-8934-d498ad0e2635', 'http://example.com/fds/station-directory3',
        'Station Directory 3: Random', 'Note for Station Directory 3 which is an example...', '', 'INVALID', NOW(),
        NOW(), NOW());

-- Train Garages
INSERT INTO train_garage (uuid, uri, display_name, note, metadata, status, last_contact_at, created_at, updated_at)
VALUES ('a3bad37e-9333-4963-88ca-7c0b05a61ee8', 'http://example.com/fds/train-garage1', 'Train Garage 1',
        'Note for Train Garage 1 which is an example...', '', 'SYNCED', NOW(), NOW(), NOW());

INSERT INTO train_garage (uuid, uri, display_name, note, metadata, status, last_contact_at, created_at, updated_at)
VALUES ('7c9594d1-d20e-449a-bd7f-393e1f6b87ac', 'http://example.com/fds/train-garage2', 'Train Garage 2',
        'Note for Train Garage 2 which is an example...', '', 'INVALID', NOW(), NOW(), NOW());

-- Train Type
INSERT INTO train_type (uuid, uri, title, note, created_at, updated_at)
VALUES ('f5eef54f-b85c-4109-b26b-18e15c8e0751', 'https://w3id.org/fdp/fdt-o#SPARQLTrain', 'SPARQL Train',
        'My note about the SPARQL Train type...', NOW(), NOW());

INSERT INTO train_type (uuid, uri, title, note, created_at, updated_at)
VALUES ('8b4acd34-5738-49d4-918f-7d4955ded0bc', 'https://w3id.org/fdp/fdt-o#DockerTrain', 'Docker Train',
        'My note about the Docker Train type...', NOW(), NOW());

INSERT INTO train_type (uuid, uri, title, note, created_at, updated_at)
VALUES ('32d36066-dbf6-4ed3-b4da-aad0edd1639a', 'https://w3id.org/fdp/fdt-o#SQLTrain', 'SQL Train',
        'My note about the SQL Train type...', NOW(), NOW());

-- Stations
INSERT INTO station (uuid, uri, title, description, keywords, metadata, status, last_contact_at, station_directory_id,
                     created_at, updated_at)
VALUES ('9b2f99bb-6ca4-4326-82c1-bba34fb39abc', 'http://example.com/fds/station1',
        'Station 1: Public Health Data Station', 'Description for station 1 ...', 'Health,SPARQL,SQL,Query', '',
        'SYNCED', NOW(), '6dfc348a-3ceb-49b2-ac5a-eff83bdf7eae', NOW(), NOW());
INSERT INTO station_train_type (train_type_id, station_id)
VALUES ('f5eef54f-b85c-4109-b26b-18e15c8e0751', '9b2f99bb-6ca4-4326-82c1-bba34fb39abc');
INSERT INTO station_train_type (train_type_id, station_id)
VALUES ('32d36066-dbf6-4ed3-b4da-aad0edd1639a', '9b2f99bb-6ca4-4326-82c1-bba34fb39abc');

INSERT INTO station (uuid, uri, title, description, keywords, metadata, status, last_contact_at, station_directory_id,
                     created_at, updated_at)
VALUES ('a4d6cf81-1e7a-4666-88e7-26fbdd992674', 'http://example.com/fds/station2',
        'Station 2: Public COVID Data Station', 'Description for station 2 ...', 'Health,COVID,SPARQL', '', 'SYNCED',
        NOW(), '6dfc348a-3ceb-49b2-ac5a-eff83bdf7eae', NOW(), NOW());
INSERT INTO station_train_type (train_type_id, station_id)
VALUES ('f5eef54f-b85c-4109-b26b-18e15c8e0751', 'a4d6cf81-1e7a-4666-88e7-26fbdd992674');

INSERT INTO station (uuid, uri, title, description, keywords, metadata, status, last_contact_at, station_directory_id,
                     created_at, updated_at)
VALUES ('f41417ab-ef19-408f-ba69-5d3a1bc42e23', 'http://example.com/fds/station3',
        'Station 3: Another COVID Data Station', 'Description for station 3 ...', 'Health,COVID,SPARQL,Docker', '',
        'SYNCED', NOW(), '4475579a-0497-4b4d-babe-56f6b7a38ee7', NOW(), NOW());
INSERT INTO station_train_type (train_type_id, station_id)
VALUES ('f5eef54f-b85c-4109-b26b-18e15c8e0751', 'f41417ab-ef19-408f-ba69-5d3a1bc42e23');
INSERT INTO station_train_type (train_type_id, station_id)
VALUES ('8b4acd34-5738-49d4-918f-7d4955ded0bc', 'f41417ab-ef19-408f-ba69-5d3a1bc42e23');

-- Stations
INSERT INTO train (uuid, uri, title, description, keywords, metadata, status, last_contact_at, train_garage_id,
                   created_at, updated_at)
VALUES ('e816b968-2663-4110-889d-7023b797c407', 'http://example.com/fds/train1',
        'SPARQL query to retrieve COVID 19 data',
        'SPARQL query to retrieve COVID 19 cases per region in the Netherlands', 'Health,COVID,SPARQL', '', 'SYNCED',
        NOW(), 'a3bad37e-9333-4963-88ca-7c0b05a61ee8', NOW(), NOW());
INSERT INTO train_train_type (train_type_id, train_id)
VALUES ('f5eef54f-b85c-4109-b26b-18e15c8e0751', 'e816b968-2663-4110-889d-7023b797c407');

INSERT INTO train (uuid, uri, title, description, keywords, metadata, status, last_contact_at, train_garage_id,
                   created_at, updated_at)
VALUES ('5dd5c8d4-d084-4ade-8647-cdf0df0702c2', 'http://example.com/fds/train2', 'SQL Train for nice table',
        'SQL train creating very nice table for health data', 'Health,SQL,Table', '', 'SYNCED', NOW(),
        'a3bad37e-9333-4963-88ca-7c0b05a61ee8', NOW(), NOW());
INSERT INTO train_train_type (train_type_id, train_id)
VALUES ('32d36066-dbf6-4ed3-b4da-aad0edd1639a', '5dd5c8d4-d084-4ade-8647-cdf0df0702c2');

INSERT INTO train (uuid, uri, title, description, keywords, metadata, status, last_contact_at, train_garage_id,
                   created_at, updated_at)
VALUES ('a1eea8ce-e332-4e7b-b41d-880b53d709f4', 'http://example.com/fds/train3', 'SPARQL query summarizing COVID data',
        'SPARQL summary of COVID 19 cases for entire dataset', 'Health,COVID,SPARQL,Summary', '', 'SYNCED', NOW(),
        'a3bad37e-9333-4963-88ca-7c0b05a61ee8', NOW(), NOW());
INSERT INTO train_train_type (train_type_id, train_id)
VALUES ('f5eef54f-b85c-4109-b26b-18e15c8e0751', 'a1eea8ce-e332-4e7b-b41d-880b53d709f4');
