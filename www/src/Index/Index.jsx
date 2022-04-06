import React, { useMemo, useState } from 'react'
import { css } from '@emotion/css'
import Link from '../components/Link.jsx'
import usePing from '../hooks/usePing.js'
import { filterPages } from '../common/pages.js'
import 'github-markdown-css'

const IndexClassName = css`
  box-sizing: border-box;
  min-height: 100vh;
  padding: 1rem;

  h2 {
    margin: .5rem 0;
    padding: 0;
  }

  .table-container {
    margin-top: .5rem;
  }

  table {
    display: block;
    width: 100%;
  }
`

export default function Index(props) {
    const [searchValue, setSearchValue] = useState('')

    usePing()

    const filteredPages = useMemo(() => filterPages(searchValue), [searchValue])

    function onSubmit(ev) {
        ev.preventDefault()
        filteredPages.length && open(filteredPages[0].url)
    }

    return (
        <div className={IndexClassName}>
            <div>
                <h2><Link href="/">nano</Link></h2>
                <form className="ui-input ui-input-search" onSubmit={onSubmit}>
                    <input id="search-input" type="search" value={searchValue} placeholder="搜索" autoFocus
                           onChange={(ev) => setSearchValue(ev.target.value)}/>
                    <label htmlFor="search-input" className="ui-icon-search"/>
                </form>
            </div>
            <div className="table-container markdown-body">
                <table>
                    <thead>
                    <tr>
                        <th>Pages</th>
                    </tr>
                    </thead>
                    <tbody>
                    {filteredPages.map((it) => {
                        return (
                            <tr key={it.name}>
                                <td>
                                    <Link href={it.url} target="_blank">{it.name}</Link>
                                </td>
                            </tr>
                        )
                    })}
                    {!filteredPages.length && (
                        <tr>
                            <td style={{ textAlign: 'center', }}>空</td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
        </div>
    )
}