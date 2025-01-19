// 全局變數
const contextPath = "/TIA104G2-SpringBoot";
const activitySelect = $("#activity-type");
const regionSelect = $("#region");
let currentPage = 1;
let totalPages = 1;
const ITEMS_PER_PAGE = 10;
let currentSpots = [];
let currentDay = 1;
let totalDays = 1;
let currentCollectionId = null;
let currentMemberId = null;

// 在 DOMContentLoaded 之前先檢查會員狀態
async function checkMemberAuth() {
  try {
    const response = await fetch(
      "/TIA104G2-SpringBoot/member/getCurrentMemberId",
      {
        method: "GET",
        credentials: "include",
      }
    );

    if (!response.ok) {
      // 如果未登入或認證失敗，導向登入頁面
      window.location.href = "/TIA104G2-SpringBoot/login";
      return false;
    }

    const data = await response.json();
    if (!data.memberId) {
      window.location.href = "/TIA104G2-SpringBoot/login";
      return false;
    }

    currentMemberId = data.memberId;
    return true;
  } catch (error) {
    console.error("檢查會員狀態失敗:", error);
    window.location.href = "/TIA104G2-SpringBoot/login";
    return false;
  }
}

// 等待 DOM 完全加載
document.addEventListener("DOMContentLoaded", async function () {
  // 先檢查會員狀態
  const isAuthenticated = await checkMemberAuth();
  if (!isAuthenticated) {
    return; // 如果未認證，停止後續初始化
  }

  // 會員已認證，初始化頁面功能
  initializeSummernote();
  initializeDayNavigation();
  initializeSpotDeletion();
  initializeToolbar();
  initializeSpotSelection();
  initializeCoverImage();
  initializePublishFunctions();

  //載入活動類型與地區類型
  loadOptions();
});

// 通用的 API 請求處理函數
async function fetchWithAuth(url, options = {}) {
  try {
    const response = await fetch(url, {
      ...options,
      credentials: "include", // 確保帶上認證信息
      headers: {
        ...options.headers,
        "Content-Type": "application/json",
      },
    });

    if (response.status === 401) {
      alert("會員認證已過期，請重新登入");
      window.location.href = "/TIA104G2-SpringBoot/login";
      return null;
    }

    if (!response.ok) {
      throw new Error("請求失敗");
    }

    return await response.json();
  } catch (error) {
    console.error(`API 請求失敗: ${url}`, error);
    throw error;
  }
}

// 先載入活動類型和地區選項
function loadOptions() {
  console.log("載入選項");

  // 定義載入選項的通用函數
  function loadSelectOptions(
    url,
    selectElement,
    defaultOptionText,
    errorMessage
  ) {
    $.ajax({
      url: url,
      type: "GET",
      dataType: "json",
      success: function (dataList) {
        selectElement.empty();
        selectElement.append(`<option value="">${defaultOptionText}</option>`);
        dataList.forEach(function (item) {
          selectElement.append(`<option value="${item}">${item}</option>`);
        });
      },
      error: function (xhr) {
        console.error(errorMessage, xhr);
        alert(errorMessage); // 替代 `showErrorMessage` 函數
      },
    });
  }

  // 載入活動類型選項
  loadSelectOptions(
    contextPath + "/tripactype/all",
    activitySelect,
    "活動類型",
    "無法取得活動類型清單"
  );

  // 載入地區選項
  loadSelectOptions(
    contextPath + "/triparea/all",
    regionSelect,
    "地區",
    "無法取得地區清單"
  );
}

