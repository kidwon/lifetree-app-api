-- 数据库初始化脚本
-- 用于创建生命树应用所需的所有表结构

-- 创建扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 用户表
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                             );

-- 创建邮箱索引
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- 需求表
CREATE TABLE IF NOT EXISTS requirements (
                                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                             );

-- 创建需求索引
CREATE INDEX IF NOT EXISTS idx_requirements_created_by ON requirements(created_by);
CREATE INDEX IF NOT EXISTS idx_requirements_status ON requirements(status);

-- 需求申请表
CREATE TABLE IF NOT EXISTS requirement_applications (
                                                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requirement_id UUID NOT NULL REFERENCES requirements(id) ON DELETE CASCADE,
    applicant_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                                 );

-- 创建需求申请索引
CREATE INDEX IF NOT EXISTS idx_requirement_applications_requirement ON requirement_applications(requirement_id);
CREATE INDEX IF NOT EXISTS idx_requirement_applications_applicant ON requirement_applications(applicant_id);
CREATE INDEX IF NOT EXISTS idx_requirement_applications_status ON requirement_applications(status);

-- 结果表
CREATE TABLE IF NOT EXISTS results (
                                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    related_requirement_id UUID REFERENCES requirements(id),
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                             );

-- 创建结果索引
CREATE INDEX IF NOT EXISTS idx_results_created_by ON results(created_by);
CREATE INDEX IF NOT EXISTS idx_results_status ON results(status);
CREATE INDEX IF NOT EXISTS idx_results_related_requirement ON results(related_requirement_id);

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
                                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                             );

-- 需求-标签关联表
CREATE TABLE IF NOT EXISTS requirement_tags (
                                                requirement_id UUID NOT NULL REFERENCES requirements(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (requirement_id, tag_id)
    );

-- 结果-标签关联表
CREATE TABLE IF NOT EXISTS result_tags (
                                           result_id UUID NOT NULL REFERENCES results(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (result_id, tag_id)
    );

-- 创建审计日志表
CREATE TABLE IF NOT EXISTS audit_logs (
                                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    user_id UUID REFERENCES users(id),
    change_data JSONB,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                            );

-- 创建审计日志索引
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);

-- 创建通知表
CREATE TABLE IF NOT EXISTS notifications (
                                             id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                   );

-- 创建通知索引
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_unread ON notifications(user_id, is_read) WHERE is_read = FALSE;

-- 刷新令牌表
CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                   );

-- 创建刷新令牌索引
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires ON refresh_tokens(expires_at);

-- 添加触发器自动更新updated_at字段
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为用户表添加触发器
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 为需求表添加触发器
CREATE TRIGGER update_requirements_updated_at
    BEFORE UPDATE ON requirements
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 为结果表添加触发器
CREATE TRIGGER update_results_updated_at
    BEFORE UPDATE ON results
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 为需求申请表添加触发器
CREATE TRIGGER update_requirement_applications_updated_at
    BEFORE UPDATE ON requirement_applications
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 添加一些初始测试数据
INSERT INTO users (id, email, name, password_hash, role, active)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'admin@example.com', '管理员', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', true), -- 密码: password123
    ('22222222-2222-2222-2222-222222222222', 'user@example.com', '普通用户', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', true); -- 密码: password123

-- 添加一些需求测试数据
INSERT INTO requirements (id, title, description, status, created_by)
VALUES
    ('33333333-3333-3333-3333-333333333333', '移动应用开发', '开发一个移动版应用，包含需求列表、结果列表等功能', 'CREATED', '11111111-1111-1111-1111-111111111111'),
    ('44444444-4444-4444-4444-444444444444', '网页设计优化', '对现有网页进行设计优化，提升用户体验和交互效果', 'IN_PROGRESS', '22222222-2222-2222-2222-222222222222'),
    ('55555555-5555-5555-5555-555555555555', '后端服务开发', '开发RESTful API服务，支持前端应用的数据需求', 'COMPLETED', '11111111-1111-1111-1111-111111111111');

-- 添加一些结果测试数据
INSERT INTO results (id, title, description, status, related_requirement_id, created_by)
VALUES
    ('66666666-6666-6666-6666-666666666666', '应用原型设计', '完成了移动应用的原型设计，包括所有主要页面的布局和交互逻辑', 'DRAFT', '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111'),
    ('77777777-7777-7777-7777-777777777777', 'UI界面优化方案', '完成了网页UI优化方案，包括新的色彩方案、字体选择和布局调整', 'COMPLETED', '44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222'),
    ('88888888-8888-8888-8888-888888888888', 'API接口文档', '完成了RESTful API接口文档，详细描述了所有接口的参数、返回值和使用方法', 'COMPLETED', '55555555-5555-5555-5555-555555555555', '11111111-1111-1111-1111-111111111111');

-- 添加一些标签数据
INSERT INTO tags (id, name)
VALUES
    ('99999999-9999-9999-9999-999999999999', '高优先级'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '设计'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '开发'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '文档');

-- 关联需求和标签
INSERT INTO requirement_tags (requirement_id, tag_id)
VALUES
    ('33333333-3333-3333-3333-333333333333', '99999999-9999-9999-9999-999999999999'),
    ('33333333-3333-3333-3333-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
    ('44444444-4444-4444-4444-444444444444', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
    ('55555555-5555-5555-5555-555555555555', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

-- 关联结果和标签
INSERT INTO result_tags (result_id, tag_id)
VALUES
    ('66666666-6666-6666-6666-666666666666', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
    ('77777777-7777-7777-7777-777777777777', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
    ('88888888-8888-8888-8888-888888888888', 'cccccccc-cccc-cccc-cccc-cccccccccccc');

-- 添加一些需求申请测试数据
INSERT INTO requirement_applications (id, requirement_id, applicant_id, status, created_at, updated_at)
VALUES
    ('aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', '33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 将一个需求状态更新为确认中
UPDATE requirements SET status = 'CONFIRMING' WHERE id = '33333333-3333-3333-3333-333333333333';