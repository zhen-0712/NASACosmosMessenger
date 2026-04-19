package com.example.line_dev.ui.chat

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.line_dev.data.model.ChatMessage
import com.example.line_dev.ui.theme.*
import com.example.line_dev.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun ChatScreen(chatViewModel: ChatViewModel = viewModel()) {
    val messages by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { keyboardController?.hide() })
            }
    ) {
        // TopBar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Nova",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }
            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NovaAvatar()
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.tertiary,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)

        // Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "輸入日期或訊息...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            // 日曆按鈕
            IconButton(
                onClick = {
                    keyboardController?.hide()
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val dateStr = "%d/%02d/%02d".format(year, month + 1, day)
                            inputText = dateStr
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = "選擇日期",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // 傳送按鈕
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        chatViewModel.sendMessage(inputText)
                        inputText = ""
                        keyboardController?.hide()
                    }
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun NovaAvatar() {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.Face,
            contentDescription = "Nova",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    if (message.isUser) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    color = White,
                    fontSize = 14.sp
                )
            }
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            NovaAvatar()
            if (message.apod != null && message.apod.mediaType == "image") {
                Column(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .clip(RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = message.content,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    AsyncImage(
                        model = message.apod.url,
                        contentDescription = message.apod.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = message.apod.title,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = message.apod.date,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .clip(RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = message.content,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}