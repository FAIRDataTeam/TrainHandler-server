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

INSERT INTO public.train_type (uuid, uri, title, note, created_at, updated_at)
VALUES ('767a46c1-0bab-4b87-a8df-de5600946a66', 'https://w3id.org/fdt/fdt-o#FHIRTrain', 'FHIR Train', '',
        NOW(), NOW());
INSERT INTO public.train_type (uuid, uri, title, note, created_at, updated_at)
VALUES ('77aedb36-592d-4577-8732-7d837acc35ba', 'https://w3id.org/fdt/fdt-o#GraphQLTrain', 'GraphQL Train', '',
        NOW(), NOW());
INSERT INTO public.train_type (uuid, uri, title, note, created_at, updated_at)
VALUES ('4ec48955-484c-4eee-a8bb-fef0633062d6', 'https://w3id.org/fdt/fdt-o#PythonTrain', 'Python Train', '',
        NOW(), NOW());
INSERT INTO public.train_type (uuid, uri, title, note, created_at, updated_at)
VALUES ('0da459b5-ec93-4cd5-930d-d9fe86be0d2a', 'https://w3id.org/fdt/fdt-o#RESTTrain', 'REST Train', '',
        NOW(), NOW());
INSERT INTO public.train_type (uuid, uri, title, note, created_at, updated_at)
VALUES ('9f32d058-1791-4356-8c1e-f735026d91b0', 'https://w3id.org/fdt/fdt-o#RTrain', 'R Train', '',
        NOW(), NOW());
INSERT INTO public.train_type (uuid, uri, title, note, created_at, updated_at)
VALUES ('f1112bb9-d888-470c-91f3-39a8e888a6d1', 'https://w3id.org/fdt/fdt-o#SingularityTrain', 'Singularity Train', '',
        NOW(), NOW());
INSERT INTO public.train_type (uuid, uri, title, note, created_at, updated_at)
VALUES ('5513164c-e440-455e-8c71-339324780576', 'https://w3id.org/fdt/fdt-o#TriplePatternFragmentTrain',
        'Triple Pattern Fragment Train', '', NOW(), NOW());