import React, { useEffect, useRef, useState } from 'react'
import { addLoadingClassName, removeLoadingClassName } from '../common/loading.js'
import { sleep } from '../utils/schedule.js'
import createVerifyingToken from '../apis/token/createVerifyingToken.js'
import getTokenVerification from '../apis/token/getTokenVerification.js'

async function pollingTokenVerification(token) {
    // 等5秒
    await sleep(5000)
    while (true) {
        const payload = await getTokenVerification(token)
        const { verifying } = payload
        switch (verifying) {
            case 'done': {
                return
            }
            case 'pending': {
                // noop
                break
            }
            case 'timeout': {
                throw new Error('timeout')
            }
        }
        await sleep(2000)
    }
}

export default function LoginForm(props) {
    const usernameInputRef = useRef()
    const [verificationCode, setVerificationCode] = useState('')
    const formRef = useRef()

    async function onLogin(ev) {
        ev.preventDefault()
        const submitButtonRef = ev.target.querySelector('button[type="submit"]')
        try {
            addLoadingClassName(submitButtonRef)
            usernameInputRef.current.disabled = true
            const username = usernameInputRef.current.value
            if (!username) {
                return
            }
            const { token, verificationCode } = await createVerifyingToken(username)
            setVerificationCode(verificationCode)
            await pollingTokenVerification(token)
            props.onLogin?.(token)
        } catch (err) {
            if (err.message === 'timeout') {
                LightTip.error('验证码超时，请重试')
                setVerificationCode('')
            }
        } finally {
            removeLoadingClassName(submitButtonRef)
            usernameInputRef.current.disabled = false
        }
    }

    useEffect(() => {
        new Validate(formRef.current, {
            validate: {
                id: 'telegram-username-input',
                report: {
                    valueMissing: '请输入用户名',
                },
            },
        })
    })

    return (
        <>
            <form ref={formRef} onSubmit={onLogin}>
                <input id="telegram-username-input" ref={usernameInputRef} type="text" required is="ui-input"
                       placeholder="请输入电报用户名"/>
                <button style={{ marginLeft: '.5rem' }} is="ui-button" type="submit">登录</button>
            </form>
            {verificationCode && (
                <div style={{ marginTop: '.5rem' }}>请输入验证码：{verificationCode}</div>
            )}
        </>
    )
}