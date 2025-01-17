// 載入map API
((g) => {
  var h,
    a,
    k,
    p = "The Google Maps JavaScript API",
    c = "google",
    l = "importLibrary",
    q = "__ib__",
    m = document,
    b = window;
  b = b[c] || (b[c] = {});
  var d = b.maps || (b.maps = {}),
    r = new Set(),
    e = new URLSearchParams(),
    u = () =>
      h ||
      (h = new Promise(async (f, n) => {
        await (a = m.createElement("script"));
        e.set("libraries", [...r] + "");
        for (k in g)
          e.set(
            k.replace(/[A-Z]/g, (t) => "_" + t[0].toLowerCase()),
            g[k]
          );
        e.set("callback", c + ".maps." + q);
        a.src = `https://maps.${c}apis.com/maps/api/js?` + e;
        d[q] = f;
        a.onerror = () => (h = n(Error(p + " could not load.")));
        a.nonce = m.querySelector("script[nonce]")?.nonce || "";
        m.head.append(a);
      }));
  d[l]
    ? console.warn(p + " only loads once. Ignoring:", g)
    : (d[l] = (f, ...n) => r.add(f) && u().then(() => d[l](f, ...n)));
})({
  key: "AIzaSyDMSnXGV5HBwDgyNrZevW4jHwXt5Wgx5EY",
  v: "weekly",
});

// 全局變數
let map;
let service;
let markers = [];
let currentPlace = null;

// 等待 DOM 完全加載
document.addEventListener("DOMContentLoaded", function () {
  // 原本的初始化函數
  initializeSummernote();
  initializeDayNavigation();
  initializeSpotDeletion();
  initializeToolbar();
  initializeSpotSelection();

  // 初始化地圖
  initTripMap();
});

// 初始化地圖
async function initTripMap() {
  try {
    const { Map } = await google.maps.importLibrary("maps");
    const { PlacesService } = await google.maps.importLibrary("places");

    // 初始化地圖，中心設在台北
    map = new Map(document.getElementById("map"), {
      center: { lat: 25.033976, lng: 121.564714 },
      zoom: 14,
      mapId: "trip_map",
    });

    // 初始化 Places 服務
    service = new PlacesService(map);

    // 綁定搜尋事件
    const searchBtn = document.getElementById("map-search-btn");
    const searchInput = document.getElementById("map-search");

    if (searchBtn) {
      searchBtn.addEventListener("click", performPlaceSearch);
    }

    if (searchInput) {
      searchInput.addEventListener("keypress", (e) => {
        if (e.key === "Enter") {
          performPlaceSearch();
        }
      });
    }
  } catch (error) {
    console.error("Error initializing map:", error);
  }
}

// 執行搜尋
function performPlaceSearch() {
  const searchInput = document.getElementById("map-search");
  const searchText = searchInput.value.trim();

  if (!searchText) {
    alert("請輸入搜尋關鍵字");
    return;
  }

  // 清除現有標記
  clearMapMarkers();

  const searchRequest = {
    query: `${searchText} 台灣`,
    location: map.getCenter(),
    radius: 50000, // 搜尋半徑（公尺）
    region: "TW",
  };

  service.textSearch(searchRequest, handleSearchResults);
}

// 清除地圖標記
function clearMapMarkers() {
  markers.forEach((marker) => {
    if (marker && marker.setMap) {
      marker.setMap(null);
    }
  });
  markers = [];
}

// 處理搜尋結果
function handleSearchResults(results, status) {
  if (status === google.maps.places.PlacesServiceStatus.OK && results) {
    // 創建地圖邊界對象
    const bounds = new google.maps.LatLngBounds();

    // 清除現有結果
    clearSearchResults();

    results.forEach((place) => {
      // 添加標記到地圖
      const marker = new google.maps.Marker({
        position: place.geometry.location,
        map: map,
        title: place.name,
      });

      markers.push(marker);
      bounds.extend(place.geometry.location);

      // 為標記添加點擊事件
      marker.addListener("click", () => {
        service.getDetails(
          {
            placeId: place.place_id,
            fields: ["name", "formatted_address", "photos"], // 只獲取需要的欄位
          },
          (placeDetail, detailStatus) => {
            if (detailStatus === google.maps.places.PlacesServiceStatus.OK) {
              showPlaceDetail(placeDetail, marker);
            }
          }
        );
      });
    });

    // 調整地圖視野以顯示所有標記
    map.fitBounds(bounds);

    // 渲染搜尋結果卡片
    renderPlaceCards(results);
  } else {
    alert("找不到相關景點，請嘗試其他關鍵字");
  }
}

