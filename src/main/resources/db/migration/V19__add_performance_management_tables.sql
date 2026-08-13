-- ═══════════════════════════════════════════════════════
-- HRMS PERFORMANCE MANAGEMENT TABLES  –  V19
-- ═══════════════════════════════════════════════════════

-- 1. Rating Scales
CREATE TABLE prf_rating_scales (
    id                UUID PRIMARY KEY,
    organization_id   UUID NOT NULL REFERENCES organizations(id),
    name              VARCHAR(100) NOT NULL,
    min_rating        INTEGER,
    max_rating        INTEGER,
    description       VARCHAR(500),
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL
);
CREATE INDEX idx_prf_rating_scales_org ON prf_rating_scales(organization_id);

-- 2. Rating Scale Levels
CREATE TABLE prf_rating_scale_levels (
    id                UUID PRIMARY KEY,
    rating_scale_id   UUID NOT NULL REFERENCES prf_rating_scales(id) ON DELETE CASCADE,
    value             INTEGER NOT NULL,
    label             VARCHAR(100) NOT NULL,
    description       VARCHAR(300)
);
CREATE INDEX idx_prf_rating_scale_levels_scale ON prf_rating_scale_levels(rating_scale_id);

-- 3. Competencies
CREATE TABLE prf_competencies (
    id                UUID PRIMARY KEY,
    organization_id   UUID NOT NULL REFERENCES organizations(id),
    name              VARCHAR(100) NOT NULL,
    description       VARCHAR(500),
    category          VARCHAR(50),
    weightage         INTEGER,
    active            BOOLEAN DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL
);
CREATE INDEX idx_prf_competencies_org ON prf_competencies(organization_id);

-- 4. Appraisal Cycles
CREATE TABLE prf_appraisal_cycles (
    id                      UUID PRIMARY KEY,
    organization_id         UUID NOT NULL REFERENCES organizations(id),
    name                    VARCHAR(150) NOT NULL,
    type                    VARCHAR(50),
    period                  VARCHAR(100),
    start_date              DATE,
    end_date                DATE,
    self_review_deadline    DATE,
    manager_review_deadline DATE,
    hr_review_deadline      DATE,
    status                  VARCHAR(30),
    eligible_count          INTEGER,
    completion_percentage   INTEGER,
    description             VARCHAR(500),
    created_at              TIMESTAMP NOT NULL,
    updated_at              TIMESTAMP NOT NULL
);
CREATE INDEX idx_prf_appraisal_cycles_org_status ON prf_appraisal_cycles(organization_id, status);

-- 5. Performance Goals (KRAs)
CREATE TABLE prf_goals (
    id                UUID PRIMARY KEY,
    organization_id   UUID NOT NULL REFERENCES organizations(id),
    title             VARCHAR(200) NOT NULL,
    description       VARCHAR(1000),
    employee_id       UUID NOT NULL REFERENCES employees(employee_id),
    cycle_id          UUID REFERENCES prf_appraisal_cycles(id),
    category          VARCHAR(50),
    weightage         INTEGER,
    kpi               VARCHAR(150),
    target_value      NUMERIC(12,2),
    current_value     NUMERIC(12,2),
    unit              VARCHAR(20),
    due_date          DATE,
    priority          VARCHAR(20),
    status            VARCHAR(30),
    progress          INTEGER,
    comments          VARCHAR(1000),
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL
);
CREATE INDEX idx_prf_goals_org_cycle ON prf_goals(organization_id, cycle_id);
CREATE INDEX idx_prf_goals_employee ON prf_goals(employee_id);

-- 6. Self Reviews
CREATE TABLE prf_self_reviews (
    id                   UUID PRIMARY KEY,
    organization_id      UUID NOT NULL REFERENCES organizations(id),
    employee_id          UUID NOT NULL REFERENCES employees(employee_id),
    cycle_id             UUID NOT NULL REFERENCES prf_appraisal_cycles(id),
    strengths            VARCHAR(2000),
    areas_of_improvement VARCHAR(2000),
    overall_rating       NUMERIC(4,2),
    status               VARCHAR(30),
    submitted_on         TIMESTAMP,
    created_at           TIMESTAMP NOT NULL,
    updated_at           TIMESTAMP NOT NULL
);
CREATE INDEX idx_prf_self_reviews_org_cycle ON prf_self_reviews(organization_id, cycle_id);
CREATE INDEX idx_prf_self_reviews_employee ON prf_self_reviews(employee_id);

