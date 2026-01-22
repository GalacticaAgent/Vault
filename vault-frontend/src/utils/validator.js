/**
 * 表单验证工具函数
 */

/**
 * 验证邮箱
 */
export function validateEmail(email) {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

/**
 * 验证手机号
 */
export function validatePhone(phone) {
  const re = /^1[3-9]\d{9}$/
  return re.test(phone)
}

/**
 * 验证密码强度
 */
export function validatePassword(password) {
  // 至少 8 位，包含字母和数字
  const re = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/
  return re.test(password)
}

/**
 * 验证学号
 */
export function validateStudentId(studentId) {
  // 学号通常是数字或字母数字组合
  const re = /^[A-Za-z0-9]{6,20}$/
  return re.test(studentId)
}

/**
 * 验证 URL
 */
export function validateUrl(url) {
  try {
    new URL(url)
    return true
  } catch {
    return false
  }
}