// 顯示地點詳細信息窗口
function showPlaceDetail(place, marker) {
  // 處理照片部分
  const photoContent =
    place.photos && place.photos.length > 0
      ? `<img src="${place.photos[0].getUrl({
          maxWidth: 200,
          maxHeight: 200,
        })}" 
               alt="${place.name}" 
               style="width: 100%; height: 150px; object-fit: cover; border-radius: 4px; margin-bottom: 8px;">`
      : "";

  // 準備景點數據
  const placeData = {
    googlePlaceId: place.place_id,
    name: place.name,
    address: place.formatted_address,
    latitude: marker.getPosition().lat(),
    longitude: marker.getPosition().lng(),
    photoUrl:
      place.photos && place.photos.length > 0
        ? place.photos[0].getUrl({ maxWidth: 200, maxHeight: 200 })
        : null,
  };

  const infoWindow = new google.maps.InfoWindow({
    content: `
            <div style="padding: 10px; max-width: 300px;">
                ${photoContent}
                <h3 style="margin: 0 0 5px;">${place.name}</h3>
                <p style="margin: 0 0 10px;">${place.formatted_address}</p>
                <button onclick="addPlaceToCurrentDay(${JSON.stringify(
                  placeData
                ).replace(/"/g, "&quot;")})"
                        style="background: #6b7280; color: white; border: none; 
                               padding: 6px 12px; border-radius: 4px; cursor: pointer;">
                    加入行程
                </button>
            </div>
        `,
  });
  infoWindow.open(map, marker);
}

// 清除搜尋結果
function clearSearchResults() {
  const resultsContainer = document.querySelector(".place-list");
  if (resultsContainer) {
    resultsContainer.innerHTML = "";
  }
}

// 渲染景點卡片
function renderPlaceCards(places) {
  const resultsContainer = document.querySelector(".place-list");
  if (!resultsContainer) return;

  resultsContainer.style.display = "block";
  resultsContainer.innerHTML = ""; // 清空現有內容

  places.forEach((place) => {
    const placeCard = createPlaceCard(place);
    resultsContainer.appendChild(placeCard);
  });
}

// 創建地圖搜尋結果的卡片
function createPlaceCard(place) {
  const card = document.createElement("div");
  card.className = "place-item";

  // 獲取圖片URL
  const photoUrl =
    place.photos && place.photos.length > 0
      ? place.photos[0].getUrl({ maxWidth: 200, maxHeight: 200 })
      : "https://via.placeholder.com/200x200?text=No+Image";

  // 包裝要傳給景點列表的資料
  const placeData = {
    googlePlaceId: place.place_id,
    name: place.name,
    address: place.formatted_address,
    latitude: place.geometry.location.lat(),
    longitude: place.geometry.location.lng(),
    rating: place.rating || "N/A",
    reviewCount: place.user_ratings_total || 0,
  };

  // 創建卡片內容
  card.innerHTML = `
    <div class="place-card-content">
      <img src="${photoUrl}" alt="${place.name}" class="place-card-image">
      <div class="place-card-details">
        <h3 class="place-card-name">${place.name}</h3>
        <p class="place-card-address">📍 ${place.formatted_address}</p>
        <button class="add-to-list-btn" data-place='${JSON.stringify(
          placeData
        )}'>
          加入行程
        </button>
      </div>
    </div>
  `;

  // 綁定加入按鈕事件
  const addButton = card.querySelector(".add-to-list-btn");
  addButton.addEventListener("click", function () {
    const placeInfo = JSON.parse(this.dataset.place);
    addToCurrentDayList(placeInfo);
  });

  return card;
}

