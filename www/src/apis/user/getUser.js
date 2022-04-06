import { withNanoApi } from '../../config.js'

export default async function getUser(token) {
    const response = await fetch(withNanoApi('/api/user/user'), {
        headers: {
            'X-Token': token
        },
    })
    const result = await response.json()
    if (result.error && response.status !== 403) {
        throw new Error(result.error)
    }
    return result.payload
}