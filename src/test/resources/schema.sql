CREATE TABLE lecturers (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           first_name VARCHAR(255) NOT NULL,
                           last_name VARCHAR(255) NOT NULL,
                           phone_number VARCHAR(20) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           city VARCHAR(255) NOT NULL,
                           status VARCHAR(50) NOT NULL
);

CREATE TABLE groups (
                        name VARCHAR(255) PRIMARY KEY,
                        whats_app VARCHAR(255),
                        skype VARCHAR(255),
                        slack VARCHAR(255),
                        status VARCHAR(50),
                        start_date DATE,
                        exp_finish_date DATE,
                        deactivate_after_30_days DATE
);

CREATE TABLE lecturer_group (
                                lecturer_id BIGINT,
                                group_id VARCHAR(255),
                                PRIMARY KEY (lecturer_id, group_id),
                                FOREIGN KEY (lecturer_id) REFERENCES lecturers(id),
                                FOREIGN KEY (group_id) REFERENCES groups(name)
);
