import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      name: "Login",
      component: () => import("@/views/common/Login.vue"),
      meta: { requiresAuth: false }
    },
    // HomeView 改为静态路由还是动态路由？
    // 通常根路径 / 可以保留，或者作为一个重定向
    // 这里保留一个基础布局，但具体的子页面由动态路由决定
    {
      path: "/",
      name: "HomeView",
      component: () => import("@/layout/admin/index.vue"),
      meta: { requiresAuth: true },
      redirect: '/home', // 默认跳转到 dashboard
      children: [
        {
          path: 'home',
          name: 'Home',
          component: () => import('@/views/common/Home.vue'),
          meta: { title: '首页', icon: 'House', affix: true } // affix: true 用于固定在 TagsView
        }
      ]
    }
  ],
});

// 全局路由守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  
  // 检查路由是否存在
  // 如果路由不存在 (to.matched.length === 0)，可能是动态路由还没加载
  // 或者是真的 404，但 404 路由现在是动态加载的，所以这里先尝试加载用户信息
  if (to.matched.length === 0) {
    if (authStore.isAuthenticated) {
      if (authStore.menus.length === 0) {
        try {
          const success = await authStore.fetchUserInfo();
          if (success) {
            // 动态路由加载成功，重新跳转
            // 注意：这里需要检查是否是 dashboard，如果是则直接放行，避免无限循环
            if (to.path === '/dashboard') {
               next({ ...to, replace: true });
            } else {
               next({ ...to, replace: true });
            }
          } else {
            authStore.logout();
          }
        } catch (error) {
          authStore.logout();
        }
      } else {
        // 已加载过菜单但仍未匹配到路由，说明是真的 404
        next('/404'); 
      }
    } else {
      // 未登录访问不存在的页面 -> 跳转登录
      next({
        path: "/login",
        query: { redirect: to.fullPath },
      });
    }
    return;
  }

  // 1. 判断是否需要认证
  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      // 未登录 -> 跳转登录页
      next({
        path: "/login",
        query: { redirect: to.fullPath },
      });
      return;
    }

    // 2. 已登录，判断是否已加载菜单/用户信息
    if (authStore.menus.length === 0) {
      try {
        const success = await authStore.fetchUserInfo();
        if (success) {
           next({ ...to, replace: true });
        } else {
          authStore.logout();
        }
      } catch (error) {
        authStore.logout();
      }
    } else {
      next();
    }
  } else {
    // 不需要认证的页面
    if (to.path === "/login" && authStore.isAuthenticated) {
      // 已登录用户访问登录页 -> 跳转首页
      next("/");
    } else {
      next();
    }
  }
});

export default router;
