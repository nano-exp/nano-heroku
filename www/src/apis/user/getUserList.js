import { withNanoApi } from '../../config.js'

export default async function getUserList(token) {
    const response = await fetch(withNanoApi('/api/user/list'), {
        method: 'GET',
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}