// ArticleAuthManager.js
class ArticleAuthManager {
    constructor() {
        this.baseUrl = "/TIA104G2-SpringBoot";
        this.isLoggedIn = false;
        this.memberId = null;
        
        // 綁定按鈕事件
        this.followBtn = document.getElementById('followBtn');
        this.collectBtn = document.getElementById('collectBtn');
        this.reportBtn = document.getElementById('reportBtn');
        this.commentForm = document.querySelector('.comment-form');
        
        this.init();
    }

    async init() {
        // 檢查登入狀態
        if (window.UserManager) {
            this.isLoggedIn = await window.UserManager.checkLoginStatus();
            if (this.isLoggedIn) {
                this.memberId = window.UserManager.getMemberId();
            }
        }
        
        // 根據登入狀態更新UI
        this.updateUIBasedOnAuth();
        // 綁定事件
        this.bindEvents();
    }

    updateUIBasedOnAuth() {
        // 控制評論表單的顯示
        if (this.commentForm) {
            this.commentForm.style.display = this.isLoggedIn ? 'block' : 'none';
        }

        // 如果未登入，為按鈕添加提示類別
        const authButtons = [this.followBtn, this.collectBtn, this.reportBtn];
        authButtons.forEach(btn => {
            if (btn) {
                btn.classList.toggle('need-auth', !this.isLoggedIn);
            }
        });
    }

    bindEvents() {
        // 綁定需要登入的功能按鈕
        if (this.followBtn) {
            this.followBtn.addEventListener('click', (e) => this.handleAuthRequired(e, this.handleFollow.bind(this)));
        }
        
        if (this.collectBtn) {
            this.collectBtn.addEventListener('click', (e) => this.handleAuthRequired(e, this.handleCollect.bind(this)));
        }
        
        if (this.reportBtn) {
            this.reportBtn.addEventListener('click', (e) => this.handleAuthRequired(e, this.handleReport.bind(this)));
        }
    }

    handleAuthRequired(event, callback) {
        event.preventDefault();
        
        if (!this.isLoggedIn) {
            // 顯示提示訊息
            this.showLoginAlert();
            // 延遲跳轉到登入頁面
            setTimeout(() => {
                window.location.href = `${this.baseUrl}/login`;
            }, 2000);
            return;
        }
        
        // 如果已登入，執行回調函數
        callback();
    }

    showLoginAlert() {
        // 創建提示元素
        const alert = document.createElement('div');
        alert.className = 'login-alert';
        alert.textContent = '請先登入後再使用此功能！正在為您導向登入頁面...';
        
        // 插入到頁面中
        document.querySelector('.article-container').insertBefore(
            alert, 
            document.querySelector('.article-actions')
        );
        
        // 2秒後移除提示
        setTimeout(() => alert.remove(), 2000);
    }

    async handleFollow() {
        try {
            const authorId = document.getElementById('author-name').dataset.authorId;
            const response = await fetch(`${this.baseUrl}/api/user/follow/${authorId}`, {
                method: 'POST',
                credentials: 'include'
            });
            
            if (response.ok) {
                this.followBtn.classList.toggle('active');
                // 更新按鈕文字
                const span = this.followBtn.querySelector('span');
                span.textContent = this.followBtn.classList.contains('active') ? '已追蹤' : '追蹤作者';
            }
        } catch (error) {
            console.error('關注作者失敗:', error);
        }
    }

    async handleCollect() {
        try {
            const articleId = window.location.pathname.split('/').pop();
            const response = await fetch(`${this.baseUrl}/api/articles/${articleId}/collect`, {
                method: 'POST',
                credentials: 'include'
            });
            
            if (response.ok) {
                this.collectBtn.classList.toggle('active');
                // 更新按鈕文字和圖標
                const span = this.collectBtn.querySelector('span');
                const icon = this.collectBtn.querySelector('i');
                if (this.collectBtn.classList.contains('active')) {
                    span.textContent = '已收藏';
                    icon.className = 'fas fa-heart';
                } else {
                    span.textContent = '收藏文章';
                    icon.className = 'far fa-heart';
                }
            }
        } catch (error) {
            console.error('收藏文章失敗:', error);
        }
    }

    async handleReport() {
        try {
            const articleId = window.location.pathname.split('/').pop();
            const response = await fetch(`${this.baseUrl}/api/articles/${articleId}/report`, {
                method: 'POST',
                credentials: 'include'
            });
            
            if (response.ok) {
                alert('檢舉已提交，我們會盡快處理');
            }
        } catch (error) {
            console.error('檢舉文章失敗:', error);
        }
    }
}

// 導出實例
window.ArticleAuthManager = ArticleAuthManager;