//載入景點清單
async function loadCollections() {
  try {
    // 先檢查登入狀態
    if (!(await UserManager.checkLoginStatus())) {
      window.location.href = contextPath + "/login";
      return;
    }

    // 獲取用戶ID
    const response = await fetch(`${contextPath}/member/getCurrentMemberId`, {
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error("未登入或session已過期");
    }

    const result = await response.json();
    console.log("getCurrentMemberId response:", result); // 檢查實際返回值
    const memberId = result.memberId; // 從返回的物件中提取 memberId

    // 使用獲取到的會員ID載入該會員的收藏清單
    const collectionsResponse = await fetch(
      `${contextPath}/editor/user/${memberId}`, // 直接使用 memberId
      {
        credentials: "include",
      }
    );

    if (!collectionsResponse.ok) {
      throw new Error("載入收藏清單失敗");
    }
    const collections = await collectionsResponse.json();

    // 更新下拉選單
    const select = document.getElementById("collection-select");
    select.innerHTML = '<option value="">選擇收藏清單</option>';

    collections.forEach((collection) => {
      const option = document.createElement("option");
      option.value = collection.trip_id; // 使用 trip_id 作為值
      option.textContent = collection.article_title; // 使用 article_title 作為顯示文字
      select.appendChild(option);
    });

    // 綁定選單改變事件
    select.addEventListener("change", async (e) => {
      const collectionId = e.target.value;
      console.log("選擇的收藏清單ID:", collectionId); // 檢查ID是否正確獲取
      if (collectionId) {
        currentCollectionId = collectionId;
        await loadCollectionSpots(collectionId, 0);
      } else {
        // 如果沒有選擇收藏清單（值為空），清空景點列表
        const grid = document.querySelector(".saved-spots-grid");
        if (grid) {
          grid.innerHTML = "";
        }
        // 重置相關變數
        currentCollectionId = null;
        currentPage = 1;
        totalPages = 1;
      }
    });
  } catch (error) {
    console.error("載入收藏清單失敗:", error);
    alert("載入收藏清單失敗，請重新登入");
  }
}

// 載入特定收藏清單的景點
async function loadCollectionSpots(collectionId, page = 0) {
  // 添加 page 參數並設默認值
  try {
    if (!collectionId) {
      throw new Error("收藏清單ID未定義");
    }
    console.log("正在載入收藏清單:", collectionId, "頁碼:", page);
    const response = await fetch(
      `${contextPath}/editor/${collectionId}/locations?page=${page}&size=${ITEMS_PER_PAGE}`,
      {
        credentials: "include",
      }
    );
    if (!response.ok) {
      const errorData = await response.json();
      console.error("API錯誤詳情:", errorData);
      throw new Error(`載入景點失敗: ${response.status}`);
    }

    const data = await response.json();
    console.log("從後端接收到的景點資料:", data.content); // 用於除錯

    // 更新全局變量
    currentSpots = data.content;
    totalPages = data.totalPages;
    currentPage = data.currentPage + 1;

    // 如果沒有數據，顯示提示訊息
    if (!data.content || data.content.length === 0) {
      const grid = document.querySelector(".saved-spots-grid");
      grid.innerHTML =
        '<div class="no-spots-message">此收藏清單中還沒有景點</div>';
      return; // 提前返回
    }

    // 渲染景點
    renderSpots();

    // 更新分頁信息
    updatePagination(data.totalPages, currentPage);
  } catch (error) {
    console.error("載入景點失敗:", error);
    alert("載入景點失敗，請稍後再試");
  }
}

// 分頁控制器
function updatePagination(totalPages, currentPage) {
  const pageInfo = document.getElementById("page-info");
  const prevBtn = document.getElementById("prev-page");
  const nextBtn = document.getElementById("next-page");

  pageInfo.textContent = `第 ${currentPage} 頁，共 ${totalPages} 頁`;
  prevBtn.disabled = currentPage === 1;
  nextBtn.disabled = currentPage === totalPages;

  // 更新分頁按鈕事件
  prevBtn.onclick = () => {
    if (currentPage > 1) {
      loadCollectionSpots(currentCollectionId, currentPage - 2); // -2 是因為後端從 0 開始計算
    }
  };

  nextBtn.onclick = () => {
    if (currentPage < totalPages) {
      loadCollectionSpots(currentCollectionId, currentPage); // 不需要 -1 因為 currentPage 已經是下一頁的索引
    }
  };
}

// Summernote 編輯器初始化
function initializeSummernote() {
  $(".summernote").summernote({
    placeholder: "開始撰寫旅遊記事...",
    tabsize: 2,
    height: 500,
    toolbar: [
      ["style", ["style"]],
      ["font", ["bold", "italic", "underline", "clear"]],
      ["fontname", ["fontname"]],
      ["color", ["color"]],
      ["para", ["ul", "ol", "paragraph"]],
      ["table", ["table"]],
      ["insert", ["link", "picture"]],
      ["view", ["fullscreen", "codeview", "help"]],
    ],
  });
}

// 日期導航功能
function initializeDayNavigation() {
  const dayNav = document.querySelector(".day-nav");
  if (!dayNav) return;

  const prevBtn = dayNav.querySelector("button:nth-child(1)");
  const nextBtn = dayNav.querySelector("button:nth-child(3)");
  const addBtn = dayNav.querySelector("button:nth-child(4)");

  // 確保編輯器容器是空的
  const editorsContainer = document.querySelector(".editors-container");
  if (editorsContainer) {
    editorsContainer.innerHTML = "";
  }

  // 初始化第一個編輯器容器
  initializeEditorContainer(1);

  // 移除可能已存在的事件監聽器
  addBtn.replaceWith(addBtn.cloneNode(true));
  const newAddBtn = dayNav.querySelector("button:nth-child(4)");

  // 重新添加事件監聽器
  newAddBtn.addEventListener("click", () => {
    totalDays++;

    // 在 days-container 中添加新的天數按鈕
    const daysContainer = document.querySelector(".days-container");
    const newDayBtn = document.createElement("button");
    newDayBtn.textContent = `第${numberToChineseDay(totalDays)}天`;
    newDayBtn.className = "day-btn";
    newDayBtn.dataset.day = totalDays;
    daysContainer.appendChild(newDayBtn);

    // 創建新的景點列表
    createSpotsListForDay(totalDays);

    // 創建新的編輯器容器
    initializeEditorContainer(totalDays);

    // 切換到新建立的天數
    switchToDay(totalDays);

    // 滾動到新添加的按鈕
    newDayBtn.scrollIntoView({
      behavior: "smooth",
      inline: "end",
      block: "nearest",
    });
  });

  // 點擊天數按鈕
  dayNav.addEventListener("click", (e) => {
    if (e.target.dataset.day) {
      switchToDay(parseInt(e.target.dataset.day));
    }
  });
}

// 為特定天數創建景點列表
function createSpotsListForDay(day) {
  const spotsListsContainer = document.querySelector(".spots-lists-container");
  if (!spotsListsContainer) {
    const container = document.createElement("div");
    container.className = "spots-lists-container";
    const oldSpotsList = document.querySelector(".spots-list");
    oldSpotsList.parentNode.replaceChild(container, oldSpotsList);

    const firstDayList = document.createElement("div");
    firstDayList.className = "spots-list";
    firstDayList.dataset.day = "1";
    firstDayList.innerHTML = oldSpotsList.innerHTML;
    container.appendChild(firstDayList);
  }

  const newSpotsList = document.createElement("div");
  newSpotsList.className = "spots-list";
  newSpotsList.dataset.day = day;
  newSpotsList.innerHTML = `
      <button class="add-spot-btn">+ 新增景點</button>
    `;
  document.querySelector(".spots-lists-container").appendChild(newSpotsList);
}

// 初始化編輯器容器
function initializeEditorContainer(day) {
  const editorsContainer =
    document.querySelector(".editors-container") ||
    document.querySelector(".summernote-container").parentElement;

  // 如果是第一天且容器不存在，創建容器
  if (day === 1 && !document.querySelector(".editors-container")) {
    const container = document.createElement("div");
    container.className = "editors-container";
    editorsContainer.parentNode.replaceChild(container, editorsContainer);
  }

  // 創建新的編輯器包裝器
  const wrapper = document.createElement("div");
  wrapper.className = "editor-wrapper";
  wrapper.dataset.day = day;
  wrapper.innerHTML = `
    <h2 class="editor-title">第${numberToChineseDay(day)}天</h2>
    <div class="summernote-container">
      <div class="summernote" data-day="${day}"></div>
    </div>
  `;

  document.querySelector(".editors-container").appendChild(wrapper);

  // 初始化新的 Summernote 編輯器
  $(`div.summernote[data-day="${day}"]`).summernote({
    placeholder: "開始撰寫旅遊記事...",
    tabsize: 2,
    height: 500,
    toolbar: [
      ["style", ["style"]],
      ["font", ["bold", "italic", "underline", "clear"]],
      ["fontname", ["fontname"]],
      ["color", ["color"]],
      ["para", ["ul", "ol", "paragraph"]],
      ["table", ["table"]],
      ["insert", ["link", "picture"]],
      ["view", ["fullscreen", "codeview", "help"]],
    ],
  });
}

// 切換天數
function switchToDay(day) {
  currentDay = day;

  // 更新按鈕狀態
  document.querySelectorAll(".day-nav button[data-day]").forEach((btn) => {
    btn.classList.toggle("active", parseInt(btn.dataset.day) === day);
  });

  // 更新景點列表顯示
  document.querySelectorAll(".spots-list").forEach((list) => {
    list.style.display = parseInt(list.dataset.day) === day ? "block" : "none";
  });

  // 更新編輯器顯示
  document.querySelectorAll(".editor-wrapper").forEach((wrapper) => {
    wrapper.style.display =
      parseInt(wrapper.dataset.day) === day ? "block" : "none";
  });

  // 修正這部分的邏輯
  const prevBtn = document.querySelector("#prevDay");
  const nextBtn = document.querySelector("#nextDay");

  // 只有在第一天時禁用上一頁按鈕
  if (prevBtn) {
    prevBtn.disabled = day <= 1;
  }

  // 只有在最後一天時禁用下一頁按鈕
  if (nextBtn) {
    nextBtn.disabled = day >= totalDays;
  }
}

// 數字轉中文天數
function numberToChineseDay(num) {
  const chineseNumbers = [
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
  if (num <= 10) return chineseNumbers[num - 1];
  if (num > 10 && num < 20)
    return "十" + (num % 10 === 0 ? "" : chineseNumbers[(num % 10) - 1]);
  return num;
}

// 景點刪除功能
function initializeSpotDeletion() {
  const deleteButtons = document.querySelectorAll(".delete-spot");
  deleteButtons.forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const spotItem = e.target.closest(".spot-item");
      spotItem.remove();
    });
  });
}

