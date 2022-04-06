import { withNanoApi } from '../../config.js'

export default async function deleteToken(token, tokenIdList) {
    const searchParams = new URLSearchParams()
    tokenIdList.forEach((id) => searchParams.append('id', id))
    const response = await fetch(withNanoApi('/api/token/delete'), {
        method: 'POST',
        headers: { 'X-Token': token },
        body: searchParams,
    })
    const result = await response.json()
    if (result.error && response.status !== 403) {
        throw new Error(result.error)
    }
    return result.payload
}