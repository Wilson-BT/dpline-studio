import {uuid} from 'vue-uuid';

/**
 * 生成ID相关的工具
 * @method UUID 生成UUID
 */
export function useIdGenerator() {
    return {
        UUID: (): string => {
            // 采用v4版本的uuid(基于随机数)
            let UUIDStr: string = uuid.v4().replaceAll("-", "");
            return UUIDStr;
        }
    }
}