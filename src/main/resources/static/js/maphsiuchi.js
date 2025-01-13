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

var memberID = null;
let map;
let service;
let markers = [];
let currentPlace = null;
const contextPath = "/TIA104G2-SpringBoot";

async function initMap() {
  try {
    const { Map } = await google.maps.importLibrary("maps");
    const { PlacesService } = await google.maps.importLibrary("places");

    map = new Map(document.getElementById("map"), {
      center: { lat: 25.033976, lng: 121.564714 },
      zoom: 14,
      mapId: "bf738dc681f8956e",
    });

    // 初始化 Places 服務
    service = new PlacesService(map);

    // 設置搜尋按鈕事件
    $("#search-place-btn").click(performSearch);

    // 設置按鈕事件監聽器
    setupModalListeners();

    console.log("Map initialized successfully");
  } catch (error) {
    console.error("Error initializing map:", error);
  }
}

function clearMarkers() {
  if (markers) {
    markers.forEach((marker) => {
      if (marker && marker.setMap) {
        marker.setMap(null);
      }
    });
    markers = [];
  }
}

function performSearch() {
  const searchText = $("#place-search").val().trim();
  if (searchText === "") {
    $(".place-list").html('<div class="no-results">請輸入搜尋關鍵字</div>');
    return;
  }

  console.log("Searching for:", searchText);
  $(".place-list").html('<div class="loading">搜尋中...</div>');

  try {
    const request = {
      query: searchText + " 台灣", // 加上台灣關鍵字以提高準確性
      location: new google.maps.LatLng(25.033976, 121.564714), // 台北市中心
      radius: 50000, // 搜尋半徑（公尺）
      region: "TW", // 限制在台灣地區
    };

    console.log("Search request:", request);
    service.textSearch(request, handleSearchResults);
  } catch (error) {
    console.error("Search error:", error);
    $(".place-list").html('<div class="error">搜尋發生錯誤，請稍後再試</div>');
  }
}

function handleSearchResults(results, status) {
  console.log("Search status:", status);
  console.log("Search results:", results);

  if (
    status === google.maps.places.PlacesServiceStatus.OK &&
    results &&
    results.length > 0
  ) {
    clearMarkers();
    const bounds = new google.maps.LatLngBounds();

    results.forEach((place) => {
      // 使用標準的 Marker
      const marker = new google.maps.Marker({
        position: place.geometry.location,
        map: map,
        title: place.name,
      });

      markers.push(marker);
      bounds.extend(place.geometry.location);

      // 添加點擊事件顯示資訊窗
      marker.addListener("click", () => {
        const infoWindow = new google.maps.InfoWindow({
          content: `<strong>${place.name}</strong><br>${place.formatted_address}`,
        });
        infoWindow.open(map, marker);
      });
    });

    // 調整地圖視角
    map.fitBounds(bounds);

    // 確保縮放等級適中
    const zoom = map.getZoom();
    if (zoom > 18) map.setZoom(18);
    if (zoom < 12) map.setZoom(12);

    renderPlaceList(results);
  } else {
    console.error("Places API error status:", status);
    $(".place-list").html('<div class="no-results">找不到相關景點</div>');
  }
}

function renderPlaceList(places) {
  console.log("Places to render:", places);
  const placeList = $(".map-section .place-list");
  placeList.empty();

  if (!places || places.length === 0) {
    placeList.html('<div class="no-results">找不到相關景點</div>');
    return;
  }

  places.forEach((place) => {
    let photoUrl = "";
    if (place.photos && place.photos.length > 0 && place.photos[0].getUrl) {
      try {
        photoUrl = place.photos[0].getUrl({ maxWidth: 100, maxHeight: 100 });
      } catch (error) {
        console.error("Error getting photo URL:", error);
      }
    }

    // 保存需要的 Google Places 資料
    const placeData = {
      googlePlaceId: place.place_id,
      name: place.name,
      address: place.formatted_address,
      latitude: place.geometry.location.lat(),
      longitude: place.geometry.location.lng(),
    };

    const ratingStars =
      "★".repeat(Math.round(place.rating || 0)) +
      "☆".repeat(5 - Math.round(place.rating || 0));

    const placeHTML = `
      <div class="place-item">
        <div class="place-info">
          ${
            photoUrl
              ? `<img src="${photoUrl}" alt="${place.name}" class="place-photo">`
              : ""
          }
          <div class="place-details">
            <h3 class="place-name">${place.name}</h3>
            <p class="place-address">${place.formatted_address}</p>
            <div class="rating">${ratingStars} ${
      place.rating ? place.rating.toFixed(1) : "N/A"
    }</div>
            <div class="button-container">
              <button class="save-btn" 
                      data-google-place-id="${placeData.googlePlaceId}"
                      data-name="${placeData.name}"
                      data-address="${placeData.address}"
                      data-latitude="${placeData.latitude}"
                      data-longitude="${placeData.longitude}">收藏</button>
            </div>
          </div>
        </div>
      </div>
    `;
    placeList.append(placeHTML);
  });

  placeList.addClass("active");
  console.log("Rendered cards count:", placeList.find(".place-item").length);
}