// 工具欄功能初始化
function initializeToolbar() {
  const toolbar = document.querySelector(".toolbar");
  const editor = document.querySelector(".editor-content");

  if (toolbar && editor) {
    toolbar.addEventListener("click", (e) => {
      if (e.target.tagName === "BUTTON") {
        const commands = {
          B: "bold",
          I: "italic",
          U: "underline",
          "❶": "formatBlock",
          "❷": "formatBlock",
          "❸": "formatBlock",
        };

        const values = {
          "❶": "h1",
          "❷": "h2",
          "❸": "h3",
        };

        const command = commands[e.target.textContent];
        const value = values[e.target.textContent] || null;

        document.execCommand(command, false, value);
      }
    });
  }
}

// 景點選擇功能初始化
function initializeSpotSelection() {
  const spotSelectorModal = document.querySelector(".spot-selector-modal");
  const closeBtn = document.querySelector(".spot-selector-close");
  const prevPageBtn = document.getElementById("prev-page");
  const nextPageBtn = document.getElementById("next-page");
  const addSelectedBtn = document.getElementById("add-selected-spots");

  // 使用事件代理來處理新增景點按鈕的點擊
  document.addEventListener("click", spotsListContainerClickHandler);

  // 移除原有的事件監聽器
  if (addSelectedBtn) {
    addSelectedBtn.removeEventListener("click", addSelectedSpotsHandler);
    addSelectedBtn.addEventListener("click", addSelectedSpotsHandler);
  }

  // 關閉按鈕事件
  if (closeBtn) {
    closeBtn.addEventListener("click", () => {
      spotSelectorModal.style.display = "none";
    });
  }

  // 點擊彈窗外部關閉
  if (spotSelectorModal) {
    spotSelectorModal.addEventListener("click", (e) => {
      if (e.target === spotSelectorModal) {
        spotSelectorModal.style.display = "none";
      }
    });
  }

  // 分頁控制
  if (prevPageBtn) {
    prevPageBtn.addEventListener("click", () => {
      if (currentPage > 1) {
        currentPage--;
        renderSpots();
      }
    });
  }

  if (nextPageBtn) {
    nextPageBtn.addEventListener("click", () => {
      if (currentPage < totalPages) {
        currentPage++;
        renderSpots();
      }
    });
  }
}

