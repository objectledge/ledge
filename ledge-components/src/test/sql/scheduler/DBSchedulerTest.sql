INSERT INTO ledge_scheduler 
(job_id, job_name, schedule_type, schedule_config, job_class_name)
VALUES (1,'foo','at','','');

ALTER SEQUENCE ledge_scheduler_seq RESTART WITH 2;
