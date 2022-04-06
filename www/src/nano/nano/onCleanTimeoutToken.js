import { withLoadingAction } from '../../common/loading.js'
import startTask from '../../apis/task/startTask.js'

export default async function onCleanTimeoutToken(target, token) {
    return await withLoadingAction(target, async () => {
        await startTask(token, {
            name: 'pruneVerifyingTimeoutTokenTask',
            description: 'Clean Timeout Token',
            options: '{}',
        })
        window?.LightTip.success('操作成功')
    })
}