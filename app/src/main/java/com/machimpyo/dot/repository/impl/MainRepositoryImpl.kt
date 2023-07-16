package com.machimpyo.dot.repository.impl

import com.machimpyo.dot.network.service.MainService
import com.machimpyo.dot.repository.MainRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepositoryImpl @Inject constructor(
    private val mainService: MainService
): MainRepository {

    /*
    예시 코드
    override suspend fun getInterviewScore(hostUUID: String): Result<InterviewScore> {
        //테스트 코드 - 초반 개발용

        //실제 서버 연결용
        return try {

            val response = mainService.getRankList(hostUUID)

            if(!response.isSuccessful) {
                throw Exception("최근 면접 기록 가져오기 오류")
            }

            val result = response.body() ?: throw Exception("최근 면접 기록 가져오기 오류")

            Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
     */


    /*
    민석
     */

    /*
    나경
     */
}