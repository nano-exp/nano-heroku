import React from 'react'
import styled from '@emotion/styled'

const A = styled.a`
  text-decoration: none;

  :hover {
    text-decoration: underline;
  }
`

export default function Link(props) {
    return (<A {...props}>{props.children}</A>)
}