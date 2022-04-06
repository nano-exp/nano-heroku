import { withNanoApi } from '../../config.js'

export default async function getChatList(token) {
    const response = await fetch(withNanoApi('/api/telegram/chat/list'), {
        method: 'GET',
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}