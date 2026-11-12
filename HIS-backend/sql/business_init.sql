-- 业务表初始化脚本（数据库 postgres，可重复执行）
-- 说明：sys_dept、sys_user 已存在，这里只补齐后端实体需要的表和字段。

-- 职称表
create table if not exists sys_title (
    id          bigserial primary key,
    title_name  varchar(50) not null,
    reg_level   varchar(50),
    reg_fee     numeric(10, 2) not null default 0,
    description varchar(255),
    enabled     boolean not null default true,
    create_date date not null default current_date,
    create_time time not null default current_time,
    update_date date not null default current_date,
    update_time time not null default current_time
);

-- 患者信息表
create table if not exists sys_patient (
    id           bigserial primary key,
    patient_name varchar(50) not null,
    gender       char(1) not null default '0',
    birthday     date,
    phone        varchar(20),
    id_card      varchar(18),
    address      varchar(255),
    enabled      boolean not null default true,
    create_date  date not null default current_date,
    create_time  time not null default current_time,
    update_date  date not null default current_date,
    update_time  time not null default current_time
);

create unique index if not exists uk_sys_patient_id_card
    on sys_patient (id_card) where id_card is not null;

-- 挂号记录表
create table if not exists sys_registration (
    id          bigserial primary key,
    reg_no      varchar(32) not null,
    patient_id  bigint,
    dept_id     bigint,
    doctor_id   bigint,
    title_id    bigint,
    reg_fee     numeric(10, 2) not null default 0,
    reg_date    date,
    reg_period  char(1),
    reg_type    char(1),
    status      char(1) not null default '1',
    remark      varchar(255),
    create_date date not null default current_date,
    create_time time not null default current_time,
    update_date date not null default current_date,
    update_time time not null default current_time
);

create unique index if not exists uk_sys_registration_reg_no
    on sys_registration (reg_no);

-- sys_user 对齐后端实体：补 title_id、intro，并给 username 默认值（接口新增用户不传 username）
alter table sys_user add column if not exists title_id bigint;
alter table sys_user add column if not exists intro varchar(500);
alter table sys_user alter column username set default '';

-- 初始化职称字典
insert into sys_title (title_name, reg_level, reg_fee, description, enabled)
select v.title_name, v.reg_level, v.reg_fee, v.description, true
from (values
    ('主任医师', '专家号', 50.00, '科室带头人，擅长疑难重症'),
    ('副主任医师', '副主任医师号', 30.00, '临床经验丰富，擅长专科疾病'),
    ('主治医师', '主治医师号', 20.00, '常见病、多发病诊治'),
    ('住院医师', '普通号', 10.00, '基础诊疗')
) as v(title_name, reg_level, reg_fee, description)
where not exists (select 1 from sys_title t where t.title_name = v.title_name);

-- 给现有医生补职称和简介（挂号费按职称自动带出）
update sys_user u set title_id = t.id, intro = '擅长高血压、冠心病、心律失常的诊治'
from sys_title t where t.title_name = '主任医师' and u.employee_no = 'D000001';

update sys_user u set title_id = t.id, intro = '擅长冠心病、心力衰竭的诊治'
from sys_title t where t.title_name = '主治医师' and u.employee_no = 'D000002';

update sys_user u set title_id = t.id, intro = '擅长慢性支气管炎、肺炎、哮喘的诊治'
from sys_title t where t.title_name = '副主任医师' and u.employee_no = 'D000003';
