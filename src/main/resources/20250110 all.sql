drop database if exists tia104g2;
CREATE DATABASE IF NOT EXISTS tia104g2;

USE tia104g2;

drop table IF EXISTS member;
drop table IF EXISTS administrator;
drop table IF EXISTS trip;
drop table IF EXISTS sub_trip;
drop table IF EXISTS location;
drop table IF EXISTS announcement;
drop table IF EXISTS location_comment;
drop table IF EXISTS trip_collection;
drop table IF EXISTS trip_like;
drop table IF EXISTS track_users;
drop table IF EXISTS itinerary_area;
drop table IF EXISTS itinerary_activity_type;
drop table IF EXISTS itinerary_activity_type_relationship;
drop table IF EXISTS trip_comment;
drop table IF EXISTS trip_location_relation;
drop table IF EXISTS trip_photo;
drop table IF EXISTS traffic_type;

-- 平台方 --
CREATE TABLE administrator (
administrator_id  			INT(11) NOT NULL AUTO_INCREMENT,
email						VARCHAR(50) NOT NULL ,
admin_account				VARCHAR(20) NOT NULL,
admin_password				VARCHAR(15) NOT NULL,
admin_name					VARCHAR(15) NOT NULL,
phone						VARCHAR(15) NOT NULL,
account_status				INT(1) 	NOT NULL,
create_time					timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
nick_name					VARCHAR(15) NOT NULL,
CONSTRAINT admin_id_pk PRIMARY KEY (administrator_id));

 INSERT INTO administrator(email, admin_account, admin_password ,admin_name, phone	,account_status,nick_name) VALUES ('seal@abc.com', 'seal123','seal456','seal','0952123456','1','seal');
 INSERT INTO administrator(email, admin_account, admin_password ,admin_name, phone	,account_status,nick_name) VALUES ('yuki@abc.com', 'yuki123','yuki456','yuki','0952123457','1','yuki');
 INSERT INTO administrator(email, admin_account, admin_password ,admin_name, phone	,account_status,nick_name) VALUES ('et@abc.com', 'et123','et456','et','0952123458','1','eatting');
 
 
-- 一般用戶表 --
CREATE TABLE member (
member_id			INT(11) NOT NULL AUTO_INCREMENT COMMENT '會員ID',
email			    VARCHAR(20) NOT NULL COMMENT '電子信箱',
account			    VARCHAR(20) NOT NULL COMMENT '帳號',
password			VARCHAR(15) NOT NULL COMMENT '密碼',
name			    VARCHAR(10) NOT NULL COMMENT '姓名',
phone			    VARCHAR(15) NOT NULL COMMENT '聯絡電話',
status			    INT(1) NOT NULL COMMENT '帳號狀態 (0: 一般狀態, 1: 黑名單)',
create_time			TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '帳號建立時間',
nick_name			VARCHAR(15) NOT NULL COMMENT '會員名稱',
gender			    INT(1) NOT NULL COMMENT '性別 (0: 男性, 1: 女性)',
birthday			DATE COMMENT '生日',
company_id			VARCHAR(8) COMMENT '公司統編',
E_receipt_carrier	VARCHAR(20) COMMENT '手機載具',
credit_card         VARCHAR(19) COMMENT '信用卡號',
tracking_number     INT(10) COMMENT '追蹤數',
fans_number         INT(10) COMMENT '粉絲數',
photo               LONGBLOB COMMENT '照片',
CONSTRAINT pk_member_id PRIMARY KEY (member_id),
CONSTRAINT uk_email UNIQUE (email),
CONSTRAINT uk_account UNIQUE (account),
CONSTRAINT uk_phone UNIQUE (phone)
)COMMENT ' 一般用戶表';

 INSERT INTO member(email,account,password,name,phone,status,create_time,nick_name,gender) VALUES ('seal@abc.com', 'seal123','seal456','seal','0952123456','0','2024-12-01 08:00:00','seal','1');
 INSERT INTO member(email,account,password,name,phone,status,create_time,nick_name,gender) VALUES ('yuki@abc.com', 'yuki123','yuki456','yuki','0952123457','0','2024-12-01 09:00:00','yuki','1');
 INSERT INTO member(email,account,password,name,phone,status,create_time,nick_name,gender) VALUES ('et@abc.com', 'et123','et456','et','0952123458','0','2024-12-01 10:00:00','eatting','0');


