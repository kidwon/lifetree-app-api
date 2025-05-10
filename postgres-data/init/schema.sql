-- 数据库初始化脚本
-- 用于创建生命树应用所需的所有表结构

-- 创建扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 用户表
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- 创建需求索引
CREATE INDEX IF NOT EXISTS idx_requirements_created_by ON requirements(created_by);
CREATE INDEX IF NOT EXISTS idx_requirements_status ON requirements(status);

-- 结果表
CREATE TABLE IF NOT EXISTS results (
                                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    related_requirement_id UUID REFERENCES requirements(id),
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- 创建结果索引
CREATE INDEX IF NOT EXISTS idx_results_created_by ON results(created_by);
CREATE INDEX IF NOT EXISTS idx_results_status ON results(status);
CREATE INDEX IF NOT EXISTS idx_results_related_requirement ON results(related_requirement_id);


-- 需求申请表 (新增)
CREATE TABLE IF NOT EXISTS requirement_applications (
                                                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    requirement_id UUID NOT NULL REFERENCES requirements(id) ON DELETE CASCADE,
    applicant_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                                 );

-- WebAuthn凭据表
CREATE TABLE IF NOT EXISTS webauthn_credentials (
                                                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    credential_id TEXT NOT NULL,
    public_key TEXT NOT NULL,
    counter BIGINT NOT NULL DEFAULT 0,
    credential_format VARCHAR(50) NOT NULL DEFAULT 'packed',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
                                                   );


-- 创建需求申请索引
CREATE INDEX IF NOT EXISTS idx_requirement_applications_requirement ON requirement_applications(requirement_id);
CREATE INDEX IF NOT EXISTS idx_requirement_applications_applicant ON requirement_applications(applicant_id);
CREATE INDEX IF NOT EXISTS idx_requirement_applications_status ON requirement_applications(status);
-- 创建索引
CREATE INDEX IF NOT EXISTS idx_webauthn_credentials_user_id ON webauthn_credentials(user_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_webauthn_credentials_credential_id ON webauthn_credentials(credential_id);

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

-- 更新需求表的status定义或确保支持新状态
-- 注意: 实际使用时检查你的数据库类型是否支持直接修改类型约束
-- 如果你的数据库支持枚举类型，并且status是一个枚举类型，则需要更新它，否则不需要对表结构进行修改
-- 例如在PostgreSQL中:
-- ALTER TYPE requirement_status ADD VALUE IF NOT EXISTS 'CONFIRMING';

-- 为需求申请表添加触发器自动更新updated_at字段
CREATE TRIGGER update_requirement_applications_updated_at
    BEFORE UPDATE ON requirement_applications
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 添加触发器自动更新updated_at字段
CREATE TRIGGER update_webauthn_credentials_updated_at
    BEFORE UPDATE ON webauthn_credentials
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 添加一些初始测试数据
INSERT INTO users (id, email, password_hash, name, role)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'admin@example.com', 'hashed_password_here', '管理员', 'ADMIN'),
    ('22222222-2222-2222-2222-222222222222', 'user@example.com', 'hashed_password_here', '普通用户', 'USER');

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