@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.PageGrid
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.SettingsModel
import dev.zwander.common.util.moveFocusOnTab
import dev.zwander.resources.common.MR
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private data class FuzzerItem(
    val title: StringResource,
    val runMethod: suspend (url: String, body: String) -> HttpResponse?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@HiddenFromObjC
fun FuzzerPage(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val items = remember {
        listOf(
            FuzzerItem(
                title = MR.strings.get,
                runMethod = { url, body ->
                    GlobalModel.httpClient.value?.genericRequest(false) {
                        get(url) {
                            contentType(ContentType.parse("application/json"))
                            setBody(body)
                        }
                    }
                },
            ),
            FuzzerItem(
                title = MR.strings.post,
                runMethod = { url, body ->
                    GlobalModel.httpClient.value?.genericRequest(false) {
                        post(url) {
                            contentType(ContentType.parse("application/json"))
                            setBody(body)
                        }
                    }
                },
            ),
        )
    }

    PageGrid(
        items = items,
        modifier = modifier,
        renderItemTitle = { Text(text = stringResource(it.title)) },
        renderItem = {
            var response by remember {
                mutableStateOf<String?>(null)
            }

            var url by remember {
                mutableStateOf("")
            }
            var body by remember {
                mutableStateOf("")
            }

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = {
                    Text(text = stringResource(MR.strings.url))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = {
                    Text(text = stringResource(MR.strings.body))
                },
                modifier = Modifier.fillMaxWidth().moveFocusOnTab(),
            )

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        response = it.runMethod(
                            "http://${SettingsModel.gatewayIp.value}/${url}",
                            body.replace("\"", "\\\"")
                        )?.bodyAsText()
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(text = stringResource(MR.strings.run))
            }

            AnimatedVisibility(
                visible = response != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                SelectionContainer {
                    Text(
                        text = response ?: "",
                    )
                }
            }
        }
    )
}
