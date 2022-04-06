import { withNanoApi } from '../../config.js'

export default async function getTokenVerification(token) {
    const response = await fetch(withNanoApi('/api/token/verification'), {
        headers: { 'X-Token': token }
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}