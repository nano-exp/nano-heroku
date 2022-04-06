export function readAsDataURL(blob) {
    const reader = new FileReader()
    return new Promise((resolve, reject) => {

        reader.addEventListener('load', () => {
            resolve(reader.result)
        })

        reader.addEventListener('error', () => {
            reject(reader.error)
        })

        reader.readAsDataURL(blob)
    })
}


export function readAsText(blob) {
    const reader = new FileReader()
    return new Promise((resolve, reject) => {

        reader.addEventListener('load', () => {
            resolve(reader.result)
        })

        reader.addEventListener('error', () => {
            reject(reader.error)
        })

        reader.readAsText(blob)
    })
}

export function readAsBase64(blob) {
    const reader = new FileReader()
    return new Promise((resolve, reject) => {

        reader.addEventListener('load', () => {
            const buffer = reader.result
            const base64d = btoa(new Uint8Array(buffer)
                .reduce((data, byte) => data + String.fromCharCode(byte), ''))
            resolve(base64d)
        })

        reader.addEventListener('error', () => {
            reject(reader.error)
        })

        reader.readAsArrayBuffer(blob)
    })
}