// 將事件處理函數分離出來
function spotsListContainerClickHandler(e) {
  if (e.target.matches(".add-spot-btn")) {
    const spotSelectorModal = document.querySelector(".spot-selector-modal");
    spotSelectorModal.style.display = "block";
    loadCollections();
    // 如果有選擇收藏清單，則載入第一頁的景點
    if (currentCollectionId) {
      console.log("正在載入已選擇的收藏清單:", currentCollectionId);
      loadCollectionSpots(currentCollectionId, 0);
    } else {
      // 如果還沒有選擇收藏清單，載入收藏清單選項
      loadCollections();
    }
  }
}

// 將添加選中景點的處理函數分離出來
function addSelectedSpotsHandler() {
  const selectedSpots = document.querySelectorAll(".saved-spot-item.selected");
  const activeSpotsList = document.querySelector(
    `.spots-list[data-day="${currentDay}"]`
  );

  if (activeSpotsList) {
    selectedSpots.forEach((selectedSpot) => {
      const spotId = selectedSpot.dataset.spotId;
      // 找到對應的完整景點資料
      const spot = currentSpots.find((s) => s.id.toString() === spotId);

      if (spot) {
        console.log("正在添加景點，完整資料:", spot);

        // 創建新的景點元素並加入到行程中
        const newSpot = createSpotElement({
          id: spot.id, // 確保傳遞 ID
          locationId: spot.id, // 為了向後兼容也設置 locationId
          name: spot.name,
          address: spot.address,
          googlePlaceId: spot.googlePlaceId,
          latitude: spot.latitude,
          longitude: spot.longitude,
          rating: spot.rating,
          reviewCount: spot.reviewCount,
        });

        // 在新增景點之前先移除選中狀態
        selectedSpot.classList.remove("selected");
        activeSpotsList.appendChild(newSpot);
      }
    });
  }
  // 關閉彈窗
  document.querySelector(".spot-selector-modal").style.display = "none";
}

