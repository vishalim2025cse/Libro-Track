-- ==============================================
-- LibroTrack - MySQL Workbench Setup
-- ==============================================

CREATE DATABASE IF NOT EXISTS librotrack;
USE librotrack;

CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(255) NOT NULL UNIQUE,
    category VARCHAR(255) NOT NULL,
    total_copies INT NOT NULL,
    available_copies INT NOT NULL
);

CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS issue_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    fine_amount DOUBLE NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_issue_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT fk_issue_student FOREIGN KEY (student_id) REFERENCES students(id)
);

-- Optional demo data
INSERT INTO books (title, author, isbn, category, total_copies, available_copies)
SELECT 'Clean Code', 'Robert C. Martin', '9780132350884', 'Programming', 3, 3
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780132350884');

INSERT INTO books (title, author, isbn, category, total_copies, available_copies)
SELECT 'The Alchemist', 'Paulo Coelho', '9780061122415', 'Fiction', 4, 4
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780061122415');

INSERT INTO students (name, email)
SELECT 'Visha', 'visha@example.com'
WHERE NOT EXISTS (SELECT 1 FROM students WHERE email = 'visha@example.com');

INSERT INTO students (name, email)
SELECT 'Arun Kumar', 'arun@example.com'
WHERE NOT EXISTS (SELECT 1 FROM students WHERE email = 'arun@example.com');

-- Check your data
SELECT * FROM books;
SELECT * FROM students;
SELECT * FROM issue_records;
