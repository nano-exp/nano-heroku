import React, { useEffect, useState } from 'react'
import { css } from '@emotion/css'
import { getHlsList } from '../../apis/hls.js'
import useGlobalLoading from '../../hooks/useGlobalLoading.js'
import { withNanoApi } from '../../config.js'
import clipboardCopy from '../../utils/clipboardCopy.js'
import Link from '../../components/Link.jsx'
import 'github-markdown-css'

const HlsClassName = css`
  box-sizing: border-box;
  min-height: 100vh;
  padding: 1rem;

  .text-center {
    text-align: center;
  }

  h2 {
    margin: .5rem 0 1rem;
    padding: 0;
  }
`

export default function Hls() {
    const { loading, setLoading } = useGlobalLoading(true)

    const [hlsList, setHlsList] = useState([])

    useEffect(() => {
        getHlsList()
            .then((hlsList) => {
                setHlsList(hlsList.map((it) => {
                    return {
                        name: it.name,
                        url: withNanoApi(it.pathname)
                    }
                }))
                setLoading(false)
            })
            .catch((err) => {
                LightTip.error(err.message)
            })
    }, [])

    async function onClickCopy(it) {
        await clipboardCopy(it.url)
        LightTip.success('复制成功：' + it.name)
    }

    if (loading) {
        return (<div className={HlsClassName}/>)
    }

    return (
        <div className={HlsClassName}>
            <h2><Link href="/">nano</Link></h2>
            <div className="markdown-body">
                <h3>HLS</h3>
                <table>
                    <thead>
                    <tr>
                        <th>频道</th>
                        <th colSpan="2">URL</th>
                    </tr>
                    </thead>
                    <tbody>
                    {hlsList.map((it) => {
                        return (
                            <tr key={it.name}>
                                <td>{it.name}</td>
                                <td>{it.url}</td>
                                <td className="text-center">
                                    <button onClick={() => onClickCopy(it)} is="ui-button">复制</button>
                                </td>
                            </tr>
                        )
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    )
}