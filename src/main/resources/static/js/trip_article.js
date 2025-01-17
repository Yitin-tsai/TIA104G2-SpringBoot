// 文章數據加載與更新
class ArticleManager {
  constructor(articleId) {
    this.articleId = articleId;
    this.baseUrl = "/api"; // API 基礎路徑
    this.initializeEvents();
    this.initRatingEvents();  
    this.initCollectEvents();  
  }

  // 初始化事件
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

  // 載入文章數據
  async loadArticleData() {
    try {
      const response = await fetch(
        `${this.baseUrl}/articles/${this.articleId}`
      );
      if (!response.ok) throw new Error("Failed to load article data");

      const data = await response.json();
      this.updateUI(data);
    } catch (error) {
      console.error("Error loading article data:", error);
    }
  }

  // 更新 UI
  updateUI(data) {
    // 更新瀏覽數
    document.getElementById("view-count").textContent = data.viewCount;

    // 更新收藏數
    document.getElementById("favorite-count").textContent = data.favoriteCount;

    // 更新評分
    this.updateRating(data.rating);

    // 更新標籤
    this.updateTags(data.tags);

    // 更新收藏狀態
    this.updateFavoriteStatus(data.isFavorited);

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
}

// 初始化文章管理器
document.addEventListener("DOMContentLoaded", () => {
  const articleId = new URLSearchParams(window.location.search).get("id");
  const articleManager = new ArticleManager(articleId);
  articleManager.loadArticleData();
});
