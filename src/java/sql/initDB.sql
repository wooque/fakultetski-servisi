create table User(	username VARCHAR(20),
                        password VARCHAR(20) NOT NULL,
                        type VARCHAR(7) NOT NULL,
                        name VARCHAR(15),
                        surname VARCHAR(15),
                        phone VARCHAR(15),
                        email VARCHAR(30),
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

create table Classroom( ClassroomID INTEGER AUTO_INCREMENT,
                        location VARCHAR(20),
                        type VARCHAR(20),
                        number INTEGER,
                        PRIMARY KEY(ClassroomID));

create table Lab(       LabID INTEGER AUTO_INCREMENT,
                        CourseID INTEGER,
                        name VARCHAR(20),
                        begin DATETIME,
                        end DATETIME,
                        ClassroomID INTEGER,
                        type INTEGER,
                        maxDemons INTEGER,
                        closed BIT,
                        accounted BIT,
                        PRIMARY KEY (LabID),
                        FOREIGN KEY (CourseID) references Course(CourseID) on update cascade on delete cascade,
                        FOREIGN KEY (ClassroomID) references Classroom(ClassroomID) on update cascade on delete cascade);

create table InvitedDemons( InvitedDemonsID INTEGER AUTO_INCREMENT,
                            LabID INTEGER,
                            username VARCHAR(20),
                            rejected BIT,
                            commentary VARCHAR(50),
                            PRIMARY KEY (InvitedDemonsID),
                            FOREIGN KEY (LabID) references Lab(LabID) on update cascade on delete cascade,
                            FOREIGN KEY (username) references User(username) on update cascade on delete cascade);
                            
create table LabDemons( LabDemonsID INTEGER AUTO_INCREMENT,
                        LabID INTEGER,
                        username VARCHAR(20),
                        dateOfPayment DATE,
                        amount FLOAT,
                        PRIMARY KEY (LabDemonsID),
                        FOREIGN KEY (LabID) references Lab(LabID) on update cascade on delete cascade,
                        FOREIGN KEY (username) references User(username) on update cascade on delete cascade);

create table Coefs(     CoefID INTEGER AUTO_INCREMENT,
                        type INTEGER,
                        coef FLOAT,
                        PRIMARY KEY (CoefID));

insert into User (username, password, type, name, surname, phone, email) values
                ('admin','admin','admin','Administrator',NULL,NULL,NULL),
                ('wooque','wooque','student','Vuk','Mirovic','064/9843407','wooque@gmail.com'),
                ('pera','pera','student','Pera','Peric','064/123456','pera@hotmail.com'),
                ('joca','joca','teacher','Jovan','Djordjevic','011/9999999','jdjordjevic@etf.rs'),
                ('zaki','zaki','teacher','Zaharije','Radivojevic','011/000000','zaki@etf.rs');

insert into Student (username, department, year, gpa) values 
                    ('wooque','IR','4','9.7'),
                    ('pera','IR','3','10');

insert into SignUp  (username, password, type, name, surname, phone, email, department, year, gpa) values
                    ('mika','mika','student','Mika','Mikic','064/555555','mika@gmail.com', 'IR', '3','8.5'),
                    ('zorz','zorz','teacher','Djordje','Djurdjevic','064/0000000','zorz@etf.rs',NULL,NULL,NULL);

insert into Course  (department, teachyear, code, semester, year, name) values
                    ('IR','3','AR2',1,'2012','Arhitektura i organizacija 2'),
                    ('IR','2','OO2',1,'2012','Objektno orjentisano 2');

-- depends on inserting order of courses
insert into Teaches (CourseID, username) values
                    ('1', 'zaki'),
                    ('1', 'joca');

insert into Classroom (location, type, number) values 
                    ('Paviljon', 'lab', '25'),
                    ('Paviljon', 'lab', '26'),
                    ('ETF', 'ucionica', '61');

insert into Coefs (type, coef) values 
                    ('1', '1.0'),
                    ('2', '1.2'),
                    ('3', '1.3'),
                    ('4', '1.0');
