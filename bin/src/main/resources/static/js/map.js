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

// 初始設置地圖
let map;
let input = document.querySelector("input[name=search]");
let search = document.querySelector("form");

// 初始化地圖
async function initMap() {
  const {Map} = await google.maps.importLibrary("maps");

  center = { lat: 35.689381,lng: 139.69181, };
  console.log(center);
  map = new Map(document.getElementById("map"), {
    center: center,
    zoom: 14,
    mapId: "bf738dc681f8956e",
    // ...
  });
}

// 取得搜尋條件
// const query = document.getElementById('mapInput').value.trim();

// document.getElementById("mapBtn").addEventListener("click",function(){
//   findPlaces();
// });

// // 查詢並顯示地點結果
// async function findPlaces() {
//   const {Map} = await google.maps.importLibrary("maps")
//   const { Place } = await google.maps.importLibrary("places");
//   const {AddressComponent} = await google.maps.importLibrary("places")
//   //@ts-ignore
//   const { AdvancedMarkerElement } = await google.maps.importLibrary("marker");
  
//   const query = document.getElementById('mapInput').value;
//   const request = {
//     textQuery: query,
//     fields: ["displayName", "location", "businessStatus","adrFormatAddress","rating","photos"],
//     isOpenNow: true,
//     maxResultCount: 10,
  
//   };

//   //@ts-ignore
//   const { places } = await Place.searchByText(request);
  
//   // 清空搜尋結果顯示區域
//   const resultsContainer = document.getElementById("searchResults");
//   resultsContainer.innerHTML = '';  // 清空先前的內容

//   if (places.length) {
//       console.log(places);
      
//       const { LatLngBounds } = await google.maps.importLibrary("core");
//       let bounds = new LatLngBounds();
  
//       // 循環並顯示每個搜尋到的地點
//       places.forEach((place) => {
//         const markerView = new AdvancedMarkerElement({
//           map,
//           position: place.location,
//           title: place.displayName,
//         });
  
//         bounds.extend(place.location);
//         console.log(place);
  
//         // 創建結果項的HTML
//         const placeElement = document.createElement('div');
//         placeElement.classList.add('place-item');
//         placeElement.style.marginBottom = '20px';
  
//         // 顯示標題
//         const titleElement = document.createElement('h3');
//         titleElement.innerText = place.displayName;
//         titleElement.style.display = 'flex';
//         titleElement.style.textAlign= 'center';
//         placeElement.appendChild(titleElement);
  
//          // 解析 adrFormatAddress
//           const addressElement = document.createElement('p');
//           if (place.adrFormatAddress) {
//               // 使用 DOMParser 解析 adrFormatAddress 格式資料
//               const adrDoc = new DOMParser().parseFromString(place.adrFormatAddress, 'text/html');
              
//               // 提取地址中的各個部分
//               const address = adrDoc.querySelector('.street-address')?.textContent || '';
//               const city = adrDoc.querySelector('.locality')?.textContent || '';
//               const postalCode = adrDoc.querySelector('.postal-code')?.textContent || '';
//               const country = adrDoc.querySelector('.country-name')?.textContent || '';

//               // 拼接完整的地址
//               const fullAddress = [address, city, postalCode, country].filter(Boolean).join(', ');

//               addressElement.innerText = fullAddress;
//           } else {
//               addressElement.innerText = "地址未提供";
//           }
//           placeElement.appendChild(addressElement);
        
//         // 顯示超連結
//         const googleMapsLink = document.createElement('a');
//           googleMapsLink.href = `https://www.google.com/maps/place/?q=place_id:${place.id}`;
//           googleMapsLink.target = "_blank"; 
//           googleMapsLink.innerText = "在Google地圖上查看";
//           placeElement.appendChild(googleMapsLink);

  
//         // 顯示評分
//         const ratingElement = document.createElement('p');
//         ratingElement.innerText = place.rating ? `評分: ${place.rating}` : "暫無評分";
//         placeElement.appendChild(ratingElement);
  
//         // 顯示照片
//         if (place.photos && place.photos.length > 0) {
//           const photoElement = document.createElement('img');
//           photoElement.src = place.photos[0].getURI({ maxWidth: 600, maxHeight: 300 });
//           photoElement.alt = place.displayName;
//           placeElement.appendChild(photoElement);
//         } else {
//           const noImageElement = document.createElement('p');
//           noImageElement.innerText = "暫無照片";
//           placeElement.appendChild(noImageElement);
//         }
         

//         // 將這個地點的HTML元素添加到搜尋結果區域
//         resultsContainer.appendChild(placeElement);
//         console.log(place.location.lat());
//         localStorage.lat = place.location.lat();
//         localStorage.lng =  place.location.lng();
        
        
//       });
  
//       // 調整地圖視野到所有標記的中心
     
     
     