// 修改保存景點元素的渲染樣式
function createSavedSpotElement(spot) {
  console.log("createSpotElement 接收到的資料:", spot);

  const newSpot = document.createElement("div");
  newSpot.className = "spot-item";

  // 確保保存完整的景點資訊到 dataset
  const spotInfo = {
    locationId: spot.id || spot.locationId, // 優先使用 id，向後兼容 locationId
    googlePlaceId: spot.googlePlaceId,
    name: spot.name,
    address: spot.address,
    latitude: spot.latitude,
    longitude: spot.longitude,
    rating: spot.rating || 0,
    reviewCount: spot.reviewCount || 0,
    dayIndex: currentDay,
  };

  // 檢查並記錄 ID 信息
  console.log("正在創建景點元素，ID資訊:", {
    原始id: spot.id,
    原始locationId: spot.locationId,
    最終使用的ID: spotInfo.locationId,
  });

  // 保存到 dataset
  newSpot.dataset.spotInfo = JSON.stringify(spotInfo);
  console.log("已保存到 dataset 的資料:", JSON.parse(newSpot.dataset.spotInfo));

  newSpot.innerHTML = `
        <button class="delete-spot" style="color: #9e9e9e;">×</button>
        <i class="fas fa-map-marker-alt location-icon" style="color: #9e9e9e;"></i>
        <div class="spot-info">
            <h3 style="color: #333; margin: 0 0 4px 0;">${spotInfo.name}</h3>
            <p style="color: #666; margin: 4px 0 0 0; font-size: 12px;">
                📍 ${spotInfo.address || ""}
            </p>
            <div class="spot-rating" style="color: #666; margin: 4px 0;">
                <span style="color: #ffd700;">⭐️</span> ${spotInfo.rating} (${
    spotInfo.reviewCount
  })
            </div>
            <div class="spot-time">
                <div class="time-input-group">
                    <label>開始時間：</label>
                    <input type="datetime-local" class="time-input start-time">
                </div>
                <div class="time-input-group">
                    <label>結束時間：</label>
                    <input type="datetime-local" class="time-input end-time">
                </div>
            </div>
        </div>
    `;

  // 綁定事件處理
  bindSpotEvents(newSpot);

  return newSpot;
}

//事件綁定輔助函數
function bindSpotEvents(spotElement) {
  // 綁定刪除事件
  spotElement.querySelector(".delete-spot").addEventListener("click", () => {
      spotElement.remove();
  });

  // 綁定時間驗證
  const startTimeInput = spotElement.querySelector(".start-time");
  const endTimeInput = spotElement.querySelector(".end-time");

  startTimeInput.addEventListener("change", () => {
      endTimeInput.min = startTimeInput.value;
      if (endTimeInput.value && endTimeInput.value < startTimeInput.value) {
          endTimeInput.value = "";
      }
  });

  endTimeInput.addEventListener("change", () => {
      if (startTimeInput.value && endTimeInput.value < startTimeInput.value) {
          alert("結束時間必須晚於開始時間");
          endTimeInput.value = "";
      }
  });
}

