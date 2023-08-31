package com.machimpyo.dot.utils

import com.machimpyo.dot.R
import com.machimpyo.dot.data.model.ContentInfo
import com.machimpyo.dot.data.model.HomeContent
import com.machimpyo.dot.data.model.Library


fun getMockThirdPartyLibraries(): List<Library> {
    val libraries = listOf(
        Library(
            title = "Dagger-Hilt",
            url = "com.google.dagger:hilt-android",
            content = "안드로이드 의존성 주입 라이브러리"
        ),
        Library(
            title = "Jetpack Compose",
            url = "androidx.compose:compose-bom",
            content = "안드로이드 UI 툴킷"
        ),
        Library(
            title = "Jetpack Compose",
            url = "androidx.compose:compose-bom",
            content = "안드로이드 UI 툴킷"
        ),
        Library(
            title = "Data Store",
            url = "androidx.datastore:datastore",
            content = "안드로이드 로컬 저장소"
        ),
        Library(
            title = "Lottie - Android",
            url = "com.airbnb.android:lottie-compose",
            content = "로티 애니메이션"
        ),
        Library(
            title = "Accompanist",
            url = "com.google.accompanist:accompanist",
            content = "안드로이드 Compose Accompanist"
        ),
        Library(
            title = "Firebase",
            url = "com.google.firebase:firebase-bom",
            content = "파이어베이스 동적링크, 인증"
        ),
        Library(
            title = "Sphere text view",
            url = "io.github.oleksandrbalan:tagcloud",
            content = "텍스트 클라우드 UI"
        ),
        Library(
            title = "Calendar",
            url = "com.kizitonwose.calendar",
            content ="캘린더 UI"
        ),
        Library(
            title = "Coil",
            url = "io.coil-kt:coil-compose",
            content = "이미지 라이브러리"
        ),
        Library(
            title = "Read More Text",
            url = "com.webtoonscorp.android:readmore-material",
            content = "Read More Text UI"
        ),
        Library(
            title = "ProtoBuf",
            url = "com.google.protobuf:protobuf-java",
            content = "for Proto Data Store"
        )
    )



    return libraries
}


fun getMockHomeContents(): List<HomeContent> {

    val result = mutableListOf<HomeContent>()

    result.add(
        HomeContent(
            thumbnail = R.drawable.home_content_thumbnail0,
            title = "퇴사는 인생 처음인 당신을 위해",
            subtitle = "처음 퇴사하는 사람들은 잘 모르는 이것 !\n" +
                    "퇴사 사유는 뭐라고 하면 좋을까 ? ",
            hashTags = listOf("퇴사 사유", "퇴사 방법"),
            contentUid = 0
        )
    )

    result.add(
        HomeContent(
            thumbnail = R.drawable.home_content_thumbnail1,
            title = "나를 찾아 떠나는 여행",
            subtitle = "이제는 미룰수없는 나를 위한 여행\n" +
                    "한국 관광공사가 추천하는 13선!",
            hashTags = listOf("여행지", "인기 장소"),
            contentUid = 1
        )
    )

    result.add(
        HomeContent(
            thumbnail = R.drawable.home_content_thumbnail2,
            title = "적절한 퇴사 말하는 시기",
            subtitle = "결정은 했는데, 언제 말할지 고민이라면,\n사회적약속은 언제일까",
            hashTags = listOf("퇴사 시기", "서로의 약속"),
            contentUid = 2
        )
    )

    return result
}

fun getMockContentInfo(contentUid: Int): ContentInfo {

    val titles = listOf(
        "회사 그만둘때\n" +
                "이렇게 말하는거 괜찮을까?", "미뤄왔던 여행,\n어디로 가면 좋을까?", "이제는 떠나야할 때\n" +
                "언제말하는게 가장 좋을까?"
    )

    val subtitles = listOf(
        "퇴사시기 눈치 게임 시작? NO! 현명한 시기 내가 알려줄게",
        "2023 한국 관광공사가 선정한 국내 살아보기 13선 소개!",
        "퇴사시기 눈치 게임 시작? NO! 현명한 시기 내가 알려줄게"
    )

    val questions = listOf(
        "회사그만둘때 \n" +
                "상사한테 말하는게 어려워 고민입니다.\n" +
                "사정이 있긴한데 솔직하게 말하는게 최선일지\n" +
                "고민입니다.",
        "그동안 바빠서 여행을 못갔어요.\n오랫동안 여행을 가지 못했더니 이제는\n어디로 가야할지도 잘 모르겠/=*어요.\n도움을 받을 수 있을까요?",
        "퇴사하기로 결정했습니다. 하지만 소심한 성격 탓에 말하기가 너무어렵네요 언제 말하는게 시기상 가장 좋을까요?선배와커피마실때? 일주일 전?"
    )

    val referenceUrls = listOf(
        "https://www.youtube.com/watch?v=xfqIwluXKYA&t=2s",
        "https://korean.visitkorea.or.kr/travelmonth/trend/trend-d.do",
        "https://www.youtube.com/watch?v=f2PESfniVVA"
    )

    val photoIds =
        listOf(R.drawable.content_detail0, R.drawable.content_detail1, R.drawable.content_detail2)

    val replyText = listOf(
        "직속 상사 또는 선배 와 면담 시에는 퇴사 사유를 미리 준비하여 질문에 대비할 수 있도록 하는 것이 좋은데, " +
                "이 때 퇴사 사유는 최대한 일반적인 사유로 말하는 것이 좋은데요. 예를 들면 ‘다른 진로에 대한 경험을 쌓고 싶어서’ " +
                "’현재 직무가 적성에 대한 공부를 해보고 싶습니다’ 등이이에요.\n\n간혹 경영진이나 인사팀에서 솔직한 퇴사 원인을 요구하는 경우도 있으나," +
                "\n이 때에도 회사나 동료에 대한 험담은 절대로 하지않는 것이 좋아요. 지나치게 직접적인 불만을 언급하는 것은 향후 장기적인 관계를 이어나가는 데 부정적으로 작용할 수 있어요.",

        "혹시 그동안 바쁘다는 이유로 하지 못했던 일들을 차근차근 하나씩 해나가기 위해 고민중이신가요? 그 중에 여행이 있다면, 이 게시글에 잘 찾아오셨어요.\n\n" +
                "최북단인 속초부터,충주·청주·예산·영동·군산·전주·함양·나주·사천·남해·통영을 지나 최남단 해남까지 총 13개의 지역에서 진행하는 2박3일부터 최대 6박 7일까지의 살아보기 여행인데요.\n\n" +
                "그동안 항상 같은 장소에서 주어진 업무를 하며 지쳤던 마음을 낯선 공간에서 훌훌 털어버리길 바라요.",

        "퇴사할 경우 최소 한달은 회사 인사팀이나 담당 부서에 연락하는 것이 좋아요. 회사에서도 담당 후임자를 뽑아야하는 기간이 필요하기 때문이에요." +
                "\n민법에 따르면 사직서 체출시 한달까지는 회사와 근로자간에 근로계약 관계가 유효합니다. " +
                "때문에 한달간 무시하고 출근하지 않으면 무단 결근으로 처리됩니다. 이때 불이익이 생긴다면 회사는 근로자에게 손해배상을 청구 할 수 있어요. " +
                "결과적으로 최소 회사에는 한달간 서로 마음의 정리를 할 시간이 필요합니다."

    )

    return ContentInfo(
        title = titles[contentUid],
        subtitle = subtitles[contentUid],
        questionText = questions[contentUid],
        referenceUrl = referenceUrls[contentUid],
        photoId = photoIds[contentUid],
        replyText = replyText[contentUid]
    )


}