USE tia104g2;

START TRANSACTION;

-- 設定變數
SET @trip_id = NULL;
SET @first_day_subtrip_id = NULL;
SET @second_day_subtrip_id = NULL;

-- 1. 建立主表資料
INSERT INTO trip (
    member_id, abstract, create_time, collections, status,
    overall_score, overall_scored_people, location_number,
    article_title, visitors_number, likes
) VALUES (
    1,
    '台北不只有熱鬧的街區，更藏著許多充滿特色的咖啡廳！這次特別規劃了文青咖啡廳路線，從松山文創園區的藝術風格咖啡廳，到大安森林公園周邊的隱藏版店家，帶您探索台北最有質感的咖啡風景。除了品嚐精品咖啡，更能享受充滿藝術氣息的空間設計與美味甜點。每家店都有其獨特故事，讓我們一起感受台北的慢活時光。',
    '2024-01-07 10:00:00',
    120,
    3,
    380,
    85,
    4,
    '台北質感咖啡館：2天1夜的慢活文青之旅',
    1800,
    420
);

SET @trip_id = LAST_INSERT_ID();

-- 2. 建立第一天的子行程
INSERT INTO sub_trip (trip_id, `index`, content) VALUES (
    @trip_id,
    1,
    '【Day 1：松山文創咖啡日】\n\n
今天的行程從松山文創園區開始。這裡不只有展覽，更有許多別具特色的咖啡店。園區內的工業風格空間，搭配香醇咖啡，是開啟一天的最佳選擇。特別推薦二號倉庫旁的文創店家，可以一邊喝咖啡一邊欣賞文創作品。\n\n
下午前往台北101附近的知名咖啡店，高樓層的視野配上精品咖啡，讓人彷彿置身雲端。傍晚時分，101的夕陽搭配咖啡香，是放鬆身心的絕佳時刻。'
);

SET @first_day_subtrip_id = LAST_INSERT_ID();

-- 3. 建立第二天的子行程
INSERT INTO sub_trip (trip_id, `index`, content) VALUES (
    @trip_id,
    2,
    '【Day 2：大安森林咖啡散策】\n\n
大安森林公園周邊藏著許多特色咖啡館。早晨先在公園晨練，感受清新的晨光。接著走訪周邊的文青咖啡店，每家都有其獨特的風格，從北歐簡約到日式禪風，讓人彷彿環遊世界。\n\n
下午來到公園對面的師大商圈，這裡的小型咖啡館各具特色，很多都是由年輕店主親手打造的理想空間。特別推薦巷弄內的隱藏版甜點咖啡店，限量手作甜點配上單品咖啡，是完美的午後時光。'
);

SET @second_day_subtrip_id = LAST_INSERT_ID();

-- 4. 建立景點關係
INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @first_day_subtrip_id, location_id, 1, '2024-01-07 09:00:00', '2024-01-07 12:00:00'
FROM location WHERE location_name = '松山文創園區';

INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @first_day_subtrip_id, location_id, 2, '2024-01-07 14:00:00', '2024-01-07 17:00:00'
FROM location WHERE location_name = '台北101';

INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @second_day_subtrip_id, location_id, 1, '2024-01-08 09:00:00', '2024-01-08 12:00:00'
FROM location WHERE location_name = '大安森林公園';

-- 5. 新增地區標籤
INSERT INTO itinerary_area (trip_id, region_content)
VALUES (@trip_id, '臺北市');

-- 6. 新增活動類型標籤
INSERT INTO itinerary_activity_type_relationship (trip_id, event_type_id)
SELECT @trip_id, event_type_id
FROM itinerary_activity_type 
WHERE event_content IN ('文創市集活動', '藝文展覽活動');

-- 提交事務
COMMIT;