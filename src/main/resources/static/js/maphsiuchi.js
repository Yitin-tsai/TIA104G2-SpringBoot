// 載入map API
(g => {
    var h, a, k, p = "The Google Maps JavaScript API", c = "google", l = "importLibrary", q = "__ib__", m = document, b = window;
    b = b[c] || (b[c] = {});
    var d = b.maps || (b.maps = {}),
        r = new Set(),
        e = new URLSearchParams(),
        u = () => h || (h = new Promise(async (f, n) => {
          await (a = m.createElement("script"));
          e.set("libraries", [...r] + "");
          for (k in g) e.set(k.replace(/[A-Z]/g, t => "_" + t[0].toLowerCase()), g[k]);
          e.set("callback", c + ".maps." + q);
          a.src = `https://maps.${c}apis.com/maps/api/js?` + e;
          d[q] = f;
          a.onerror = () => h = n(Error(p + " could not load."));
          a.nonce = m.querySelector("script[nonce]")?.nonce || "";
          m.head.append(a);
        }));
    d[l] ? console.warn(p + " only loads once. Ignoring:", g) : d[l] = (f, ...n) => r.add(f) && u().then(() => d[l](f, ...n))
  })({
  
    key: "AIzaSyDMSnXGV5HBwDgyNrZevW4jHwXt5Wgx5EY",
    v: "weekly"
  });
  

let map;
let service;
let markers = [];
let currentPlace = null;

async function initMap() {
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
}

function performSearch() {
  const searchText = $("#place-search").val().trim();
  if (searchText === "") return;

  // 顯示載入狀態
  $(".place-list").html('<div class="loading">搜尋中...</div>');

  const request = {
    query: searchText,
    bounds: new google.maps.LatLngBounds(
      new google.maps.LatLng(21.8, 120.0), // 台灣西南角
      new google.maps.LatLng(25.3, 122.0)  // 台灣東北角
    ),
    type: ['tourist_attraction', 'point_of_interest']
  };

  service.textSearch(request, handleSearchResults);
}

function handleSearchResults(results, status) {
  if (status === google.maps.places.PlacesServiceStatus.OK) {
    clearMarkers();
    const bounds = new google.maps.LatLngBounds();
    
    // 格式化搜尋結果
    const formattedResults = results.map(place => ({
      placeId: place.place_id,
      name: place.name,
      address: place.formatted_address,
      rating: place.rating || 0,
      location: place.geometry.location,
      photos: place.photos
    }));

    // 在地圖上添加標記
    formattedResults.forEach(place => {
      const marker = new google.maps.Marker({
        map,
        position: place.location,
        title: place.name
      });
      markers.push(marker);
      bounds.extend(place.location);
    });

    map.fitBounds(bounds);
    renderPlaceList(formattedResults);
  } else {
    $(".place-list").html('<div class="no-results">找不到相關景點</div>');
  }
}

function clearMarkers() {
  markers.forEach(marker => marker.setMap(null));
  markers = [];
}

// 渲染景點列表
function renderPlaceList(places) {
  const placeList = $(".place-list");
  placeList.empty();

  places.forEach((place) => {
    // 處理照片 URL
    let photoUrl = '';
    if (place.photos && place.photos.length > 0) {
      photoUrl = place.photos[0].getUrl({maxWidth: 100, maxHeight: 100});
    }

    // 處理評分顯示
    const ratingStars = '★'.repeat(Math.round(place.rating)) + 
                       '☆'.repeat(5 - Math.round(place.rating));

    const placeHTML = `
      <div class="place-item">
        <div class="place-info">
          ${photoUrl ? `<img src="${photoUrl}" alt="${place.name}" class="place-photo">` : ''}
          <div class="place-details">
            <div class="place-name">${place.name}</div>
            <div class="place-address">${place.address}</div>
            <div class="rating">${ratingStars} ${place.rating ? place.rating.toFixed(1) : 'N/A'}</div>
          </div>
        </div>
        <button class="save-btn" data-place='${JSON.stringify(place)}'>收藏</button>
      </div>
    `;
    placeList.append(placeHTML);
  });

  placeList.addClass("active");
}

function setupModalListeners() {
  // 收藏按鈕事件
  $(document).on("click", ".save-btn", function() {
    currentPlace = $(this).data("place");
    $("#save-modal").addClass("active");
  });

  // 關閉彈窗
  $(".modal-close, .cancel-btn").click(function() {
    $("#save-modal").removeClass("active");
    currentPlace = null;
  });

  // 確認收藏
  $(".create-list-btn").click(function() {
    if (!currentPlace) return;
    
    const selectedList = $(".list-select").val();
    const newListName = $(".create-list-input").val();
    
    if (selectedList || newListName) {
      const listName = newListName || selectedList;
      console.log(`已將 ${currentPlace.name} 收藏到 ${listName}`);
      // 這裡可以添加實際的收藏邏輯
      
      $("#save-modal").removeClass("active");
      currentPlace = null;
      $(".create-list-input").val("");
    }
  });
}

// 初始化地圖
initMap();