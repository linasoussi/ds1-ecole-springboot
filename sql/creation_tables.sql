-- =====================================================================
-- Création MANUELLE de la base et des tables dans MySQL (port 3307)
-- (Optionnel : avec spring.jpa.hibernate.ddl-auto=update, Spring Boot
--  crée automatiquement les mêmes tables au démarrage.)
-- Les noms de tables = nom de l'entité en minuscules (sans @Table).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS test;
USE test;

-- 1) Tables sans clé étrangère
CREATE TABLE department (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE classroom (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    name     VARCHAR(255) NOT NULL UNIQUE,
    capacity INT,
    PRIMARY KEY (id)
);

CREATE TABLE student (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    email      VARCHAR(255) NOT NULL UNIQUE,
    birth_date DATE,
    status     VARCHAR(20),
    PRIMARY KEY (id)
);

-- 2) Tables avec clés étrangères (ManyToOne)
CREATE TABLE teacher (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    first_name    VARCHAR(255),
    last_name     VARCHAR(255),
    email         VARCHAR(255) NOT NULL UNIQUE,
    department_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (department_id) REFERENCES department (id)
);

CREATE TABLE course (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    title        VARCHAR(255) NOT NULL UNIQUE,
    credits      INT,
    teacher_id   BIGINT,
    classroom_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (teacher_id)   REFERENCES teacher (id),
    FOREIGN KEY (classroom_id) REFERENCES classroom (id)
);

CREATE TABLE grade (
    id         BIGINT NOT NULL AUTO_INCREMENT,
    score      DOUBLE,
    date       DATE,
    student_id BIGINT,
    course_id  BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (student_id) REFERENCES student (id),
    FOREIGN KEY (course_id)  REFERENCES course (id)
);

-- 3) Table d'association du ManyToMany Student <-> Course
CREATE TABLE student_course (
    student_id BIGINT NOT NULL,
    course_id  BIGINT NOT NULL,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student (id),
    FOREIGN KEY (course_id)  REFERENCES course (id)
);

-- Vérification
SHOW TABLES;
