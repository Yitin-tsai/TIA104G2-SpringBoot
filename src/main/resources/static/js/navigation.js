document.addEventListener("DOMContentLoaded", function() {
    const baseUrl = '/TIA104G2-SpringBoot'; 
    
    fetch(`${baseUrl}/api/navigation-paths`)
        .then(response => {
            if (!response.ok) {
                throw new Error('網路回應不正常');
            }
            return response.json();
        })
        .then(data => {
            // 為每個導航項添加點擊事件處理
            const navItems = {
                'nav-go': data.goPath,
                'nav-product': data.productPath,
                'nav-profile': data.profilePath,
                'nav-guide': data.guidePath,
                'nav-basic-info': data.basicInfoPath,
                'nav-coupons': data.couponsPath,
                'nav-orders': data.ordersPath,
                'nav-support': data.supportPath,
                'nav-cart': data.cartPath
            };

            // 使用迴圈統一處理所有導航項目
            Object.entries(navItems).forEach(([id, path]) => {
                const element = document.getElementById(id);
                if (element) {
                    element.addEventListener('click', function(e) {
                        e.preventDefault();
                        window.location.href = `${baseUrl}${path}`;
                    });
                }
            });
        })
        .catch(error => {
            console.error('獲取導航路徑時發生錯誤:', error);
        });
});