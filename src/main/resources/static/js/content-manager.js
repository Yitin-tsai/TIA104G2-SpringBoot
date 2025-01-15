// 全局變數
const contentGrid = document.querySelector(".content-grid");
console.log("Content grid element:", contentGrid);
const contentTypeSelect = document.getElementById("content-type");
const sortTypeSelect = document.getElementById("sort-type");
const applyFilterBtn = document.getElementById("apply-filter");
let currentPage = 0;
const itemsPerPage = 12;
let currentArticles = [];

// 排序選項配置
const sortOptions = {
  articles: [
    { value: "publish-time", label: "發表時間" },
    { value: "popularity", label: "熱門程度" },
    { value: "likes", label: "點讚數" },
    { value: "saves", label: "收藏數" },
    { value: "rating", label: "評分" },
  ],
  places: [
    { value: "save-time", label: "收藏時間" },
    { value: "comments", label: "評論數" },
    { value: "rating", label: "評分" },
  ],
};

// 更新排序選項函數
function updateSortOptions(contentType) {
  const options =
    contentType === "saved-places" ? sortOptions.places : sortOptions.articles;

  sortTypeSelect.innerHTML = `
        <option value="">選擇排序方式</option>
        ${options
          .map(
            (option) => `
            <option value="${option.value}">${option.label}</option>
        `
          )
          .join("")}
    `;
}

// 監聽內容類型選擇變化
contentTypeSelect.addEventListener("change", function (e) {
  console.log("Content type changed to:", e.target.value);
  currentPage = 0; // 重置頁碼
  updateSortOptions(e.target.value); // 更新排序選項
  loadContent({
    contentType: e.target.value,
    sortBy: sortTypeSelect.value,
  });
});

// 確認按鈕只處理排序變更
applyFilterBtn.addEventListener("click", function () {
  const filters = {
    contentType: contentTypeSelect.value,
    sortBy: sortTypeSelect.value,
  };
  loadContent(filters);
});

// 載入內容
async function loadContent(filters) {
  try {
    // 根據內容類型更改容器的樣式
    if (filters.contentType === "saved-places") {
      contentGrid.style.display = "grid";
      contentGrid.style.gridTemplateColumns = "repeat(3, 1fr)";
      contentGrid.classList.add("places-layout");
      contentGrid.classList.remove("content-grid");

      // 加載景點數據...
    } else {
      contentGrid.style.display = "grid";
      contentGrid.style.gridTemplateColumns = "repeat(3, 1fr)";
      contentGrid.classList.remove("places-layout");
      contentGrid.classList.add("content-grid");

      // 加載文章數據...
    }

    if (filters.contentType === "saved-places") {
      console.log("Rendering places");
      // 模擬景點資料（保持不變）
      const mockPlaces = [
        {
          name: "淺草寺",
          address: "東京都台東區淺草2-3-1",
          rating: 4.5,
          ratingCount: 1000,
          saveTime: "2024-01-13",
          commentCount: 150,
          comments: [
            {
              userName: "旅行者小明",
              userAvatar: "/api/placeholder/40/40",
              rating: 4.5,
              time: "2024-01-13",
              content: "很棒的景點！建築很漂亮，環境也很清幽。",
              image: "/api/placeholder/200/150",
            },
            {
              userName: "小花",
              userAvatar: "/api/placeholder/40/40",
              rating: 4.0,
              time: "2024-01-12",
              content: "氣氛很好，適合拍照。就是假日人比較多。",
            },
          ],
        },
        {
          name: "明治神宮",
          address: "東京都澀谷區代代木神園町1-1",
          rating: 4.7,
          ratingCount: 800,
          saveTime: "2024-01-12",
          commentCount: 120,
          comments: [
            {
              userName: "日本通",
              userAvatar: "/api/placeholder/40/40",
              rating: 5.0,
              time: "2024-01-10",
              content: "必去的景點！很有日本特色。",
              image: "/api/placeholder/200/150",
            },
          ],
        },
      ];

      const sortedPlaces = sortPlaces(mockPlaces, filters.sortBy);
      renderPlaces(sortedPlaces);
      renderPagination(sortedPlaces.length);
      return;
    }

    // 其他內容類型的處理
    const endpoint = getEndpointByType(filters.contentType);
    let url = endpoint;
    if (filters.sortBy) {
      url += `?sortBy=${filters.sortBy}`;
    }

    const response = await fetch(url);
    const data = await response.json();

    currentArticles = data;
    renderArticles();
    renderPagination(data.length);
  } catch (error) {
    console.error("Error loading content:", error);
    showErrorMessage("載入內容失敗");
  }
}

