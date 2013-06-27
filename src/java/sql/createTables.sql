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

create table Course(    CourseID INTEGER AUTO_INCREMENT,
                        department VARCHAR(2),
                        teachyear INTEGER,
                        code VARCHAR(3),
                        name VARCHAR(30),
                        semester BIT,
                        year INTEGER,
                        PRIMARY KEY(CourseID));

create table Teaches(   TeachesID INTEGER AUTO_INCREMENT,
                        CourseID INTEGER NOT NULL,
                        username VARCHAR(20),
                        PRIMARY KEY(TeachesID),
                        FOREIGN KEY (CourseID) references Course(CourseID) on delete cascade on update cascade,
                        FOREIGN KEY (username) references User(username) on delete cascade on update cascade);

create table Apply(     ApplyID INTEGER AUTO_INCREMENT,
                        username VARCHAR(20),
                        CourseID INTEGER,
                        PRIMARY KEY(ApplyID),
                        FOREIGN KEY(username) references User(username) on delete cascade on update cascade,
                        FOREIGN KEY(CourseID) references Course(CourseID) on delete cascade on update cascade);

create table Demonstrator(  DemonstratorID INTEGER AUTO_INCREMENT,
                            CourseID INTEGER,
                            username VARCHAR(20),
                            PRIMARY KEY(DemonstratorID),
                            FOREIGN KEY(CourseID) references Course(CourseID) on update cascade on delete cascade,
                            FOREIGN KEY(username) references User(username) on update cascade on delete cascade);