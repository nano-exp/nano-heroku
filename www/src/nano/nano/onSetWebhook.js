import { setWebhook } from '../../apis/webhook.js'
import { withLoadingAction } from '../../common/loading.js'

export default async function onSetWebhook(target, token) {
    return await withLoadingAction(target, async () => {
        const resultMap = await setWebhook(token)
        const message = Object.keys(resultMap).map((it) => `${it.padEnd(10, ' ')}${resultMap[it].description}`).join('\n')
        new Dialog({
            title: '操作成功',
            content: `<pre>${message}</pre>`,
            buttons: [{
                value: '关闭'
            }]
        });
    })
}