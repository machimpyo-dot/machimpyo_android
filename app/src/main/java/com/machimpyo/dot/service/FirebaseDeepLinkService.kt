package com.machimpyo.dot.service

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.machimpyo.dot.utils.extension.DOMAIN_URI_PREFIX

data class DotDynamicLink(
    val domainUriPrefix: String = DOMAIN_URI_PREFIX,
    val link: Link,
    val androidArgumentDynamicLink: AndroidArgumentDynamicLink = AndroidArgumentDynamicLink(),
    val iosArgumentDynamicLink: IosArgumentDynamicLink = IosArgumentDynamicLink(),
    val otherPlatformArgumentDynamicLink: OtherPlatformArgumentDynamicLink = OtherPlatformArgumentDynamicLink(),
    val socialMetaTagArgumentDynamicLink: SocialMetaTagArgumentDynamicLink = SocialMetaTagArgumentDynamicLink(),
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
        fun parse(link: Uri): Link? {
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
        var result: String = ""

        result =  (if(ibi.isNotBlank()) ("&ibi=${ibi}") else "") +
                (if(ifl.isNotBlank()) ("&ifl=${ifl}") else "") +
                (if(ius.isNotBlank()) ("&ius=${ius}") else "") +
                (if(ipfl.isNotBlank()) ("&ipfl=${ipfl}") else "") +
                (if(ipbi.isNotBlank()) ("&ipbi=${ipbi}") else "") +
                (if(isi.isNotBlank()) ("&isi=${isi}") else "") +
                (if(imv.isNotBlank()) ("&imv=${imv}") else "") +
                (if(efr.isNotBlank()) ("&efr=${efr}") else "")

        return result
    }
}

data class OtherPlatformArgumentDynamicLink(
    val ofl: String = "",
){
    override fun toString(): String {
        var result: String = ""

        result = (if(ofl.isNotBlank()) ("&ofl=${ofl}") else "")

        return result
    }
}

data class SocialMetaTagArgumentDynamicLink(
    val st: String = "",
    val sd: String = "",
    val si: Uri? = null,
){
    override fun toString(): String {
        var result: String = ""

        result = if(st.isNotBlank()) "&st=${st}" else "" +
                if(sd.isNotBlank()) "&sd=${sd}" else "" +
                if(si != null) "&si=${si}" else ""

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
        var result: String = ""

        result =  (if(utm_source.isNotBlank()) ("&utm_source=${utm_source}") else "") +
                (if(utm_medium.isNotBlank()) ("&utm_medium=${utm_medium}") else "") +
                (if(utm_campaign.isNotBlank()) ("&utm_campaign=${utm_campaign}") else "") +
                (if(utm_term.isNotBlank()) ("&utm_term=${utm_term}") else "") +
                (if(utm_content.isNotBlank()) ("&utm_content=${utm_content}") else "")


        return result
    }
}


object FirebaseDeepLinkService: FirebaseDynamicLinks() {
    var latestOpenLink: Link? = null

    private val dynamicLink: FirebaseDynamicLinks = Firebase.dynamicLinks
    override fun getDynamicLink(intent: Intent?): Task<PendingDynamicLinkData> {
        return dynamicLink.getDynamicLink(intent)
    }

    override fun getDynamicLink(dynamicLinkUri: Uri): Task<PendingDynamicLinkData> {
        return dynamicLink.getDynamicLink(dynamicLinkUri)
    }

    override fun createDynamicLink(): DynamicLink.Builder {
        return dynamicLink.createDynamicLink()
    }

    fun readDynamicLink(callback: (Link?) -> Unit = {}, intent: Intent?) {
        Log.i("인텐트", intent.toString())
        getDynamicLink(intent)
            .addOnSuccessListener {pendingDynamicLinkData: PendingDynamicLinkData? ->
                //GetDeeplink from result
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    deepLink?.let {
                        latestOpenLink = Link.parse(link = it)

                        callback(latestOpenLink)
                    }
                }
                Log.i("DYNAMIC_LINK", deepLink.toString())

            }
            .addOnFailureListener { e -> Log.w("DYNAMIC_LINK", "getDynamicLink:onFailure", e) }
    }


    fun makeDynamicLink(callback: (String) -> Unit, dynamicLink: DotDynamicLink){
        val dynamicLinkResult: DynamicLink

        dynamicLinkResult = Firebase.dynamicLinks.dynamicLink  {
            Log.i("DYNAMIC_LINK", "생성 요청")
            link = Uri.parse(dynamicLink.link.toString())
            domainUriPrefix = dynamicLink.domainUriPrefix

            androidParameters(packageName = dynamicLink.androidArgumentDynamicLink.apn) {
                minimumVersion = dynamicLink.androidArgumentDynamicLink.amv
                fallbackUrl = Uri.parse(dynamicLink.androidArgumentDynamicLink.afl)
            }

            socialMetaTagParameters {
                this.title = dynamicLink.socialMetaTagArgumentDynamicLink.st
                this.description = dynamicLink.socialMetaTagArgumentDynamicLink.sd
                dynamicLink.socialMetaTagArgumentDynamicLink.si?.let {
                    this.imageUrl = it
                }
            }

        }

        Firebase.dynamicLinks.shortLinkAsync  {
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
//            Log.i("DYNAMIC_LINK", "shortLink 생성 완료 ${shortDynamicLink.shortLink}")
        }.addOnFailureListener { exception ->
            callback("실패")
            Log.e("DYNAMIC_LINK", "생성 실패\n ${exception}")
        }.addOnCanceledListener {
            Log.i("DYNAMIC_LINK", "생성 취소")
        }
    }

    fun openDynamicLink(callback: (Link) -> Unit, link: Link?): Boolean {
        if (link == null) return false
        if (link.nav.isBlank()) return false

        callback(link)

        return true
    }
}