// 排序景點函數更新，同時保持評論數據
function sortPlaces(places, sortBy) {
  if (!sortBy) return places;

  return [...places].sort((a, b) => {
    switch (sortBy) {
      case "save-time":
        return new Date(b.saveTime) - new Date(a.saveTime);
      case "comments":
        return b.commentCount - a.commentCount;
      case "rating":
        return b.rating - a.rating;
      default:
        return 0;
    }
  });
}

// 根據內容類型獲取對應的 API endpoint
function getEndpointByType(type) {
  const endpoints = {
    public: "/api/articles/public",
    private: "/api/articles/private",
    "saved-trips": "/api/trips/saved",
    "saved-places": "/api/places/saved",
    deleted: "/api/articles/deleted",
    drafts: "/api/articles/drafts",
  };
  return endpoints[type] || endpoints.public;
}

// 渲染景點卡片
function renderPlaces(places) {
  contentGrid.innerHTML = ""; // 清空現有內容

  const startIndex = currentPage * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const pagePlaces = places.slice(startIndex, endIndex);

  pagePlaces.forEach((place) => {
    const placeCard = createPlaceCard(place);
    contentGrid.appendChild(placeCard);
  });
}

// 創建景點卡片
function createPlaceCard(place) {
  const card = document.createElement("div");
  card.className = "place-card";

  // 產生星星評分HTML
  const generateStars = (rating, isInput = false) => {
    // 添加 isInput 參數並設定默認值為 false
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      if (isInput) {
        stars.push(
          `<span class="star rating-star" data-rating="${i}">☆</span>`
        );
      } else {
        if (i <= Math.floor(rating)) {
          stars.push('<span class="star">★</span>');
        } else if (i === Math.ceil(rating) && rating % 1 >= 0.5) {
          stars.push('<span class="star">☆</span>');
        } else {
          stars.push('<span class="star" style="color: #e0e0e0;">☆</span>');
        }
      }
    }
    return stars.join("");
  };

  // 產生評論HTML
  const generateComments = (comments = []) => {
    if (!comments.length) return "";

    return `
            <div class="comments-list">
                ${comments
                  .map(
                    (comment) => `
                    <div class="comment-item">
                        <div class="comment-header">
                            <img src="${
                              comment.userAvatar || "/api/placeholder/40/40"
                            }" 
                                 alt="${comment.userName}" 
                                 class="comment-user-avatar">
                            <div class="comment-user-info">
                                <div class="comment-user-name">${
                                  comment.userName
                                }</div>
                                <div class="comment-rating">
                                    ${generateStars(comment.rating)}
                                    <span class="comment-time">${
                                      comment.time
                                    }</span>
                                </div>
                            </div>
                        </div>
                        <div class="comment-content">${comment.content}</div>
                        ${
                          comment.image
                            ? `
                            <img src="${comment.image}" 
                                 alt="評論圖片" 
                                 class="comment-image">
                        `
                            : ""
                        }
                    </div>
                `
                  )
                  .join("")}
            </div>
        `;
  };

  // 模擬評論數據
  const mockComments = [
    {
      userName: "旅行者小明",
      userAvatar: "/api/placeholder/40/40",
      rating: 4.5,
      time: "2024-01-13",
      content: "很棒的景點！建築很漂亮，環境也很清幽。",
      image: "/api/placeholder/200/150",
    },
    {
      userName: "小花",
      userAvatar: "/api/placeholder/40/40",
      rating: 4.0,
      time: "2024-01-12",
      content: "氣氛很好，適合拍照。就是假日人比較多。",
    },
  ];

  card.innerHTML = `
        <div class="place-header">
            <div class="place-info">
                <h3 class="place-title">${place.name}</h3>
                <p class="place-address">${place.address}</p>
                <div class="place-rating">
                    <div class="star-container">
                        ${generateStars(place.rating)}
                    </div>
                    <span class="rating-text">${place.rating.toFixed(1)} (${
    place.ratingCount
  } 則評價)</span>
                </div>
            </div>
        </div>

        <div class="comments-section">
            <h4 class="comments-title">評論區</h4>
            ${generateComments(mockComments)}
            <div class="comment-input-container">
               <div class="rating-input" id="ratingInput">
                ${generateStars(0, true)} 
                <span class="rating-text"></span>
                    <span class="rating-text"></span>
                </div>
                <textarea class="comment-input" placeholder="分享您的旅遊體驗..."></textarea>
                <div class="comment-actions">
                    <label class="upload-photo">
                        <i class="fas fa-camera"></i>
                        <span>上傳照片</span>
                        <input type="file" accept="image/*" style="display: none;">
                    </label>
                    <button class="submit-comment">發布評論</button>
                </div>
            </div>
        </div>
    `;

  // 添加評論相關的事件監聽器
  const fileInput = card.querySelector('input[type="file"]');
  const submitButton = card.querySelector(".submit-comment");
  const commentInput = card.querySelector(".comment-input");
  const ratingStars = card.querySelectorAll(".rating-star");
  const ratingText = card.querySelector(".rating-input .rating-text");
  let currentRating = 0;

  // 添加評分功能的事件監聽
  ratingStars.forEach((star) => {
    // 滑鼠懸停效果
    star.addEventListener("mouseenter", () => {
      const rating = parseInt(star.dataset.rating);
      ratingStars.forEach((s, index) => {
        if (index < rating) {
          s.textContent = "★";
          s.style.color = "#ffd700";
        } else {
          s.textContent = "☆";
          s.style.color = "#e0e0e0";
        }
      });
      ratingText.textContent = `${rating}.0 分`;
    });

    // 點擊評分
    star.addEventListener("click", () => {
      currentRating = parseInt(star.dataset.rating);
      ratingStars.forEach((s, index) => {
        if (index < currentRating) {
          s.textContent = "★";
          s.style.color = "#ffd700";
        } else {
          s.textContent = "☆";
          s.style.color = "#e0e0e0";
        }
      });
    });
  });

  // 滑鼠離開評分區域時的處理
  const ratingInput = card.querySelector(".rating-input");
  ratingInput.addEventListener("mouseleave", () => {
    ratingStars.forEach((star, index) => {
      if (index < currentRating) {
        star.textContent = "★";
        star.style.color = "#ffd700";
      } else {
        star.textContent = "☆";
        star.style.color = "#e0e0e0";
      }
    });
    ratingText.textContent = currentRating ? `${currentRating}.0 分` : "";
  });

  // 文件上傳事件
  fileInput.addEventListener("change", (e) => {
    const file = e.target.files[0];
    if (file) {
      console.log("Selected file:", file.name);
    }
  });

  // 提交評論事件（更新為包含評分）
  submitButton.addEventListener("click", () => {
    const comment = commentInput.value;
    console.log("Submit comment:", {
      rating: currentRating,
      comment: comment,
    });
    // TODO: 這裡可以添加提交到後端的邏輯
  });

  fileInput.addEventListener("change", (e) => {
    const file = e.target.files[0];
    if (file) {
      console.log("Selected file:", file.name);
    }
  });

  submitButton.addEventListener("click", () => {
    const comment = commentInput.value;
    console.log("Submit comment:", comment);
  });

  return card;
}