-- 行程 --
create table trip (
trip_id					int(11) not null auto_increment comment'行程ID',
member_id				int(11) not null comment'會員ID',
abstract				longtext comment'行程概述',
create_time				timestamp default current_timestamp not null comment'建立時間',
collections				int(8) comment'收藏數',
status				    int(1) not null comment'文章狀態',  -- 0收藏景點 1收藏文章 2儲存文章草稿 3用戶發表的公開文章 4用戶發表的私人文章 5用戶已刪除文章 6管理員刪除文章 --
overall_score			int(10) DEFAULT 0 comment'總評分',
overall_scored_people 	int(10) DEFAULT 0 comment'總評分人數',
location_number 		int(10) DEFAULT 0 comment'景點數', 
article_title 			varchar(30) not null comment'文章標題',
visitors_number			int(10) DEFAULT 0 comment'瀏覽人數',
likes					int(10) DEFAULT 0 comment'點讚數',
constraint fk_member_member_id
foreign key (member_id) references member(member_id),
constraint trip_id_pk primary key(trip_id)
)comment'行程表';

insert into trip (member_id,abstract,create_time,collections,status,overall_score,overall_scored_people,location_number,article_title,visitors_number,likes) values ('1','東京三日遊，第一天...第二天...第三天','2024-12-9','3','0','5.0','10','3','東京三日遊','3','10'); -- 我是東京 --
insert into trip (member_id,abstract,create_time,collections,status,overall_score,overall_scored_people,location_number,article_title,visitors_number,likes) values ('1','京都三日遊，第一天...第二天...第三天','2024-12-9','4','0','4.5','20','3','京都三日遊','3','11'); -- 我是京都 --
insert into trip (member_id,abstract,create_time,collections,status,overall_score,overall_scored_people,location_number,article_title,visitors_number,likes) values ('1','大阪三日遊，第一天...第二天...第三天','2024-12-9','5','0','4.0','30','3','大阪三日遊','3','12'); -- 我是大阪 --


-- 子行程 --

create table sub_trip(
sub_trip_id						int(11) not null auto_increment  comment'子行程ID',
trip_id							int(11) not null comment'行程ID',
`index`							int(2) not null comment'在行程中的順序',
content							longtext not null comment'文章內容',
constraint fk_sub_trip_trip_trip_id
foreign key(trip_id) references trip(trip_id),
constraint sub_trip_id primary key(sub_trip_id)
)comment'子行程表';

insert into sub_trip (trip_id,`index`,content) values ('1','1','東京三日遊，第一天去了大巨蛋、東京塔、澀谷'); -- 我是第一天 --
insert into sub_trip (trip_id,`index`,content) values ('1','2','東京三日遊，第二天去了迪士尼、淺草寺、原宿'); -- 我是第二天 --
insert into sub_trip (trip_id,`index`,content) values ('1','3','東京三日遊，第三天去了美術館、博物館、台場'); -- 我是第三天 --

