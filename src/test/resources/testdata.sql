delete from activity_logs;
delete from payments;
delete from student_groups;
delete from students;

INSERT INTO students (id, first_name, last_name, phone_number, email, city, course, source, lead_status, original_group, total_sum_to_pay, group_name, created_by, updated_by)
VALUES (2, 'Jane', 'Smith', '0987654321', 'kate2@example.com', 'Los Angeles', 'Science', 'Referral', 'LEAD', 'Group A', 5000, NULL, 'admin', 'admin');
