// 全局變數
const contentGrid = document.querySelector(".content-grid");
console.log("Content grid element:", contentGrid);
const primaryFilter = document.getElementById("primary-filter");
const secondaryFilter = document.getElementById("secondary-filter");
const sortTypeSelect = document.getElementById("sort-type");
const applyFilterBtn = document.getElementById("apply-filter");
let currentPage = 0;
const itemsPerPage = 12;
let currentArticles = [];
let currentPlaces = [];

// 排序選項配置
const filterOptions = {
  "my-articles": {
    options: [
      { value: "public", label: "公開文章" },
      { value: "private", label: "私人文章" },
      { value: "drafts", label: "草稿箱" },
      { value: "deleted", label: "已刪除文章" },
    ],
    sortOptions: [
      { value: "publish-time", label: "發表時間" },
      { value: "popularity", label: "熱門程度" },
      { value: "likes", label: "點讚數" },
      { value: "saves", label: "收藏數" },
      { value: "rating", label: "評分" },
    ],
  },
  "saved-articles": {
    options: [], // 無子選項
    sortOptions: [
      { value: "publish-time", label: "發表時間" },
      { value: "popularity", label: "熱門程度" },
      { value: "likes", label: "點讚數" },
      { value: "saves", label: "收藏數" },
      { value: "rating", label: "評分" },
    ],
  },
  "saved-places": {
    options: [], // 從後端動態獲取景點列表
    sortOptions: [
      { value: "save-time", label: "收藏時間" },
      { value: "comments", label: "評論數" },
      { value: "rating", label: "評分" },
    ],
  },
};

// 監聽第一層選擇變化
primaryFilter.addEventListener("change", async function (e) {
  const selectedType = e.target.value;

  // 清空並重置次級選擇器
  secondaryFilter.innerHTML = '<option value="">請選擇</option>';
  sortTypeSelect.innerHTML = '<option value="">選擇排序方式</option>';

  if (!selectedType) {
    contentGrid.innerHTML = ""; // 清空內容
    return;
  }

  const options = filterOptions[selectedType];

  // 更新第二層選項
  if (selectedType === "saved-places") {
    try {
      // 從後端獲取景點列表
      const response = await fetch("/api/places/list");
      const places = await response.json();
      places.forEach((place) => {
        const option = document.createElement("option");
        option.value = place.id;
        option.textContent = place.name;
        secondaryFilter.appendChild(option);
      });
    } catch (error) {
      console.error("無法載入景點列表:", error);
      // 使用模擬數據作為備選
      const mockPlaces = [
        { id: "place1", name: "淺草寺" },
        { id: "place2", name: "明治神宮" },
      ];
      mockPlaces.forEach((place) => {
        const option = document.createElement("option");
        option.value = place.id;
        option.textContent = place.name;
        secondaryFilter.appendChild(option);
      });
    }
  } else if (options.options.length > 0) {
    options.options.forEach((option) => {
      const optionElement = document.createElement("option");
      optionElement.value = option.value;
      optionElement.textContent = option.label;
      secondaryFilter.appendChild(optionElement);
    });
  }

  // 更新排序選項
  options.sortOptions.forEach((option) => {
    const optionElement = document.createElement("option");
    optionElement.value = option.value;
    optionElement.textContent = option.label;
    sortTypeSelect.appendChild(optionElement);
  });
});

