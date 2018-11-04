-- From scratch
CREATE USER super;
CREATE DATABASE nextclass;
CREATE SCHEMA main;
GRANT ALL PRIVILEGES ON DATABASE nextclass TO super;

set SCHEMA 'main';

-- Table: university
CREATE TABLE university
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name character varying,
    CONSTRAINT PRIMARY KEY (id)
);

-- Table: campus
CREATE TABLE campus
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name character varying,
    short_code character varying,
    university_id integer NOT NULL,
    CONSTRAINT PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (university_id)
        REFERENCES university (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: building
CREATE TABLE building
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name character varying,
    short_code character varying,
    campus_id integer NOT NULL,
    university_id integer NOT NULL,
    CONSTRAINT PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (campus_id)
        REFERENCES campus (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FOREIGN KEY (university_id)
        REFERENCES university (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: room
CREATE TABLE room
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name character varying,
    "number" character varying,
    building_id integer NOT NULL,
    university_id integer NOT NULL,
    CONSTRAINT PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (building_id)
        REFERENCES building (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FOREIGN KEY (university_id)
        REFERENCES university (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: department
CREATE TABLE department
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name character varying,
    short_code character varying,
    university_id integer NOT NULL,
    CONSTRAINT PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (university_id)
        REFERENCES university (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: semester
CREATE TABLE semester
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name character varying,
    code character varying,
    university_id integer NOT NULL,
    CONSTRAINT PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (university_id)
        REFERENCES university (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: course
CREATE TABLE course
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name character varying,
    code character varying,
    semester_id integer NOT NULL,
    department_id integer NOT NULL,
    university_id integer NOT NULL,
    CONSTRAINT PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (department_id)
        REFERENCES department (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FOREIGN KEY (semester_id)
        REFERENCES semester (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FOREIGN KEY (university_id)
        REFERENCES university (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: instructor
CREATE TABLE instructor
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name character varying,
    short_name character varying,
    department_id integer NOT NULL,
    university_id integer NOT NULL,
    CONSTRAINT PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (department_id)
        REFERENCES department (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FOREIGN KEY (university_id)
        REFERENCES university (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: section
CREATE TABLE section
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    local_id integer,
    online boolean NOT NULL,
    open boolean,
    weight numeric,
    remaining_capacity integer,
    total_capacity integer,
    course_id integer NOT NULL,
    university_id integer NOT NULL,
    CONSTRAINT PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (course_id)
        REFERENCES course (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FOREIGN KEY (university_id)
        REFERENCES university (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: meeting
CREATE TABLE meeting
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    start_time time without time zone,
    end_time time without time zone,
    start_date date,
    end_date date,
    type character varying,
    room_id integer,
    section_id integer NOT NULL,
    university_id integer NOT NULL,
    CONSTRAINT PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (room_id)
        REFERENCES room (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FOREIGN KEY (section_id)
        REFERENCES section (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FOREIGN KEY (university_id)
        REFERENCES university (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: meeting_day
CREATE TABLE meeting_day
(
    id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    day_code smallint NOT NULL,
    meeting_id integer NOT NULL,
    university_id integer NOT NULL,
    CONSTRAINT PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (meeting_id)
        REFERENCES meeting (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FOREIGN KEY (university_id)
        REFERENCES university (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: section_instructor_map
CREATE TABLE section_instructor_map
(
    section_id integer NOT NULL,
    instructor_id integer NOT NULL,
    CONSTRAINT PRIMARY KEY (section_id, instructor_id),
    CONSTRAINT FOREIGN KEY (instructor_id)
        REFERENCES instructor (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT FOREIGN KEY (section_id)
        REFERENCES section (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);