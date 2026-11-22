CREATE TABLE optimization_jobs (
    request_id UUID PRIMARY KEY,
    scenario_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('SUBMITTED', 'SOLVED', 'UNROUTABLE', 'FAILED')),
    submitted_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ,
    routes JSONB NOT NULL DEFAULT '[]'::jsonb,
    CONSTRAINT chk_optimization_job_completion CHECK
        ((status = 'SUBMITTED' AND completed_at IS NULL) OR (status <> 'SUBMITTED' AND completed_at IS NOT NULL))
);
CREATE INDEX ix_optimization_jobs_scenario_id ON optimization_jobs(scenario_id);
CREATE INDEX ix_optimization_jobs_status ON optimization_jobs(status);
