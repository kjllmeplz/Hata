package com.ranko.hata

import android.annotation.SuppressLint
import android.app.Activity
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker

class ModuleHook {

    @XposedHooker
    class SettingsHooker : XposedInterface.Hooker {
        companion object {
            @SuppressLint("DiscouragedApi")
            @Suppress("UNCHECKED_CAST")
            @JvmStatic
            @AfterInvocation
            fun after(param: XposedInterface.AfterHookCallback) {
                val mContext = (param.thisObject as Activity).baseContext

                val ids = ArrayList<Int>()
                ids.add(mContext.resources.getIdentifier("security_status", "id", mContext.packageName))
                ids.add(mContext.resources.getIdentifier("safety_emergency_settings", "id", mContext.packageName))
                ids.add(mContext.resources.getIdentifier("feedback_services_settings", "id", mContext.packageName))

                val headers = param.args[0] as MutableList<Any>
                val removeIDs = ArrayList<Int>()

                val ggTitle: CharSequence = "Google"
                var ggHeader: Any? = null

                for ((i, head) in headers.withIndex()) {
                    val fieldID = head.javaClass.getDeclaredField("id")
                    val fieldTitle = head.javaClass.getDeclaredField("title")

                    for (id in ids) {
                        if (fieldID.getLong(head) == id.toLong()) {
                            removeIDs.add(i)
                            break
                        }
                    }

                    if (fieldTitle.get(head) != null) {
                        if (fieldTitle.get(head)?.equals(ggTitle) == true) {
                            removeIDs.add(i)
                            ggHeader = head
                        }
                    }
                }

                if (removeIDs.isNotEmpty()) {
                    removeIDs.sortDescending()
                    for (removeID in removeIDs) {
                        headers.removeAt(removeID)
                    }
                }

                if (ggHeader != null) {
                    headers.add(ggHeader)
                }
            }
        }
    }

    @XposedHooker
    class SystemUIHooker(private val magic: Boolean) : XposedInterface.Hooker {
        companion object {
            @JvmStatic
            @BeforeInvocation
            fun before(callback: XposedInterface.BeforeHookCallback): SystemUIHooker {
                return SystemUIHooker(false)
            }
        }
    }
}