USE tia104g2;
START TRANSACTION;

-- 設定變數
SET @trip_id = NULL;
SET @first_day_subtrip_id = NULL;
SET @second_day_subtrip_id = NULL;
SET @third_day_subtrip_id = NULL;

-- 1. 建立主表資料
INSERT INTO trip (
    member_id, abstract, create_time, collections, status,
    overall_score, overall_scored_people, location_number,
    article_title, visitors_number, likes
) VALUES (
    1,
    '跟著在地人David深入探索府城台南！這次精心規劃了三天兩夜的行程，不只有必訪的古蹟與美食，更帶您深入巷弄，發現不為人知的台南之美。從古色古香的赤崁樓到充滿文創能量的藍晒圖文創園區，從傳統小吃到文青咖啡廳，讓您體驗最道地的台南生活。特別推薦林百貨的夕陽時分，站在頂樓眺望整個城市，彷彿時光倒流，重返老台南最繁華的年代。',
    '2024-01-05 09:00:00',
    150,
    3,
    450,
    100,
    6,
    '府城風華：跟著在地人玩台南三天兩夜深度文化之旅',
    2000,
    500
);

SET @trip_id = LAST_INSERT_ID();

-- 2. 建立三天的子行程
INSERT INTO sub_trip (trip_id, `index`, content) VALUES (
    @trip_id,
    1,
    '【Day 1：古都文化探索】\n\n首站來到台南地標「赤崁樓」，建議一早就來避開人潮。這裡不只是觀光景點，更是了解台南歷史的重要場所。建築融合了荷蘭、漢人文化，每一塊磚瓦都訴說著精彩的故事。特別推薦在一樓的展示區細細閱讀歷史文物，再登上二樓飽覽四周風景。\n\n接著步行前往「藍晒圖文創園區」，這裡原是荒廢的老屋建築，現在搖身一變成為文青打卡熱點。園區內不只有標誌性的藍晒圖壁畫，更有許多文創商店和展覽空間。建議午餐就在園區內的文青餐廳用餐，體驗新舊融合的台南味。\n\n下午造訪「林百貨」，這棟建於日治時期的百貨公司，完整保留了當時的建築之美。特別推薦傍晚時分前往頂樓的神社遺跡，夕陽灑落的光影特別迷人，是拍照打卡的絕佳位置。'
);

SET @first_day_subtrip_id = LAST_INSERT_ID();

INSERT INTO sub_trip (trip_id, `index`, content) VALUES (
    @trip_id,
    2,
    '【Day 2：藝術與歷史的對話】\n\n早上先前往「台南美術館2館」，這裡不只有精彩的藝術展覽，建築本身就是一件藝術品。建議先在一樓的咖啡廳享用早餐，感受充滿藝術氣息的清晨時光。館內不定期舉辦各種特展，展現傳統與當代藝術的精彩對話。\n\n下午來到「安平古堡」，這座台灣最古老的城堡擁有400多年歷史。建議請導覽人員帶路，細細品味每個角落的歷史故事。特別推薦登上瞭望台，欣賞安平港的迷人風光。結束參觀後，可以在古堡附近的老街享用道地的安平小吃。'
);

SET @second_day_subtrip_id = LAST_INSERT_ID();

INSERT INTO sub_trip (trip_id, `index`, content) VALUES (
    @trip_id,
    3,
    '【Day 3：文化與生活的交融】\n\n最後一天來到「奇美博物館」，這座歐式宮殿風格的博物館是台南人的驕傲。建議一早就來排隊入場，館內收藏豐富的西洋藝術品和樂器，處處都是驚喜。特別推薦音樂廳區域，不定時有現場演出。戶外的噴泉和雕塑公園也非常適合散步拍照。\n\n感謝大家跟著我一起探索台南！這座城市的美，不只在於古蹟與美食，更在於巷弄間的人情味。希望這份行程能幫助大家發現不一樣的台南風景。'
);

SET @third_day_subtrip_id = LAST_INSERT_ID();

-- 3. 建立景點關係
INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @first_day_subtrip_id, location_id, 1, '2024-01-05 09:00:00', '2024-01-05 11:00:00'
FROM location WHERE location_name = '赤崁樓';

INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @first_day_subtrip_id, location_id, 2, '2024-01-05 11:30:00', '2024-01-05 14:30:00'
FROM location WHERE location_name = '藍晒圖文創園區';

INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @first_day_subtrip_id, location_id, 3, '2024-01-05 15:00:00', '2024-01-05 17:30:00'
FROM location WHERE location_name = '林百貨';

INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @second_day_subtrip_id, location_id, 1, '2024-01-06 09:30:00', '2024-01-06 12:30:00'
FROM location WHERE location_name = '台南美術館2館';

INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @second_day_subtrip_id, location_id, 2, '2024-01-06 14:00:00', '2024-01-06 17:00:00'
FROM location WHERE location_name = '安平古堡';

INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @third_day_subtrip_id, location_id, 1, '2024-01-07 09:30:00', '2024-01-07 15:30:00'
FROM location WHERE location_name = '奇美博物館';

-- 4. 新增地區標籤
INSERT INTO itinerary_area (trip_id, region_content)
VALUES (@trip_id, '臺南市');

-- 5. 新增活動類型標籤
INSERT INTO itinerary_activity_type_relationship (trip_id, event_type_id)
SELECT @trip_id, event_type_id
FROM itinerary_activity_type 
WHERE event_content IN ('古蹟巡禮活動', '藝文展覽活動');

-- 如果所有操作都成功，提交事務
COMMIT;

-- 如果發生錯誤，自動回滾
-- ROLLBACK;