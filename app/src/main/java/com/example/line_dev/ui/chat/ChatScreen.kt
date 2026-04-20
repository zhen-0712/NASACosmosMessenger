package com.example.line_dev.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.data.model.ChatMessage
import com.example.line_dev.ui.theme.*
import com.example.line_dev.utils.ShareUtils
import com.example.line_dev.utils.StarCardGenerator
import com.example.line_dev.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatViewModel: ChatViewModel = viewModel()) {
    val messages by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()
    val snackbarMessage by chatViewModel.snackbarMessage.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            chatViewModel.clearSnackbar()
        }
    }

    val calendarColors = DatePickerDefaults.colors(
        containerColor = WinterGray,
        titleContentColor = SilverMilk,
        headlineContentColor = White,
        weekdayContentColor = SilverMilk,
        subheadContentColor = SilverMilk,
        navigationContentColor = SilverMilk,
        yearContentColor = SoftWhite,
        currentYearContentColor = SilverMilk,
        selectedYearContentColor = WinterGray,
        selectedYearContainerColor = SilverMilk,
        dayContentColor = SoftWhite,
        disabledDayContentColor = IceBlue.copy(alpha = 0.35f),
        selectedDayContentColor = WinterGray,
        selectedDayContainerColor = SilverMilk,
        todayContentColor = SilverMilk,
        todayDateBorderColor = SilverMilk,
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = millis
                        inputText = "%d/%02d/%02d".format(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                    }
                    showDatePicker = false
                }) {
                    Text("確定", color = SilverMilk, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消", color = IceBlue)
                }
            },
            colors = calendarColors,
            shape = RoundedCornerShape(20.dp)
        ) {
            DatePicker(state = datePickerState, colors = calendarColors)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = WinterGray,
                    contentColor = White,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(SilverMilk.copy(alpha = 0.5f), IceBlue.copy(alpha = 0.4f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Nightlight,
                            contentDescription = "Nova",
                            tint = WinterGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Nova",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "宇宙天文助理",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(
                        message = message,
                        onLongPress = { apod -> chatViewModel.saveFavorite(apod) },
                        onShare = { apod ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("正在產生星空卡...")
                                val file = StarCardGenerator.generateStarCard(
                                    context = context,
                                    imageUrl = apod.url,
                                    title = apod.title,
                                    date = apod.date
                                )
                                if (file != null) {
                                    ShareUtils.shareStarCard(context, file, apod.title, apod.date)
                                } else {
                                    snackbarHostState.showSnackbar("星空卡產生失敗，請稍後再試")
                                }
                            }
                        }
                    )
                }
                if (isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NovaAvatar()
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp))
                                    .background(White)
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = SilverMilk,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("輸入日期或訊息...", color = SilverMilk, fontSize = 14.sp)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SilverMilk,
                        unfocusedBorderColor = SilverMilkLight,
                        focusedTextColor = WinterGray,
                        unfocusedTextColor = WinterGray,
                        cursorColor = WinterGray
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "選擇日期",
                        tint = SilverMilk
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank())
                                Brush.linearGradient(listOf(IceBlue, WinterGray))
                            else
                                Brush.linearGradient(listOf(SilverMilkLight, SilverMilkLight))
                        ),
                    contentAlignment = Alignment.Center
                ) {
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
                            tint = if (inputText.isNotBlank()) White else IceBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NovaAvatar() {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(SilverMilk.copy(alpha = 0.5f), IceBlue.copy(alpha = 0.4f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.Nightlight,
            contentDescription = "Nova",
            tint = WinterGray,
            modifier = Modifier.size(18.dp)
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    message: ChatMessage,
    onLongPress: (ApodResponse) -> Unit,
    onShare: (ApodResponse) -> Unit
) {
    if (message.isUser) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                        ambientColor = WinterGray.copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp))
                    .background(Brush.linearGradient(colors = listOf(IceBlue, WinterGray)))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(text = message.content, color = White, fontSize = 14.sp)
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
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp),
                            ambientColor = SilverMilk.copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                        .background(White)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { onLongPress(message.apod) }
                        )
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = message.content, color = IceBlue, fontSize = 13.sp)
                    Box {
                        AsyncImage(
                            model = message.apod.url,
                            contentDescription = message.apod.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(175.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        // 長按收藏提示
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                                .background(
                                    color = WinterGray.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(text = "長按收藏", fontSize = 9.sp, color = White)
                        }
                    }
                    HorizontalDivider(color = SoftWhite, thickness = 0.5.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = message.apod.title,
                                color = WinterGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = message.apod.date,
                                color = SilverMilk,
                                fontSize = 10.sp
                            )
                        }
                        // 分享星空卡按鈕
                        IconButton(
                            onClick = { onShare(message.apod) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = "分享星空卡",
                                tint = SilverMilk,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .shadow(
                            elevation = 1.dp,
                            shape = RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp),
                            ambientColor = SilverMilk.copy(alpha = 0.15f)
                        )
                        .clip(RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                        .background(White)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(text = message.content, color = WinterGray, fontSize = 14.sp)
                }
            }
        }
    }
}