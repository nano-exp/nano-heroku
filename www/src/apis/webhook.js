import { withNanoApi } from '../config.js'

export async function setWebhook(token) {
    const response = await fetch(withNanoApi('/api/telegram/setWebhook'), {
        method: 'POST',
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}