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

// 文章數據加載與更新
class ArticleManager {
  constructor(articleId) {
    this.articleId = articleId;
    this.baseUrl = "/api";
    this.currentData = null;
    this.currentDay = 1;
    this.map = null;
    this.markers = [];

    // 需要先初始化基本事件和地圖
    this.initializeEvents();
    this.initMap(); // 先初始化地圖
    // 再初始化其他功能
    this.initCollectEvents();
    // 最後載入數據
    this.loadArticleData(); // 這個應該放最後
  }

  // 初始化事件
  // 初始化地圖
  async initMap() {
    try {
      const { Map } = await google.maps.importLibrary("maps");
      this.map = new Map(document.getElementById("map"), {
        center: { lat: 25.033976, lng: 121.564714 },
        zoom: 14,
      });
      window.map = this.map; // 保存地圖實例到全局

      // 地圖初始化完成後再載入數據
      await this.loadArticleData();
    } catch (error) {
      console.error("Error initializing map:", error);
    }
  }

  initializeEvents() {
    // 基本初始化工作
    const dayNavigation = document.querySelector(".day-navigation");
    if (dayNavigation) {
      dayNavigation.innerHTML = ""; // 清空原有內容
    }
    const spotsList = document.querySelector(".spots-list");
    if (spotsList) {
      spotsList.innerHTML = ""; // 清空原有內容
    }
  }

  // 更新地圖標記
  updateMapMarkers(dayNumber) {
    const dayData = this.currentData.days[dayNumber - 1];
    if (this.map && dayData.spots) {
      // 清除現有標記
      this.markers.forEach((marker) => marker.setMap(null));
      this.markers = [];

      // 添加新標記
      dayData.spots.forEach((spot) => {
        const marker = new google.maps.Marker({
          position: { lat: spot.latitude, lng: spot.longitude },
          map: this.map,
          title: spot.name,
        });
        this.markers.push(marker);
      });

      // 調整視角
      if (this.markers.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        this.markers.forEach((marker) => bounds.extend(marker.getPosition()));
        this.map.fitBounds(bounds);
      }
    }
  }

