import { useEffect } from 'react'
import ping from '../apis/ping.js'

export default function usePing() {
    useEffect(() => {
        ping().catch((err) => {
            console.warn(err.message)
        })
    }, [])
}