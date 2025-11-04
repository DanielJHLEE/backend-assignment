/*IGNORE 구문 사용 중복방지*/
INSERT IGNORE INTO user (name, email, created_at) VALUES
('이민호', 'minho@example.com', NOW()),
('김지수', 'jisu@example.com', NOW()),
('박서준', 'seojun@example.com',NOW()),
('최유진', 'yujin@example.com', NOW()),
('한예슬', 'yesle@example.com', NOW());