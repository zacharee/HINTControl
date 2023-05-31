@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

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
import androidx.compose.ui.Modifier
import dev.zwander.common.components.PageGrid
import dev.zwander.common.model.Endpoints
import dev.zwander.common.model.GlobalModel
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
    val title: String,
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
                title = "GET",
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
                title = "POST",
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
        renderItemTitle = { Text(text = it.title) },
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
            )

            OutlinedTextField(
                value = body,
                onValueChange = { body = it }
            )

            Button(
                onClick = {
                    scope.launch {
                        response = it.runMethod(
                            "${Endpoints.baseIp}/${url}",
                            body.replace("\"", "\\\"")
                        )?.bodyAsText()
                    }
                },
            ) {
                Text(text = "RUN")
            }

            Text(
                text = response ?: "",
            )
        }
    )
}