  // 切換天數顯示
  switchDay(dayNumber) {
    this.currentDay = dayNumber;

    // 更新按鈕狀態
    document.querySelectorAll(".day-btn").forEach((btn) => {
      btn.classList.toggle("active", parseInt(btn.dataset.day) === dayNumber);
    });

    // 更新景點列表
    this.renderSpotsList(dayNumber);

    // 更新文章內容
    this.renderDayContent(dayNumber);

    // 更新地圖標記
    this.updateMapMarkers(dayNumber);
  }
  // 初始化評分相關事件
  initRatingEvents() {
    const ratingBtn = document.getElementById("ratingBtn");
    const ratingModal = document.getElementById("ratingModal");
    const stars = document.querySelectorAll(".rating-stars i");
    const submitBtn = ratingModal.querySelector(".submit");
    const cancelBtn = ratingModal.querySelector(".cancel");
    let selectedRating = 0;

    // 打開評分彈窗
    ratingBtn.addEventListener("click", () => {
      ratingModal.style.display = "block";
    });

    // 關閉評分彈窗
    cancelBtn.addEventListener("click", () => {
      ratingModal.style.display = "none";
      // 重置星星狀態
      stars.forEach((star) => {
        star.className = "far fa-star";
      });
      selectedRating = 0;
    });

    // 星星懸停效果
    stars.forEach((star, index) => {
      star.addEventListener("mouseover", () => {
        stars.forEach((s, i) => {
          s.className = i <= index ? "fas fa-star" : "far fa-star";
        });
      });

      star.addEventListener("mouseout", () => {
        stars.forEach((s, i) => {
          s.className = i < selectedRating ? "fas fa-star" : "far fa-star";
        });
      });

      star.addEventListener("click", () => {
        selectedRating = index + 1;
        stars.forEach((s, i) => {
          s.className = i < selectedRating ? "fas fa-star" : "far fa-star";
        });
      });
    });

    // 提交評分
    submitBtn.addEventListener("click", async () => {
      if (selectedRating === 0) {
        alert("請選擇評分");
        return;
      }

      try {
        const response = await fetch(
          `${this.baseUrl}/articles/${this.articleId}/rate`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ rating: selectedRating }),
          }
        );

        if (!response.ok) throw new Error("Failed to submit rating");

        const data = await response.json();
        this.updateRating(data.rating);
        ratingModal.style.display = "none";
      } catch (error) {
        console.error("Error submitting rating:", error);
        alert("評分失敗，請稍後再試");
      }
    });

    // 點擊彈窗外部關閉
    ratingModal.addEventListener("click", (e) => {
      if (e.target === ratingModal) {
        ratingModal.style.display = "none";
        stars.forEach((star) => {
          star.className = "far fa-star";
        });
        selectedRating = 0;
      }
    });
  }

  // 初始化收藏按鈕事件
  initCollectEvents() {
    const collectBtn = document.getElementById("collectBtn");

    collectBtn.addEventListener("click", async () => {
      const isCollected = collectBtn.classList.contains("active");

      try {
        const response = await fetch(
          `${this.baseUrl}/articles/${this.articleId}/collect`,
          {
            method: isCollected ? "DELETE" : "POST",
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        if (!response.ok) throw new Error("Failed to toggle collection");

        const data = await response.json();
        this.updateCollectStatus(data.isCollected);
        document.getElementById("favorite-count").textContent =
          data.collectCount;
      } catch (error) {
        console.error("Error toggling collection:", error);
        alert("操作失敗，請稍後再試");
      }
    });
  }

  // 渲染天數按鈕
  renderDayButtons(totalDays) {
    const dayNavigation = document.querySelector(".day-navigation");
    dayNavigation.innerHTML = "";

    for (let i = 1; i <= totalDays; i++) {
      const button = document.createElement("button");
      button.className = `day-btn ${i === 1 ? "active" : ""}`;
      button.textContent = `第${this.numberToChinese(i)}天`;
      button.dataset.day = i;
      button.addEventListener("click", () => this.switchDay(i));
      dayNavigation.appendChild(button);
    }
  }

  // 切換天數顯示
  switchDay(dayNumber) {
    this.currentDay = dayNumber;

    // 更新按鈕狀態
    document.querySelectorAll(".day-btn").forEach((btn) => {
      btn.classList.toggle("active", parseInt(btn.dataset.day) === dayNumber);
    });

    // 更新景點列表
    this.renderSpotsList(dayNumber);

    // 更新文章內容
    this.renderDayContent(dayNumber);

    // 更新地圖標記（如果需要）
    this.updateMapMarkers(dayNumber);
  }

  // 渲染景點列表
  renderSpotsList(dayNumber) {
    const spotsList = document.querySelector(".spots-list");
    const dayData = this.currentData.days[dayNumber - 1];

    spotsList.innerHTML = dayData.spots
      .map(
        (spot) => `
        <div class="spot-item">
            <h3>${spot.name}</h3>
            <div class="spot-time">${spot.startTime} - ${spot.endTime}</div>
            <div class="spot-address">${spot.address}</div>
        </div>
    `
      )
      .join("");
  }

  // 渲染當天文章內容
  renderDayContent(dayNumber) {
    const dayContent = document.querySelector(".day-content");
    const dayData = this.currentData.days[dayNumber - 1];

    dayContent.innerHTML = `
        <h2 class="day-title">第${this.numberToChinese(dayNumber)}天</h2>
        ${dayData.content}
    `;
  }

  // 更新地圖標記
  updateMapMarkers(dayNumber) {
    const dayData = this.currentData.days[dayNumber - 1];
    // 假設地圖實例是全局可用的
    if (window.map && dayData.spots) {
      // 清除現有標記
      window.markers?.forEach((marker) => marker.setMap(null));
      window.markers = [];

      // 添加新標記
      dayData.spots.forEach((spot) => {
        const marker = new google.maps.Marker({
          position: { lat: spot.latitude, lng: spot.longitude },
          map: window.map,
          title: spot.name,
        });
        window.markers.push(marker);
      });

      // 調整地圖視角以顯示所有標記
      if (window.markers.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        window.markers.forEach((marker) => bounds.extend(marker.getPosition()));
        window.map.fitBounds(bounds);
      }
    }
  }

  // 數字轉中文
  numberToChinese(num) {
    const chineseNums = [
      "零",
      "一",
      "二",
      "三",
      "四",
      "五",
      "六",
      "七",
      "八",
      "九",
      "十",
    ];
    if (num <= 10) return chineseNums[num];
    if (num > 10 && num < 20)
      return "十" + (num % 10 === 0 ? "" : chineseNums[num % 10]);
    return num;
  }

  // 載入文章數據
  async loadArticleData() {
    try {
      // 模擬數據
      const mockData = {
        id: "123",
        title: "東京五日遊行程安排",
        authorName: "愛旅遊的Seal",
        publishTime: "2025-1-1 20:00",
        viewCount: 100,
        favoriteCount: 50,
        rating: 4.5,
        isFavorited: false,
        days: [
          {
            dayNumber: 1,
            spots: [
              {
                name: "松山文創園區",
                startTime: "09:00",
                endTime: "11:00",
                address: "台北市信義區光復南路133號",
                latitude: 25.0478,
                longitude: 121.5598,
              },
              {
                name: "台北101",
                startTime: "11:30",
                endTime: "14:00",
                address: "台北市信義區信義路五段7號",
                latitude: 25.0338,
                longitude: 121.5645,
              },
              {
                name: "象山步道",
                startTime: "14:30",
                endTime: "16:30",
                address: "台北市信義區信義路五段150巷",
                latitude: 25.0278,
                longitude: 121.5715,
              },
              {
                name: "四四南村",
                startTime: "17:00",
                endTime: "18:30",
                address: "台北市信義區信義路五段",
                latitude: 25.0278,
                longitude: 121.5605,
              },
              {
                name: "永春市場",
                startTime: "19:00",
                endTime: "20:30",
                address: "台北市信義區永春街163號",
                latitude: 25.0458,
                longitude: 121.5765,
              },
              {
                name: "饒河街觀光夜市",
                startTime: "21:00",
                endTime: "22:30",
                address: "台北市松山區饒河街",
                latitude: 25.0508,
                longitude: 121.5775,
              },
            ],
            content: `
                <p>今天的行程從松山文創園區開始，這裡是台北最具代表性的文創基地之一。
                園區內保留了許多日治時期的建築，現在已經轉變為展覽、表演、市集等多元用途的文創空間。</p>
                
                <p>接著我們前往台北101，這裡不只是台北的地標建築，更是一個集結美食、購物、觀景的複合式景點。
                特別推薦大家一定要上89樓的觀景台，在這裡可以將整個台北盆地的美景盡收眼底。</p>
                
                <p>下午登上象山步道，這裡是拍攝台北101最佳的觀景點，步道難度適中，
                大約需要30分鐘就能到達主要觀景台，可以欣賞台北市區的壯麗景色。</p>
                
                <p>接著造訪四四南村，這是一個保存完整的眷村，現在已經轉型為文創園區，
                裡面有許多特色小店和文創商品。</p>
                
                <p>晚餐時段來到永春市場，這裡有許多在地美食，從傳統小吃到創新料理都可以找到，
                是體驗台北在地生活的好去處。</p>
                
                <p>最後以饒河街夜市作為今天行程的完美句點，這裡不僅有各式各樣的美食，
                還有許多有趣的商品和遊戲攤位，是台北最熱鬧的夜市之一。</p>`,
          },
          {
            dayNumber: 3,
            spots: [
              {
                name: "龍山寺",
                startTime: "10:00",
                endTime: "11:30",
                address: "台北市萬華區廣州街211號",
                latitude: 25.0374,
                longitude: 121.4999,
              },
              {
                name: "剝皮寮老街",
                startTime: "13:00",
                endTime: "15:00",
                address: "台北市萬華區廣州街",
                latitude: 25.0366,
                longitude: 121.5033,
              },
            ],
            content: `
                    <p>今天我們探訪具有歷史意義的龍山寺，這座寺廟建於1738年，是台北市最古老的寺廟之一。
                    寺內精美的雕刻和建築值得細細品味。</p>
                    <p>下午則前往附近的剝皮寮老街，這裡保留了清末至日治時期的街屋立面，
                    充滿古色古香的氛圍，適合漫步拍照。</p>`,
          },
        ],
      };

      // 使用模擬數據更新UI
      this.currentData = mockData;
      await this.updateUI(mockData);
      this.renderDayButtons(mockData.days.length);
      await this.switchDay(1);
    } catch (error) {
      console.error("Error loading article data:", error);
    }
  }

  // 更新 UI
  async updateUI(data) {
    // 更新瀏覽數
    document.getElementById("view-count").textContent = data.viewCount;

    // 更新收藏數
    document.getElementById("favorite-count").textContent = data.favoriteCount;

    // 更新評分
    this.updateRating(data.rating);

    // 更新作者和發布時間
    document.getElementById("author-name").textContent = data.authorName;
    document.getElementById("publish-time").textContent = data.publishTime;
  }

  // 更新星級評分顯示
  updateRating(rating) {
    const ratingContainer = document.getElementById("rating");
    const ratingValue = document.getElementById("rating-value");
    const stars = ratingContainer.getElementsByTagName("i");

    // 更新數值顯示
    ratingValue.textContent = rating.toFixed(1);

    // 更新星星顯示
    for (let i = 0; i < stars.length; i++) {
      stars[i].className =
        i < Math.floor(rating)
          ? "fas fa-star" // 實心星星
          : i < rating
          ? "fas fa-star-half-alt"
          : "far fa-star"; // 半星或空心星星
    }
  }

  // 更新收藏狀態
  updateFavoriteStatus(isFavorited) {
    const favoriteBtn = document.getElementById("favorite-btn");
    favoriteBtn.classList.toggle("active", isFavorited);
    this.isFavorited = isFavorited;
  }

  // 切換收藏狀態
  async toggleFavorite() {
    const favoriteBtn = document.getElementById("favorite-btn");
    favoriteBtn.classList.add("loading");

    try {
      const response = await fetch(
        `${this.baseUrl}/articles/${this.articleId}/favorite`,
        {
          method: this.isFavorited ? "DELETE" : "POST",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) throw new Error("Failed to toggle favorite");

      const data = await response.json();
      this.updateFavoriteStatus(data.isFavorited);
      document.getElementById("favorite-count").textContent =
        data.favoriteCount;
    } catch (error) {
      console.error("Error toggling favorite:", error);
    } finally {
      favoriteBtn.classList.remove("loading");
    }
  }

  // 更新標籤
  updateTags(tags) {
    const tagsContainer = document.querySelector(".article-tags");
    tagsContainer.innerHTML = tags
      .map(
        (tag) => `
            <a href="#" class="tag" data-tag-id="${tag.id}">${tag.name}</a>
        `
      )
      .join("");

    // 重新綁定事件
    this.initializeEvents();
  }
  updateReport(authorId){
    const reportbtn = document.getElementById("reportBtn")
  }

}

// 評論管理器
class CommentManager {
  constructor() {
    this.currentRating = 0;
    this.initializeEvents();
  }

  initializeEvents() {
    // 先初始化評分功能
    this.initializeRating();
    this.initializeCommentSubmit();
    this.initializePhotoUpload();
  }

  initializeRating() {
    const stars = document.querySelectorAll(".comment-form .star-input i");

    if (stars.length === 0) {
      console.error("找不到評分星星元素");
      return;
    }

    stars.forEach((star, index) => {
      // 滑鼠懸停效果
      star.addEventListener("mouseover", () => {
        this.updateStarsDisplay(stars, index);
      });

      // 滑鼠移出效果
      star.addEventListener("mouseout", () => {
        this.updateStarsDisplay(stars, this.currentRating - 1);
      });

      // 點擊效果
      star.addEventListener("click", () => {
        this.currentRating = index + 1;
        this.updateStarsDisplay(stars, index);
        console.log("設置評分：", this.currentRating);
      });
    });
  }

  updateStarsDisplay(stars, activeIndex) {
    stars.forEach((star, index) => {
      if (index <= activeIndex) {
        star.classList.remove("far");
        star.classList.add("fas");
      } else {
        star.classList.remove("fas");
        star.classList.add("far");
      }
    });
  }

  resetStarsDisplay(stars) {
    stars.forEach((star, index) => {
      if (index < this.currentRating) {
        star.classList.remove("far");
        star.classList.add("fas");
      } else {
        star.classList.remove("fas");
        star.classList.add("far");
      }
    });
  }

  initializeCommentSubmit() {
    const submitBtn = document.querySelector(".submit-btn");
    submitBtn.addEventListener("click", (e) => {
      e.preventDefault();
      this.handleCommentSubmit();
    });
  }

  initializePhotoUpload() {
    const photoInput = document.getElementById("commentPhotos");
    if (photoInput) {
      photoInput.addEventListener("change", (e) => this.handlePhotoUpload(e));
    }
  }

  handleCommentSubmit() {
    const textarea = document.querySelector(".comment-form textarea");
    const content = textarea.value.trim();

    if (!content) {
      alert("請輸入評論內容");
      return;
    }

    if (!this.currentRating) {
      alert("請選擇評分");
      return;
    }

    // 模擬提交評論
    console.log("提交評論:", {
      content,
      rating: this.currentRating,
      photos: Array.from(document.querySelectorAll(".photo-preview img")).map(
        (img) => img.src
      ),
    });

    // 清空表單
    this.resetForm();
  }

  handlePhotoUpload(e) {
    const files = Array.from(e.target.files);
    const preview = document.querySelector(".photo-preview");

    if (files.length > 4) {
      alert("最多只能上傳4張照片");
      e.target.value = "";
      return;
    }

    preview.innerHTML = "";

    files.forEach((file) => {
      if (file.type.startsWith("image/")) {
        const reader = new FileReader();
        reader.onload = (e) => {
          const img = document.createElement("img");
          img.src = e.target.result;
          img.style.width = "100px";
          img.style.height = "100px";
          img.style.objectFit = "cover";
          img.style.margin = "5px";
          preview.appendChild(img);
        };
        reader.readAsDataURL(file);
      }
    });
  }

  resetForm() {
    const textarea = document.querySelector(".comment-form textarea");
    const preview = document.querySelector(".photo-preview");
    const photoInput = document.getElementById("commentPhotos");
    const stars = document.querySelectorAll(".star-input i");

    textarea.value = "";
    preview.innerHTML = "";
    photoInput.value = "";
    this.currentRating = 0;

    stars.forEach((star) => {
      star.classList.remove("fas");
      star.classList.add("far");
    });
  }
}

// 初始化文章管理器
document.addEventListener("DOMContentLoaded", () => {
  const articleId =
    new URLSearchParams(window.location.search).get("id") || "1";
  const articleManager = new ArticleManager(articleId);
  const commentManager = new CommentManager();
});
