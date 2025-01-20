window.UserManager = {
    config: {
        contextPath: "/TIA104G2-SpringBoot"
    },
    
    state: {
        isLoggedIn: false,
        memberId: null,
        memberInfo: null
    },

    // 初始化用戶狀態
    init: async function() {
        await this.checkLoginStatus();
    },

    // 檢查登入狀態
    checkLoginStatus: async function() {
        try {
            const response = await fetch(`${this.config.contextPath}/member/getCurrentMemberId`, {
                credentials: "include"
            });
            this.state.isLoggedIn = response.ok;
            return this.state.isLoggedIn;
        } catch (error) {
            console.error("檢查登入狀態失敗:", error);
            this.state.isLoggedIn = false;
            return false;
        }
    },

    
    // 載入會員詳細資訊
    loadMemberInfo: async function() {
        if (!this.state.memberId) return;
        
        try {
            const response = await fetch(`${this.config.contextPath}/member/${this.state.memberId}`);
            if (response.ok) {
                this.state.memberInfo = await response.json();
            }
        } catch (error) {
            console.error('載入會員資訊失敗:', error);
        }
    },

    // 取得會員ID
    getMemberId: function() {
        return this.state.memberId;
    },

    // 取得會員資訊
    getMemberInfo: function() {
        return this.state.memberInfo;
    },

    // 檢查是否登入
    isLoggedIn: function() {
        return this.state.isLoggedIn;
    }
};

// 導出實例
window.UserManager = UserManager;