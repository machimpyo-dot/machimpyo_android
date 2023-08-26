package com.machimpyo.dot.service

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.machimpyo.dot.utils.extension.DOMAIN_URI_PREFIX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

data class DotDynamicLink(
    val domainUriPrefix: String = DOMAIN_URI_PREFIX,
    val link: Link,
    val androidArgumentDynamicLink: AndroidArgumentDynamicLink = AndroidArgumentDynamicLink(),
    val iosArgumentDynamicLink: IosArgumentDynamicLink = IosArgumentDynamicLink(),
    val otherPlatformArgumentDynamicLink: OtherPlatformArgumentDynamicLink = OtherPlatformArgumentDynamicLink(),
    val gA4ArgumentDynamicLink: GA4ArgumentDynamicLink = GA4ArgumentDynamicLink()
){
    override fun toString(): String {
        val result: String = ""
        result.apply {
            plus(domainUriPrefix)
            plus("?link="+link.toString())
            plus(androidArgumentDynamicLink.toString())
            plus(iosArgumentDynamicLink.toString())
            plus(otherPlatformArgumentDynamicLink.toString())
            plus(gA4ArgumentDynamicLink.toString())
        }

        return result
    }
}

data class Link(
    val url:String = DOMAIN_URI_PREFIX,
    val nav: String = "",
    val uid: String = "",
)
{
    override fun toString(): String {
        var result: String = ""

        result = "${url}"+
                (if(url.endsWith("/"))"" else "/")+
                "?nav=${nav}"+
                "&uid=${uid}"

        return result
    }

    companion object {
        val TAG = "Link"
        //link 문자열을 Link클래스로 분해
        fun parseLink(link: Uri): Link? {
            var parsedLink: Link? = null
            try {
                parsedLink= Link(
                    url= link.host.orEmpty(),
                    nav= link.getQueryParameter("nav").orEmpty(),
                    uid= link.getQueryParameter("uid").orEmpty(),
                )
                Log.i(TAG, parsedLink.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
            return parsedLink
        }
    }
}
data class AndroidArgumentDynamicLink(
    val apn: String = "",
    val amv: Int = 1,
    val afl: String = "",
){
    override fun toString(): String {
        var result: String = ""
        result = (if(apn.isNotBlank()) ("&apn=${apn}") else "") +
                (if(afl.isNotBlank()) ("&afl=${afl}") else "") +
                "&afl=${afl}"

        return result
    }
}

data class IosArgumentDynamicLink(
    val ibi: String = "",
    val ifl: String = "",
    val ius: String = "",
    val ipfl: String = "",
    val ipbi: String = "",
    val isi: String = "",
    val imv: String = "",
    val efr: String = "",
){
    override fun toString(): String {
        val result: String = ""
        result.apply {
            if(ibi.isNotBlank()) plus("&ibi=${ibi}")
            if(ifl.isNotBlank()) plus("&ifl=${ifl}")
            if(ius.isNotBlank()) plus("&ius=${ius}")
            if(ipfl.isNotBlank()) plus("&ipfl=${ipfl}")
            if(ipbi.isNotBlank()) plus("&ipbi=${ipbi}")
            if(isi.isNotBlank()) plus("&isi=${isi}")
            if(imv.isNotBlank()) plus("&imv=${imv}")
            if(efr.isNotBlank()) plus("&efr=${efr}")
        }

        return result
    }
}

data class OtherPlatformArgumentDynamicLink(
    val ofl: String = "",
){
    override fun toString(): String {
        val result: String = ""
        result.apply {
            if(ofl.isNotBlank()) plus("&ofl=${ofl}")
        }

        return result
    }
}

data class GA4ArgumentDynamicLink(
    val utm_source: String = "",
    val utm_medium: String = "",
    val utm_campaign: String = "",
    val utm_term: String = "",
    val utm_content: String = "",
){
    override fun toString(): String {
        val result: String = ""
        result.apply {
            if(utm_source.isNotBlank()) plus("&utm_source=${utm_source}")
            if(utm_medium.isNotBlank()) plus("&utm_medium=${utm_medium}")
            if(utm_campaign.isNotBlank()) plus("&utm_campaign=${utm_campaign}")
            if(utm_term.isNotBlank()) plus("&utm_term=${utm_term}")
            if(utm_content.isNotBlank()) plus("&utm_content=${utm_content}")
        }

        return result
    }
}


object FirebaseDeepLinkService: FirebaseDynamicLinks() {
    val dynamicLink: FirebaseDynamicLinks = Firebase.dynamicLinks
    override fun getDynamicLink(intent: Intent?): Task<PendingDynamicLinkData> {
        return dynamicLink.getDynamicLink(intent)
    }

    override fun getDynamicLink(dynamicLinkUri: Uri): Task<PendingDynamicLinkData> {
        return dynamicLink.getDynamicLink(dynamicLinkUri)
    }

    override fun createDynamicLink(): DynamicLink.Builder {
        return dynamicLink.createDynamicLink()
    }

    fun openDynamicLink(callback: (Link?) -> Unit = {}, intent: Intent?) {
        Log.i("인텐트", intent.toString())
        getDynamicLink(intent)
            .addOnSuccessListener {pendingDynamicLinkData: PendingDynamicLinkData? ->
                //GetDeeplink from result
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    deepLink?.let {
                        callback(Link.parseLink(link = it))
                    }
                }
                Log.i("DYNAMIC_LINK", deepLink.toString())

            }
            .addOnFailureListener { e -> Log.w("DYNAMIC_LINK", "getDynamicLink:onFailure", e) }
    }

    fun makeDynamicLink(callback: (String) -> Unit, dynamicLink: DotDynamicLink){
        var dynamicLinkResult: DynamicLink

        dynamicLinkResult = Firebase.dynamicLinks.dynamicLink  {
            Log.i("DYNAMIC_LINK", "생성 요청")
            link = Uri.parse(dynamicLink.link.toString())
            domainUriPrefix = dynamicLink.domainUriPrefix

            androidParameters(packageName = dynamicLink.androidArgumentDynamicLink.apn) {
                minimumVersion = dynamicLink.androidArgumentDynamicLink.amv.toInt()
//                fallbackUrl = Uri.parse(dynamicLink.androidArgumentDynamicLink.afl)
            }

        }

        Log.i("DYNAMIC_LINK", "dynamiclink 생성 완료 ${dynamicLinkResult.uri}")

        Firebase.dynamicLinks.shortLinkAsync  {
            Log.i("DYNAMIC_LINK", "생성 요청")
            longLink = dynamicLinkResult.uri
//            link = Uri.parse(dynamicLink.link.toString())
//            domainUriPrefix = dynamicLink.domainUriPrefix
//
//            androidParameters(packageName = dynamicLink.androidArgumentDynamicLink.apn) {
//                minimumVersion = dynamicLink.androidArgumentDynamicLink.amv
////                fallbackUrl = Uri.parse(dynamicLink.androidArgumentDynamicLink.afl)
//            }
//            Log.i("DYNAMIC_LINK", "생성 요청")
        }.addOnSuccessListener { shortDynamicLink ->
            callback(shortDynamicLink.shortLink.toString())
            Log.i("DYNAMIC_LINK", "shortLink 생성 완료 ${shortDynamicLink.shortLink}")
        }.addOnFailureListener { exception ->
            callback("실패")
            Log.e("DYNAMIC_LINK", "생성 실패\n ${exception}")
        }.addOnCanceledListener {
            Log.i("DYNAMIC_LINK", "생성 취소")
        }
    }
}