// 添加景點到當前天的列表
function addToCurrentDayList(placeData) {
  const currentList = document.querySelector(
    `.spots-list[data-day="${currentDay}"]`
  );

  if (!currentList) {
    alert("請先選擇要添加景點的日期");
    return;
  }

  // 創建新的景點元素
  const spotElement = createSpotElement({
    name: placeData.name,
    rating: placeData.rating,
    reviewCount: placeData.reviewCount,
    address: placeData.address,
    googlePlaceId: placeData.googlePlaceId,
    latitude: placeData.latitude,
    longitude: placeData.longitude,
  });

  // 添加到列表並儲存資料
  currentList.appendChild(spotElement);
}

// 將景點添加到當前天數的列表中
window.addPlaceToCurrentDay = function (placeData) {
  const currentList = document.querySelector(
    `.spots-list[data-day="${currentDay}"]`
  );

  if (!currentList) {
    alert("請先選擇要添加景點的日期");
    return;
  }

  // 確保不會添加到 "新增景點" 按鈕前面
  const addSpotBtn = currentList.querySelector(".add-spot-btn");

  const spotElement = document.createElement("div");
  spotElement.className = "spot-item";
  spotElement.innerHTML = `
        <button class="delete-spot" style="color: #9e9e9e;">×</button>
        <i class="fas fa-map-marker-alt location-icon" style="color: #9e9e9e;"></i>
        <div class="spot-info">
            <h3 style="color: #333; margin: 0 0 4px 0;">${placeData.name}</h3>
            <p style="color: #666; margin: 4px 0 0 0; font-size: 12px;">
                📍 ${placeData.address}
            </p>
            <div class="spot-time">
                <div class="time-input-group">
                    <label>開始時間：</label>
                    <input type="datetime-local" class="time-input start-time">
                </div>
                <div class="time-input-group">
                    <label>結束時間：</label>
                    <input type="datetime-local" class="time-input end-time">
                </div>
            </div>
        </div>
    `;

  // 綁定刪除事件
  spotElement.querySelector(".delete-spot").addEventListener("click", () => {
    spotElement.remove();
  });

  // 綁定時間驗證
  const startTimeInput = spotElement.querySelector(".start-time");
  const endTimeInput = spotElement.querySelector(".end-time");

  // 設置預設最小時間為當前時間
  const now = new Date();
  startTimeInput.min = now.toISOString().slice(0, 16);
  endTimeInput.min = now.toISOString().slice(0, 16);

  // 監聽開始時間變更
  startTimeInput.addEventListener("change", () => {
    endTimeInput.min = startTimeInput.value;
    if (endTimeInput.value && endTimeInput.value < startTimeInput.value) {
      endTimeInput.value = "";
    }
  });

  // 監聽結束時間變更
  endTimeInput.addEventListener("change", () => {
    if (startTimeInput.value && endTimeInput.value < startTimeInput.value) {
      alert("結束時間必須晚於開始時間");
      endTimeInput.value = "";
    }
  });

  // 保存景點資料
  spotElement.dataset.spotInfo = JSON.stringify({
    googlePlaceId: placeData.googlePlaceId,
    name: placeData.name,
    address: placeData.address,
    latitude: placeData.latitude,
    longitude: placeData.longitude,
  });

  // 將新景點加入到列表中，確保在 "新增景點" 按鈕後面
  currentList.appendChild(spotElement);
};

function collectAllDaysData() {
  const allData = [];

  // 收集每一天的資料
  for (let day = 1; day <= totalDays; day++) {
    // 收集該天的景點列表
    const spotsList = document.querySelector(`.spots-list[data-day="${day}"]`);
    const spots = Array.from(spotsList.querySelectorAll(".spot-item")).map(
      (spot) => {
        const spotInfo = JSON.parse(spot.dataset.spotInfo);
        return {
          ...spotInfo,
          startTime: spot.querySelector(".start-time").value || null,
          endTime: spot.querySelector(".end-time").value || null,
        };
      }
    );

    // 收集該天的編輯器內容
    const editor = $(`.summernote[data-day="${day}"]`);
    const content = editor.summernote("code");

    allData.push({
      day: day,
      spots: spots,
      content: content,
    });
  }

  return allData;
}

// 初始化地圖
initTripMap();
