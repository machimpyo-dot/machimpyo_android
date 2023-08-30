package com.machimpyo.dot.repository.impl

import android.util.Log
import com.machimpyo.dot.data.model.ContentInfo
import com.machimpyo.dot.data.model.LetterBox
import com.machimpyo.dot.data.model.LetterBoxItem
import com.machimpyo.dot.data.model.LetterName
import com.machimpyo.dot.data.model.request.ExitDateUpdate
import com.machimpyo.dot.data.store.AuthDataStore
import com.machimpyo.dot.network.service.MainService
import com.machimpyo.dot.repository.MainRepository
import com.machimpyo.dot.utils.getMockContentInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepositoryImpl @Inject constructor(
    private val mainService: MainService,
    private val dataStore: AuthDataStore
): MainRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.getIdTokenFlow().collectLatest {
                Log.e("TAG","메인 레포지토리에서 아이디 토큰 감지 !!! : $it")
                val response = mainService.getUserInfo()

                if(!response.isSuccessful) {
                    return@collectLatest
                }

                val userInfo = response.body() ?: return@collectLatest

                dataStore.updateUserInfo(userInfo)
            }

        }
    }

    /*
    민석
     */
    override suspend fun updateUserNickname(nickname: String) {
        try {
            val data = mapOf("nickname" to nickname)
            mainService.updateUserNickname(data)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "updateUserNickName(): $e")
        }
    }

    override suspend fun updateUserCompany(company: String) {
        try {
            val data = mapOf("company_name" to company)
            mainService.updateUserCompany(data)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "updateUserCompany(): $e")
        }
    }

    override suspend fun updateUserExitDate(exitDate: LocalDate?, isExitDateDetermined: Boolean) {
        try {
            val data = ExitDateUpdate(date = exitDate, valid = isExitDateDetermined)
            mainService.updateUserExitDate(data)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "updateUserExitDate(): $e")
        }
    }

    override suspend fun getContentInfo(contentUid: Int): Result<ContentInfo> {
        return try {
            Result.success(getMockContentInfo(contentUid = contentUid))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }





    override suspend fun getLetterBox(): Result<List<LetterBoxItem>> {
        return try {
            Result.success(LetterBox.getMock().letterBoxItems)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getLetterNames(): Result<List<LetterName>> {
        return try {
            val letterNames: MutableList<LetterName> = mutableListOf()

            repeat(10) {
                letterNames.add(
                    LetterName.getMock()
                )
            }

            Result.success(letterNames)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }


    /*
    나경
     */

}