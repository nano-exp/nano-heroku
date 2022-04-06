import { withNanoApi } from '../config.js'

export default async function ping() {
    const response = await fetch(withNanoApi('/api/nano/ping'))
    return await response.text()
}