// 確認按鈕處理所有篩選參數
applyFilterBtn.addEventListener("click", async function () {
  const primaryValue = primaryFilter.value;
  const secondaryValue = secondaryFilter.value;
  const sortValue = sortTypeSelect.value;
  currentPage = 0; // 重置頁碼

  // 確保至少選擇了主要分類
  if (!primaryValue) {
    showErrorMessage("請選擇內容類型");
    return;
  }

  try {
    // 準備要送到後端的參數
    const params = {
      primaryFilter: primaryValue,
      secondaryFilter: secondaryValue || null,
      sortBy: sortValue || null,
    };

    // 構建 query string
    const queryString = new URLSearchParams(params).toString();

    // 發送請求到後端的統一入口點
    const response = await fetch(`/api/content/filter?${queryString}`);
    const data = await response.json();

    // 根據 primaryFilter 決定渲染方式
    if (primaryValue === "saved-places") {
      contentGrid.classList.add("places-layout");
      contentGrid.classList.remove("content-grid");
      contentGrid.style.display = "grid";
      // 保存並渲染景點數據
      currentPlaces = sortPlaces(data.content || [], params.sortBy);
      renderPlaces(currentPlaces);
      renderPagination(currentPlaces.length);
    } else {
      contentGrid.classList.remove("places-layout");
      contentGrid.classList.add("content-grid");
      contentGrid.style.display = "grid";
      currentArticles = data.content || [];
      renderArticles();
      renderPagination(currentArticles.length);
    }
  } catch (error) {
    console.error("載入內容時發生錯誤:", error);
    showErrorMessage("載入內容時發生錯誤");

    // 使用模擬數據（測試用）
    if (primaryValue === "saved-places") {
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
              id: "comment1", // 添加評論 ID
              userName: "旅行者小明",
              userAvatar: "/api/placeholder/40/40",
              rating: 4.5,
              time: "2024-01-13",
              content: "很棒的景點！建築很漂亮，環境也很清幽。",
              image: "/api/placeholder/200/150",
            },
            {
              id: "comment2", // 添加評論 ID
              userName: "小花",
              userAvatar: "/api/placeholder/40/40",
              rating: 5,
              time: "2024-01-12",
              content: "必去景點！人潮很多但秩序良好。",
              image: null,
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
              id: "comment3", // 添加評論 ID
              userName: "小王",
              userAvatar: "/api/placeholder/40/40",
              rating: 4.7,
              time: "2024-01-11",
              content: "環境很清幽，非常適合放鬆心情。",
              image: "/api/placeholder/200/150",
            },
          ],
        },
      ];
      contentGrid.classList.add("places-layout");
      contentGrid.classList.remove("content-grid");
      currentPlaces = sortPlaces(mockPlaces, sortValue);
      renderPlaces(currentPlaces);
      renderPagination(currentPlaces.length);
    } else {
      const mockArticles = [
        {
          id: 1,
          title: "日本自由行攻略",
          description: "詳細的日本自由行規劃指南",
          image: "https://picsum.photos/400/320",
          views: 1500,
          likes: 300,
        },
        {
          id: 2,
          title: "京都賞櫻攻略",
          description: "最佳賞櫻景點推薦",
          image: "https://picsum.photos/400/320",
          views: 2000,
          likes: 450,
        },
      ];
      contentGrid.classList.remove("places-layout");
      contentGrid.classList.add("content-grid");
      currentArticles = mockArticles;
      renderArticles();
      renderPagination(mockArticles.length);
    }
  }
});

// 排序景點函數
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

// 渲染景點卡片
function renderPlaces(places) {
  if (!places || places.length === 0) {
    contentGrid.innerHTML = "<div>暫無收藏景點</div>";
    return;
  }

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
        <div class="comment-item" data-comment-id="${comment.id}">
          <div class="comment-header">
            <img src="${comment.userAvatar || "/api/placeholder/40/40"}" 
                 alt="${comment.userName}" 
                 class="comment-user-avatar">
            <div class="comment-user-info">
              <div class="comment-user-name">${comment.userName}</div>
              <div class="comment-rating">
                ${generateStars(comment.rating)}
                <span class="comment-time">${comment.time}</span>
              </div>
            </div>
            <div class="comment-actions-dropdown">
              <!-- 改用更明顯的「...」符號 -->
              <button class="comment-actions-toggle" title="評論操作選項">
                ⋮
              </button>
              <div class="comment-actions-menu">
                <button class="comment-action-btn edit-comment-btn">
                  ✎ 編輯
                </button>
                <button class="comment-action-btn delete-comment-btn">
                  🗑️ 刪除
                </button>
                <button class="comment-action-btn report-comment-btn">
                  ⚠️ 檢舉
                </button>
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
      ${generateComments(place.comments)}
      <div class="comment-input-container">
        <div class="rating-input" id="ratingInput">
          ${generateStars(0, true)} 
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

  // 評論相關的事件監聽器
  const addCommentEventListeners = () => {
    card.querySelectorAll(".comment-item").forEach((commentEl) => {
      const commentId = commentEl.dataset.commentId;
      const actionsToggle = commentEl.querySelector(".comment-actions-toggle");
      const actionsMenu = commentEl.querySelector(".comment-actions-menu");

      // 切換選單顯示
      actionsToggle?.addEventListener("click", (e) => {
        e.stopPropagation();
        actionsMenu.classList.toggle("show");
      });

      // 編輯評論
      commentEl
        .querySelector(".edit-comment-btn")
        ?.addEventListener("click", () => {
          handleEditComment(commentId);
        });

      // 刪除評論
      commentEl
        .querySelector(".delete-comment-btn")
        ?.addEventListener("click", () => {
          handleDeleteComment(commentId);
        });

      // 檢舉評論
      commentEl
        .querySelector(".report-comment-btn")
        ?.addEventListener("click", () => {
          handleReportComment(commentId);
        });
    });

    // 點擊其他地方關閉選單
    document.addEventListener("click", () => {
      card.querySelectorAll(".comment-actions-menu.show").forEach((menu) => {
        menu.classList.remove("show");
      });
    });
  };

  // 添加評論相關的事件監聽器
  addCommentEventListeners();

  return card;
}

