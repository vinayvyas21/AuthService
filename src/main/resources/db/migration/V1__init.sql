CREATE TABLE `role`
(
    id     BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)          NULL,
    created_at     datetime              NULL,
    updated_at     datetime              NULL,
    deleted_at     datetime              NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE session
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    token          VARCHAR(255)          NULL,
    expiring_at    datetime              NULL,
    user_id        BIGINT                NULL,
    session_status TINYINT               NULL,
    device_id      VARCHAR(50)          NULL,
    ip_address      VARCHAR(50)          NULL,
    created_at     datetime              NULL,
    updated_at     datetime              NULL,
    deleted_at     datetime              NULL,
    CONSTRAINT pk_session PRIMARY KEY (id)
);

CREATE TABLE user
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    email    VARCHAR(255)          NULL,
    password_salt VARCHAR(255)          NULL,
    gender VARCHAR(50)          NULL,
    created_at     datetime              NULL,
    updated_at     datetime              NULL,
    deleted_at     datetime              NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    user_id  BIGINT NOT NULL,
    roles_id BIGINT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, roles_id)
);

ALTER TABLE session
    ADD CONSTRAINT FK_SESSION_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (roles_id) REFERENCES `role` (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES user (id);