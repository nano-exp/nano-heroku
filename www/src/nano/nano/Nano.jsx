import React, { useEffect, useState } from 'react'
import { css } from '@emotion/css'
import useToken from '../../hooks/useToken.js'
import { addLoadingClassName, removeLoadingClassName } from '../../common/loading.js'
import useGlobalLoading from '../../hooks/useGlobalLoading.js'
import LoginForm from '../LoginForm.jsx'
import logout from '../../apis/token/logout.js'
import getUser from '../../apis/user/getUser.js'
import Link from '../../components/Link.jsx'
import onSetWebhook from './onSetWebhook.js'
import onCleanTimeoutToken from './onCleanTimeoutToken.js'

const NanoClassName = css`
  box-sizing: border-box;
  min-height: 100vh;
  padding: 1rem;

  h2 {
    margin: .5rem 0 1rem;
    padding: 0;
  }

  button {
    margin-left: .5rem;
  }
`

export default function Nano(props) {
    const { token, setToken } = useToken()
    const { loading, setLoading } = useGlobalLoading(true)
    const [user, setUser] = useState()

    async function load() {
        try {
            setLoading(true)
            if (token) {
                setUser(await getUser(token))
            } else if (user) {
                setUser(null)
            }
            //
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        const _ = load()
    }, [token])

    async function onLogout({ target }) {
        try {
            addLoadingClassName(target)
            await logout(token)
            setToken('')
        } finally {
            removeLoadingClassName(target)
        }
    }

    function onLogin(token) {
        setToken(token)
    }

    if (loading) {
        return (<div className={NanoClassName}/>)
    }

    return (
        <div className={NanoClassName}>
            <h2><Link href="/">nano</Link></h2>
            {user ? (
                <>
                    <span className="name">{user['firstname']}</span>
                    <button is="ui-button" type="danger" onClick={onLogout}>登出</button>
                    <button is="ui-button" onClick={(ev) => onSetWebhook(ev.target, token)}>
                        设置Webhook
                    </button>
                    <button is="ui-button" onClick={(ev) => onCleanTimeoutToken(ev.target, token)}>
                        清除过期Token
                    </button>
                    <hr/>
                    <div>
                        <Link href="/src/nano/hls/">HLS</Link>
                        &nbsp;&nbsp;
                        <Link href="/src/nano/object/">Object</Link>
                        &nbsp;&nbsp;
                        <Link href="/src/nano/pg/">Postgres</Link>
                    </div>
                </>
            ) : (
                <>
                    <LoginForm onLogin={onLogin}/>
                </>
            )}
        </div>
    )
}