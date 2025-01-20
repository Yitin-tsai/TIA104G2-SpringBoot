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
    '想逃離城市的喧囂嗎？讓我們一起到花蓮來趟療癒之旅！在太魯閣感受大自然的壯麗，在七星潭聆聽海浪的聲音。這次的行程特別精心規劃了最佳觀景時間，不只能欣賞壯闊的峽谷景觀，更能享受最美的晨昏時分。在地人推薦的秘境景點、最佳攝影點都在這篇文章中一次告訴你！適合喜歡親近自然，想遠離塵囂的旅人。',
    '2024-01-08 11:00:00',
    180,
    3,
    470,
    95,
    3,
    '花蓮大自然探索：太魯閣峽谷與七星潭的完美邂逅',
    1900,
    460
);

SET @trip_id = LAST_INSERT_ID();

-- 2. 建立第一天的子行程
INSERT INTO sub_trip (trip_id, `index`, content) VALUES (
    @trip_id,
    1,
    '【Day 1：太魯閣峽谷探索】\n\n
清晨出發前往太魯閣國家公園，越早到越能享受清新的空氣和避開人潮。園區內首先參觀長春祠，這個嵌在峭壁上的建築，背後是壯麗的峽谷，前方是湍急的立霧溪，是太魯閣最經典的景點。拍照建議：上午9-11點陽光最適合，可以拍到漂亮的光影。\n\n
接著沿著步道前往燕子口，這段路徑可以最近距離體驗太魯閣峽谷的氣勢。需注意配戴安全帽，也建議攜帶手電筒，因為有些隧道較暗。沿途有許多觀景台，都是絕佳的攝影點。午餐可以在遊客中心附近用餐，或是自備輕食野餐。\n\n
下午前往九曲洞步道，這裡是太魯閣最驚豔的景點之一。步道依著峭壁開鑿，腳下是湍急的立霧溪，遠方是層層疊疊的大理石峽谷，讓人不禁讚嘆大自然的鬼斧神工。特別提醒：記得查詢步道開放時間，有時會因為落石維修而關閉。'
);

SET @first_day_subtrip_id = LAST_INSERT_ID();

-- 3. 建立第二天的子行程
INSERT INTO sub_trip (trip_id, `index`, content) VALUES (
    @trip_id,
    2,
    '【Day 2：七星潭海濱悠遊】\n\n
今天的行程較為輕鬆，早上可以稍晚出發。七星潭是一個新月形的海灣，雖然名為潭，但其實是一個美麗的海灣。這裡最特別的是遍佈的青綠色石頭，每顆都經過海浪千百次的打磨，晶瑩剔透。建議可以漫步在石礫灘上，尋找自己喜歡的小石頭，但記得不要帶走喔！\n\n
特別推薦在日出時分來訪，清晨的七星潭特別寧靜，看著太陽從海平面緩緩升起，絕對是難忘的體驗。喜歡攝影的朋友，更不能錯過這個絕佳的拍攝時機。在北側的觀景平台，可以同時拍到海景和遠方的山景，構圖相當豐富。\n\n
午後可以在海邊悠閒漫步，或是租台腳踏車沿著海岸線騎行。自行車道規劃完善，沿途可以欣賞美麗的太平洋風光。記得準備防曬用品，因為海邊陽光較強。結束行程前，別忘了在附近的餐廳享用一頓海鮮餐，為旅程畫下完美句點。'
);

SET @second_day_subtrip_id = LAST_INSERT_ID();

-- 4. 建立景點關係
INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @first_day_subtrip_id, location_id, 1, '2024-01-08 08:00:00', '2024-01-08 17:00:00'
FROM location WHERE location_name = '太魯閣國家公園';

INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end)
SELECT @second_day_subtrip_id, location_id, 1, '2024-01-09 05:30:00', '2024-01-09 14:00:00'
FROM location WHERE location_name = '七星潭';

-- 5. 新增地區標籤
INSERT INTO itinerary_area (trip_id, region_content)
VALUES (@trip_id, '花蓮縣');

-- 6. 新增活動類型標籤
INSERT INTO itinerary_activity_type_relationship (trip_id, event_type_id)
SELECT @trip_id, event_type_id
FROM itinerary_activity_type 
WHERE event_content IN ('生態探索活動', '攝影寫真活動');

-- 提交事務
COMMIT;