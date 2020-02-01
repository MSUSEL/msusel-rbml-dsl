--
-- The MIT License (MIT)
--
-- MSUSEL RBML DSL
-- Copyright (c) 2015-2019 Montana State University, Gianforte School of Computing,
-- Software Engineering Laboratory and Idaho State University, Informatics and
-- Computer Science, Empirical Software Engineering Laboratory
--
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
--
-- The above copyright notice and this permission notice shall be included in all
-- copies or substantial portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
-- SOFTWARE.
--

create table systems (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  name VARCHAR,
  sysKey VARCHAR
);

create table pattern_repositories (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  repoKey VARCHAR,
  name VARCHAR
);

create table patterns (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  patternKey VARCHAR,
  name VARCHAR,
  pattern_repository_id INTEGER REFERENCES pattern_repositories(id)
);

create table roles (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  roleKey VARCHAR,
  name VARCHAR,
  role_binding_id INTEGER REFERENCES role_bindings(id),
  pattern_id INTEGER REFERENCES patterns(id)
);

create table relations (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  relKey VARCHAR,
  project_id INTEGER,
  reference_id INTEGER,
  to_id INTEGER,
  from_id INTEGER,
  type INTEGER
);

create table pattern_chains (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  chainKey VARCHAR,
  system_id INTEGER REFERENCES systems(id)
);

create table pattern_instances (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  instKey VARCHAR,
  pattern_chain_id INTEGER REFERENCES pattern_chains(id),
  project_id INTEGER REFERENCES projects(id),
  pattern_id INTEGER REFERENCES patterns(id)
);

create table role_bindings (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  pattern_instance_id INTEGER REFERENCES pattern_instances(id)
);

create table refs (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  refKey VARCHAR,
  type INTEGER,
  parent_id INTEGER,
  parent_type VARCHAR
);

create table findings (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  findingKey VARCHAR,
  rule_id INTEGER REFERENCES rules(id),
  project_id INTEGER REFERENCES projects(id),
  reference_id INTEGER REFERENCES refs(id)
);

create table rules (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  ruleKey VARCHAR,
  name VARCHAR,
  description VARCHAR,
  priority INTEGER,
  rule_repository_id INTEGER REFERENCES rule_repositories(id)
);

create table rules_tags (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  rule_id INTEGER REFERENCES rules(id),
  tag_id INTEGER REFERENCES tags(id)
);

create table tags (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  tag VARCHAR
);

create table rule_repositories (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  repoKey VARCHAR,
  name VARCHAR
);

create table measures (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  measureKey VARCHAR,
  value DOUBLE,
  metric_id INTEGER REFERENCES metrics(id),
  project_id INTEGER REFERENCES projects(id)
);

create table metrics (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  metricKey VARCHAR,
  name VARCHAR,
  description VARCHAR,
  metric_repository_id INTEGER REFERENCES metric_repositories(id)
);

create table metric_repositories (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  repoKey VARCHAR,
  name VARCHAR
);

create table projects (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  projKey VARCHAR,
  name VARCHAR,
  version VARCHAR,
  system_id INTEGER REFERENCES systems(id)
);

create table languages (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  name VARCHAR
);

create table projects_languages (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  project_id INTEGER REFERENCES projects(id),
  language_id INTEGER REFERENCES languages(id)
);

create table modules (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  moduleKey VARCHAR,
  name VARCHAR,
  project_id INTEGER REFERENCES projects(id)
);

create table scms (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  scmKey VARCHAR,
  name VARCHAR,
  tag VARCHAR,
  branch VARCHAR,
  url VARCHAR,
  project_id INTEGER REFERENCES projects(id),
  type INTEGER
);

create table namespaces (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  nsKey VARCHAR,
  name VARCHAR,
  namespace_id INTEGER REFERENCES namespaces(id),
  module_id INTEGER REFERENCES modules(id)
);

create table files (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  fileKey VARCHAR,
  name VARCHAR,
  type INTEGER,
  namespace_id INTEGER REFERENCES namespaces(id)
);

create table imports (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  name VARCHAR,
  file_id INTEGER REFERENCES files(id)
);

create table unknown_types (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  start INTEGER,
  end INTEGER,
  compKey VARCHAR,
  name VARCHAR,
  accessibility INTEGER,
  file_id INTEGER REFERENCES files(id)
);

create table classes (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  start INTEGER,
  end INTEGER,
  compKey VARCHAR,
  name VARCHAR,
  abstract INTEGER,
  accessibility INTEGER,
  file_id INTEGER REFERENCES files(id),
  parent_id INTEGER,
  parent_type VARCHAR
);

create table enums (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  start INTEGER,
  end INTEGER,
  compKey VARCHAR,
  name VARCHAR,
  accessibility INTEGER,
  file_id INTEGER REFERENCES files(id),
  parent_id INTEGER,
  parent_type VARCHAR
);

create table interfaces (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  start INTEGER,
  end INTEGER,
  compKey VARCHAR,
  name VARCHAR,
  accessibility INTEGER,
  file_id INTEGER REFERENCES files(id),
  parent_id INTEGER,
  parent_type VARCHAR
);

create table literals (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  start INTEGER,
  end INTEGER,
  compKey VARCHAR,
  name VARCHAR,
  accessibility INTEGER,
  parent_id INTEGER,
  parent_type VARCHAR
);

