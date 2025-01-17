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

  //載入活動類型與地區類型
  loadOptions();
  //載入景點收藏清單
  loadCollections();
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
  document.removeEventListener("click", spotsListContainerClickHandler); // 先移除可能存在的事件監聽
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
    loadSpots();
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
      const spot = currentSpots.find((s) => s.id.toString() === spotId);
      if (spot) {
        const newSpot = createSpotElement(spot);
        // 在新增景點之前先移除選中狀態
        selectedSpot.classList.remove("selected");
        activeSpotsList.appendChild(newSpot);
      }
    });
  }

  document.querySelector(".spot-selector-modal").style.display = "none";
}

// 載入收藏清單
async function loadCollections() {
  try {
    const response = await fetch("/api/collections");
    const collections = await response.json();

    const collectionSelect = document.getElementById("collection-select");
    collectionSelect.innerHTML = '<option value="">選擇收藏清單</option>';

    collections.forEach((collection) => {
      const option = document.createElement("option");
      option.value = collection.id;
      option.textContent = collection.name;
      collectionSelect.appendChild(option);
    });
  } catch (error) {
    console.error("Error loading collections:", error);
  }
}

// 載入特定收藏清單的景點
async function loadSpots(collectionId) {
  // 模擬 API 回應的假資料
  currentSpots = [
    {
      id: 1,
      name: "台北 101",
      rating: 4.5,
      reviewCount: 2468,
      address: "台北市信義區信義路五段7號",
      type: "景點",
      openTime: "上午 9:00 - 下午 10:00",
      description: "台北標誌性建築，擁有觀景台及購物中心",
    },
    {
      id: 2,
      name: "國立故宮博物院",
      rating: 4.8,
      reviewCount: 3254,
      address: "台北市士林區至善路二段221號",
      type: "博物館",
      openTime: "上午 8:30 - 下午 6:30",
      description: "世界級博物館，收藏眾多珍貴文物",
    },
    {
      id: 3,
      name: "龍山寺",
      rating: 4.6,
      reviewCount: 1857,
      address: "台北市萬華區廣州街211號",
      type: "寺廟",
      openTime: "上午 6:00 - 下午 10:00",
      description: "台北市最古老的寺廟之一，建築精美",
    },
  ];

  totalPages = Math.ceil(currentSpots.length / ITEMS_PER_PAGE);
  renderSpots();
}

// 修改保存景點元素的渲染樣式
function createSavedSpotElement(spot) {
  const element = document.createElement("div");
  element.className = "saved-spot-item";
  element.dataset.spotId = spot.id;
  element.innerHTML = `
        <div style="margin-bottom: 8px;">
          <h4 style="margin: 0 0 4px 0; color: #333;">${spot.name}</h4>
          <div style="color: #666; font-size: 14px;">
            <div style="margin-bottom: 2px;">
              <span style="color: #ffd700;">⭐️</span> ${spot.rating} (${spot.reviewCount} 評價)
            </div>
            <div style="margin-bottom: 2px;">📍 ${spot.address}</div>
          </div>
        </div>
      `;

  element.addEventListener("click", () => {
    element.classList.toggle("selected");
  });

  return element;
}

// 渲染景點到網格
function renderSpots() {
  const grid = document.querySelector(".saved-spots-grid");
  const pageInfo = document.getElementById("page-info");
  const prevBtn = document.getElementById("prev-page");
  const nextBtn = document.getElementById("next-page");

  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
  const pageSpots = currentSpots.slice(startIndex, startIndex + ITEMS_PER_PAGE);

  grid.innerHTML = "";
  pageSpots.forEach((spot) => {
    const spotElement = createSavedSpotElement(spot);
    grid.appendChild(spotElement);
  });

  pageInfo.textContent = `第 ${currentPage} 頁，共 ${totalPages} 頁`;
  prevBtn.disabled = currentPage === 1;
  nextBtn.disabled = currentPage === totalPages;
}

// 創建景點元素
function createSpotElement(spot) {
  const newSpot = document.createElement("div");
  newSpot.className = "spot-item";
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

  // 設置預設最小時間為當前時間
  const now = new Date();
  startTimeInput.min = now.toISOString().slice(0, 16);
  endTimeInput.min = now.toISOString().slice(0, 16);

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
