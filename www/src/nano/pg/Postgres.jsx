import React, { useState } from 'react'
import styled from '@emotion/styled'
import SQLEditor from '../../components/SQLEditor.jsx'
import useToken from '../../hooks/useToken.js'
import { applyQuery } from '../../apis/postgres.js'
import 'github-markdown-css'
import Link from '../../components/Link.jsx'

const PostgresContainer = styled.div`
  box-sizing: border-box;
  padding: 1rem;
  min-height: 100vh;

  h3 {
    margin: .5rem 0 1rem;
    padding: 0;
  }

  .wrapper {
    border: 1px solid #ccc;
  }

  .head {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .data-list {
    margin-top: 1rem;
  }

  .query-button {
    min-height: 30px;
    min-width: 72px;
  }

  h2 {
    margin: .5rem 0 1rem;
    padding: 0;
  }
`

function getValue(it) {
    if (typeof it !== 'object' || it === null) {
        return it
    }
    if (it?.type === 'jsonb') {
        return it.value
    }
    return it
}

export default function Postgres(props) {

    const { token } = useToken()

    const [inputText, setInputText] = useState()
    const [dataList, setDataList] = useState([])

    function onInputChange(_update, view) {
        setInputText(view.state.sliceDoc())
    }

    async function onClickQuery(ev) {
        try {
            ev.target.disabled = true
            ev.target.textContent = '查询中...'
            const dataList = await applyQuery(token, inputText)
            if (dataList.length) {
                setDataList(dataList)
            } else {
                setDataList([{ info: 'empty' }])
            }
        } catch (err) {
            setDataList([{ error: err.message }])
        } finally {
            ev.target.disabled = false
            ev.target.textContent = '查询'
        }
    }

    const headerList = dataList.length ? Object.keys(dataList[0]) : []

    return (
        <PostgresContainer>
            <h2><Link href="/">nano</Link></h2>
            <div className="head">
                <h3>Postgres</h3>
                <button className="query-button" onClick={onClickQuery}>查询</button>
            </div>
            <div className="wrapper">
                <SQLEditor onChange={onInputChange}/>
            </div>
            <div className="data-list markdown-body">
                <table>
                    <thead>
                    <tr>
                        {headerList.map((name) => {
                            return (<th key={name}>{name}</th>)
                        })}
                    </tr>
                    </thead>
                    <tbody>
                    {dataList.map((data, index) => {
                        return (
                            <tr key={index}>
                                {headerList.map((name) => {
                                    return (<td key={name}>{getValue(data[name])}</td>)
                                })}
                            </tr>
                        )
                    })}
                    </tbody>
                </table>
            </div>
        </PostgresContainer>
    )
}