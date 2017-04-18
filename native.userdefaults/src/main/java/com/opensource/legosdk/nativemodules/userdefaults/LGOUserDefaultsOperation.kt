package com.opensource.legosdk.nativemodules.userdefaults

import android.content.Context
import android.content.SharedPreferences
import android.util.LruCache
import com.opensource.legosdk.core.LGORequestable
import com.opensource.legosdk.core.LGOResponse

/**
 * Created by PonyCui_Home on 2017/4/18.
 */
class LGOUserDefaultsOperation(val request: LGOUserDefaultsRequest): LGORequestable() {

    override fun requestSynchronize(): LGOResponse {
        val aKey = request.key ?: return LGOResponse().reject("Native.UserDefaults", -1, "null key value")
        when(request.opt) {
            "update", "create" -> {
                if (request.suite.startsWith("memory://")) {
                    memoryData.put(request.suite + aKey, request.value)
                }
                else if (request.suite.startsWith("cache://")) {
                    cacheData.put(request.suite + aKey, request.value)
                }
                else {
                    val preferences = request.context?.requestContentContext()?.getSharedPreferences(request.suite, Context.MODE_PRIVATE)
                    preferences?.let {
                        val editor = it.edit()
                        editor.putString(aKey, request.value)
                        editor.apply()
                        return LGOResponse().accept(null)
                    }
                }
            }
            "read" -> {
                if (request.suite.startsWith("memory://")) {
                    return LGOUserDefaultsResponse(memoryData[request.suite + aKey] as? String).accept(null)
                }
                else if (request.suite.startsWith("cache://")) {
                    return LGOUserDefaultsResponse(cacheData[request.suite + aKey] as? String).accept(null)
                }
                else {
                    val preferences = request.context?.requestContentContext()?.getSharedPreferences(request.suite, Context.MODE_PRIVATE)
                    preferences?.let {
                        return LGOUserDefaultsResponse(it.getString(aKey, null)).accept(null)
                    }
                }
            }
            "delete" -> {
                if (request.suite.startsWith("memory://")) {
                    memoryData.remove(request.suite + aKey)
                    return LGOResponse().accept(null)
                }
                else if (request.suite.startsWith("cache://")) {
                    cacheData.remove(request.suite + aKey)
                    return LGOResponse().accept(null)
                }
                else {
                    val preferences = request.context?.requestContentContext()?.getSharedPreferences(request.suite, Context.MODE_PRIVATE)
                    preferences?.let {
                        val editor = it.edit()
                        editor.remove(aKey)
                        editor.apply()
                        return LGOResponse().accept(null)
                    }
                }
            }
        }
        return LGOResponse().reject("Native.UserDefaults", -2, "invalid opt value");
    }

    companion object {

        val memoryData: HashMap<String, Any> = hashMapOf()

        val cacheData: LruCache<String, Any> = LruCache(4 * 1024 * 1024)

    }

}