// 渲染景點列表
function renderSpots() {
  const grid = document.querySelector(".saved-spots-grid");
  grid.innerHTML = "";

  currentSpots.forEach((spot) => {
    const spotElement = document.createElement("div");
    spotElement.className = "saved-spot-item";
    spotElement.dataset.spotId = spot.id;

    spotElement.innerHTML = `
      <div class="spot-content">
                <h4>${spot.name}</h4>
                <div class="spot-info">
                    <div class="rating-line">⭐️ ${spot.rating.toFixed(1)} (${
      spot.reviewCount
    })</div>
                    <div class="address-line">📍 ${spot.address}</div>
                </div>
            </div>
    `;

    spotElement.addEventListener("click", () => {
      spotElement.classList.toggle("selected");
    });

    grid.appendChild(spotElement);
  });

  // 取得分頁元素
  const pageInfoElement = document.getElementById("page-info");
  const prevButton = document.getElementById("prev-page");
  const nextButton = document.getElementById("next-page");

  // 更新分頁資訊
  if (pageInfoElement) {
    pageInfoElement.textContent = `第 ${currentPage} 頁，共 ${totalPages} 頁`;
  }

  // 更新按鈕狀態
  if (prevButton) {
    prevButton.disabled = currentPage === 1;
  }
  if (nextButton) {
    nextButton.disabled = currentPage === totalPages;
  }
}

// 創建景點元素
function createSpotElement(spot) {
  const newSpot = document.createElement("div");
  newSpot.className = "spot-item";

  // 確保保存完整的景點資訊到 dataset
  const spotInfo = {
    locationId: spot.id || spot.locationId, // 確保兼容兩種來源的資料
    googlePlaceId: spot.googlePlaceId,
    name: spot.name,
    address: spot.address,
    latitude: spot.latitude,
    longitude: spot.longitude,
    rating: spot.rating || 0,
    reviewCount: spot.reviewCount || 0,
    dayIndex: currentDay,
  };

  // 保存到 dataset
  newSpot.dataset.spotInfo = JSON.stringify(spotInfo);

  newSpot.innerHTML = `
      <button class="delete-spot" style="color: #9e9e9e;">×</button>
      <i class="fas fa-map-marker-alt location-icon" style="color: #9e9e9e;"></i>
      <div class="spot-info">
        <h3 style="color: #333; margin: 0 0 4px 0;">${spot.name}</h3>
        <p style="color: #666; margin: 0;">
          <span style="color: #ffd700;">⭐️</span> ${spot.rating} (${spot.reviewCount})
        </p>
        <div class="spot-time">
          <div class="time-input-group">
            <label>開始時間：</label>
            <input type="datetime-local" class="time-input start-time">
          </div>
          <div class="time-input-group">
            <label>結束時間：</label>
            <input type="datetime-local" class="time-input end-time">
          </div>
        </div>
      </div>
    `;

  // 綁定刪除事件
  newSpot.querySelector(".delete-spot").addEventListener("click", () => {
    newSpot.remove();
  });

  // 綁定時間驗證
  const startTimeInput = newSpot.querySelector(".start-time");
  const endTimeInput = newSpot.querySelector(".end-time");

  // 監聽開始時間變更
  startTimeInput.addEventListener("change", () => {
    endTimeInput.min = startTimeInput.value;
    // 如果結束時間早於開始時間，清空結束時間
    if (endTimeInput.value && endTimeInput.value < startTimeInput.value) {
      endTimeInput.value = "";
    }
  });

  // 監聽結束時間變更
  endTimeInput.addEventListener("change", () => {
    if (startTimeInput.value && endTimeInput.value < startTimeInput.value) {
      alert("結束時間必須晚於開始時間");
      endTimeInput.value = "";
    }
  });

  return newSpot;
}

// 添加選中的景點到行程
function addSelectedSpotsToTrip() {
  const selectedSpots = document.querySelectorAll(".saved-spot-item.selected");
  const spotsList = document.querySelector(".spots-list");

  selectedSpots.forEach((selectedSpot) => {
    const spotId = selectedSpot.dataset.spotId;
    const spot = currentSpots.find((s) => s.id.toString() === spotId);
    if (spot) {
      const newSpot = createSpotElement(spot);
      spotsList.appendChild(newSpot);
    }
  });

  document.querySelector(".spot-selector-modal").style.display = "none";
}