-- 7. Self Review Goal Ratings
CREATE TABLE prf_self_review_goal_ratings (
    id                 UUID PRIMARY KEY,
    self_review_id     UUID NOT NULL REFERENCES prf_self_reviews(id) ON DELETE CASCADE,
    goal_id            UUID NOT NULL REFERENCES prf_goals(id),
    employee_rating    NUMERIC(4,2),
    employee_comment   VARCHAR(1000)
);
CREATE INDEX idx_prf_self_review_goal_ratings_review ON prf_self_review_goal_ratings(self_review_id);

-- 8. Self Review Competency Ratings
CREATE TABLE prf_self_review_comp_ratings (
    id                 UUID PRIMARY KEY,
    self_review_id     UUID NOT NULL REFERENCES prf_self_reviews(id) ON DELETE CASCADE,
    competency_id      UUID NOT NULL REFERENCES prf_competencies(id),
    employee_rating    NUMERIC(4,2),
    employee_comment   VARCHAR(1000)
);
CREATE INDEX idx_prf_self_review_comp_ratings_review ON prf_self_review_comp_ratings(self_review_id);

-- 9. Manager Reviews
CREATE TABLE prf_manager_reviews (
    id                       UUID PRIMARY KEY,
    organization_id          UUID NOT NULL REFERENCES organizations(id),
    self_review_id           UUID REFERENCES prf_self_reviews(id),
    employee_id              UUID NOT NULL REFERENCES employees(employee_id),
    manager_id               UUID NOT NULL REFERENCES employees(employee_id),
    cycle_id                 UUID NOT NULL REFERENCES prf_appraisal_cycles(id),
    promotion_recommendation VARCHAR(100),
    training_recommendation  VARCHAR(500),
    improvement_plan         VARCHAR(2000),
    overall_rating           NUMERIC(4,2),
    manager_comments         VARCHAR(2000),
    status                   VARCHAR(30),
    completed_on             TIMESTAMP,
    created_at               TIMESTAMP NOT NULL,
    updated_at               TIMESTAMP NOT NULL
);
CREATE INDEX idx_prf_manager_reviews_org_cycle ON prf_manager_reviews(organization_id, cycle_id);
CREATE INDEX idx_prf_manager_reviews_manager ON prf_manager_reviews(manager_id);
CREATE INDEX idx_prf_manager_reviews_self_review ON prf_manager_reviews(self_review_id);

-- 10. Manager Review Goal Ratings
CREATE TABLE prf_mgr_review_goal_ratings (
    id                 UUID PRIMARY KEY,
    manager_review_id  UUID NOT NULL REFERENCES prf_manager_reviews(id) ON DELETE CASCADE,
    goal_id            UUID NOT NULL REFERENCES prf_goals(id),
    manager_rating     NUMERIC(4,2),
    manager_comment    VARCHAR(1000)
);
CREATE INDEX idx_prf_mgr_review_goal_ratings_review ON prf_mgr_review_goal_ratings(manager_review_id);

-- 11. Manager Review Competency Ratings
CREATE TABLE prf_mgr_review_comp_ratings (
    id                 UUID PRIMARY KEY,
    manager_review_id  UUID NOT NULL REFERENCES prf_manager_reviews(id) ON DELETE CASCADE,
    competency_id      UUID NOT NULL REFERENCES prf_competencies(id),
    manager_rating     NUMERIC(4,2),
    manager_comment    VARCHAR(1000)
);
CREATE INDEX idx_prf_mgr_review_comp_ratings_review ON prf_mgr_review_comp_ratings(manager_review_id);

-- 12. Calibration Records
CREATE TABLE prf_calibration_records (
    id                 UUID PRIMARY KEY,
    organization_id    UUID NOT NULL REFERENCES organizations(id),
    employee_id        UUID NOT NULL REFERENCES employees(employee_id),
    cycle_id           UUID NOT NULL REFERENCES prf_appraisal_cycles(id),
    current_rating     NUMERIC(4,2),
    proposed_rating    NUMERIC(4,2),
    final_rating       NUMERIC(4,2),
    reviewer           VARCHAR(150),
    status             VARCHAR(30),
    created_at         TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP NOT NULL
);
CREATE INDEX idx_prf_calibration_records_org_cycle ON prf_calibration_records(organization_id, cycle_id);
