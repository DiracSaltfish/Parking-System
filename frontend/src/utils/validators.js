export const plateNumberPattern =
  /^[\u4e00-\u9fa5][A-Z][A-Z0-9]{5,6}$/

export const phonePattern = /^1\d{10}$/

export const usernamePattern = /^[a-zA-Z0-9_]{4,16}$/

export const passwordPattern =
  /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!@#$%^&*._-]{6,20}$/

export const recordIdPattern = /^R\d{4,}$/

export const spaceCodePattern = /^[A-Z]-\d{3}$/

export function normalizePlateNumber(value) {
  return (value || '').trim().toUpperCase()
}

export function requiredRule(message) {
  return {
    required: true,
    message,
    trigger: ['blur', 'change']
  }
}

export function patternRule(pattern, message) {
  return {
    pattern,
    message,
    trigger: ['blur', 'change']
  }
}

export function positiveNumberRule(message) {
  return {
    validator: (_, value, callback) => {
      if (value === null || value === undefined || value === '') {
        callback(new Error(message))
        return
      }
      if (Number(value) < 0) {
        callback(new Error(message))
        return
      }
      callback()
    },
    trigger: ['blur', 'change']
  }
}
