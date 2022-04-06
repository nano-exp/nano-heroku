import { withNanoApi } from '../../config.js'

export default async function startTask(token, task) {
    const response = await fetch(withNanoApi('/api/task/start'), {
        method: 'POST',
        headers: {
            'X-Token': token,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(task),
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}