delete from activity_logs;
delete from payments;
delete from student_groups;
delete from students;

insert into students (id, first_name, last_name, phone_number, email, city, course, source, lead_status)
values (2, 'Jane', 'Smith', '0987654321', 'kate2@example.com', 'Los Angeles', 'Science', 'Referral', 'Prospect');