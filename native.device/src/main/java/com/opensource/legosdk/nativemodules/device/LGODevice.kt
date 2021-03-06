package com.opensource.legosdk.nativemodules.device

import com.opensource.legosdk.core.*
import org.json.JSONObject

/**
 * Created by PonyCui_Home on 2017/4/17.
 */
class LGODevice : LGOModule() {

    override fun buildWithJSONObject(obj: JSONObject, context: LGORequestContext): LGORequestable? {
        return LGODeviceOperation(context)
    }

    override fun buildWithRequest(request: LGORequest): LGORequestable? {
        val context = request.context ?: return null
        return LGODeviceOperation(context)
    }

    override fun synchronizeResponse(context: LGORequestContext): LGOResponse? {
        return LGODeviceOperation(context).requestSynchronize()
    }

    companion object {

        val custom: HashMap<String, Any> = hashMapOf()

    }

}