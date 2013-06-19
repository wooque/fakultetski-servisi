create table User(	username VARCHAR(20),
                        password VARCHAR(20) NOT NULL,
                        type VARCHAR(7),
                        name VARCHAR(15),
                        surname VARCHAR(15),
                        phone VARCHAR(15),
                        email VARCHAR(20),
                        PRIMARY KEY(username));
