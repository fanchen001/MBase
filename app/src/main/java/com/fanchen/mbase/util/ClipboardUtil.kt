package com.fanchen.mbase.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri


/**
 * 剪切板相關
 * Created by fanchen on 2018/9/5.
 */
object ClipboardUtil {
    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    fun copyText(context: Context?,text: CharSequence) {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText("text", text))
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    fun getText(context: Context?): CharSequence? {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.getPrimaryClip()
        return if (clip != null && clip.getItemCount() > 0) {
            clip.getItemAt(0).coerceToText(context)
        } else null
    }

    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    fun copyUri(context: Context?,uri: Uri) {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newUri(context?.getContentResolver(), "uri", uri))
    }

    /**
     * 获取剪贴板的uri
     *
     * @return 剪贴板的uri
     */
    fun getUri(context: Context?): Uri? {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.getPrimaryClip()
        return if (clip != null && clip.getItemCount() > 0) {
            clip.getItemAt(0).getUri()
        } else null
    }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    fun copyIntent(context: Context?,intent: Intent) {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newIntent("intent", intent))
    }

    /**
     * 获取剪贴板的意图
     *
     * @return 剪贴板的意图
     */
    fun getIntent(context: Context?): Intent? {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.getPrimaryClip()
        return if (clip != null && clip.getItemCount() > 0) {
            clip.getItemAt(0).getIntent()
        } else null
    }
}