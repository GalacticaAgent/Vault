import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/auth/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/auth/Register.vue')
    },
    {
      path: '/test-api',
      name: 'test-api',
      component: () => import('../views/common/ApiTest.vue')
    },
    {
      path: '/student',
      name: 'student',
      component: () => import('../views/student/StudentLayout.vue'),
      redirect: '/student/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'student-dashboard',
          component: () => import('../views/student/Dashboard.vue')
        },
        {
          path: 'chat',
          name: 'student-chat',
          component: () => import('../views/student/Chat.vue')
        },
        {
          path: 'questionnaire',
          name: 'student-questionnaire',
          component: () => import('../views/student/Questionnaire.vue')
        },
        {
          path: 'profile',
          name: 'student-profile',
          component: () => import('../views/student/Profile.vue')
        }
      ]
    },
    {
      path: '/teacher',
      name: 'teacher',
      component: () => import('../views/teacher/TeacherLayout.vue'),
      redirect: '/teacher/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'teacher-dashboard',
          component: () => import('../views/teacher/Dashboard.vue')
        },
        {
          path: 'chat',
          name: 'teacher-chat',
          component: () => import('../views/teacher/Chat.vue')
        },
        {
          path: 'questionnaire-generator',
          name: 'teacher-questionnaire-generator',
          component: () => import('../views/teacher/QuestionnaireGenerator.vue')
        },
        {
          path: 'material-upload',
          name: 'teacher-material-upload',
          component: () => import('../views/teacher/MaterialUpload.vue')
        },
        {
          path: 'skill-management',
          name: 'teacher-skill-management',
          component: () => import('../views/teacher/SkillManagement.vue')
        },
        {
          path: 'student-analysis',
          name: 'teacher-student-analysis',
          component: () => import('../views/teacher/StudentAnalysis.vue')
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../views/common/NotFound.vue')
    }
  ]
})

// 导入用户store
import { useUserStore } from '@/store/modules/user'

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 白名单路由（不需要登录）
  const whiteList = ['/', '/login', '/register', '/test-api']
  
  if (userStore.isLoggedIn) {
    // 已登录
    if (to.path === '/login' || to.path === '/register') {
      // 如果已登录，访问登录或注册页面，重定向到对应角色首页
      const role = userStore.userInfo?.role
      if (role === 'STUDENT') {
        next('/student')
      } else if (role === 'TEACHER') {
        next('/teacher')
      } else {
        next('/')
      }
    } else {
      next()
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      next()
    } else {
      // 重定向到登录页
      next('/login')
    }
  }
})

export default router