//       map.setCenter(bounds.getCenter());    
      
//       } else {
//         resultsContainer.innerHTML = "<p>未找到任何結果。</p>";
//       }
    
// }
// async function nearbySearch() {
//   //@ts-ignore
  
//   const resultsContainer = document.getElementById("searchResults");
//   const {Map} = await google.maps.importLibrary("maps");
//   const { Place, SearchNearbyRankPreference } = await google.maps.importLibrary(
//     "places",
//   );
//   const { AdvancedMarkerElement } = await google.maps.importLibrary("marker");
//    // Restrict within the map viewport.
//    const { LatLng } = await google.maps.importLibrary("core");
//    const { LatLngBounds } = await google.maps.importLibrary("core");
//   const newlat = localStorage.lat;
//   const newlng = localStorage.lng;
//   console.log(newlat);
//   console.log(newlng);
//   center = { lat: parseFloat(newlat) ,lng: parseFloat(newlng) };
  
//   console.log(center)
//    const request = {
//      // required parameters
//      fields: ["displayName", "location", "businessStatus","adrFormatAddress","rating","photos"],
//      locationRestriction: {
//        center: center,
//        radius: 500,
//      },
//      // optional parameters
//      includedPrimaryTypes: ["restaurant"],
//      maxResultCount: 5,
//      rankPreference: SearchNearbyRankPreference.POPULARITY,
   
//    };
//    //@ts-ignore
//    const { places } = await Place.searchNearby(request);
//    if (places.length) {
//     console.log(places);
    
//     const { LatLngBounds } = await google.maps.importLibrary("core");
//     const bounds = new LatLngBounds();
    
//     // 循環並顯示每個搜尋到的地點
//     places.forEach((place) => {
//       const markerView = new AdvancedMarkerElement({
//         map,
//         position: place.location,
//         title: place.displayName,
//       });

//       bounds.extend(place.location);
    

//       // 創建結果項的HTML
//       const placeElement = document.createElement('div');
//       placeElement.classList.add('place-item');
//       placeElement.style.marginBottom = '20px';

//       // 顯示標題
//       const titleElement = document.createElement('h3');
//       titleElement.innerText = place.displayName;
//       titleElement.style.display = 'flex';
//       titleElement.style.textAlign= 'center';
//       placeElement.appendChild(titleElement);

//        // 解析 adrFormatAddress
//         const addressElement = document.createElement('p');
//         if (place.adrFormatAddress) {
//             // 使用 DOMParser 解析 adrFormatAddress 格式資料
//             const adrDoc = new DOMParser().parseFromString(place.adrFormatAddress, 'text/html');
            
//             // 提取地址中的各個部分
//             const address = adrDoc.querySelector('.street-address')?.textContent || '';
//             const city = adrDoc.querySelector('.locality')?.textContent || '';
//             const postalCode = adrDoc.querySelector('.postal-code')?.textContent || '';
//             const country = adrDoc.querySelector('.country-name')?.textContent || '';

//             // 拼接完整的地址
//             const fullAddress = [address, city, postalCode, country].filter(Boolean).join(', ');

//             addressElement.innerText = fullAddress;
//         } else {
//             addressElement.innerText = "地址未提供";
//         }
//         placeElement.appendChild(addressElement);
      
//       // 顯示超連結
//       const googleMapsLink = document.createElement('a');
//         googleMapsLink.href = `https://www.google.com/maps/place/?q=place_id:${place.id}`;
//         googleMapsLink.target = "_blank"; 
//         googleMapsLink.innerText = "在Google地圖上查看";
//         placeElement.appendChild(googleMapsLink);


//       // 顯示評分
//       const ratingElement = document.createElement('p');
//       ratingElement.innerText = place.rating ? `評分: ${place.rating}` : "暫無評分";
//       placeElement.appendChild(ratingElement);

//       // 顯示照片
//       if (place.photos && place.photos.length > 0) {
//         const photoElement = document.createElement('img');
//         photoElement.src = place.photos[0].getURI({ maxWidth: 600, maxHeight: 300 });
//         photoElement.alt = place.displayName;
//         placeElement.appendChild(photoElement);
//       } else {
//         const noImageElement = document.createElement('p');
//         noImageElement.innerText = "暫無照片";
//         placeElement.appendChild(noImageElement);
//       }
       

//       // 將這個地點的HTML元素添加到搜尋結果區域
//       resultsContainer.appendChild(placeElement);
//     });

//     // 調整地圖視野到所有標記的中心
//     map.setCenter(bounds.getCenter());
//   } else {
//     resultsContainer.innerHTML = "<p>未找到任何結果。</p>";
//   } 

// }
// document.getElementById("nearRest").addEventListener("click",function(){
//   nearbySearch();
  
// });


initMap();
