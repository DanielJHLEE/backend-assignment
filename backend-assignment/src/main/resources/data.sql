-- 🟢 기존 데이터 더미데이터 user, product테이블 삭제
DELETE FROM user;
DELETE FROM product;

-- 🟢 USER유저 데이터 초기 더미 데이터
INSERT INTO user (name, email, created_at) VALUES
('이민호', 'minho@example.com', NOW()),
('김지수', 'jisu@example.com', NOW()),
('박서준', 'seojun@example.com',NOW()),
('최유진', 'yujin@example.com', NOW()),
('한예슬', 'yesle@example.com', NOW());

-- 🟢 PRODUCT 초기 더미 데이터
INSERT INTO product (name, brand, category, price, stock, sold_out, created_at) VALUES
-- 👟 신발
('에어맥스 270', '나이키', '신발', 159000, 15, FALSE, NOW()),
('울트라부스트 22', '아디다스', '신발', 189000, 8, FALSE, NOW()),
('첵테일 클래식', '컨버스', '신발', 69000, 0, TRUE, NOW()),
('조던 1 미드', '나이키', '신발', 179000, 3, FALSE, NOW()),

-- 👕 의류
('드라이핏 티셔츠', '나이키', '의류', 39000, 25, FALSE, NOW()),
('무신사 스탠다드 셔츠', '무신사', '의류', 29000, 0, TRUE, NOW()),
('리바이스 501 진', '리바이스', '의류', 99000, 12, FALSE, NOW()),
('유니클로 히트텍', '유니클로', '의류', 19900, 0, TRUE, NOW()),

-- 💻 전자제품
('갤럭시 S24', '삼성전자', '전자제품', 1299000, 5, FALSE, NOW()),
('아이폰 15', '애플', '전자제품', 1550000, 0, TRUE, NOW()),
('LG 그램 16', 'LG전자', '전자제품', 1890000, 7, FALSE, NOW()),
('에어팟 프로 2세대', '애플', '전자제품', 359000, 10, FALSE, NOW()),

-- 🍜 식품
('진라면 매운맛', '오뚜기', '식품', 4500, 100, FALSE, NOW()),
('삼다수 2L', '제주개발공사', '식품', 1200, 300, FALSE, NOW()),
('허니버터칩', '해태', '식품', 1800, 0, TRUE, NOW()),
('비비고 왕교자', 'CJ제일제당', '식품', 7980, 200, FALSE, NOW());