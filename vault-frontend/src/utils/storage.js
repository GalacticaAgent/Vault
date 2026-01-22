/**
 * 本地存储工具函数
 */

/**
 * 保存数据（自动 JSON 序列化）
 */
export function setItem(key, value) {
  try {
    const serializedValue = JSON.stringify(value)
    localStorage.setItem(key, serializedValue)
  } catch (error) {
    console.error('Error saving to localStorage:', error)
  }
}

/**
 * 获取数据（自动 JSON 反序列化）
 */
export function getItem(key, defaultValue = null) {
  try {
    const item = localStorage.getItem(key)
    return item ? JSON.parse(item) : defaultValue
  } catch (error) {
    console.error('Error reading from localStorage:', error)
    return defaultValue
  }
}

/**
 * 删除数据
 */
export function removeItem(key) {
  localStorage.removeItem(key)
}

/**
 * 清空存储
 */
export function clear() {
  localStorage.clear()
}

