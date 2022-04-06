import { withNanoApi } from '../../config.js'

export default async function dropObject(token, keyList) {
    const response = await fetch(withNanoApi('/api/object/drop'), {
        method: 'POST',
        headers: {
            'X-Token': token,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(keyList),
    })
    const result = await response.json()
    if (result.error && response.status !== 403) {
        throw new Error(result.error)
    }
    return result.payload
}