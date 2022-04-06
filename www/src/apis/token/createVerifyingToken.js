import { withNanoApi } from '../../config.js'

export default async function createVerifyingToken(username) {
    const response = await fetch(withNanoApi('/api/token/createVerifyingToken'), {
        method: 'POST',
        body: new URLSearchParams({ username })
    })
    const result = await response.json()
    if (result.error) {
        throw new Error(result.error)
    }
    return result.payload
}