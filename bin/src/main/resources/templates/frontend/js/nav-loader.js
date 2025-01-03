// 定義基礎路徑
const CONTEXT_PATH = "TIA104G2-SpringBoot";

// 頁面映射
const pageMapping = {
  "nav-go": `/${CONTEXT_PATH}/go`,
  "nav-product": `/${CONTEXT_PATH}/product`,
  "nav-profile": `/${CONTEXT_PATH}/profile`,
  "nav-guide": `/${CONTEXT_PATH}/guide`,
  "nav-basic-info": `/${CONTEXT_PATH}/basic_info`,
  "nav-coupons": `/${CONTEXT_PATH}/coupons`,
  "nav-orders": `/${CONTEXT_PATH}/orders`,
  "nav-support": `/${CONTEXT_PATH}/support`,
  "nav-cart": `/${CONTEXT_PATH}/cart`,
};

// 初始化導航
async function initializeNavigation() {
  try {
    // 從後端獲取導航路徑
    const response = await fetch(`/${CONTEXT_PATH}/api/navigation`);
    console.log('CONTEXT_PATH:', CONTEXT_PATH);
    console.log(response);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const navigationData = await response.json();

    // 更新頁面映射
    Object.keys(navigationData.links).forEach((key) => {
      if (pageMapping[key]) {
        pageMapping[key] = navigationData.links[key];
      }
    });

    // 設置導航事件監聽器
    setupNavigationListeners();
  } catch (error) {
    console.error("Error fetching navigation data:", error);
    // 如果獲取失敗，使用默認映射
    setupNavigationListeners();
  }
}

// 設置導航監聽器
function setupNavigationListeners() {
  Object.keys(pageMapping).forEach((navId) => {
    const navElement = document.getElementById(navId);
    if (navElement) {
      navElement.href = pageMapping[navId];

      navElement.addEventListener("click", async function (e) {
        e.preventDefault();
        console.log("NavId:", navId);
        console.log("Mapped Path:", pageMapping[navId]);
        try {
          const response = await fetch(pageMapping[navId]);
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }

          const content = await response.text();
          const mainContent = document.getElementById("main-content");
          if (mainContent) {
            mainContent.innerHTML = content;
            // 更新 URL 但不重新載入頁面
            history.pushState({}, "", pageMapping[navId]);
          }
        } catch (error) {
          console.error("Error loading page:", error);
          console.log('Fallback URL:', pageMapping[navId]);
          // 如果載入失敗，回退到傳統導航
          window.location.href = pageMapping[navId];
        }
      });
    }
  });
}

// 處理瀏覽器的後退/前進按鈕
window.addEventListener("popstate", async function () {
  try {
    const response = await fetch(window.location.pathname);
    if (response.ok) {
      const content = await response.text();
      const mainContent = document.getElementById("main-content");
      if (mainContent) {
        mainContent.innerHTML = content;
      }
    }
  } catch (error) {
    console.error("Error handling navigation:", error);
  }
});

// 當 DOM 加載完成時初始化
document.addEventListener("DOMContentLoaded", initializeNavigation);
