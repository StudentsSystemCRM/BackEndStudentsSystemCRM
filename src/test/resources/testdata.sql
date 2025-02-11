
DELETE FROM activity_logs;
DELETE FROM payments;
DELETE FROM groups_students;
DELETE FROM groups;
DELETE FROM students;

INSERT INTO students (
    id, first_name, last_name, phone_number, email, city, course, source, lead_status, original_group_id, total_sum_to_pay, created_by, updated_by
) VALUES (
             2, 'Jane', 'Smith', '0987654321', 'kate2@example.com', 'Los Angeles', 'Science', 'Referral', 'LEAD', 1, 5000, 'admin', 'admin'
         );

INSERT INTO groups (
    id, name, whats_app, skype, slack, status, start_date, exp_finish_date, deactivate_after_30_days, created_by, updated_by
) VALUES (
             1,'Group A', 'whatsapp_link', 'skype_link', 'slack_link', 'ACTIVE', CURRENT_DATE, DATEADD('DAY', 90, CURRENT_DATE), false, 'admin', 'admin'
         );

INSERT INTO groups_students (group_id, students_id)
VALUES (1, 2);