// 渲染文章卡片
function renderArticles() {
  contentGrid.innerHTML = ""; // 清空現有內容
  contentGrid.style.display = "grid"; // 恢復網格布局

  const startIndex = currentPage * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const pageArticles = currentArticles.slice(startIndex, endIndex);

  pageArticles.forEach((article) => {
    const articleCard = createArticleCard(article);
    contentGrid.appendChild(articleCard);
  });
}

// 創建文章卡片元素
function createArticleCard(article) {
  const card = document.createElement("div");
  card.className = "content-card";

  card.innerHTML = `
        <img src="${article.image || "/api/placeholder/400/320"}" 
             alt="${article.title}" 
             class="card-image"
             onerror="this.src='/api/placeholder/400/320'">
        <div class="card-body">
            <h2 class="card-title">${article.title}</h2>
            <p class="card-description">${article.description}</p>
            <div class="card-footer">
                <div class="card-stats">
                    <span>👁️ ${article.views || 0}</span>
                    <span>❤️ ${article.likes || 0}</span>
                </div>
                <div class="card-actions">
                    <button class="action-button edit-btn" data-id="${
                      article.id
                    }">編輯</button>
                    <button class="action-button delete-btn" data-id="${
                      article.id
                    }">刪除</button>
                </div>
            </div>
        </div>
    `;

  // 添加按鈕事件監聽器
  const editBtn = card.querySelector(".edit-btn");
  const deleteBtn = card.querySelector(".delete-btn");

  editBtn.addEventListener("click", () => handleEdit(article.id));
  deleteBtn.addEventListener("click", () => handleDelete(article.id));

  return card;
}