function setupModalListeners() {
  // 初始狀態設定
  function initializeModal() {
    $("#existing-list-section").show();
    $("#new-list-section").hide();
    $('input[name="listType"][value="existing"]').prop("checked", true);
    $(".create-list-input").val("");
    $(".list-select").val("");
  }

  // 先移除所有現有的事件監聽器
  $(document).off("click", ".save-btn");
  $('input[name="listType"]').off("change");
  $(".create-btn").off("click");
  $(".modal-close, .cancel-btn").off("click");

  // 收藏按鈕點擊時重置 modal 狀態
  $(document).on("click", ".save-btn", async function () {
    try {
      currentPlace = {
        googlePlaceId: $(this).data("googlePlaceId"),
        name: $(this).data("name"),
        address: $(this).data("address"),
        latitude: $(this).data("latitude"),
        longitude: $(this).data("longitude"),
      };
      // 初始化 modal
      $("#save-modal").addClass("active");
      $('input[name="listType"][value="existing"]').prop("checked", true);
      $("#existing-list-section").show();
      $("#new-list-section").hide();

      // 只在選擇現有清單時載入清單
      if ($('input[name="listType"]:checked').val() === "existing") {
        await loadExistingCollections();
      }
    } catch (error) {
      handleError(error);
    }
  });

  // radio button 切換事件
  $('input[name="listType"]').change(function () {
    const selectedType = $(this).val();
    if (selectedType === "existing") {
      $("#existing-list-section").show();
      $("#new-list-section").hide();
      $(".create-list-input").val("");
      // 重新載入現有清單
      loadExistingCollections();
    } else {
      $("#existing-list-section").hide();
      $("#new-list-section").show();
      $(".list-select").val("");
    }
  });

  // 載入現有清單的函數
  async function loadExistingCollections() {
    try {
      const collections = await $.ajax({
        url: `${contextPath}/member/locationCollections`,
        type: "GET",
        xhrFields: { withCredentials: true },
      });

      if (!collections || collections.length === 0) {
        $(".list-select").html('<option value="">尚無收藏清單</option>');
        return;
      }

      const options = collections
        .map(
          (collection) =>
            `<option value="${collection.trip_id}">${
              collection.article_title || "未命名收藏"
            }</option>`
        )
        .join("");

      $(".list-select").html(
        '<option value="">請選擇收藏清單...</option>' + options
      );
    } catch (error) {
      handleError(error);
    }
  }

  // 收藏按鈕點擊事件
  $(document).on("click", ".save-btn", async function () {
    try {
      currentPlace = {
        googlePlaceId: $(this).data("googlePlaceId"),
        name: $(this).data("name"),
        address: $(this).data("address"),
        latitude: $(this).data("latitude"),
        longitude: $(this).data("longitude"),
      };

      // 初始化 modal
      $("#save-modal").addClass("active");
      $('input[name="listType"][value="existing"]').prop("checked", true);
      $("#existing-list-section").show();
      $("#new-list-section").hide();

      // 載入現有清單
      await loadExistingCollections();
    } catch (error) {
      handleError(error);
    }
  });

  // radio button 切換事件
  $('input[name="listType"]').on("change", function () {
    const selectedType = $(this).val();
    if (selectedType === "existing") {
      $("#existing-list-section").show();
      $("#new-list-section").hide();
      $(".create-list-input").val("");
      loadExistingCollections();
    } else {
      $("#existing-list-section").hide();
      $("#new-list-section").show();
      $(".list-select").val("");
    }
  });

  // 確認按鈕事件處理
  $(".create-btn").on("click", async function () {
    try {
      if (!currentPlace) {
        alert("未選擇景點");
        return;
      }

      const selectedType = $('input[name="listType"]:checked').val();
      const locationData = {
        googlePlaceId: currentPlace.googlePlaceId,
        locationName: currentPlace.name,
        address: currentPlace.address,
        latitude: currentPlace.latitude,
        longitude: currentPlace.longitude,
      };

      const requestData = {
        type: selectedType,
        location: locationData,
      };

      if (selectedType === "new") {
        const newListName = $(".create-list-input").val().trim();
        if (!newListName) {
          alert("請輸入新清單名稱");
          return;
        }
        requestData.articleTitle = newListName;
      } else {
        const selectedList = $(".list-select").val();
        if (!selectedList) {
          alert("請選擇收藏清單");
          return;
        }
        requestData.tripId = selectedList;
      }

      const response = await $.ajax({
        url: `${contextPath}/member/saveLocation`,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(requestData),
        xhrFields: { withCredentials: true },
      });

      if (response.success) {
        alert(response.message);
        closeModal();
      }
    } catch (error) {
      console.error("Operation error:", error);
      if (error.status === 401) {
        alert("請先登入會員");
        window.location.href = `${contextPath}/login`;
      } else {
        alert(error.responseJSON?.message || "操作失敗，請稍後再試");
      }
    }
  });

  // 關閉按鈕事件
  $(".modal-close, .cancel-btn").on("click", closeModal);
}

// 統一的錯誤處理
function handleError(error) {
  console.error("Operation error:", error);
  if (error.status === 401) {
    alert("請先登入會員");
    window.location.href = `${contextPath}/login`;
  } else {
    alert(error.responseJSON?.message || "操作失敗，請稍後再試");
  }
}

function closeModal() {
  $("#save-modal").removeClass("active");
  $(".create-list-input").val("");
  $(".list-select").val("");
  currentPlace = null;
}

// 初始化地圖
initMap();
