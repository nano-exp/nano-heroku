import { useEffect, useState } from 'react'

const TOKEN = 'token'

export default function useToken() {
    const [tokenState, setTokenState] = useState(() => localStorage.getItem(TOKEN))

    useEffect(() => {
        function onStorageTokenChange(ev) {
            if (ev.key === TOKEN && ev.storageArea === localStorage) {
                setTokenState(ev.newValue)
            }
        }

        window.addEventListener('storage', onStorageTokenChange)
        return () => window.removeEventListener('storage', onStorageTokenChange)
    }, [])

    return {
        token: tokenState,
        setToken(newToken) {
            setTokenState(newToken)
            localStorage.setItem(TOKEN, newToken)
        },
    }
}