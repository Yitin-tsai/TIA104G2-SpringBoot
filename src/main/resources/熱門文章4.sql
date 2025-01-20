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
    '台中的藝術氣息越來越濃厚！這次的行程將帶您探索台中最具特色的文創景點。從充滿實驗性的台中國家歌劇院到保有舊時代風華的審計新村，再到藝術氣息濃厚的國立台灣美術館，每個景點都蘊含著獨特的故事。漫步在草悟道上，感受台中獨特的文創能量。特別推薦傍晚時分到美術館園區，欣賞台中最美的藝術風景。',
    '2024-01-10 10:00:00',
    160,
    3,
    425,
    90,
    4,
    '台中文青藝術之旅：走進城市的文創靈魂',
    1850,
    440
);

SET @trip_id = LAST_INSERT_ID();

-- 2. 建立第一天的子行程
INSERT INTO sub_trip (trip_id, `index`, content) VALUES (
    @trip_id,
    1,
    '【Day 1：當代建築與文創能量】\n\n
首站來到台中國家歌劇院，這座由日本建築大師伊東豊雄設計的建築本身就是一件藝術品。建議先在一樓大廳感受建築的獨特曲線，特別是充滿曲線美的「美聲涵洞」。如果時間允許，建議參加建築導覽，了解更多設計巧思。拍照重點：建議使用廣角鏡頭，才能完整捕捉建築的曲線美。\n\n
中午前往審計新村，這裡是台中文青必訪景點。老屋改建的特色小店和文創商店，充滿復古情懷。建議預留充足時間慢慢逛，每個轉角都可能有驚喜。特別推薦在裡面的文青咖啡廳享用午餐，感受老屋新生的魅力。\n\n
傍晚時分，到草悟道散步。這條綠意盎然的藝術步道串連了許多藝文景點，沿途可以欣賞街頭藝術和裝置藝術。特別推薦傍晚時分來訪，夕陽和綠蔭相映成趣，非常適合拍照。'
);

SET @first_day_subtrip_id = LAST_INSERT_ID();

-- 3. 建立第二天的子行程
INSERT INTO sub_trip (trip_id, `index`, content) VALUES (
    @trip_id,
    2,
    '【Day 2：藝術與歷史的對話】\n\n
早上來到國立台灣美術館，這是台灣最早成立的美術館。建議先在美術館咖啡廳享用早餐，一邊欣賞庭院的藝術雕塑。美術館常有精彩的特展，記得提前查詢展覽資訊。館內攝影需注意規則，部分展區可能會限制拍照。\n\n
下午可以繼續在美術園區附近探索。這裡不只有美術館，周邊還有許多藝廊和文創店家。特別推薦參觀美術館對面的藝文空間，經常有新銳藝術家的作品展出。\n\n
傍晚可以回到草悟道，體驗不同時段的氛圍。夜晚的草悟道有另一種美，燈光設計為整條步道增添了浪漫氣息。週末時還可能遇到街頭藝人表演，為旅程增添意想不到的驚喜。'
);

SET @second_day_subtrip_id = LAST_INSERT_ID();

-- 4. 建立景點關係
INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @first_day_subtrip_id, location_id, 1, '2024-01-10 09:00:00', '2024-01-10 12:00:00'
FROM location WHERE location_name = '台中國家歌劇院';

INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @first_day_subtrip_id, location_id, 2, '2024-01-10 13:00:00', '2024-01-10 15:00:00'
FROM location WHERE location_name = '審計新村';

INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @first_day_subtrip_id, location_id, 3, '2024-01-10 16:00:00', '2024-01-10 18:00:00'
FROM location WHERE location_name = '草悟道';

INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @second_day_subtrip_id, location_id, 1, '2024-01-11 09:30:00', '2024-01-11 16:00:00'
FROM location WHERE location_name = '國立臺灣美術館';

-- 5. 新增地區標籤
INSERT INTO itinerary_area (trip_id, region_content)
VALUES (@trip_id, '臺中市');

-- 6. 新增活動類型標籤
INSERT INTO itinerary_activity_type_relationship (trip_id, event_type_id)
SELECT @trip_id, event_type_id
FROM itinerary_activity_type 
WHERE event_content IN ('藝文展覽活動', '文創市集活動');

-- 提交事務
COMMIT;