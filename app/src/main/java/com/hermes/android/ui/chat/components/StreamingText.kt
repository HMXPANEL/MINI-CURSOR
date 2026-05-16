package com.hermes.android.ui.chat.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.linkify.LinkifyPlugin

@Composable
fun StreamingText(
    content: String,
    isStreaming: Boolean,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            val markwon = Markwon.builder(context)
                .usePlugin(TablePlugin.create(context))
                .usePlugin(HtmlPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .build()
            androidx.appcompat.widget.AppCompatTextView(context).apply {
                setTextColor(android.graphics.Color.WHITE)
                textSize = 16f
                setLineSpacing(0f, 1.2f)
            }
        },
        update = { view ->
            val markwon = Markwon.builder(view.context)
                .usePlugin(TablePlugin.create(view.context))
                .usePlugin(HtmlPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .build()
            markwon.setMarkdown(view, content + if (isStreaming) "▌" else "")
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    )
}
