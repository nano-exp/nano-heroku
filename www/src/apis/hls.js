import { withNanoApi } from '../config.js'

export async function getHlsList() {
    const response = await fetch(withNanoApi('/api/hls/list'))
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}