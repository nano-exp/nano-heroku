const pages = [
    { name: 'nano', url: '/src/nano/', },
    { name: '5566', url: '//5566.net/', },
    { name: 'JS Pen', url: '//alchemy-works.github.io/js-pen/', },
    { name: 'V2EX', url: '//v2ex.com/', },
    { name: 'GitHub', url: '//github.com/', },
    { name: '我来', tag: 'wolai', url: '//www.wolai.com/', },
    { name: 'CodeSandbox', url: '//codesandbox.io/', },
]

export function filterPages(search = '') {
    function includeIgnoreCase(text) {
        return text?.toLowerCase().includes(search.toLowerCase())
    }

    return pages.filter((it) => {
        return includeIgnoreCase(it.name) || includeIgnoreCase(it.tag)
    })
}