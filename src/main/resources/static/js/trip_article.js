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
    this.baseUrl = "/TIA104G2-SpringBoot";
    this.currentData = null;
    this.currentDay = 1;
    this.map = null;
    this.markers = [];
    this.daysCache = new Map();
    this.isLoading = false;

    // 基本事件初始化
    this.initializeEvents();
    this.initCollectEvents();

    // 啟動初始化流程
    this.init();
  }

  async init() {
    try {
      // 同時進行地圖初始化和數據載入
      await Promise.all([this.initMap(), this.loadArticleData()]);
    } catch (error) {
      console.error("Initialization error:", error);
      this.showError("初始化失敗");
    }
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
      window.map = this.map;
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

  showLoading() {
    this.isLoading = true;
    const container = document.querySelector(".article-container");
    if (container) {
      container.classList.add("loading");
    }
  }

  hideLoading() {
    this.isLoading = false;
    const container = document.querySelector(".article-container");
    if (container) {
      container.classList.remove("loading");
    }
  }

  showError(message) {
    const errorDiv = document.createElement("div");
    errorDiv.className = "error-message";
    errorDiv.textContent = message;

    document.querySelector(".article-container")?.appendChild(errorDiv);

    // 3秒後自動移除錯誤訊息
    setTimeout(() => {
      errorDiv.remove();
    }, 3000);
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
    try {
      this.currentDay = dayNumber;

      // 從快取或當前數據獲取天數資料
      const dayData =
        this.daysCache.get(dayNumber) || this.currentData.days[dayNumber - 1];
      if (!dayData) {
        throw new Error(`No data found for day ${dayNumber}`);
      }

      // 更新按鈕狀態
      document.querySelectorAll(".day-btn").forEach((btn) => {
        btn.classList.toggle("active", parseInt(btn.dataset.day) === dayNumber);
      });

      // 依序更新UI元素
      this.renderSpotsList(dayNumber);
      this.renderDayContent(dayNumber);
      this.updateMapMarkers(dayNumber);
    } catch (error) {
      console.error("Error switching day:", error);
      this.showError("切換行程天數失敗");
    }
  }

  // 渲染景點列表
  renderSpotsList(dayNumber) {
    const spotsList = document.querySelector(".spots-list");
    const dayData = this.currentData.days[dayNumber - 1];

    if (!dayData || !dayData.spots) {
      spotsList.innerHTML = '<div class="spot-item">無景點資料</div>';
      return;
    }

    spotsList.innerHTML = dayData.spots
      .filter((spot) => spot.name) // 只顯示有名稱的景點
      .map(
        (spot) => `
            <div class="spot-item">
                <h3>${spot.name || ""}</h3>
                <div class="spot-time">${spot.startTime || ""} - ${
          spot.endTime || ""
        }</div>
                <div class="spot-address">${spot.address || ""}</div>
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
        <p>${dayData.dayContent}</p>
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
      this.showLoading();

      // 1. 載入基本資訊
      const basicData = await this.loadBasicInfo(this.articleId);

      // 2. 載入天數資訊
      const daysData = await this.loadDaysInfo(this.articleId);

      // 3. 預設顯示第一天
      if (daysData && daysData.days && daysData.days.length > 0) {
        this.switchDay(1);
      }
    } catch (error) {
      console.error("Error loading article data:", error);
      this.showError("載入文章資料失敗");
    } finally {
      this.hideLoading();
    }
  }

  async loadBasicInfo() {
    try {
      const response = await fetch(
        `${this.baseUrl}/api/articles/${this.articleId}/basic`,
        {
          credentials: "include",
        }
      );

      if (!response.ok) throw new Error("Failed to load basic info");

      const data = await response.json();
      await this.updateBasicUI(data);
      return data;
    } catch (error) {
      console.error("Error loading basic info:", error);
      throw error;
    }
  }

  async loadDaysInfo() {
    try {
      const response = await fetch(
        `${this.baseUrl}/api/articles/${this.articleId}/days`,
        {
          credentials: "include",
        }
      );

      if (!response.ok) throw new Error("Failed to load days info");

      const data = await response.json();
      console.log("Received days data:", data);

      // 初始化 days 陣列
      this.currentData = {
        ...this.currentData,
        days: data.days || [],
      };

      // 儲存到當前數據和快取
      this.currentData = { ...this.currentData, days: data.days };
      data.days.forEach((dayData, index) => {
        console.log(`Day ${index + 1} data:`, dayData);
        this.daysCache.set(index + 1, dayData);
      });

      // 只在有資料時執行
      if (data.days && data.days.length > 0) {
        data.days.forEach((dayData, index) => {
          this.daysCache.set(index + 1, dayData);
        });
        this.renderDayButtons(data.days.length);
      }

      return data;
    } catch (error) {
      console.error("Error loading days info:", error);
      throw error;
    }
  }

  async updateBasicUI(data) {
    // 更新標題
    document.querySelector(".article-title").textContent = data.title;

    // 更新摘要
    document.querySelector(".article-abstract").textContent = data.tripAbstract;

    // 更新標籤
    const tagsGroup = document.querySelector(".tags-group");
    if (tagsGroup) {
      tagsGroup.innerHTML = [
        ...data.tags.areas.map((area) => `<a href="#" class="tag">${area}</a>`),
        ...data.tags.activities.map(
          (activity) => `<a href="#" class="tag">${activity}</a>`
        ),
      ].join("");
    }

    // 更新統計資訊
    document.getElementById("view-count").textContent = data.stats.viewCount;
    document.getElementById("favorite-count").textContent =
      data.stats.favoriteCount;

    // 更新評分
    const ratingContainer = document.getElementById("rating");
    const ratingValue = document.getElementById("rating-value");
    if (ratingValue) {
      ratingValue.textContent = data.stats.rating.toFixed(1);
    }
    if (ratingContainer) {
      const stars = ratingContainer.getElementsByTagName("i");
      const rating = data.stats.rating;
      for (let i = 0; i < stars.length; i++) {
        stars[i].className =
          i < Math.floor(rating)
            ? "fas fa-star" // 實心星星
            : i < rating
            ? "fas fa-star-half-alt" // 半星
            : "far fa-star"; // 空心星星
      }
    }

    // 更新作者資訊
    const authorNameElement = document.getElementById("author-name");
    if (authorNameElement) {
      authorNameElement.textContent = data.authorName;
      if (data.authorId) {
        authorNameElement.dataset.authorId = data.authorId;
      }
    }

    // 更新發布時間
    const publishTimeElement = document.getElementById("publish-time");
    if (publishTimeElement && data.publishTime) {
      // 格式化時間
      const date = new Date(data.publishTime);
      publishTimeElement.textContent = date.toLocaleString("zh-TW", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      });
    }

    // 更新收藏按鈕狀態
    if (data.stats.isCollected) {
      const collectBtn = document.getElementById("collectBtn");
      if (collectBtn) {
        collectBtn.classList.add("active");
        const span = collectBtn.querySelector("span");
        const icon = collectBtn.querySelector("i");
        if (span) span.textContent = "已收藏";
        if (icon) icon.className = "fas fa-heart";
      }
    }

    // 更新封面照片
    const coverImage = document.querySelector(".cover-image");
    if (coverImage) {
      if (data.coverPhoto) {
        coverImage.src = `data:image/jpeg;base64,${data.coverPhoto}`;
      } else {
        coverImage.src = "https://picsum.photos/1440/800";
      }
      coverImage.alt = data.title + " 封面照片";
    }
  }

  updateTags({ areas, activities }) {
    const tagsGroup = document.querySelector(".tags-group");
    if (!tagsGroup) return;

    const tagsHtml = [
      ...areas.map((area) => `<a href="#" class="tag">${area}</a>`),
      ...activities.map(
        (activity) => `<a href="#" class="tag">${activity}</a>`
      ),
    ].join("");

    tagsGroup.innerHTML = tagsHtml;
  }

  // 更新 UI
  async updateUI(data) {
    // 更新作者資訊，包含作者ID
    const authorNameElement = document.getElementById("author-name");
    if (authorNameElement) {
      authorNameElement.textContent = data.authorName;
      authorNameElement.dataset.authorId = data.authorId; // 設置作者ID為data屬性
    }

    // 更新標題
    document.querySelector(".article-title").textContent = data.title;

    // 更新標籤
    const tagsGroup = document.querySelector(".tags-group");
    // ... 更新標籤邏輯

    // 更新統計資訊
    document.getElementById("view-count").textContent = data.viewCount;
    document.getElementById("favorite-count").textContent = data.favoriteCount;

    // 更新評分
    this.updateRating(data.rating);

    // 更新作者和發布時間
    document.getElementById("author-name").textContent = data.authorName;
    document.getElementById("author-name").dataset.authorId = data.authorId;
    document.getElementById("publish-time").textContent = data.publishTime;

    // 更新其他UI元素
    document.getElementById("view-count").textContent = data.viewCount;
    document.getElementById("favorite-count").textContent = data.favoriteCount;
    document.getElementById("publish-time").textContent = data.publishTime;

    // 更新評分
    this.updateRating(data.rating);

    // 如果文章已被收藏，更新收藏按鈕狀態
    if (data.isCollected) {
      const collectBtn = document.getElementById("collectBtn");
      if (collectBtn) {
        collectBtn.classList.add("active");
        const span = collectBtn.querySelector("span");
        const icon = collectBtn.querySelector("i");
        if (span) span.textContent = "已收藏";
        if (icon) icon.className = "fas fa-heart";
      }
    }

    // 如果已追蹤作者，更新追蹤按鈕狀態
    if (data.isFollowing) {
      const followBtn = document.getElementById("followBtn");
      if (followBtn) {
        followBtn.classList.add("active");
        const span = followBtn.querySelector("span");
        if (span) span.textContent = "已追蹤";
      }
    }
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
  updateReport(authorId) {
    const reportbtn = document.getElementById("reportBtn");
  }
}

// 評論管理器
class CommentManager {
  constructor(articleId) {
    this.articleId = articleId; // 保存到實例變數
    this.currentPage = 0;
    this.totalPages = 0;
    this.currentRating = 0;
    this.baseUrl = "/TIA104G2-SpringBoot";
    this.isLoggedIn = false;

    this.init();
  }

  async init() {
    try {
      // 檢查登入狀態
      if (window.UserManager) {
        this.isLoggedIn = await window.UserManager.checkLoginStatus();
        // 只在登入狀態下獲取用戶ID
        this.currentUserId = this.isLoggedIn
          ? document.querySelector("#nav-profile")?.dataset.memberId
          : null;
      }

      // 先載入評論（不管是否登入）
      await this.loadComments(0);

      // 評論框顯示邏輯
      const commentForm = document.querySelector(".comment-form");
      if (!this.isLoggedIn) {
        // 未登入顯示登入提示
        commentForm.style.display = "none";
        const loginAlert = document.createElement("div");
        loginAlert.className = "login-alert";
        loginAlert.innerHTML = '請<a href="/login">登入</a>後發表評論';
        commentForm.parentNode.insertBefore(loginAlert, commentForm);
      } else {
        // 已登入但需檢查是否為文章作者
        const authorId =
          document.querySelector("#author-name")?.dataset.authorId;
        if (this.currentUserId === authorId) {
          commentForm.style.display = "none";
        } else {
          commentForm.style.display = "block";
          this.initializeCommentEvents();
        }
      }
    } catch (error) {
      console.error("Comment initialization error:", error);
    }
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
  async loadComments(page = 0) {
    try {
      console.log(
        `Loading comments for article ${this.articleId}, page ${page}`
      );
      const response = await fetch(
        `${this.baseUrl}/api/articles/${this.articleId}/comments?page=${page}`,
        { credentials: "include" }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log("Received comments data:", data);

      if (data.comments) {
        this.renderComments(data.comments);
        this.updatePagination(data);
      } else {
        this.renderComments([]);
      }
    } catch (error) {
      console.error("載入評論失敗:", error);
      // 顯示空評論區
      this.renderComments([]);
    }
  }

  renderComments(comments) {
    const commentsList = document.querySelector(".comments-list");
    if (!commentsList) return;

    if (!comments || comments.length === 0) {
      commentsList.innerHTML = '<div class="no-comments">暫無評論</div>';
      return;
    }

    commentsList.innerHTML = comments
      .map((comment) => this.createCommentHTML(comment))
      .join("");
  }

  createCommentHTML(comment) {
    const isOwnComment = this.currentUserId === comment.userId;
    const actionButtons = isOwnComment
      ? `<button class="comment-edit"><i class="far fa-edit"></i> 編輯</button>
         <button class="comment-delete"><i class="far fa-trash-alt"></i> 刪除</button>`
      : `<button class="comment-report"><i class="far fa-flag"></i> 檢舉</button>`;

    return `
        <div class="comment-item" data-comment-id="${comment.id}">
            <div class="comment-avatar">
                <img src="${
                  comment.userAvatar || "https://picsum.photos/50/50"
                }" alt="User Avatar">
            </div>
            <div class="comment-content">
                <div class="comment-header">
                    <div class="comment-user-info">
                        <span class="user-name">${comment.userName}</span>
                        <div class="comment-rating">
                            ${this.generateStars(comment.rating)}
                            <span>${comment.rating.toFixed(1)}</span>
                        </div>
                    </div>
                    <div class="comment-time">${comment.createdAt}</div>
                </div>
                <div class="comment-text">${comment.content}</div>
                ${
                  comment.photos
                    ? this.generatePhotoGallery(comment.photos)
                    : ""
                }
                <div class="comment-actions">
                    ${actionButtons}
                </div>
            </div>
        </div>
    `;
  }

  renderComments(comments) {
    const commentsList = document.querySelector(".comments-list");
    if (!commentsList) return;

    commentsList.innerHTML = comments
      .map(
        (comment) => `
        <div class="comment-item">
            <div class="comment-avatar">
                <img src="${
                  comment.userAvatar || "https://picsum.photos/50/50"
                }" alt="User Avatar">
            </div>
            <div class="comment-content">
                <div class="comment-header">
                    <div class="comment-user-info">
                        <span class="user-name">${comment.userName}</span>
                        <div class="comment-rating">
                            ${this.generateStars(comment.rating)}
                            <span>${comment.rating.toFixed(1)}</span>
                        </div>
                    </div>
                    <div class="comment-time">${comment.createdAt}</div>
                </div>
                <div class="comment-text">${comment.content}</div>
                ${this.generatePhotoGallery(comment.photos)}
                ${this.isLoggedIn ? this.generateCommentActions(comment) : ""}
            </div>
        </div>
    `
      )
      .join("");
  }

  updatePagination() {
    const pagination = document.querySelector(".comments-pagination");
    const prevBtn = pagination.querySelector("button:first-child");
    const nextBtn = pagination.querySelector("button:last-child");
    const pageInfo = pagination.querySelector(".page-info");

    prevBtn.disabled = this.currentPage === 0;
    nextBtn.disabled = this.currentPage === this.totalPages - 1;
    pageInfo.textContent = `第 ${this.currentPage + 1} 頁，共 ${
      this.totalPages
    } 頁`;

    prevBtn.onclick = () => this.loadComments(this.currentPage - 1);
    nextBtn.onclick = () => this.loadComments(this.currentPage + 1);
  }

  generateStars(rating) {
    return Array(5)
      .fill(0)
      .map((_, i) => `<i class="${i < rating ? "fas" : "far"} fa-star"></i>`)
      .join("");
  }

  generatePhotoGallery(photos) {
    if (!photos || !photos.length) return "";
    return `
        <div class="comment-photos">
            ${photos
              .map(
                (photo) => `
                <img src="${photo}" alt="Comment photo">
            `
              )
              .join("")}
        </div>
    `;
  }

  generateCommentActions(comment) {
    const isOwner = comment.userId === window.UserManager.getMemberId();
    return `
        <div class="comment-actions">
            ${
              isOwner
                ? `
                <button class="comment-edit">
                    <i class="far fa-edit"></i> 編輯
                </button>
                <button class="comment-delete">
                    <i class="far fa-trash-alt"></i> 刪除
                </button>
            `
                : `
                <button class="comment-report">
                    <i class="far fa-flag"></i> 檢舉
                </button>
            `
            }
        </div>
    `;
  }
}

// 初始化
document.addEventListener("DOMContentLoaded", async () => {
  const articleId = window.location.pathname.split("/tripArticle/").pop();
  console.log("Initializing with article ID:", articleId);

  // 依序初始化各個管理器
  try {
    if (window.UserManager) {
      await window.UserManager.init();
    }

    const articleManager = new ArticleManager(articleId);
    const commentManager = new CommentManager(articleId);

    window.articleAuthManager = new ArticleAuthManager();
  } catch (error) {
    console.error("Initialization error:", error);
  }
});
