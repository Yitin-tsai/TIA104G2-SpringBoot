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

 
 
-- 一般用戶表 --
CREATE TABLE member (
member_id			INT(11) NOT NULL AUTO_INCREMENT COMMENT '會員ID',
email			    VARCHAR(100) NOT NULL COMMENT '電子信箱',
account			    VARCHAR(100) NOT NULL COMMENT '帳號',
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
likes					int(10) DEFAULT 0 comment'收藏數',
constraint fk_member_member_id
foreign key (member_id) references member(member_id),
constraint trip_id_pk primary key(trip_id)
)comment'行程表';



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


-- 景點 --
CREATE TABLE location (
    location_id          INT(11) NOT NULL AUTO_INCREMENT COMMENT '景點ID',
    google_place_id      VARCHAR(255) NOT NULL COMMENT 'Google Place ID',
    location_name        VARCHAR(100) NOT NULL COMMENT 'Google景點名稱',
    address             LONGTEXT NOT NULL COMMENT 'Google地址',
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

 
-- 行程地區表 --
 CREATE TABLE itinerary_area (
trip_location_id         INT(11) NOT NULL AUTO_INCREMENT COMMENT '行程地區ID',
trip_id                  INT(11) NOT NULL COMMENT '行程ID',
region_content           LONGTEXT  NOT NULL COMMENT'地區類型內容',
CONSTRAINT pk_trip_location_id PRIMARY KEY (trip_location_id),
CONSTRAINT fk_trip_id FOREIGN KEY (trip_id) REFERENCES trip(trip_id)
)COMMENT '行程地區表';


-- 行程活動類型表 --
 CREATE TABLE itinerary_activity_type (
event_type_id         INT(11) NOT NULL AUTO_INCREMENT COMMENT '活動類型ID',
event_content         LONGTEXT  NOT NULL COMMENT '活動類型內容',
CONSTRAINT pk_event_type_id PRIMARY KEY (event_type_id)
)COMMENT '行程活動類型表';

 

-- 行程活動類型關係表 --
CREATE TABLE itinerary_activity_type_relationship (
    itinerary_activity_relationship_id INT(11) NOT NULL AUTO_INCREMENT COMMENT '行程活動類型關係ID',
    trip_id INT(11) NOT NULL COMMENT '行程ID',
    event_type_id INT(11) NOT NULL COMMENT '活動類型ID',
    CONSTRAINT pk_itinerary_activity_relationship_id PRIMARY KEY (itinerary_activity_relationship_id),
    CONSTRAINT fk_itinerary_activity_type_relationship_trip_id FOREIGN KEY (trip_id) REFERENCES trip(trip_id),
    CONSTRAINT fk_itinerary_activity_type_relationship_event_type_id FOREIGN KEY (event_type_id) REFERENCES itinerary_activity_type(event_type_id)
) COMMENT '行程活動類型關係表';


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


-- 行程照片 --
create table trip_photo(
trip_photo_id				int(11) not null auto_increment,
trip_id						int(11) not null,
photo						longblob,
photo_type					int(1),				-- 0封面照片，1內文照片 --
constraint fk_trip_photo_trip_trip_id
foreign key(trip_id) references trip(trip_id),
constraint trip_photo_id primary key(trip_photo_id));


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



