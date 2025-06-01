// src/api/home.ts
import api from './request';
import { ElMessage } from 'element-plus';

// 后端实际返回的完整的响应格式
// 它包含了 status, message 以及所有业务数据
interface BackendFullResponse {
    status: string;
    message: string;
    username: string;
    userType: string; // 后端返回的是字符串类型，例如 "0", "1", "2"
    userId: string;
    finishedLectures: number;
    finishedExercises: number;
    gradeLevel: string;
    answerNum: number; // 后端返回的是 answerNum
    todayTasks: string[];
    notifications: string[];
}

// 定义前端期望的 HomeData 接口 (转换后的数据)
// 注意：answerRate 的类型，如果前端最终显示的是百分比字符串，这里也可以定义为 string
export interface HomeData {
    username: string;
    userType: 0 | 1 | 2; // 前端期望的数字类型，包含 0, 1, 2
    userId: string;
    finishedLectures: number;
    finishedExercises: number;
    gradeLevel: string;
    answerRate: number; // 保持为 number，在组件中进行格式化为百分比
    todayTasks: string[];
    notifications: string[];
}

export const HomeService = {
    /**
     * 获取用户首页数据
     * @param {string} userId - 用户的ID
     * @param {0 | 1 | 2} userType - 用户的类型 (0: Admin, 1: 学生, 2: 教师) - 这个参数是前端传递的数字类型
     * @returns {Promise<HomeData>} 首页数据对象 (前端期望的转换后数据)
     */
    async getHomeData(userId: string, userType: 0 | 1 | 2): Promise<HomeData> {
        try {
            // api.get 的泛型参数现在直接是 BackendFullResponse，因为它直接对应后端返回的整个对象
            const response = await api.get<BackendFullResponse>('/home/student', {
                params: {
                    userId: userId,
                    userType: userType // 直接发送数字类型的 userType
                }
            });

            // 检查后端返回的 status 字段
            // 后端返回的是 "success" 字符串
            if (response.data.status !== "success") {
                // 如果后端返回了错误信息，使用后端的信息，否则使用通用错误信息
                throw new Error(response.data.message || '获取首页数据失败');
            }

            // rawData 现在就是 response.data 本身，因为业务数据在顶层
            const rawData = response.data;

            // 确保 rawData 存在且包含必要的字段
            // 虽然后端返回的JSON结构保证了这些字段的存在，但出于健壮性可以做简单检查
            if (!rawData.username || rawData.userType === undefined || !rawData.userId) {
                throw new Error('后端返回的数据格式不正确或缺少必要字段');
            }

            // 对后端返回的原始数据进行转换，使其符合前端 HomeData 接口的期望
            const transformedData: HomeData = {
                username: rawData.username,
                // 核心转换：将后端返回的 string 类型 userType ("0", "1", "2") 转换为 number (0, 1, 2)
                userType: parseInt(rawData.userType) as (0 | 1 | 2),
                userId: rawData.userId,
                finishedLectures: rawData.finishedLectures,
                finishedExercises: rawData.finishedExercises,
                gradeLevel: rawData.gradeLevel,
                answerRate: rawData.answerNum, // 映射 answerNum 到 answerRate
                todayTasks: rawData.todayTasks || [], // 确保是数组，以防后端返回null或undefined
                notifications: rawData.notifications || [], // 确保是数组，以防后端返回null或undefined
            };

            return transformedData;
        } catch (error: any) {
            console.error('获取首页数据失败:', error);
            // 考虑更细致的错误处理，例如如果 error.response 存在
            if (error.response && error.response.data && error.response.data.message) {
                ElMessage.error(error.response.data.message); // 显示后端返回的错误信息
            } else if (error.message) {
                ElMessage.error(error.message); // 显示转换过程中抛出的错误信息 (例如上述 throw new Error)
            }
            else {
                ElMessage.error('获取首页数据失败，请检查网络或联系管理员！');
            }
            throw error; // 重新抛出错误以便调用方可以捕获
        }
    },
};