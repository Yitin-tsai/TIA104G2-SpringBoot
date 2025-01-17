const ArticleManager = {
  // 常數與設定
  config: {
    contextPath: "/TIA104G2-SpringBoot",
    pageSize: 12,
  },

  // 狀態管理
  state: {
    isLoggedIn: false,
    currentPage: 0,
    currentArticles: [],
  },

  // DOM 元素
  elements: {
    articlesGrid: null,
    activitySelect: null,
    regionSelect: null,
    searchInput: null,
    searchBtn: null,
    pagination: null,
  },

  // 初始化
  init: function () {
    // 初始化 DOM 元素
    this.elements.articlesGrid = $(".articles-grid");
    this.elements.activitySelect = $("#activity-type");
    this.elements.regionSelect = $("#region");
    this.elements.searchInput = $("#search-input");
    this.elements.searchBtn = $("#search-btn");
    this.elements.pagination = $(".pagination");

    // 綁定事件
    this.bindEvents();

    // 初始化功能
    this.loadOptions();
    this.checkLoginStatus();
    this.loadArticles(0);
  },

  // 檢查登入狀態
  checkLoginStatus: async function () {
    try {
      const response = await fetch(
        `${this.config.contextPath}/member/getCurrentMemberId`,
        {
          method: "GET",
          credentials: "include",
          headers: {
            Accept: "application/json",
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) return null;

      const data = await response.json();
      const memberId = parseInt(data.memberId);
      this.state.isLoggedIn = !!memberId;
      return memberId;
    } catch (error) {
      console.error("檢查登入狀態發生錯誤:", error);
      return null;
    }
  },

  // 事件綁定
  bindEvents: function () {
    this.elements.searchBtn.on("click", (e) => {
      e.preventDefault();
      this.performSearch();
    });

    this.elements.searchInput.on("keypress", (e) => {
      if (e.which === 13) {
        e.preventDefault();
        this.performSearch();
      }
    });
  },

  // 載入選項
  loadOptions: function () {
    $.ajax({
      url: `${this.config.contextPath}/tripactype/all`,
      type: "GET",
      success: (typeList) => {
        this.elements.activitySelect
          .empty()
          .append('<option value="">活動類型</option>')
          .append(
            typeList.map((type) => `<option value="${type}">${type}</option>`)
          );
      },
      error: (xhr) => this.showErrorMessage("載入活動類型失敗"),
    });

    $.ajax({
      url: `${this.config.contextPath}/triparea/all`,
      type: "GET",
      success: (regions) => {
        this.elements.regionSelect
          .empty()
          .append('<option value="">地區</option>')
          .append(
            regions.map(
              (region) => `<option value="${region}">${region}</option>`
            )
          );
      },
      error: (xhr) => this.showErrorMessage("載入地區清單失敗"),
    });
  },

  // 載入文章
  loadArticles: async function (page = 0) {
    try {
      console.log("Sending request with:", {
        page: page,
        size: this.config.pageSize,
      });

      const response = await $.ajax({
        url: `${this.config.contextPath}/trip/all`,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
          page: page,
          size: this.config.pageSize,
        }),
      });

      console.log("Received response:", response); // 查看響應內容

      if (response && response.content) {
        this.state.currentArticles = this.transformTripsToArticles(
          response.content
        );

        console.log("Transformed articles:", this.state.currentArticles); // 查看轉換後的文章

        this.renderArticles(this.state.currentArticles);
        this.renderPagination(response);
      } else {
        console.log("Response is missing content:", response); // 查看無內容的情況
      }
    } catch (xhr) {
      console.error("Error details:", xhr); // 查看錯誤詳情
      if (xhr.status === 404) {
        this.showNoResultsMessage();
      } else {
        this.showErrorMessage("載入文章失敗");
      }
    }
  },

  // 轉換資料
  transformTripsToArticles: function (tripDataList) {
    return tripDataList.map((trip) => ({
      id: trip.id,
      title: trip.title || "未命名行程",
      author: trip.author || "未知作者",
      description: trip.description || "沒有行程簡介",
      image: this.processImage(trip.image),
      tags: trip.tags || [],
      views: trip.views || 0,
      likes: trip.likes || 0,
      rating: trip.rating || 4.5,
      authorId: trip.authorId,
    }));
  },

  // 處理圖片
  processImage: function (image) {
    if (!image) return "https://via.placeholder.com/350x200?text=NoImage";
    return image.startsWith("/") ? `data:image/png;base64,${image}` : image;
  },

  // 將 checkIfTracked 加入到 ArticleManager 物件中
  checkIfTracked: async function (authorId) {
    try {
      const response = await fetch(
        `${this.config.contextPath}/trackMember/checkTrack/${authorId}`
      );
      const isTracked = await response.json();
      const followButton = document.querySelector(
        `button.follow-btn[data-id="${authorId}"]`
      );
      if (followButton) {
        if (isTracked) {
          followButton.innerHTML = "追蹤中";
          followButton.classList.add("cancelfollow-btn");
          followButton.classList.remove("follow-btn");
          followButton.removeEventListener(
            "click",
            this.followSystem.followHandler
          );
          followButton.addEventListener(
            "click",
            this.followSystem.cancelFollowHandler
          );
        } else {
          followButton.innerHTML = "追蹤";
          followButton.classList.add("follow-btn");
          followButton.classList.remove("cancelfollow-btn");
          followButton.removeEventListener(
            "click",
            this.followSystem.cancelFollowHandler
          );
          followButton.addEventListener(
            "click",
            this.followSystem.followHandler
          );
        }
      }
    } catch (error) {
      console.error("檢查是否追蹤失敗:", error);
    }
  },

  // 渲染文章
  renderArticles: function (articles) {
    if (!articles || articles.length === 0) {
      this.showNoResultsMessage();
      return;
    }

    const startIndex = this.state.currentPage * this.config.pageSize;
    const pageArticles = articles.slice(
      startIndex,
      startIndex + this.config.pageSize
    );

    this.elements.articlesGrid.empty();

    pageArticles.forEach((article) => {
      const articleHTML = this.createArticleHTML(article);
      this.elements.articlesGrid.append(articleHTML);
      this.checkIfTracked.call(this, article.authorId);
    });
    this.renderPagination(articles);
  },

  // 建立文章 HTML
  createArticleHTML: function (article) {
    return `
            <article class="article-card">
                <img src="${article.image}"
                     alt="${article.title}"
                     class="article-image"
                     onerror="this.src='https://via.placeholder.com/350x200?text=NoImage';" />
                <div class="article-content">
                    <div class="article-meta">
                        <span class="author">作者：${article.author}</span>
                        <button class="follow-btn" data-id="${
                          article.authorId
                        }">追蹤</button>
                    </div>
                    <h2 class="article-title">${article.title}</h2>
                    <p class="article-description">${article.description}</p>
                    <div class="article-tags">
                        ${article.tags
                          .map((tag) => `<span class="tag">${tag}</span>`)
                          .join("")}
                    </div>
                    <div class="article-stats">
                        <span>👁️ ${article.views}</span>
                        <span>❤️ ${article.likes}</span>
                        <span class="rating">★★★★★ ${article.rating.toFixed(
                          1
                        )}</span>
                    </div>
                </div>
            </article>
        `;
  },

  // 渲染分頁
  renderPagination: function (response) {
    // response 包含 totalPages, currentPage 等資訊
    const totalPages = Math.ceil(response.totalElements / this.config.pageSize);
    const currentPage = response.number || 0; // 當前頁碼，從0開始

    const $pageNumbers = $(".page-numbers");
    const $prevBtn = $(".prev-btn");
    const $nextBtn = $(".next-btn");

    // 清空現有頁碼
    $pageNumbers.empty();

    // 設置上一頁/下一頁按鈕狀態
    $prevBtn.prop("disabled", currentPage === 0);
    $nextBtn.prop("disabled", currentPage === totalPages - 1);

    // 決定顯示的頁碼範圍
    let startPage = Math.max(0, currentPage - 2);
    let endPage = Math.min(totalPages - 1, currentPage + 2);

    // 調整頁碼範圍，確保至少顯示5頁
    if (endPage - startPage < 4) {
      if (startPage === 0) {
        endPage = Math.min(4, totalPages - 1);
      } else {
        startPage = Math.max(0, endPage - 4);
      }
    }

    // 生成頁碼
    // 第一頁
    if (startPage > 0) {
      $pageNumbers.append(`<span class="page-number" data-page="0">1</span>`);
      if (startPage > 1) {
        $pageNumbers.append('<span class="page-dots">...</span>');
      }
    }

    // 中間頁碼
    for (let i = startPage; i <= endPage; i++) {
      $pageNumbers.append(
        `<span class="page-number${i === currentPage ? " active" : ""}" 
                       data-page="${i}">${i + 1}</span>`
      );
    }

    // 最後頁
    if (endPage < totalPages - 1) {
      if (endPage < totalPages - 2) {
        $pageNumbers.append('<span class="page-dots">...</span>');
      }
      $pageNumbers.append(
        `<span class="page-number" data-page="${
          totalPages - 1
        }">${totalPages}</span>`
      );
    }

    // 綁定點擊事件
    $(".page-number").on("click", (e) => {
      const page = $(e.target).data("page");
      this.loadArticles(page);
    });

    // 綁定上一頁/下一頁按鈕事件
    $prevBtn.off("click").on("click", () => {
      if (currentPage > 0) {
        this.loadArticles(currentPage - 1);
      }
    });

    $nextBtn.off("click").on("click", () => {
      if (currentPage < totalPages - 1) {
        this.loadArticles(currentPage + 1);
      }
    });
  },

  // 追蹤系統
  followSystem: {
    checkIfTracked: async function (authorId) {
      try {
        const response = await fetch(
          `${ArticleManager.config.contextPath}/trackMember/checkTrack/${authorId}`
        );
        const isTracked = await response.json();
        this.updateFollowButton(authorId, isTracked);
      } catch (error) {
        console.error("檢查追蹤狀態失敗:", error);
      }
    },

    updateFollowButton: function (authorId, isTracked) {
      const button = document.querySelector(
        `button.follow-btn[data-id="${authorId}"]`
      );
      if (!button) return;

      if (isTracked) {
        button.innerHTML = "追蹤中";
        button.classList.add("cancelfollow-btn");
        button.classList.remove("follow-btn");
      } else {
        button.innerHTML = "追蹤";
        button.classList.add("follow-btn");
        button.classList.remove("cancelfollow-btn");
      }
    },

    followAuthor: async function (authorId) {
      try {
        const response = await fetch(
          `${ArticleManager.config.contextPath}/trackMember/track/${authorId}`,
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
          }
        );
        const data = await response.text();
        this.handleFollowResponse(data);
      } catch (error) {
        console.error(error);
        alert("發生錯誤，請稍後再試！");
      }
    },

    handleFollowResponse: function (data) {
      if (data === "nologin") {
        alert("尚未登入，請登入");
        window.location.href = ArticleManager.config.contextPath + "/login";
      } else if (data === "success") {
        alert("成功追蹤！");
        ArticleManager.loadArticles(0);
      } else {
        alert("追蹤失敗，請稍後再試！");
      }
    },
  },

  // 錯誤處理
  showErrorMessage: function (message) {
    const errorDiv = $("<div>").addClass("error-message").text(message);

    this.elements.articlesGrid.before(errorDiv);
    setTimeout(() => errorDiv.remove(), 3000);
  },

  showNoResultsMessage: function () {
    this.elements.articlesGrid.html(
      '<div class="no-results">沒有找到相關文章</div>'
    );
  },
};

// 初始化
$(document).ready(() => {
  ArticleManager.init();
});
