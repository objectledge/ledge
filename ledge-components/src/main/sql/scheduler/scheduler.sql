CREATE TABLE scheduler (
    job_id BIGINT NOT NULL,
    job_name VARCHAR(255) NOT NULL,
    schedule_type VARCHAR(32) NOT NULL,
    schedule_config VARCHAR(255) NOT NULL,
    job_class_name VARCHAR(255) NOT NULL,
    argument VARCHAR(255),
    run_count INTEGER DEFAULT 0,
    run_count_limit INTEGER DEFAULT -1,
    last_run_time TIMESTAMP WITH TIME ZONE,
    run_time_limit_start TIMESTAMP WITH TIME ZONE,
    run_time_limit_end TIMESTAMP WITH TIME ZONE,
    auto_clean INTEGER DEFAULT 0,
    reentrant INTEGER DEFAULT 0,
    enabled INTEGER DEFAULT 1,
    PRIMARY KEY (job_id),
    UNIQUE (job_name)
);
