import { withNanoApi } from '../../config.js'

export default async function putObject(token, fileList) {
    const formData = new FormData()
    fileList.forEach(it => formData.append('file', it))
    const response = await fetch(withNanoApi('/api/object/put'), {
        method: 'POST',
        body: formData,
        headers: { 'X-Token': token },
    })
    const result = await response.json()
    if (result.error && response.status !== 403) {
        throw new Error(result.error)
    }
    return result.payload
}