-- 景點 --
CREATE TABLE location (
    location_id          INT(11) NOT NULL AUTO_INCREMENT COMMENT '景點ID',
    google_place_id      VARCHAR(255) NOT NULL COMMENT 'Google Place ID',
    location_name        VARCHAR(100) NOT NULL COMMENT 'Google景點名稱',
    address             LONGTEXT NOT NULL COMMENT 'Google地s址',
    latitude            DECIMAL(10,8) COMMENT 'Google緯度',
    longitude           DECIMAL(11,8) COMMENT 'Google經度',
    score               FLOAT(2,1) DEFAULT 0.0 COMMENT '平均評分',
    rating_count        INT DEFAULT 0 COMMENT '評分數量',
    comments_number     INT DEFAULT 0 COMMENT '評論數量',
    create_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '景點建立時間',
    update_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Google資料更新時間',
    
    CONSTRAINT pk_location_location_id PRIMARY KEY (location_id),
    UNIQUE KEY uk_google_place_id (google_place_id)
) COMMENT='景點表';
-- 赤崁樓
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJmyG9qoN2bjQRi5sGL5YH5YA',
    '赤崁樓',
    '700台南市中西區民族路二段212號',
    23.000375, 120.202665,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 安平古堡
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJB4m_qXB2bjQRXXa1ma6_5X0',
    '安平古堡',
    '708台南市安平區國勝路82號',
    23.001413, 120.160541,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 奇美博物館
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJ7WNrVuV1bjQRqL8VxOX_8VM',
    '奇美博物館',
    '717台南市仁德區文華路二段66號',
    22.934736, 120.226998,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 藍晒圖文創園區
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJkQv-Poh2bjQR-O8bx4gxcBo',
    '藍晒圖文創園區',
    '700台南市中西區西門路一段689巷',
    22.989629, 120.197934,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 林百貨
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJUYpnYYl2bjQRXHH7sxtQUMA',
    '林百貨',
    '700台南市中西區忠義路二段63號',
    22.991843, 120.198873,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 台南美術館2館
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJ_____4h2bjQRA1bR5O2k8vM',
    '台南美術館2館',
    '700台南市中西區忠義路二段1號',
    22.991277, 120.198479,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);


-- 台北101
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJL44r5l2rQjQRB8xzGyoS5Jw',
    '台北101',
    '110台北市信義區信義路五段7號',
    25.033976, 121.564472,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 國立故宮博物院
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJtd9Yn0upQjQRW_eXYgLrAQU',
    '國立故宮博物院',
    '111台北市士林區至善路二段221號',
    25.102355, 121.548493,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 中正紀念堂
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJtV7kRxupQjQRFr6JNyhXZ-8',
    '中正紀念堂',
    '100台北市中正區中山南路21號',
    25.034689, 121.521627,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 松山文創園區
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJLV6fpeCrQjQRR7K_15vXGD0',
    '松山文創園區',
    '110台北市信義區光復南路133號',
    25.043888, 121.560349,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 龍山寺
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJ27R_N3epQjQRn8qcKog0FJQ',
    '龍山寺',
    '108台北市萬華區廣州街211號',
    25.037100, 121.499960,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 北投圖書館
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJqSDYxdCmQjQRFqPCkwGGJwY',
    '北投圖書館',
    '112台北市北投區光明路251號',
    25.137389, 121.506287,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 大安森林公園
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJSSr5Y8mrQjQRQeKuXJI9F3k',
    '大安森林公園',
    '106台北市大安區新生南路二段1號',
    25.031663, 121.535833,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);



-- 國立臺灣美術館
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJ7SJDghYWaTQRqxV6yW6M3qM',
    '國立臺灣美術館',
    '403台中市西區五權西路一段2號',
    24.139994, 120.662907,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 審計新村
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJkzd-YRkWaTQRFxuXxI6r8Pg',
    '審計新村',
    '403台中市西區民生路368巷',
    24.143514, 120.662249,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 台中歌劇院
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJB4PLFvEXaTQR9E0NHgHUXpE',
    '台中國家歌劇院',
    '407台中市西屯區惠來路二段101號',
    24.162690, 120.640740,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 草悟道
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJ3b54mxgWaTQRZnXD4Bo3-Aw',
    '草悟道',
    '403台中市西區市民廣場',
    24.144674, 120.662907,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 宮原眼科
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJE4ExJhsWaTQRXezXbwDfmyA',
    '宮原眼科',
    '400台中市中區中山路20號',
    24.137861, 120.683567,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 高美濕地
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJseMYlKYUaTQR3mxS4PwRACA',
    '高美濕地',
    '436台中市清水區美堤街',
    24.312368, 120.544137,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- 東海大學路思義教堂
