document.addEventListener("DOMContentLoaded", function () {
    const breadcrumbNav = document.getElementById("breadcrumb-nav");

    // 導航欄nav的href載入器
    document.getElementById("nav-go").href = "./go.html";
    document.getElementById("nav-product").href = "./product.html";
    document.getElementById("nav-profile").href = "./profile.html";
    document.getElementById("nav-guide").href = "./guide.html";
    document.getElementById("nav-basic-info").href = "./basic_info.html";
    document.getElementById("nav-coupons").href = "./coupons.html";
    document.getElementById("nav-orders").href = "./orders.html";
    document.getElementById("nav-support").href = "./support.html";
    document.getElementById("nav-cart").href = "./cart.html";
    document.getElementById("ChillTrip_logo").href = "./index.html";
    // 定義麵包屑導航的項目
    const breadcrumbItems = [
        { name: "首頁", link: "index.html" },
        { name: "Go！行程", link: "go.html" },
        { name: "行程詳情", link: "go_single_editor.html" },
      ];

      // 動態生成麵包屑導航的 HTML 結構
      breadcrumbItems.forEach((item, index) => {
        const li = document.createElement("li");
        li.className = "breadcrumb-item";
        if (index === breadcrumbItems.length - 1) {
          li.classList.add("active");
          li.setAttribute("aria-current", "page");
          li.textContent = item.name;
        } else {
          const a = document.createElement("a");
          a.href = item.link;
          a.textContent = item.name;
          li.appendChild(a);
        }
        breadcrumbNav.appendChild(li);
      });
});