// 渲染分頁控制器
function renderPagination(totalItems) {
  const totalPages = Math.ceil(totalItems / itemsPerPage);

  const paginationContainer = document.createElement("div");
  paginationContainer.className = "pagination";

  // 上一頁按鈕
  const prevBtn = document.createElement("button");
  prevBtn.textContent = "上一頁";
  prevBtn.className = "page-btn prev-btn";
  prevBtn.disabled = currentPage === 0;
  prevBtn.addEventListener("click", () => changePage(currentPage - 1));

  // 下一頁按鈕
  const nextBtn = document.createElement("button");
  nextBtn.textContent = "下一頁";
  nextBtn.className = "page-btn next-btn";
  nextBtn.disabled = currentPage === totalPages - 1;
  nextBtn.addEventListener("click", () => changePage(currentPage + 1));

  // 頁碼
  const pageNumbers = document.createElement("div");
  pageNumbers.className = "page-numbers";

  for (let i = 0; i < totalPages; i++) {
    const pageNumber = document.createElement("span");
    pageNumber.className = `page-number${i === currentPage ? " active" : ""}`;
    pageNumber.textContent = i + 1;
    pageNumber.addEventListener("click", () => changePage(i));
    pageNumbers.appendChild(pageNumber);
  }

  paginationContainer.appendChild(prevBtn);
  paginationContainer.appendChild(pageNumbers);
  paginationContainer.appendChild(nextBtn);

  // 替換現有的分頁控制器
  const existingPagination = document.querySelector(".pagination");
  if (existingPagination) {
    existingPagination.remove();
  }
  contentGrid.after(paginationContainer);
}

// 切換頁面
function changePage(newPage) {
  currentPage = newPage;
  const contentType = contentTypeSelect.value;
  const sortBy = sortTypeSelect.value;

  loadContent({
    contentType: contentType,
    sortBy: sortBy,
  });
}

// 編輯文章
function handleEdit(articleId) {
  console.log("編輯文章:", articleId);
  // TODO: 實現編輯功能
}

// 刪除文章
function handleDelete(articleId) {
  if (confirm("確定要刪除這篇文章嗎？")) {
    console.log("刪除文章:", articleId);
    // TODO: 實現刪除功能
  }
}

// 顯示錯誤訊息
function showErrorMessage(message) {
  // TODO: 實現錯誤提示UI
  console.error(message);
}

// 初始載入內容
document.addEventListener("DOMContentLoaded", () => {
  updateSortOptions("public"); // 設定初始排序選項
  loadContent({
    contentType: "public",
    sortBy: "",
  });
});