//圖片上傳功能
function initializeCoverImage() {
  const coverInput = document.getElementById("coverImageInput");
  const preview = document.getElementById("coverPreview");
  const removeBtn = document.getElementById("removeCoverImage");

  coverInput.addEventListener("change", function (e) {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        preview.src = e.target.result;
        preview.style.objectFit = "cover";
        removeBtn.style.display = "block";
      };
      reader.readAsDataURL(file);
    }
  });

  removeBtn.addEventListener("click", function () {
    preview.src = "https://via.placeholder.com/800x400?text=Upload+Cover+Image";
    coverInput.value = "";
    this.style.display = "none";
  });
}

// 添加提醒函數
function validateTripData(tripData) {
  // 檢查基本欄位
  if (!tripData.articleTitle?.trim()) {
      throw new Error("請輸入行程標題");
  }
  if (!tripData.abstract?.trim()) {
      throw new Error("請輸入行程摘要");
  }
  if (!tripData.activityType) {
      throw new Error("請選擇活動類型");
  }
  if (!tripData.region) {
      throw new Error("請選擇地區");
  }
  if (!tripData.subTrips || tripData.subTrips.length === 0) {
      throw new Error("至少需要一天的行程");
  }

  // 檢查每天的行程內容
  tripData.subTrips.forEach((subTrip, index) => {
      // 檢查每天是否有景點
      if (!subTrip.spots || subTrip.spots.length === 0) {
          throw new Error(`第 ${index + 1} 天未加入任何景點`);
      }
      
      // 檢查每個景點的必要資訊
      subTrip.spots.forEach((spot, spotIndex) => {
          if (!spot.locationId) {
              throw new Error(`第 ${index + 1} 天的第 ${spotIndex + 1} 個景點缺少位置ID`);
          }
          
          // 如果有設置開始時間，結束時間也必須設置
          if (spot.timeStart && !spot.timeEnd) {
              throw new Error(`第 ${index + 1} 天的第 ${spotIndex + 1} 個景點缺少結束時間`);
          }
          
          // 如果有設置時間，確保開始時間早於結束時間
          if (spot.timeStart && spot.timeEnd) {
              if (new Date(spot.timeStart) >= new Date(spot.timeEnd)) {
                  throw new Error(`第 ${index + 1} 天的第 ${spotIndex + 1} 個景點的開始時間必須早於結束時間`);
              }
          }
      });

      // 檢查天數索引是否正確
      if (subTrip.index !== index + 1) {
          throw new Error(`行程天數索引不正確：第 ${index + 1} 天`);
      }
  });

  // 確保 memberId 存在
  if (!tripData.memberId) {
      throw new Error("找不到會員ID，請重新登入");
  }

  return true; // 如果所有驗證都通過，返回 true
}

// =============== 發布相關功能 ===============

// 發布相關功能初始化
function initializePublishFunctions() {
  // 綁定立即發布按鈕
  const publishBtn = document.querySelector(".action-btn.publish");
  if (publishBtn) {
    publishBtn.addEventListener("click", () => {
      const isPublic =
        document.querySelector("select:last-of-type").value === "公開";
      handlePublish(isPublic ? 3 : 4);
    });
  }

  // 綁定儲存草稿按鈕
  const draftBtn = document.querySelector(".action-btn.draft");
  if (draftBtn) {
    draftBtn.addEventListener("click", () => {
      handlePublish(2);
    });
  }
}

