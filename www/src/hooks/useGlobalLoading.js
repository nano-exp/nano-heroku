import { useState } from 'react'

export default function useGlobalLoading(loading = false) {
    const [loadingState, setLoadingState] = useState(() => {
        document.loading = loading
        return loading
    })

    return {
        loading: loadingState,
        setLoading(loading) {
            document.loading = loading
            setLoadingState(loading)
        },
    }
}