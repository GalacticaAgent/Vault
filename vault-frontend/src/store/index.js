/**
 * Store 入口文件
 * 
 * Pinia 会自动注册 modules/ 目录下的所有 store 模块
 * 这里可以导出常用的 store 以便使用
 */

// 导出各个模块的 store（可选，因为可以直接从 modules 导入）
export { useUserStore } from './modules/user'
export { useChatStore } from './modules/chat'
export { useDashboardStore } from './modules/dashboard'
export { useQuestionnaireStore } from './modules/questionnaire'

