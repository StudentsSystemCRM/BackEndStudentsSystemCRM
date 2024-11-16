
DELETE FROM groups;
DELETE FROM lecturers;
DELETE FROM lecturer_group;


INSERT INTO groups (
    id, name, whats_app, skype, slack, status, start_date, exp_finish_date, deactivate_after_30_days, created_by, updated_by
) VALUES (
             1, 'Group A', 'whatsapp_link', 'skype_link', 'slack_link', 'ACTIVE', CURRENT_DATE, DATEADD('DAY', 90, CURRENT_DATE), DATEADD('DAY', 120, CURRENT_DATE), 'admin', 'admin'
         );

INSERT INTO lecturers (
    id, first_name, last_name, phone_number, email, city, status, created_by, updated_by
) VALUES
      (4, 'Alice', 'Johnson', '123456789', 'alice.johnson@example.com', 'Haifa', 'ACTIVE', 'admin', 'admin'),
      (2, 'Jane', 'Smith', '0987654321', 'kate2@example.com', 'Tel-Aviv', 'ACTIVE', 'admin', 'admin'),
      (3, 'Existing', 'Lecturer', '1112223333', 'existing.lecturer@example.com', 'Haifa', 'ACTIVE', 'admin', 'admin');

INSERT INTO lecturer_group (lecturer_id, group_id) VALUES
                                                       (4, 1),
                                                       (2, 1),
                                                       (3, 1);
