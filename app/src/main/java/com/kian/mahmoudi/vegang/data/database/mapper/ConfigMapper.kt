package com.kian.mahmoudi.vegang.data.database.mapper

import com.kian.mahmoudi.vegang.data.database.model.ConfigModel
import com.kian.mahmoudi.vegang.dto.ProfileItem
import com.kian.mahmoudi.vegang.enums.EConfigType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigMapper @Inject constructor() {

    fun toEntity(profile: ProfileItem): ConfigModel {
        return ConfigModel(
            id = profile.id,
            configVersion = profile.configVersion,
            configType = profile.configType.name,
            subscriptionId = profile.subscriptionId,
            addedTime = profile.addedTime,
            remarks = profile.remarks,
            description = profile.description,
            server = profile.server,
            serverPort = profile.serverPort,
            password = profile.password,
            method = profile.method,
            flow = profile.flow,
            username = profile.username,
            network = profile.network,
            headerType = profile.headerType,
            host = profile.host,
            path = profile.path,
            seed = profile.seed,
            kcpMtu = profile.kcpMtu,
            kcpTti = profile.kcpTti,
            quicSecurity = profile.quicSecurity,
            quicKey = profile.quicKey,
            mode = profile.mode,
            serviceName = profile.serviceName,
            authority = profile.authority,
            xhttpMode = profile.xhttpMode,
            xhttpExtra = profile.xhttpExtra,
            finalMask = profile.finalMask,
            security = profile.security,
            sni = profile.sni,
            alpn = profile.alpn,
            fingerPrint = profile.fingerPrint,
            insecure = profile.insecure,
            echConfigList = profile.echConfigList,
            verifyPeerCertByName = profile.verifyPeerCertByName,
            pinnedCA256 = profile.pinnedCA256,
            publicKey = profile.publicKey,
            shortId = profile.shortId,
            spiderX = profile.spiderX,
            mldsa65Verify = profile.mldsa65Verify,
            secretKey = profile.secretKey,
            preSharedKey = profile.preSharedKey,
            localAddress = profile.localAddress,
            reserved = profile.reserved,
            mtu = profile.mtu,
            obfsPassword = profile.obfsPassword,
            portHopping = profile.portHopping,
            portHoppingInterval = profile.portHoppingInterval,
            pinSHA256 = profile.pinSHA256,
            bandwidthDown = profile.bandwidthDown,
            bandwidthUp = profile.bandwidthUp,
            policyGroupType = profile.policyGroupType,
            policyGroupSubscriptionId = profile.policyGroupSubscriptionId,
            policyGroupFilter = profile.policyGroupFilter,
            proxyChainProfiles = profile.proxyChainProfiles,
            browserDialerMode = profile.browserDialerMode
        )
    }

    fun toProfileItem(entity: ConfigModel): ProfileItem {
        return ProfileItem(
            id = entity.id,
            latency = entity.latency,
            configVersion = entity.configVersion,
            configType = EConfigType.valueOf(entity.configType),
            subscriptionId = entity.subscriptionId,
            addedTime = entity.addedTime,
            remarks = entity.remarks,
            description = entity.description,
            server = entity.server,
            serverPort = entity.serverPort,
            password = entity.password,
            method = entity.method,
            flow = entity.flow,
            username = entity.username,
            network = entity.network,
            headerType = entity.headerType,
            host = entity.host,
            path = entity.path,
            seed = entity.seed,
            kcpMtu = entity.kcpMtu,
            kcpTti = entity.kcpTti,
            quicSecurity = entity.quicSecurity,
            quicKey = entity.quicKey,
            mode = entity.mode,
            serviceName = entity.serviceName,
            authority = entity.authority,
            xhttpMode = entity.xhttpMode,
            xhttpExtra = entity.xhttpExtra,
            finalMask = entity.finalMask,
            security = entity.security,
            sni = entity.sni,
            alpn = entity.alpn,
            fingerPrint = entity.fingerPrint,
            insecure = entity.insecure,
            echConfigList = entity.echConfigList,
            verifyPeerCertByName = entity.verifyPeerCertByName,
            pinnedCA256 = entity.pinnedCA256,
            publicKey = entity.publicKey,
            shortId = entity.shortId,
            spiderX = entity.spiderX,
            mldsa65Verify = entity.mldsa65Verify,
            secretKey = entity.secretKey,
            preSharedKey = entity.preSharedKey,
            localAddress = entity.localAddress,
            reserved = entity.reserved,
            mtu = entity.mtu,
            obfsPassword = entity.obfsPassword,
            portHopping = entity.portHopping,
            portHoppingInterval = entity.portHoppingInterval,
            pinSHA256 = entity.pinSHA256,
            bandwidthDown = entity.bandwidthDown,
            bandwidthUp = entity.bandwidthUp,
            policyGroupType = entity.policyGroupType,
            policyGroupSubscriptionId = entity.policyGroupSubscriptionId,
            policyGroupFilter = entity.policyGroupFilter,
            proxyChainProfiles = entity.proxyChainProfiles,
            browserDialerMode = entity.browserDialerMode
        )
    }

    fun toEntityList(profiles: List<ProfileItem>): List<ConfigModel> {
        return profiles.map { toEntity(it) }
    }

    fun toProfileList(entities: List<ConfigModel>): List<ProfileItem> {
        return entities.map { toProfileItem(it) }
    }

}