window.NavigationManager = class NavigationManager {
  constructor() {
    this.config = {
      baseUrl: "/TIA104G2-SpringBoot",
      paths: {
        home: "/home",
        go: "/go",
        myTrip: "/viewMyTrip",
        login: "/login",
        profile: "/viewProfile",
        chat: "/chatroom",
        editor: "/editor/create",
        article: "/tripArticle",
        memberTrip: "/viewTrip",
      },
    };

    this.state = {
      isLoggedIn: false,
      memberId: null,
    };
  }

  static getInstance() {
    if (!window.navigationManager) {
      window.navigationManager = new NavigationManager();
    }
    return window.navigationManager;
  }

  async init() {
    try {
      // 確保在初始化時立即檢查登入狀態
      await this.checkLoginStatus();
      this.setupNavigationEvents();
      this.updateUIBasedOnAuth();
      this.setupSupportMenu();

      // 添加路由監聽器，每次 URL 改變時重新檢查狀態
      window.addEventListener("popstate", async () => {
        await this.checkLoginStatus();
        this.updateUIBasedOnAuth();
      });
    } catch (error) {
      console.error("Navigation initialization failed:", error);
    }
  }

  async checkLoginStatus() {
    try {
      const response = await fetch(
        `${this.config.baseUrl}/member/getCurrentMemberId`,
        {
          credentials: "include",
          headers: {
            "Cache-Control": "no-cache",
            Pragma: "no-cache",
          },
        }
      );

      if (response.ok) {
        const data = await response.json();
        if (data && data.memberId) {
          this.state.isLoggedIn = true;
          this.state.memberId = data.memberId;
          localStorage.setItem("isLoggedIn", "true");
          localStorage.setItem("memberId", data.memberId);
        } else {
          this.resetLoginState();
        }
        console.log("Current login state:", this.state);
      } else {
        this.resetLoginState();
      }
    } catch (error) {
      console.error("Login status check failed:", error);
      this.resetLoginState();
    }

    // 每次檢查後立即更新 UI
    this.updateUIBasedOnAuth();
  }

  setupNavigationEvents() {
    // 設置主導航
    const homeBtn = document.getElementById("nav-home");
    const goBtn = document.getElementById("nav-go");
    const myGoBtn = document.getElementById("nav-mygo");
    const loginBtn = document.getElementById("nav-login");
    const profileBtn = document.getElementById("nav-profile");
    const chatBtn = document.getElementById("goToChatRoom");

    // 首頁導航
    if (homeBtn) {
      homeBtn.addEventListener("click", () => {
        window.location.href = `${this.config.baseUrl}${this.config.paths.home}`;
      });
    }

    // Go 頁面導航
    if (goBtn) {
      goBtn.addEventListener("click", () => {
        window.location.href = `${this.config.baseUrl}${this.config.paths.go}`;
      });
    }

    // My行程導航
    if (myGoBtn) {
      myGoBtn.addEventListener("click", () => {
        window.location.href = `${this.config.baseUrl}${this.config.paths.myTrip}`;
      });
    }

    // 登入/登出按鈕
    if (loginBtn) {
      loginBtn.addEventListener("click", (e) => {
        if (this.state.isLoggedIn) {
          e.preventDefault();
          this.handleLogout();
        }
      });
    }

    // 個人資料頁面
    if (profileBtn) {
      profileBtn.addEventListener("click", () => {
        window.location.href = `${this.config.baseUrl}${this.config.paths.profile}`;
      });
    }

    // 聊天室
    if (chatBtn) {
      chatBtn.addEventListener("click", () => {
        window.location.href = `${this.config.baseUrl}${this.config.paths.chat}`;
      });
    }
  }
  
  resetLoginState() {
    this.state.isLoggedIn = false;
    this.state.memberId = null;
    localStorage.removeItem("isLoggedIn");
    localStorage.removeItem("memberId");
  }

  updateUIBasedOnAuth() {
    const loginBtn = document.getElementById("nav-login");
    const myGoBtn = document.getElementById("nav-mygo");
    const profileBtn = document.getElementById("nav-profile");
    const chatBtn = document.getElementById("goToChatRoom");

    // 先檢查本地存儲的登入狀態
    const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";

    if (isLoggedIn) {
      if (loginBtn) {
        loginBtn.textContent = "登出";
        loginBtn.style.color = "#f17575";
        loginBtn.href = "#";
      }
      if (myGoBtn) myGoBtn.style.display = "";
      if (profileBtn) profileBtn.style.display = "";
      if (chatBtn) chatBtn.style.display = "";
    } else {
      if (loginBtn) {
        loginBtn.textContent = "登入／註冊";
        loginBtn.style.color = "#f17575";
        loginBtn.href = `${this.config.baseUrl}/login`;
      }
      if (myGoBtn) myGoBtn.style.display = "none";
      if (profileBtn) profileBtn.style.display = "none";
      if (chatBtn) chatBtn.style.display = "none";
    }
  }

  async handleLogout() {
    try {
      const response = await fetch(`${this.config.baseUrl}/member/logout`, {
        method: "POST",
        credentials: "include",
      });

      if (response.ok) {
        this.resetLoginState();
        this.updateUIBasedOnAuth();
        window.location.href = `${this.config.baseUrl}/home`;
      } else {
        throw new Error("Logout failed");
      }
    } catch (error) {
      console.error("Logout failed:", error);
      alert("登出失敗，請稍後再試");
    }
  }

  setupSupportMenu() {
    const supportBtn = document.getElementById("nav-support");
    if (!supportBtn) return;

    const dropdownContainer = document.createElement("div");
    dropdownContainer.className = "support-dropdown";
    dropdownContainer.style.display = "none";
    dropdownContainer.innerHTML = `
            <div class="support-menu">
                <div class="support-header">
                    <h3>通知中心</h3>
                </div>
                <div class="support-content">
                    <div class="notification-list">暫無通知</div>
                </div>
            </div>
        `;

    supportBtn.appendChild(dropdownContainer);

    supportBtn.onclick = (e) => {
      e.preventDefault();
      e.stopPropagation();
      const isVisible = dropdownContainer.style.display === "block";
      dropdownContainer.style.display = isVisible ? "none" : "block";
    };

    document.addEventListener("click", (e) => {
      if (!supportBtn.contains(e.target)) {
        dropdownContainer.style.display = "none";
      }
    });
  }
};

// 建立全局實例
window.navigationManager = new NavigationManager();

// 確保立即創建實例並初始化
document.addEventListener("DOMContentLoaded", async () => {
  const navManager = NavigationManager.getInstance();
  await navManager.init();

  // 監聽頁面可見性變化
  document.addEventListener("visibilitychange", () => {
    if (document.visibilityState === "visible") {
      navManager.checkLoginStatus();
    }
  });
});
