package com.machimpyo.dot.repository.impl

import android.util.Log
import com.google.gson.GsonBuilder
import com.machimpyo.dot.data.model.ColorList
import com.machimpyo.dot.data.model.ContentInfo
import com.machimpyo.dot.data.model.Letter
import com.machimpyo.dot.data.model.LetterBoxItem
import com.machimpyo.dot.data.model.LetterName
import com.machimpyo.dot.data.model.request.ExitDateUpdate
import com.machimpyo.dot.data.model.response.AbstractLetter
import com.machimpyo.dot.network.service.MainService
import com.machimpyo.dot.repository.MainRepository
import com.machimpyo.dot.utils.getMockContentInfo
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepositoryImpl @Inject constructor(
    private val mainService: MainService
): MainRepository {

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

            val response = mainService.getLetterBox()

            if(!response.isSuccessful) {
                throw Exception("마이 페이지 데이터 불러오기 실패")
            }

            val letterBoxItems = response.body() ?: throw Exception("마이 페이지 데이터 불러오기 실패")

//            val letterBoxItems = letterBox.letterBoxItems

            Result.success(letterBoxItems)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getLetterNames(): Result<List<LetterName>> {
        return try {

            val response = mainService.getLetterNames()

            if(!response.isSuccessful) {
                throw Exception("박스 아이템 리스트 가져오기 실패")
            }

            val letterNames = response.body() ?: throw Exception("박스 아이템 리스트 가져오기 실패")

//            val letterNames: MutableList<LetterName> = mutableListOf()

//            repeat(10) {
//                letterNames.add(
//                    LetterName.getMock()
//                )
//            }

            Result.success(letterNames)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getAbstractLetters(): Result<List<AbstractLetter>> {
        return try {
            val response = mainService.getAbstractLetters()
            if(!response.isSuccessful) {
                throw Exception("받은 대표 편지들 가져오기 실패")
            }

            val abstractLetters = response.body() ?: throw Exception("받은 대표 편지들 가져오기 실패")
            Result.success(abstractLetters)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }


    /*
    나경
     */

    override suspend fun getLetterDesign(): Result<ColorList> {
        try {

            val response = mainService.getLetterDesign()

            if (response.isSuccessful) {
                response.body()?.let { list ->
                    return Result.success(ColorList(list))
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }

        return Result.failure(NoSuchElementException("색상 리스트를 찾을 수 없음"))
    }

    override suspend fun createLetter(letter: Letter): Result<Long> {
        return try {
            val response = mainService.createLetter(letter)
            Result.success(response.body()!!.getValue("letter_uid"))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getLetter(uid: Long): Result<Letter> {
        return try {
            Result.success(mainService.getLetter(uid).body()!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}