import React, { Component, createRef } from 'react'
import styled from '@emotion/styled'
import { EditorState, basicSetup } from "@codemirror/basic-setup"
import { EditorView, ViewPlugin, keymap } from '@codemirror/view'
import { defaultKeymap, indentWithTab } from '@codemirror/commands'
import { sql } from '@codemirror/lang-sql'

const Container = styled.div`
  .cm-editor {
    height: 150px;

    &.cm-focused {
      outline: none;
    }

    & > .cm-scroller > .cm-content {
      white-space: pre-wrap;
    }
  }
`

export default class SQLEditor extends Component {

    constructor(props, context) {
        super(props, context)
        this.containerRef = createRef()
    }

    componentDidMount() {
        const extensions = [
            basicSetup,
            keymap.of([...defaultKeymap, indentWithTab]),
            sql(),
        ]

        if (typeof this.props.onChange === 'function') {
            extensions.push(ViewPlugin.define((view) => {
                return {
                    update: (_update) => {
                        this.props.onChange(_update, view)
                    },
                }
            }))
        }

        new EditorView({
            state: EditorState.create({ extensions, }),
            parent: this.containerRef.current,
        })
    }

    render() {
        return (
            <Container ref={this.containerRef}/>
        )
    }
}
