// mytrip-manager.js
class MyTripManager {
  constructor() {
    this.baseUrl = "/TIA104G2-SpringBoot";
    this.publishBtn = document.getElementById("publish-mytrip");
    console.log("MyTripManager initialized, publishBtn:", this.publishBtn);
    this.init();
  }

  init() {
    this.setupEventListeners();
  }

  setupEventListeners() {
    // 發布文章按鈕事件
    if (this.publishBtn) {
      this.publishBtn.onclick = async (e) => {
        console.log("Publish button clicked");
        e.preventDefault();

        try {
          // 檢查是否登入
          const response = await fetch(`${this.baseUrl}/member/checkLogin`, {
            credentials: "include",
            headers: {
              "Content-Type": "application/json",
            },
          });
          console.log("Login check response:", response);

          const data = await response.json();
          console.log("Login check data:", data);

          if (!data.loggedIn) {
            alert("請先登入");
            window.location.href = `${this.baseUrl}/login`;
            return;
          }

          // 導向編輯器頁面
          console.log("Redirecting to editor page");
          window.location.href = `${this.baseUrl}/editor/create`;
        } catch (error) {
          console.error("發布文章時發生錯誤:", error);
          alert("系統發生錯誤，請稍後再試");
        }
      };
      console.log("Publish button event listener set up");
    } else {
      console.warn("Publish button not found in DOM");
    }
  }
}

// 當 DOM 加載完成後初始化
document.addEventListener("DOMContentLoaded", () => {
  console.log("DOM loaded, initializing MyTripManager");
  window.myTripManager = new MyTripManager();
});