// 處理發布邏輯
async function handlePublish(status) {
  try {
    // 先收集數據
    const tripData = await collectPublishData(status);
    if (!tripData) {
      console.log("數據收集失敗或用戶取消");
      return;
    }

    // 驗證數據
    validateTripData(tripData);

    // 確認是否要送出
    if (!confirm("確定要發布嗎？")) {
      return;
    }

    // 在發送前檢查並打印收集到的數據
    console.log("準備發送到後端的數據:", {
      基本信息: {
        標題: tripData.articleTitle,
        摘要: tripData.abstract,
        狀態: tripData.status,
        活動類型: tripData.activityType,
        地區: tripData.region,
      },
      景點信息: tripData.subTrips.map((trip) => ({
        天數: trip.index,
        景點數: trip.spots.length,
        景點列表: trip.spots.map((spot) => ({
          locationId: spot.locationId,
          順序: spot.index,
          開始時間: spot.timeStart,
          結束時間: spot.timeEnd,
        })),
      })),
    });

    // 發送到後端
    const response = await fetch(`${contextPath}/editor/publish`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(tripData),
    });

    if (!response.ok) {
      const errorData = await response.json();
      console.error("伺服器錯誤詳情:", errorData);
      throw new Error(`發布失敗: ${errorData.error || "未知錯誤"}`);
    }

    const result = await response.json();
    console.log("發布成功，返回結果:", result);

    // 導向單一行程頁面
    window.location.href = `${contextPath}/trip/${result.tripId}`;
  } catch (error) {
    console.error("發布過程中發生錯誤:", error);
    console.error("錯誤堆疊:", error.stack);
    alert(`發布失敗: ${error.message}`);
  }
}

// 收集發布資料
async function collectPublishData(status) {
  // 基本資料結構
  const tripData = {
    memberId: currentMemberId,
    articleTitle: document.querySelector(".search-input").value,
    abstract: document.querySelector(".editor-content").value,
    status: status,
    createTime: new Date().toISOString(),
    collections: 0, // 預設值
    overallScore: 0, // 預設值
    overallScoredPeople: 0, // 預設值
    visitorNumber: 0, // 預設值
    likes: 0, // 預設值
    locationNumber: 0, // 將根據實際景點數量計算
    subTrips: [],
    activityType: document.getElementById("activity-type").value,
    region: document.getElementById("region").value,
    coverPhoto: null,
  };

  try {
    // 處理封面照片
    const coverPreview = document.getElementById("coverPreview");
    if (
      coverPreview &&
      coverPreview.src &&
      !coverPreview.src.includes("placeholder.com")
    ) {
      tripData.coverPhoto = coverPreview.src.split(",")[1];
    }

    // 收集每天的資料
    let totalLocations = 0;
    for (let day = 1; day <= totalDays; day++) {
      console.log(`正在處理第 ${day} 天的數據`);

      // 獲取該天的編輯器內容
      const editorContent = $(`.summernote[data-day="${day}"]`).summernote(
        "code"
      );

      // 獲取該天的景點列表
      const spotsList = document.querySelector(
        `.spots-list[data-day="${day}"]`
      );
      if (!spotsList) {
        console.warn(`找不到第 ${day} 天的景點列表`);
        continue;
      }

      // 獲取所有景點元素
      const spots = Array.from(spotsList.querySelectorAll(".spot-item")).map(
        (spotElement, index) => {
          const spotInfo = JSON.parse(spotElement.dataset.spotInfo || "{}");
          console.log(`正在處理第 ${day} 天第 ${index + 1} 個景點:`, spotInfo);

          if (!spotInfo.locationId) {
            throw new Error(
              `第 ${day} 天第 ${
                index + 1
              } 個景點缺少位置ID，請確保所有景點都已正確保存`
            );
          }

          return {
            locationId: spotInfo.locationId,
            index: index + 1,
            timeStart: spotElement.querySelector(".start-time")?.value || "",
            timeEnd: spotElement.querySelector(".end-time")?.value || "",
          };
        }
      );

      // 累計總景點數
      totalLocations += spots.length;

      // 構建子行程數據
      const subTrip = {
        index: day, // 天數索引
        content: editorContent, // 當天的內容
        spots: spots, // 當天的景點列表
      };

      tripData.subTrips.push(subTrip);
      console.log(`第 ${day} 天的數據已處理完成:`, subTrip);
    }

    // 更新總景點數
    tripData.locationNumber = totalLocations;

    // 驗證必要欄位
    if (!tripData.articleTitle) throw new Error("請輸入標題");
    if (!tripData.abstract) throw new Error("請輸入摘要");
    if (!tripData.activityType) throw new Error("請選擇活動類型");
    if (!tripData.region) throw new Error("請選擇地區");
    if (tripData.subTrips.length === 0) throw new Error("至少需要一天的行程");

    console.log("完整行程數據:", tripData);
    return tripData;
  } catch (error) {
    console.error("收集資料時發生錯誤:", error);
    alert("收集資料失敗: " + error.message);
    return null;
  }
}
