import { withNanoApi } from '../config.js'

export async function applyQuery(token, sql) {
    const response = await fetch(withNanoApi('/api/postgres/query'), {
        method: 'POST',
        headers: {
            'X-Token': token,
        },
        body: sql,
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}