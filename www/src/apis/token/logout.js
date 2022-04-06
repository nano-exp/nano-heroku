import { withNanoApi } from '../../config.js'

export default async function logout(token) {
    const response = await fetch(withNanoApi('/api/token/deleteSelf'), {
        method: 'POST',
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}