create table initializers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  start INTEGER,
  end INTEGER,
  compKey VARCHAR,
  name VARCHAR,
  accessibility INTEGER,
  parent_id INTEGER,
  parent_type VARCHAR,
  number INTEGER,
  instance INTEGER(1) -- boolean
);

create table fields (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  start INTEGER,
  end INTEGER,
  compKey VARCHAR,
  name VARCHAR,
  accessibility INTEGER,
  parent_id INTEGER,
  parent_type VARCHAR
);

create table methods (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  start INTEGER,
  end INTEGER,
  compKey VARCHAR,
  name VARCHAR,
  accessibility INTEGER,
  cfg VARCHAR,
  parent_id INTEGER,
  parent_type VARCHAR
);

create table constructors (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  start INTEGER,
  end INTEGER,
  compKey VARCHAR,
  name VARCHAR,
  accessibility INTEGER,
  parent_id INTEGER,
  parent_type VARCHAR
);

create table destructors (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  start INTEGER,
  end INTEGER,
  compKey VARCHAR,
  name VARCHAR,
  accessibility INTEGER,
  parent_id INTEGER,
  parent_type VARCHAR
);

create table parameters (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  name VARCHAR,
  parent_id INTEGER,
  parent_type VARCHAR
);

create table method_exceptions (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  parent_id INTEGER,
  parent_type VARCHAR
);

create table type_refs (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  dimensions VARCHAR,
  typeName VARCHAR,
  type INTEGER,
  typeref_id INTEGER REFERENCES type_refs(id),
  is_bound INTEGER(1),
  parent_id INTEGER,
  parent_type VARCHAR
);

create table modifiers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  name VARCHAR
);

create table classes_modifiers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  class_id INTEGER REFERENCES classes(id),
  modifier_id INTEGER REFERENCES modifiers(id)
);

create table enums_modifiers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  enum_id INTEGER REFERENCES enums(id),
  modifier_id INTEGER REFERENCES modifiers(id)
);

create table interfaces_modifiers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  interface_id INTEGER REFERENCES interfaces(id),
  modifier_id INTEGER REFERENCES modifiers(id)
);

create table literals_modifiers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  literal_id INTEGER REFERENCES literals(id),
  modifier_id INTEGER REFERENCES modifiers(id)
);

create table initializers_modifiers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  initializer_id INTEGER REFERENCES initializers(id),
  modifier_id INTEGER REFERENCES modifiers(id)
);

create table fields_modifiers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  field_id INTEGER REFERENCES fields(id),
  modifier_id INTEGER REFERENCES modifiers(id)
);

create table methods_modifiers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  method_id INTEGER REFERENCES methods(id),
  modifier_id INTEGER REFERENCES modifiers(id)
);

create table constructors_modifiers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  constructor_id INTEGER REFERENCES constructors(id),
  modifier_id INTEGER REFERENCES modifiers(id)
);

create table destructors_modifiers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  destructor_id INTEGER REFERENCES destructors(id),
  modifier_id INTEGER REFERENCES modifiers(id)
);

create table parameters_modifiers (
  id INTEGER NOT NULL PRIMARY KEY Autoincrement,
  parameter_id INTEGER REFERENCES parameters(id),
  modifier_id INTEGER REFERENCES modifiers(id)
);

insert into modifiers (name)
values ('STATIC'),
       ('FINAL'),
       ('ABSTRACT'),
       ('NATIVE'),
       ('STRICTFP'),
       ('SYNCHRONIZED'),
       ('TRANSIENT'),
       ('VOLATILE'),
       ('DEFAULT'),
       ('ASYNC'),
       ('CONST'),
       ('EXTERN'),
       ('READONLY'),
       ('SEALED'),
       ('UNSAFE'),
       ('VIRTUAL'),
       ('OUT'),
       ('REF'),
       ('PARAMS'),
       ('OVERRIDE'),
       ('NEW'),
       ('PARTIAL'),
       ('EXPLICIT'),
       ('IMPLICIT'),
       ('YIELD'),
       ('THIS');

insert into pattern_repositories (repoKey, name)
values ('gof', 'gof');

insert into patterns (patternKey, name, pattern_repository_id)
values ('gof:abstract_factory', 'Abstract Factory', 1),
       ('gof:builder', 'Builder', 1),
       ('gof:factory_method', 'Factory Method', 1),
       ('gof:prototype', 'Prototype', 1),
       ('gof:singleton', 'Singleton', 1),
       ('gof:adapter', 'Adapter', 1),
       ('gof:bridge', 'Bridge', 1),
       ('gof:composite', 'Composite', 1),
       ('gof:decorator', 'Decorator', 1),
       ('gof:facade', 'Facade', 1),
       ('gof:flyweight', 'Flyweight', 1),
       ('gof:proxy', 'Proxy', 1),
       ('gof:chain_of_responsibility', 'Chain of Responsibility', 1),
       ('gof:command', 'Command', 1),
       ('gof:interpreter', 'Interpreter', 1),
       ('gof:iterator', 'Iterator', 1),
       ('gof:mediator', 'Mediator', 1),
       ('gof:memento', 'Memento', 1),
       ('gof:observer', 'Observer', 1),
       ('gof:state', 'State', 1),
       ('gof:strategy', 'Strategy', 1),
       ('gof:template_method', 'Template Method', 1),
       ('gof:visitor', 'Visitor', 1);