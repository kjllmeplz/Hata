package com.ranko.hata

import android.annotation.SuppressLint
import android.app.Activity
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import io.github.libxposed.api.XposedInterface.Hooker
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.XposedHooker
import org.apache.commons.lang3.ClassUtils

private lateinit var module: ModuleMain

class ModuleMain(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {

    init {
        module = this
    }

    @XposedHooker
    class SettingsHooker : Hooker {
        companion object {
            @SuppressLint("DiscouragedApi")
            @Suppress("UNCHECKED_CAST")
            @JvmStatic
            @AfterInvocation
            fun after(param: AfterHookCallback) {
                val mContext = (param.thisObject as Activity).baseContext

                val ids = ArrayList<Int>()
                ids.add(
                    mContext.resources.getIdentifier(
                        "security_status",
                        "id",
                        mContext.packageName
                    )
                )
                ids.add(
                    mContext.resources.getIdentifier(
                        "safety_emergency_settings",
                        "id",
                        mContext.packageName
                    )
                )
                ids.add(
                    mContext.resources.getIdentifier(
                        "feedback_services_settings",
                        "id",
                        mContext.packageName
                    )
                )

                val headers = param.args[0] as MutableList<Any>
                val removeIDs = ArrayList<Int>()

                val ggTitle: CharSequence = "Google"
                var ggHeader: Any? = null

                for ((i, head) in headers.withIndex()) {
                    val fieldID = head.javaClass.getDeclaredField("id")
                    val fieldTitle = head.javaClass.getDeclaredField("title")

                    for (id in ids) {
                        if (fieldID.getLong(head) == id.toLong()) {
                            //module.log("Found id $id at Index $i")
                            removeIDs.add(i)
                        }
                    }

                    if (fieldTitle.get(head) != null) {
                        if (fieldTitle.get(head)?.equals(ggTitle) == true) {
                            //module.log("Found Google at Index $i")
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

                module.log("Rearrange the menu success!")
            }
        }
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        if (!param.isFirstPackage) return

        if (param.packageName == "com.android.settings") {
            val loadClass = ClassUtils.getClass(param.classLoader,"com.android.settings.MiuiSettings")
            val loadMethod = loadClass.getDeclaredMethod("updateHeaderList", List::class.java)
            loadMethod.isAccessible = true
            hook(loadMethod, SettingsHooker::class.java)
        }
    }
}
