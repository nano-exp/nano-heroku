export function addLoadingClassName(element) {
    if (!element) {
        return
    }
    element.classList.add('loading')
}

export function removeLoadingClassName(element) {
    if (!element) {
        return
    }
    element.classList.remove('loading')
}

export async function withLoadingAction(target, action) {
    try {
        addLoadingClassName(target)
        return await action()
    } catch (err) {
        window?.LightTip.error(err.message)
    } finally {
        removeLoadingClassName(target)
    }
}