INSERT INTO location (
    google_place_id, location_name, address, 
    latitude, longitude, score, rating_count, 
    comments_number, create_time, update_time
) VALUES (
    'ChIJ8cn7YckVaTQROPxkPLKVyYA',
    '東海大學路思義教堂',
    '407台中市西屯區台灣大道四段1727號',
    24.179851, 120.604669,
    0.0, 0, 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
-- 公告 FK 平台方 --
 
 CREATE TABLE announcement (
announcement_id         INT(11) NOT NULL AUTO_INCREMENT,
admin_id				INT NOT NULL ,
title					LONGTEXT not null,
content          		LONGTEXT NOT NULL ,
create_time			timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
start_time			DATE NOT NULL ,
end_time           DATE NOT NULL ,
cover_photo		LONGBLOB,

constraint fk_announcement_administrator_id
foreign key (admin_id) references administrator (administrator_id),
CONSTRAINT announcement_id_pk PRIMARY KEY (announcement_id));

 insert into announcement(admin_id,title,content,start_time,end_time,cover_photo) values('1','冬季旅遊推薦','不要出門','2024-12-01','2024-12-31',null);
 insert into announcement(admin_id,title,content,start_time,end_time,cover_photo) values('2','聖誕旅遊推薦','不要去耶誕城','2024-12-10','2024-12-26',null);
 insert into announcement(admin_id,title,content,start_time,end_time,cover_photo) values('3','過年旅遊推薦','不要去親戚家','2025-2-01','2024-2-15',null);

-- 景點留言 FK 會員  景點 --

Create table  location_comment(
location_comment_id 	INT(11) NOT NULL AUTO_INCREMENT,
member_id				INT(11) NOT NULL ,
location_id        		INT(11) NOT NULL ,
content           		LONGTEXT NOT NULL ,
photo					LONGBLOB,
score 					INT(1) NOT NULL,
create_time				timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
constraint fk_location_comment_member_member_id
foreign key (member_id) references member (member_id),
constraint fk__location_comment_location_location_id
foreign key (location_id) references location (location_id),
CONSTRAINT location_comment_id_pk PRIMARY KEY (location_comment_id));

insert into location_comment(member_id,location_id,content,photo,score) values('1','1','超讚不能只有我來過',null,'1');
insert into location_comment(member_id,location_id,content,photo,score) values('1','2','超讚不能只有我來過',null,'1');
insert into location_comment(member_id,location_id,content,photo,score) values('1','3','超讚不能只有我來過',null,'1');


-- 收藏行程  FK 行程 會員--
create table trip_collection(
trip_collection_id    	INT(11) NOT NULL AUTO_INCREMENT,
trip_id					INT(11) NOT NULL,
member_id				INT(11) NOT NULL,
create_time				timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
constraint fk_trip_collection_member_member_id
foreign key (member_id) references member (member_id),
constraint fk_trip_collection_trip_trip_id
foreign key (trip_id) references trip (trip_id),
constraint trip_collection_id_pk primary key (trip_collection_id));

insert into trip_collection(trip_id,member_id) values('1','1');
insert into trip_collection(trip_id,member_id) values('2','2');
insert into trip_collection(trip_id,member_id) values('3','3');
-- 按讚行程  FK 行程 會員--
create table trip_like(
trip_like_id    		INT(11) NOT NULL AUTO_INCREMENT,
trip_id					INT(11) NOT NULL,
member_id				INT(11) NOT NULL,
create_time				timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
constraint fk_trip_like_member_member_id
foreign key (member_id) references member (member_id),
constraint fk_trip_like_trip_trip_id
foreign key (trip_id) references trip (trip_id),
constraint trip_like_id_pk primary key (trip_like_id));

insert into trip_like(trip_id, member_id) values('1','1');
insert into trip_like(trip_id, member_id) values('2','2');
insert into trip_like(trip_id, member_id) values('3','3');

-- 追蹤用戶  FK 會員 --
create table track_users(
track_users_id			INT(11) NOT NULL AUTO_INCREMENT,
track_member_id			INT(11) NOT NULL,
being_tracked_member_id	INT(11) NOT NULL,
track_time				timestamp DEFAULT CURRENT_TIMESTAMP  NOT NULL,
constraint fk_track_users_track_member_id_member_member_id
foreign key (track_member_id) references member (member_id),
constraint fk_track_users_being_tracked_member_id_member_member_id
foreign key (being_tracked_member_id) references member (member_id),			
constraint track_users_id_pk primary key (track_users_id));

insert into track_users(track_member_id,being_tracked_member_id) values('1','2');
insert into track_users(track_member_id,being_tracked_member_id) values('2','3');
insert into track_users(track_member_id,being_tracked_member_id) values('3','2');
 
-- 行程地區表 --
 CREATE TABLE itinerary_area (
trip_location_id         INT(11) NOT NULL AUTO_INCREMENT COMMENT '行程地區ID',
trip_id                  INT(11) NOT NULL COMMENT '行程ID',
region_content           LONGTEXT  NOT NULL COMMENT'地區類型內容',
CONSTRAINT pk_trip_location_id PRIMARY KEY (trip_location_id),
CONSTRAINT fk_trip_id FOREIGN KEY (trip_id) REFERENCES trip(trip_id)
)COMMENT '行程地區表';

 insert into itinerary_area(trip_id,region_content) values('1','東京');
 insert into itinerary_area(trip_id,region_content) values('2','台北');
 insert into itinerary_area(trip_id,region_content) values('3','桃園');

-- 行程活動類型表 --
 CREATE TABLE itinerary_activity_type (
event_type_id         INT(11) NOT NULL AUTO_INCREMENT COMMENT '活動類型ID',
event_content         LONGTEXT  NOT NULL COMMENT '活動類型內容',
CONSTRAINT pk_event_type_id PRIMARY KEY (event_type_id)
)COMMENT '行程活動類型表';

 insert into itinerary_activity_type(event_content) values('觀光活動');
 insert into itinerary_activity_type(event_content) values('藝文活動');
 insert into itinerary_activity_type(event_content) values('空中活動');
 insert into itinerary_activity_type(event_content) values('海上活動');
 insert into itinerary_activity_type(event_content) values('體驗活動');
 insert into itinerary_activity_type(event_content) values('樂園活動');
 

-- 行程活動類型關係表 --
CREATE TABLE itinerary_activity_type_relationship (
    itinerary_activity_relationship_id INT(11) NOT NULL AUTO_INCREMENT COMMENT '行程活動類型關係ID',
    trip_id INT(11) NOT NULL COMMENT '行程ID',
    event_type_id INT(11) NOT NULL COMMENT '活動類型ID',
    CONSTRAINT pk_itinerary_activity_relationship_id PRIMARY KEY (itinerary_activity_relationship_id),
    CONSTRAINT fk_itinerary_activity_type_relationship_trip_id FOREIGN KEY (trip_id) REFERENCES trip(trip_id),
    CONSTRAINT fk_itinerary_activity_type_relationship_event_type_id FOREIGN KEY (event_type_id) REFERENCES itinerary_activity_type(event_type_id)
) COMMENT '行程活動類型關係表';

 insert into itinerary_activity_type_relationship(trip_id,event_type_id) values(1,1);
insert into itinerary_activity_type_relationship(trip_id,event_type_id) values(1,2);
insert into itinerary_activity_type_relationship(trip_id,event_type_id) values(1,3);


-- 行程留言表 --
CREATE TABLE trip_comment (
    trip_comment_id INT(11) NOT NULL AUTO_INCREMENT COMMENT '行程留言ID',
    member_id INT(11) NOT NULL COMMENT '會員ID',
    trip_id INT(11) NOT NULL COMMENT '行程ID',
    score INT(1) NOT NULL COMMENT '評分',
    photo LONGBLOB COMMENT '照片',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    content LONGTEXT NOT NULL COMMENT '留言內容',
    CONSTRAINT pk_trip_comment_id PRIMARY KEY (trip_comment_id),
    CONSTRAINT fk_trip_comment_member_id FOREIGN KEY (member_id) REFERENCES member(member_id),
    CONSTRAINT fk_trip_comment_trip_id FOREIGN KEY (trip_id) REFERENCES trip(trip_id)
) COMMENT '行程留言表';


insert into trip_comment(member_id,trip_id,score,photo,create_time,content) values('1','1','5',null,'2024-12-01 15:30:45','這個行程太讚了吧!好想馬上照著行程去玩~');
insert into trip_comment(member_id,trip_id,score,photo,create_time,content) values('2','1','4',null,'2024-12-01 12:30:45','我也想要照這個行程安排旅行');
insert into trip_comment(member_id,trip_id,score,photo,create_time,content) values('3','1','4',null,'2024-12-01 13:30:45','好想馬上出發去旅遊哦~');


-- 行程景點關係 --
create table trip_location_relation(
trip_location_relation_id				int(11) not null auto_increment comment'行程景點關係ID',
sub_trip_id 					int(11) not null comment'子行程ID',
location_id						int(11) not null comment'景點ID',
`index`							int(2) not null comment'在子行程中的順序',
time_start						timestamp comment '景點開始時間',
time_end						timestamp comment'景點結束時間',
constraint fk_trip_location_relation_sub_trip_sub_trip_id
foreign key(sub_trip_id) references sub_trip(sub_trip_id),
constraint fk_trip_location_relation_location_location_id
foreign key(location_id)references location(location_id),
constraint trip_location_relation_id_pk primary key(trip_location_relation_id))comment '行程景點關係表';

insert into trip_location_relation(sub_trip_id,location_id,`index`,time_start,time_end)values('1','1','1','2024-12-12 8:00','2024-12-12 9:00'); -- 我是行程1拿出來的大巨蛋 --
insert into trip_location_relation(sub_trip_id,location_id,`index`,time_start,time_end)values('1','2','3','2024-12-12 10:00','2024-12-12 11:00'); -- 我是行程1拿出來的東京塔 --
insert into trip_location_relation(sub_trip_id,location_id,`index`,time_start,time_end)values('1','3','5','2024-12-12 12:00','2024-12-12 13:00'); -- 我是行程1拿出來的澀谷 --

-- 行程照片 --
create table trip_photo(
trip_photo_id				int(11) not null auto_increment,
trip_id						int(11) not null,
photo						longblob,
photo_type					int(1),				-- 0封面照片，1內文照片 --
constraint fk_trip_photo_trip_trip_id
foreign key(trip_id) references trip(trip_id),
constraint trip_photo_id primary key(trip_photo_id));
-- 照片記得換成自己的 --
insert into trip_photo (trip_id, photo, photo_type) values ('1','20BB660D-1CF8-4564-9642-1DCA0407BAC6.JPG','0');


-- 交通方式 --
create table traffic_type(
traffic_type_id		int(11) not null auto_increment comment'交通方式ID',
`type`				int(1) not null comment'類型', -- 0未選擇,1開車,2火車,3捷運,4公車,5高鐵,6走路,7飛機 --
time_spent			int(11) comment'花費時間',
`index`				int(2) comment'在子行程中的順序',
trip_location_relation_id	int(11) not null comment'景點行程關係',
constraint pk_traffic_type_id primary key (traffic_type_id),
constraint fk_traffic_type_trip_location_relation_id foreign key (trip_location_relation_id) references trip_location_relation(trip_location_relation_id)
)comment '交通方式表';

insert into traffic_type (`type`,time_spent,`index`,trip_location_relation_id) values ('1','45','1','1');
insert into traffic_type (`type`,time_spent,`index`,trip_location_relation_id) values ('1','50','6','1');


