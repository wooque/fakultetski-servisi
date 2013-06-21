create table User(	username VARCHAR(20),
                        password VARCHAR(20) NOT NULL,
                        type VARCHAR(7) NOT NULL,
                        name VARCHAR(15),
                        surname VARCHAR(15),
                        phone VARCHAR(15),
                        email VARCHAR(20),
                        PRIMARY KEY(username));

create table Student(   StudentID INTEGER AUTO_INCREMENT,
                        username VARCHAR(20),
                        department VARCHAR(3),
                        year INTEGER,
                        gpa FLOAT,
                        PRIMARY KEY(StudentID),
                        FOREIGN KEY(username) REFERENCES User(username) on update cascade on delete cascade);

create table Signup(	username VARCHAR(20),
                        password VARCHAR(20) NOT NULL,
                        type VARCHAR(7) NOT NULL,
                        name VARCHAR(15),
                        surname VARCHAR(15),
                        phone VARCHAR(15),
                        email VARCHAR(20),
                        department VARCHAR(3),
                        year INTEGER,
                        gpa FLOAT,
                        PRIMARY KEY(username));