// 評論操作處理函數
function handleEditComment(commentId) {
  console.log("編輯評論:", commentId);
  // TODO: 實現編輯評論功能
}

function handleDeleteComment(commentId) {
  if (confirm("確定要刪除這則評論嗎？")) {
    console.log("刪除評論:", commentId);
    // TODO: 實現刪除評論功能
  }
}

function handleReportComment(commentId) {
  if (confirm("確定要檢舉這則評論嗎？")) {
    console.log("檢舉評論:", commentId);
    // TODO: 實現檢舉評論功能
  }
}

// 渲染文章卡片
function renderArticles() {
  contentGrid.innerHTML = ""; // 清空現有內容

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

  // 獲取當前選擇的內容類型
  const currentContentType = primaryFilter.value;

  // 根據內容類型決定按鈕的 HTML
  const getActionButtons = () => {
    if (currentContentType === "saved-articles") {
      // 收藏文章只顯示「移除收藏」按鈕
      return `
        <button class="action-button delete-btn" data-id="${article.id}">移除收藏</button>
      `;
    } else {
      // 我的文章顯示「編輯」和「刪除」按鈕
      return `
        <button class="action-button edit-btn" data-id="${article.id}">編輯</button>
        <button class="action-button delete-btn" data-id="${article.id}">刪除</button>
      `;
    }
  };

  card.innerHTML = `
    <img src="${article.image || "https://picsum.photos/400/320"}" 
     alt="${article.title}" 
     class="card-image"
     onerror="this.src='https://picsum.photos/400/320'">
    <div class="card-body">
      <h2 class="card-title">${article.title}</h2>
      <p class="card-description">${article.description}</p>
      <div class="card-footer">
        <div class="card-stats">
          <span>👁️ ${article.views || 0}</span>
          <span>❤️ ${article.likes || 0}</span>
        </div>
        <div class="card-actions">
          ${getActionButtons()}
        </div>
      </div>
    </div>
  `;

  // 添加按鈕事件監聽器
  const deleteBtn = card.querySelector(".delete-btn");
  const editBtn = card.querySelector(".edit-btn");

  // 根據內容類型設定不同的刪除行為
  deleteBtn.addEventListener("click", () => {
    if (currentContentType === "saved-articles") {
      handleRemoveSaved(article.id);
    } else {
      handleDelete(article.id);
    }
  });

  // 只有在編輯按鈕存在時才添加事件監聽器
  if (editBtn) {
    editBtn.addEventListener("click", () => handleEdit(article.id));
  }

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

  // 直接使用當前的數據重新渲染
  const primaryValue = primaryFilter.value;
  if (primaryValue === "saved-places" && currentPlaces.length > 0) {
    renderPlaces(currentPlaces);
    renderPagination(currentPlaces.length);
  } else if (currentArticles.length > 0) {
    renderArticles();
    renderPagination(currentArticles.length);
  }
}

// 處理移除收藏的函數
function handleRemoveSaved(articleId) {
  if (confirm("確定要移除這篇收藏的文章嗎？")) {
    console.log("移除收藏文章:", articleId);
    // TODO: 實現移除收藏的 API 調用
    // 可以在這裡添加與後端的通信邏輯
  }
}

// 編輯函數
function handleEdit(articleId) {
  console.log("編輯文章:", articleId);
  // TODO: 實現編輯功能
}

// 刪除函數
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

// 頁面載入初始化
document.addEventListener("DOMContentLoaded", () => {
  // 設置預設值
  primaryFilter.value = "my-articles";
  secondaryFilter.innerHTML = '<option value="">請選擇</option>';
  filterOptions["my-articles"].options.forEach((option) => {
    const optionElement = document.createElement("option");
    optionElement.value = option.value;
    optionElement.textContent = option.label;
    secondaryFilter.appendChild(optionElement);
  });
  secondaryFilter.value = "public";

  // 設置排序選項
  sortTypeSelect.innerHTML = '<option value="">選擇排序方式</option>';
  filterOptions["my-articles"].sortOptions.forEach((option) => {
    const optionElement = document.createElement("option");
    optionElement.value = option.value;
    optionElement.textContent = option.label;
    sortTypeSelect.appendChild(optionElement);
  });

  // 使用模擬數據直接渲染初始內容，而不是觸發按鈕點擊
  const mockArticles = [
    {
      id: 1,
      title: "日本自由行攻略",
      description: "詳細的日本自由行規劃指南",
      image: "https://picsum.photos/400/320",
      views: 1500,
      likes: 300,
    },
    {
      id: 2,
      title: "京都賞櫻攻略",
      description: "最佳賞櫻景點推薦",
      image: "https://picsum.photos/400/320",
      views: 2000,
      likes: 450,
    },
  ];

  currentArticles = mockArticles;
  renderArticles();
  renderPagination(mockArticles.length);
});
