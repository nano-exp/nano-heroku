import React, { useEffect, useState } from 'react'
import { css } from '@emotion/css'
import useGlobalLoading from '../../hooks/useGlobalLoading.js'
import { withObjectPath } from '../../config.js'
import clipboardCopy from '../../utils/clipboardCopy.js'
import Link from '../../components/Link.jsx'
import 'github-markdown-css'
import getObjectList from '../../apis/object/getObjectList.js'
import useToken from '../../hooks/useToken.js'
import { formatBytes } from '../../utils/bytes.js'

const ObjectClassName = css`
  box-sizing: border-box;
  min-height: 100vh;
  padding: 1rem;

  h2 {
    margin: .5rem 0 1rem;
    padding: 0;
  }
`

export default function Hls() {
    const { loading, setLoading } = useGlobalLoading(true)
    const { token } = useToken()

    const [objectList, setObjectList] = useState([])

    useEffect(() => {
        getObjectList(token)
            .then((list) => {
                console.log(list)
                setObjectList(list.map((it) => {
                    return {
                        url: withObjectPath(it.key),
                        formattedSize: formatBytes(it.size),
                        ...it,
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
        LightTip.success('复制成功')
    }

    if (loading) {
        return (<div className={ObjectClassName}/>)
    }

    return (
        <div className={ObjectClassName}>
            <h2><Link href="/">nano</Link></h2>
            <div className="markdown-body">
                <h3>Object</h3>
                <table>
                    <thead>
                    <tr>
                        <th>文件名</th>
                        <th>类型</th>
                        <th>大小</th>
                    </tr>
                    </thead>
                    <tbody>
                    {objectList.map((it) => {
                        return (
                            <tr key={it.key}>
                                <td>
                                    <Link href={it.url} target="_blank">{it.name}</Link>
                                </td>
                                <td>{it.type}</td>
                                <td>{it.formattedSize}</td>
                            </tr>
                        )
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    )
}