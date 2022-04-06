export default function render(template = '', scope = {}) {
    return template.replace(/(\${(\w+)})/g, (...args) => scope[args[2]] ?? args[0])
}