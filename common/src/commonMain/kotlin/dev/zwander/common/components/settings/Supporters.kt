package dev.zwander.common.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.zwander.common.util.PatreonSupportersParser
import dev.zwander.common.util.SupporterInfo
import dev.zwander.common.util.UrlHandler
import dev.zwander.common.util.animateContentHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Supporters() {
    var supporters by remember {
        mutableStateOf(listOf<SupporterInfo>())
    }

    LaunchedEffect(null) {
        supporters = PatreonSupportersParser.getInstance().parseSupporters()
    }

    LazyColumn(
        modifier = Modifier.heightIn(max = 300.dp)
            .animateContentHeight(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(items = supporters, key = { it.hashCode() }) { item ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    UrlHandler.launchUrl(item.link)
                },
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .heightIn(min = 56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.name,
                    )
                }
            }
        }
    }
}
