export const NANO_API = 'https://api.jianzhao.com/'

export function withNanoApi(endpoint) {
    return new URL(endpoint, NANO_API).toString()
}

export function withObjectPath(key) {
    return new URL('/api/object/-/' + key, NANO_API).toString()
}
